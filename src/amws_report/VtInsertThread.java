package amws_report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static amws_report.Main.dosyayaYaz;
import static amws_report.Main.getCnfg;
import static amws_report.Veritabani.reportRequestTableViewGuncelle;
import static amws_report.Veritabani.vtBaglantisiKur;

public class VtInsertThread extends Thread
{
    private Config cnfg = null;
    private Connection conn = null;
    private YeniRaporIstek yri;

    public VtInsertThread(YeniRaporIstek yri)
    {
        this.yri = yri;
        cnfg = getCnfg();
    }

    @Override
    public void run()
    {
        try
        {
            dosyayaYaz("insert thread olusturuldu");
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
            System.out.println("hata : " + e.getMessage());
        }
    }

    public void reportRequestKayitEkle()
    {
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
            System.out.println("yri hata: " + e.getMessage());
        }
    }
}
