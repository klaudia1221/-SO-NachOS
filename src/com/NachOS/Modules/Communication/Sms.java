package com.NachOS.Modules.Communication;




public class Sms
{
    private int senID; //ID nadawcy
    private int recID; //ID odbiorcy
    private String mes; //tresc wiadomosci
    private int mesSize; //rozmiar wiadomosci

    public Sms(String mes)
    {
        //this.senID = senID;
        //this.recID = recID;
        this.mes = mes;
        this.mesSize = mes.length();
    }

    public void set_senID(int senID)
    {
        this.senID=senID;
    }

    public void set_recID(int recID)
    {
        this.recID=recID;
    }

    public int get_senID()
    {
        return this.senID;
    }

    public int get_recID()
    {
        return this.recID;
    }

    public String get_mes()
    {
        return this.mes;
    }

    public int get_mesSize()
    {
        return this.mesSize;
    }}
