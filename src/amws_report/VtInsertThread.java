package amws_report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static amws_report.Main.dosyayaYaz;
import static amws_report.Main.getCnfg;
import static amws_report.Main.uuidYenile;
import static amws_report.Veritabani.reportRequestTableViewGuncelle;
import static amws_report.Veritabani.vtBaglantisiKur;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Statement;
import static amws_report.Main.txtToDBSetText;

public class VtInsertThread extends Thread
{

    private Config cnfg = null;
    private Connection conn = null;
    private YeniRaporIstek yri;
    private File file;
    private String uuid;
    private Rapor ri;
    private int tip = -1;
    private final int TIP_REQUEST = 1;
    private final int TIP_CONTENTS = 2;
    private final int TIP_CONTENTS_2 = 3;

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
     * arayuzden secilen rapor icerigini contents tablosuna eklemek icin kurucu
     * fonksiyon
     *
     * @param file : content tablosuna eklenecek dosya
     * @param uuid
     */
    public VtInsertThread(File file, String uuid)
    {
        tip = TIP_CONTENTS;
        this.file = file;
        this.uuid = uuid;
        cnfg = getCnfg();
    }

    /**
     * reportRequest isleminde indirilen rapor icerigini contents tablosuna
     * eklemek icin kurucu fonksiyon
     *
     * @param file : indirilen dosya
     * @param ri : rapor bilgileri
     */
    public VtInsertThread(File file, Rapor ri)
    {
        tip = TIP_CONTENTS_2;
        this.file = file;
        this.ri = ri;
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
                        reportContentsKayitEkle(uuid);
                        conn.close();
                    }
                }
                catch (SQLException e)
                {
                    dosyayaYaz("hata 25 : " + e.getMessage());
                }
                break;

            case TIP_CONTENTS_2:
                try
                {
                    dosyayaYaz("contents-2 insert thread olusturuldu");
                    conn = vtBaglantisiKur(cnfg);
                    if (conn != null)
                    {
                        reportContentsKayitEkle(ri);
                        conn.close();
                    }
                }
                catch (SQLException e)
                {
                    dosyayaYaz("hata 34 : " + e.getMessage());
                }
                break;

            default:
                dosyayaYaz("hata 26 : hatali insert tipi");
        }
    }

    /**
     * contents tablosuna kayit ekler
     *
     * @param uuid : kayit uuid si
     */
    public void reportContentsKayitEkle(String uuid)
    {
        txtToDBSetText("inserting");
        dosyayaYaz(cnfg.getTABLE_CONTENTS() + " tablosuna kayit ekleniyor. uuid : " + uuid);
        BufferedReader br = null;
        try
        {
            String sorgu;
            Statement statement = conn.createStatement();

            String sCurrentLine;
            br = new BufferedReader(new FileReader(file));
            while ((sCurrentLine = br.readLine()) != null)
            {
                String sorgu1 = "INSERT INTO " + cnfg.getTABLE_CONTENTS() + " (REPORT_ID, ROW_ID, UUID";
                String sorgu2 = "VALUES (0, 0, '" + uuid + "' ";

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
            dosyayaYaz(cnfg.getTABLE_CONTENTS() + " tablosuna kayit eklenirken hata olustu");
            dosyayaYaz("hata 27 : " + e.getMessage());
        }
        catch (SQLException e)
        {
            dosyayaYaz(cnfg.getTABLE_CONTENTS() + " tablosuna kayit eklenirken hata olustu");
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
                dosyayaYaz(cnfg.getTABLE_CONTENTS() + " tablosuna kayit eklenirken hata olustu");
                dosyayaYaz("hata 29 : " + e.getMessage());
            }
        }

        uuidYenile();
        txtToDBSetText("insert done");
        dosyayaYaz(cnfg.getTABLE_CONTENTS() + " tablosuna kayit eklendi");
    }

    /**
     * contents tablosuna kayit ekler
     *
     * @param ri : rapor bilgileri
     */
    public void reportContentsKayitEkle(Rapor ri)
    {
        txtToDBSetText("inserting");
        dosyayaYaz(cnfg.getTABLE_CONTENTS() + " tablosuna kayit ekleniyor. uuid : " + ri.getUuid());
        BufferedReader br = null;
        try
        {
            String sorgu;
            Statement statement = conn.createStatement();

            String sCurrentLine;
            br = new BufferedReader(new FileReader(file));

            int index = 0;

            while ((sCurrentLine = br.readLine()) != null)
            {
                String sorgu1 = "INSERT INTO " + cnfg.getTABLE_CONTENTS() + " (REPORT_ID, ROW_ID, UUID";
                //String sorgu2 = "VALUES (0, 0, '" + uuid + "' ";
                String sorgu2 = "VALUES (" + ri.getReportRequestID() + ", " + String.valueOf(index) + ", '" + ri.getUuid() + "' ";

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

                index++;
            }
            statement.executeBatch();

            dosyayaYaz("vt guncelleniyor");

            PreparedStatement pst = conn.prepareStatement("UPDATE " + cnfg.getTABLE_REQUEST() + " SET DOWNLOADED_DB=1 WHERE ID=" + ri.getId() + ";");
            if (pst.executeUpdate() == 0)
            {
                dosyayaYaz("hata 32 : " + cnfg.getTABLE_REQUEST() + " tablosu guncellenirken hata olustu");
            }

            dosyayaYaz("vt guncellendi");
        }
        catch (IOException e)
        {
            dosyayaYaz(cnfg.getTABLE_CONTENTS() + " tablosuna kayit eklenirken hata olustu");
            dosyayaYaz("hata 27 : " + e.getMessage());
        }
        catch (SQLException e)
        {
            dosyayaYaz(cnfg.getTABLE_CONTENTS() + " tablosuna kayit eklenirken hata olustu");
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
                dosyayaYaz(cnfg.getTABLE_CONTENTS() + " tablosuna kayit eklenirken hata olustu");
                dosyayaYaz("hata 29 : " + e.getMessage());
            }
        }

        uuidYenile();
        txtToDBSetText("insert done");
        dosyayaYaz(cnfg.getTABLE_CONTENTS() + " tablosuna kayit eklendi");
    }

    /**
     * request tablosuna kayıt ekler
     */
    public void reportRequestKayitEkle()
    {
        txtToDBSetText("inserting");
        dosyayaYaz(cnfg.getTABLE_REQUEST() + " tablosuna kayit ekleniyor");
        try
        {
            PreparedStatement pst = conn.prepareStatement("INSERT INTO " + cnfg.getTABLE_REQUEST() + " (START_DATE, END_DATE, REPORT_TYPE, UUID) VALUES (?,?,?,?)");
            pst.setString(1, yri.getBaslangicTarihi());
            pst.setString(2, yri.getBitisTarihi());
            pst.setString(3, yri.getTip());
            pst.setString(4, yri.getUuid());
            pst.execute();
        }
        catch (SQLException e)
        {
            dosyayaYaz(cnfg.getTABLE_REQUEST() + " tablosuna kayit eklenirken hata olustu");
            dosyayaYaz("hata 30 : " + e.getMessage());
        }

        uuidYenile();

        try
        {
            conn.close();
        }
        catch (SQLException e)
        {
            dosyayaYaz("vt baglantisi kapatilamadi, conn : " + conn);
            dosyayaYaz("hata 35 : " + e.getMessage());
        }

        txtToDBSetText("insert done");
        dosyayaYaz(cnfg.getTABLE_REQUEST() + " tablosuna kayit eklendi");
    }
}
