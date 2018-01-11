package com.NachOS.Modules.Communication;

import com.NachOS.Modules.Exceptions.*;
import com.NachOS.Modules.ProcessManager.ProcessManager;
import com.NachOS.Modules.MemoryManagment.RAM;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IPC
{

    public static final String zs = "-";//znak oddzielajacy fragmenty wiadomosci w ramie
    public static final String zk = "$";//znak konca wiadomosci w ramie


    private Map<Integer, Integer> firstFree = new HashMap<>(); //<PID,pierwsze wolne miejsce w RAMie>

    private ProcessManager pm;
    private RAM ram;

    private static final int ramCap = 31; //miejsca w RAMie - 1

    private static final int maxSmsSize = 8;

    private ArrayList<Sms> allSent = new ArrayList<>();
    private ArrayList<Sms> allReceived = new ArrayList<>();

    public IPC(ProcessManager pm, RAM ram)
    {
        this.pm=pm;
        this.ram=ram;
    }

    public void sendMessage(int recID, Sms sms) throws WrongGroupException, WrongProcessIDException, SenderReceiverException, TooLongException, NoReceiverException {
        sms.set_recID(recID);
        sms.set_senID(pm.getActivePCB().getPID());
        if(pm.getActivePCB().getPID()==recID) //sprawdza czy nadawca nie jest odbiorca
        {
            throw new SenderReceiverException("Nadawca nie moze byc jednoczesnie odbiorca");
        }
        if(sms.get_mesSize()>maxSmsSize) //sprawdza czy wiadomosc nie przekracza max rozmiaru
        {
            throw new TooLongException("Wiadomosc jest zbyt dluga");
        }

        if(pm.checkIfProcessExists(recID)==null) //sprawdza czy istnieje proces o ID==recID
        {
            throw new NoReceiverException("Nie znaleziono procesu odbiorcy");
        }

            if(pm.getActivePCB().getPGID()!=pm.checkIfProcessExists(recID).getPGID()) //sprawdza czy procesy sa w tej samej grupie
            {
                throw new WrongGroupException("Odbiorca w innej grupie niz nadawca");
            }
            //zapisz w odpowiednim polu PCB danego procesu wiadomość
            ArrayList<Sms> temp_list = pm.getPCB(recID).getSmsList();
            temp_list.add(sms);
            pm.getPCB(recID).setSmsList(temp_list);
            //System.out.println("smslist(0)=="+pm.getActivePCB().getSmsList().get(0));
            //System.out.println("sms=="+sms.get_senID()+sms.get_recID()+sms.get_mes());


            //zapisz wiadomosc w kontenerze wyslanych
            allSent.add(sms);

            //powiadom procesy czekajace na wiadomosc o nowej wiadomości metodą signalAll()
            try {
                pm.getConditionVariable(recID).signal();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Wyslano wiadomosc o tresci "+sms.get_mes()+" od procesu "+sms.get_senID()+" do procesu o ID "+recID);
        }


    /*private String mergeMessage(Sms sms)
    {
        String str="";
        str+=sms.get_senID()+";"+sms.get_recID()+";"+sms.get_mes();
        return str;
    }*/

    public void saveMessage(Sms sms) //do wywołania w receiveMessage bo zapisuje recID
    {
        int temp;
        int PID = sms.get_recID();
        String mes="";
        mes+=sms.get_senID()+zs+sms.get_recID()+zs+sms.get_mes();
        if(!firstFree.containsKey(sms.get_recID()))
        {
            firstFree.put(PID,0);
        }
        try
        {
            System.out.println("MESSAGE: "+mes);
            for(char c : mes.toCharArray())
            {
                pm.setSafeMemory(firstFree.get(PID),c);
                //ram.writeCharToRam(sms.get_recID(),adr,c);
                temp=firstFree.get(PID);
                temp++;
                firstFree.put(PID,temp);
            }
            pm.setSafeMemory(firstFree.get(PID),zk.charAt(0));
            temp=firstFree.get(PID);
            temp++;
            firstFree.put(PID,temp);
        } catch(Exception e)
        {
            System.out.println("Blad zapisu wiadomosci");
        }
    }

    public void receiveMessage() throws ChangedToWaitingException
    {
        ArrayList<Sms> temp_list = pm.getActivePCB().getSmsList();
        if(temp_list.size()==0)//kontener wiadomości z PCB jest pusty
        {
            //przechodzi w stan waiting
            try {
                System.out.println("Proces o ID " + pm.getActivePCB().getPID() + " czeka na wiadomosc, przechodzi w stan waiting");
                pm.getConditionVariable(pm.getActivePCB().getPID()).await(true); //zawsze true dla komunikacji
                throw new ChangedToWaitingException("");
            }catch (ChangedToWaitingException e){
                throw e;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }else
        {
            Sms sms = temp_list.get(0);
            //wyświetla pierwszą wiadomość, którą znajdzie w kontenerze wiadomości w PCB
            System.out.println("Proces o ID "+sms.get_recID()+" odebral wiadomosc o tresci "+sms.get_mes()+" od procesu o ID "+sms.get_senID());

            //zapisuje wiadomosc w kontenerze odebranych i ID nadawcy w PCB
            allReceived.add(sms);
            pm.getActivePCB().setLastSenderID(sms.get_senID());

            //usuwa z kontenera w PCB pierwszą wiadomość
            temp_list.remove(0);
            pm.getActivePCB().setSmsList(temp_list);

            saveMessage(sms);
        }
    }

    /*public Sms loadSms(int adr) //odczytuje wiadomosc (SMS) z podanego adresu w RAMie
    {
        String str="", parts[];
        char c;
        c=ram.getFromRam(adr);
        while(c!='$')
        {
            str+=c;
        }
        parts=str.split(";");
        Sms sms = new Sms(parts[2]);
        sms.set_senID(Integer.parseInt(parts[0]));
        sms.set_recID(Integer.parseInt(parts[1]));

        return sms;
    }*/

    public String loadMessage(int adr) //odczytuje tresc wiadomosci z podanego adresu w RAMie
    {
        String str="", parts[];
        char c;
        c=ram.getFromRam(adr);
        //c=pm.getSafeMemory(adr);
        while(c!=zk.charAt(0))
        {
            System.out.println(c);
            str+=c;
            //c=pm.getSafeMemory(adr);
            c=ram.getFromRam(adr);
            adr++;
        }

        //parts=str.split(Character.toString(zs));
        parts=str.split(zs);

        //Sms sms = new Sms(parts[2]);
        //sms.set_senID(Integer.parseInt(parts[0]));
        //sms.set_recID(Integer.parseInt(parts[1]));

        return parts[2];
    }

    public void loadAndSend(int recID, int adr) throws TooLongException, WrongProcessIDException, SenderReceiverException, WrongGroupException, NoReceiverException {
        Sms sms = new Sms(loadMessage(adr));
        sendMessage(recID, sms);
    }

    public void display_sent()
    {
        for(Sms sms : allSent)//przegląda historie wyslanych
        {
            //wyświetla wiadomości
            System.out.println("ID nadawcy: "+sms.get_senID()+"; ID odbiorcy: "+sms.get_recID()+"; tresc: "+sms.get_mes());
        }
    }

    public void display_received()
    {
        for(Sms sms : allReceived)//przegląda historie wyslanych
        {
            //wyświetla wiadomości
            System.out.println("ID nadawcy: "+sms.get_senID()+"; ID odbiorcy: "+sms.get_recID()+"; tresc: "+sms.get_mes());
        }
    }

    public void display_all()
    {
        System.out.println("Wyslane wiadomosci:");
        display_sent();
        System.out.println("Odebrane wiadomosci:");
        display_received();
    }
}
