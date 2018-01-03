package com.BamoOS.Modules.Communication;

import com.BamoOS.Modules.ProcessManager.ProcessManager;

import java.util.ArrayList;

public class IPC
{
    private ProcessManager pm;

    private static final int maxSmsSize = 8;

    private ArrayList<Sms> allMessages;

    public IPC(ProcessManager pm)
    {
        this.pm=pm;
    }

    public void SM(int recID, Sms sms)
    {
        sms.set_recID(recID);
        if(pm.getActivePCB().getPID()==recID) //senID==ID procesu wywolujacego SM();
        {
            System.out.println("Nadawca nie moze byc jednoczesnie odbiorca");
            return;
        }
        if(sms.get_mesSize()>maxSmsSize)
        {
            System.out.println("Wiadomosc jest zbyt dluga");
            return;
        }

        if(pm.checkIfProcessExists(recID)!=null) //sprawdza czy istnieje proces o ID==recID
        {
            if(pm.getActivePCB().getPGID()!=pm.checkIfProcessExists(recID).getPGID()) //sprawdza czy procesy sa w tej samej grupie
            {
                System.out.println("Proces o ID "+recID+" znajduje sie w innej grupie");
                return;
            }
            //zapisz w odpowiednim polu PCB danego procesu wiadomość
            ArrayList<Sms> temp_list = pm.getActivePCB().getSmsList();
            temp_list.add(sms);
            pm.getActivePCB().setSmsList(temp_list);

            //zapisz wiadomosc w globalnym kontenerze wszystich wiadomości
            allMessages.add(sms);

            //powiadom proces-odbiorcę o wiadomości metodą signal()
            pm.getConditionVariable(recID).signal();

            System.out.println("Wyslano wiadomosc o tresci "+sms.get_mes()+" do procesu o ID "+recID);
            return;
        }

        System.out.println("Nie znaleziono procesu o ID "+recID);
    }
    //ogarnąć te kontenery wiadomości czy dla grupy czy nie
    public void RM(int senID, Sms sms) //wyjebać te bezsensowne parametry z dupy
    {

        ArrayList<Sms> temp_list = pm.getActivePCB().getSmsList();
        if(temp_list.size()==0)//kontener wiadomości z PCB jest pusty
        {
            //przechodzi w stan waiting
            //pm.getConditionVariable().(); //gettować CV

        }else
        {
            //wyświetla pierwszą wiadomość, którą znajdzie w kontenerze wiadomości w PCB
            System.out.println(temp_list.get(0).get_mes());

            //usuwa z kontenera w PCB pierwszą wiadomość
            temp_list.remove(0);
        }
    }

    public void display_all()
    {
        for(Sms sms : allMessages)//przegląda cały kontener wiadomości z grupy procesow, historie
        {
            //wyświetla wiadomości
            System.out.println("ID nadawcy: "+sms.get_senID()+"; ID odbiorcy: "+sms.get_recID()+"; tresc: "+sms.get_mes());
        }
    }
}
