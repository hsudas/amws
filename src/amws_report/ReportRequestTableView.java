package amws_report;

import javafx.beans.property.SimpleStringProperty;

/**
 * reportRequest tablosu ile TableView baglantisi kurar
 */
public class ReportRequestTableView
{
    //buradaki değişkenler Controller sinifinda initTableView da gosterilmeli
    public SimpleStringProperty stn1;
    public SimpleStringProperty stn2;
    public SimpleStringProperty stn3;
    public SimpleStringProperty stn4;
    public SimpleStringProperty stn5;
    public SimpleStringProperty stn6;
    public SimpleStringProperty stn7;
    public SimpleStringProperty stn8;
    public SimpleStringProperty stn9;

    public ReportRequestTableView(String stn1, String stn2, String stn3, String stn4, String stn5, String stn6, String stn7, String stn8, String stn9)
    {
        this.stn1 = new SimpleStringProperty(stn1);
        this.stn2 = new SimpleStringProperty(stn2);
        this.stn3 = new SimpleStringProperty(stn3);
        this.stn4 = new SimpleStringProperty(stn4);
        this.stn5 = new SimpleStringProperty(stn5);
        this.stn6 = new SimpleStringProperty(stn6);
        this.stn7 = new SimpleStringProperty(stn7);
        this.stn8 = new SimpleStringProperty(stn8);
        this.stn9 = new SimpleStringProperty(stn9);
    }

    public String getStn1()
    {
        return stn1.get();
    }

    public SimpleStringProperty stn1Property()
    {
        return stn1;
    }

    public String getStn2()
    {
        return stn2.get();
    }

    public SimpleStringProperty stn2Property()
    {
        return stn2;
    }

    public String getStn3()
    {
        return stn3.get();
    }

    public SimpleStringProperty stn3Property()
    {
        return stn3;
    }

    public String getStn4()
    {
        return stn4.get();
    }

    public SimpleStringProperty stn4Property()
    {
        return stn4;
    }

    public String getStn5()
    {
        return stn5.get();
    }

    public SimpleStringProperty stn5Property()
    {
        return stn5;
    }

    public String getStn6()
    {
        return stn6.get();
    }

    public SimpleStringProperty stn6Property()
    {
        return stn6;
    }

    public String getStn7()
    {
        return stn7.get();
    }

    public SimpleStringProperty stn7Property()
    {
        return stn7;
    }

    public String getStn8()
    {
        return stn8.get();
    }

    public SimpleStringProperty stn8Property()
    {
        return stn8;
    }

    public String getStn9()
    {
        return stn9.get();
    }

    public SimpleStringProperty stn9Property()
    {
        return stn9;
    }
}
