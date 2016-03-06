package amws_report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static amws_report.Main.dosyayaYaz;
import static amws_report.Main.getCnfg;
import static amws_report.Veritabani.reportRequestTableViewGuncelle;
import static amws_report.Veritabani.vtBaglantisiKur;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Statement;

public class VtInsertThread extends Thread
{

    private Config cnfg = null;
    private Connection conn = null;
    private YeniRaporIstek yri;
    private File file;
    private int tip = -1;
    private final int TIP_REQUEST = 1;
    private final int TIP_CONTENTS = 2;

    /**
     * request tablosuna eklemek icin kurucu fonksiyon
     *
     * @param yri : request tablosuna eklenecek nesne
     */
    public VtInsertThread(YeniRaporIstek yri)
    {
        tip = TIP_REQUEST;
        this.yri = yri;
        cnfg = getCnfg();
    }

    /**
     * contents tablosuna eklemek icin kurucu fonksiyon
     *
     * @param file : content tablosuna eklenecek dosya
     */
    public VtInsertThread(File file)
    {
        tip = TIP_CONTENTS;
        this.file = file;
        cnfg = getCnfg();
    }

    @Override
    public void run()
    {
        switch (tip)
        {
            case TIP_REQUEST:
                try
                {
                    dosyayaYaz("request insert thread olusturuldu");
                    conn = vtBaglantisiKur(cnfg);
                    if (conn != null)
                    {
                        reportRequestKayitEkle();
                        reportRequestTableViewGuncelle(conn, cnfg);

                        conn.close();
                    }
                }
                catch (SQLException e)
                {
                    dosyayaYaz("hata 24 : " + e.getMessage());
                }
                break;

            case TIP_CONTENTS:
                try
                {
                    dosyayaYaz("contents insert thread olusturuldu");
                    conn = vtBaglantisiKur(cnfg);
                    if (conn != null)
                    {
                        reportContentsKayitEkle();
                        conn.close();
                    }
                }
                catch (SQLException e)
                {
                    dosyayaYaz("hata 25 : " + e.getMessage());
                }
                break;
            default:
                dosyayaYaz("hata 26 : hatali insert tipi");
        }
    }

    /**
     * contents tablosuna kayit ekler
     */
    public void reportContentsKayitEkle()
    {
        dosyayaYaz(cnfg.getTABLE_CONTENTS() + "tablosuna kayit ekleniyor");
        BufferedReader br = null;
        try
        {
            String sorgu;
            Statement statement = conn.createStatement();

            String sCurrentLine;
            br = new BufferedReader(new FileReader(file));
            while ((sCurrentLine = br.readLine()) != null)
            {
                String sorgu1 = "INSERT INTO " + cnfg.getTABLE_CONTENTS() + " (REPORT_ID, ROW_ID";
                String sorgu2 = "VALUES (0, 0";

                String[] satirIcerigi = sCurrentLine.split("\t");
                for (int i = 0; i < satirIcerigi.length; i++)
                {
                    satirIcerigi[i] = satirIcerigi[i].replace("'", "''");
                    satirIcerigi[i] = satirIcerigi[i].replaceAll("[\n\r]", "");

                    sorgu1 = sorgu1 + ", Column" + String.valueOf(i + 1);
                    sorgu2 = sorgu2 + ", '" + satirIcerigi[i] + "'";
                }
                sorgu1 = sorgu1 + ")";
                sorgu2 = sorgu2 + ");";
                sorgu = sorgu1 + sorgu2;
                statement.addBatch(sorgu);
            }
            statement.executeBatch();
        }
        catch (IOException e)
        {
            dosyayaYaz(cnfg.getTABLE_CONTENTS() + "tablosuna kayit eklenirken hata olustu");
            dosyayaYaz("hata 27 : " + e.getMessage());
        }
        catch (SQLException e)
        {
            dosyayaYaz(cnfg.getTABLE_CONTENTS() + "tablosuna kayit eklenirken hata olustu");
            dosyayaYaz("hata 28 : " + e.getMessage());
        }
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
            }
            catch (IOException e)
            {
                dosyayaYaz(cnfg.getTABLE_CONTENTS() + "tablosuna kayit eklenirken hata olustu");
                dosyayaYaz("hata 29 : " + e.getMessage());
            }
        }
        dosyayaYaz(cnfg.getTABLE_CONTENTS() + "tablosuna kayit eklendi");
    }

    /**
     * request tablosuna kayıt ekler
     */
    public void reportRequestKayitEkle()
    {
        dosyayaYaz(cnfg.getTABLE_REQUEST() + "tablosuna kayit ekleniyor");
        try
        {
            PreparedStatement pst = conn.prepareStatement("INSERT INTO " + cnfg.getTABLE_REQUEST() + " (START_DATE, END_DATE, REPORT_TYPE) VALUES (?,?,?)");
            pst.setString(1, yri.getBaslangicTarihi());
            pst.setString(2, yri.getBitisTarihi());
            pst.setString(3, yri.getTip());
            pst.execute();
        }
        catch (SQLException e)
        {
            dosyayaYaz(cnfg.getTABLE_REQUEST() + "tablosuna kayit eklenirken hata olustu");
            dosyayaYaz("hata 25 : " + e.getMessage());
        }
        dosyayaYaz(cnfg.getTABLE_REQUEST() + "tablosuna kayit eklendi");
    }
}
