package amws_report;

import javafx.beans.property.SimpleStringProperty;

/**
 * reportRequest tablosu ile TableView baglantisi kurar
 */
public class ReportRequestTableView
{

    //buradaki değişkenler Controller sinifinda initTableView da gosterilmeli
    public SimpleStringProperty stn1;//START_DATE
    public SimpleStringProperty stn2;//END_DATE
    public SimpleStringProperty stn3;//REPORT_TYPE
    public SimpleStringProperty stn4;//SUBMIT_DATE
    public SimpleStringProperty stn5;//STATUS
    public SimpleStringProperty stn6;//REPORT_REQUEST_ID
    public SimpleStringProperty stn7;//GENERATED_REPORT_ID
    public SimpleStringProperty stn8;//DOWNLOADED_DB
    public SimpleStringProperty stn9;//DOWNLOAD_TYPE
    public SimpleStringProperty stn10;//SCHEDULE_ID
    public SimpleStringProperty stn11;//UUID
    public SimpleStringProperty stn12;//DOWNLOADED_PC

    public ReportRequestTableView(String stn1,
            String stn2,
            String stn3,
            String stn4,
            String stn5,
            String stn6,
            String stn7,
            String stn8,
            String stn9,
            String stn10,
            String stn11,
            String stn12)
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
        this.stn10 = new SimpleStringProperty(stn10);
        this.stn11 = new SimpleStringProperty(stn11);
        this.stn12 = new SimpleStringProperty(stn12);
    }

    public String getStn1()
    {
        return stn1.get();
    }

    public String getStn2()
    {
        return stn2.get();
    }

    public String getStn3()
    {
        return stn3.get();
    }

    public String getStn4()
    {
        return stn4.get();
    }

    public String getStn5()
    {
        return stn5.get();
    }

    public String getStn6()
    {
        return stn6.get();
    }

    public String getStn7()
    {
        return stn7.get();
    }

    public String getStn8()
    {
        return stn8.get();
    }

    public String getStn9()
    {
        return stn9.get();
    }

    public String getStn10()
    {
        return stn10.get();
    }

    public String getStn11()
    {
        return stn11.get();
    }

    public String getStn12()
    {
        return stn12.get();
    }

    /*
    public SimpleStringProperty stn1Property()
    {
        return stn1;
    }
    
    public SimpleStringProperty stn2Property()
    {
        return stn2;
    }
    
    public SimpleStringProperty stn3Property()
    {
        return stn3;
    }

    public SimpleStringProperty stn4Property()
    {
        return stn4;
    }

    public SimpleStringProperty stn5Property()
    {
        return stn5;
    }
    
    public SimpleStringProperty stn6Property()
    {
        return stn6;
    }
    
    public SimpleStringProperty stn7Property()
    {
        return stn7;
    }
    
    public SimpleStringProperty stn8Property()
    {
        return stn8;
    }
    
    public SimpleStringProperty stn9Property()
    {
        return stn9;
    }
    
    public SimpleStringProperty stn10Property()
    {
        return stn10;
    }
     */
}
