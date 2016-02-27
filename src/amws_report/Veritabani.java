package amws_report;

import java.sql.*;

import static amws_report.Main.*;

public class Veritabani
{

    /**
     * reportRequest tablo icerigini tableView a yazar
     */
    public static void reportRequestTableViewGuncelle(Connection conn, Config cnfg)
    {
        dosyayaYaz("TableView guncelleniyor");
        try
        {
            listTableViewTemizle();
            PreparedStatement pst = conn.prepareStatement("SELECT START_DATE, END_DATE, REPORT_TYPE, SUBMIT_DATE, STATUS, REPORT_REQUEST_ID, GENERATED_REPORT_ID, DOWNLOADED, DOWNLOAD_TYPE FROM " + cnfg.getTABLE_REQUEST());
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                tableViewSatirEkle(rs.getString("START_DATE"),
                                   rs.getString("END_DATE"),
                                   rs.getString("REPORT_TYPE"),
                                   rs.getString("SUBMIT_DATE"),
                                   rs.getString("STATUS"),
                                   rs.getString("REPORT_REQUEST_ID"),
                                   rs.getString("GENERATED_REPORT_ID"),
                                   rs.getString("DOWNLOADED"),
                                   rs.getString("DOWNLOAD_TYPE"));
            }
        }
        catch (SQLException e)
        {
            dosyayaYaz("hata 16 : " + e.getMessage());
            dosyayaYaz("TableView guncellenirken hata olustu");
        }
        dosyayaYaz("TableView guncellendi");
    }

    /**
     * vt ye baglanir
     *
     * @return vt connection nesnesi
     */
    public static Connection vtBaglantisiKur(Config cnfg)
    {
        dosyayaYaz("veritabani baglantisi kuruluyor");

        //vt baglanti stringi
        String connectionUrl = "jdbc:sqlserver://" + cnfg.getVT_IP() + ";databaseName=" + cnfg.getVT_DATABASE_NAME() + ";";
        Connection con = null;

        try
        {
            //vt baglantisi kuruluyor
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(connectionUrl, cnfg.getVT_USERNAME(), cnfg.getVT_PASSWORD());

            dosyayaYaz("veritabani baglantisi kuruldu : conn : " + con);
        }
        catch (ClassNotFoundException e)
        {
            dosyayaYaz("hata 12 : " + e.getMessage());
            dosyayaYaz("veritabani baglantisi kurulurken hata olustu");
        }
        catch (SQLException e)
        {
            dosyayaYaz("hata 13 : " + e.getMessage());
            dosyayaYaz("veritabani baglantisi kurulurken hata olustu");
        }
        dosyayaYaz("veritabani baglantisi kuruldu");

        return con;
    }
}
