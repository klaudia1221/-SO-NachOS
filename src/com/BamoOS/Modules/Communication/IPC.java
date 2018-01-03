package com.BamoOS.Modules.Communication;

import com.BamoOS.Modules.ProcessManager.ProcessManager;

import java.util.ArrayList;

public class IPC
{
    private ProcessManager pm;

    private int maxSmsSize = 8;

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

        if(pm.checkIfProcessExists(recID)==null) //jesli ID procesu o indeksie i == recID
        {
            //zapisz w odpowiednim polu PCB danego procesu wiadomość
            //zapisz wiadomosc w globalnym kontenerze wszystich wiadomości
            //powiadom proces-odbiorcę o wiadomości metodą signal()
            System.out.println("Wyslano wiadomosc o tresci "+sms.get_mes()+" do procesu o ID "+recID);
            return;
        }
        System.out.println("W tej grupie nie znaleziono procesu o ID "+recID);
    }

    public void RM(int senID, Sms sms)
    {
        if()//kontener wiadomości z PCB jest pusty
        {
            //przechodzi w stan waiting
        }else
        {
            //wyświetla pierwszą wiadomość, którą znajdzie w kontenerze wiadomości w PCB
            //usuwa z kontenera w PCB pierwszą wiadomość
        }
    }

    public void display_all()
    {
        for()//przegląda cały kontener wiadomości z PCB
        {
            //wyświetla wiadomości
        }
    }
}
