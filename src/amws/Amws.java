package amws;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Amws
{

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static Config cnfg;

    public static void main(String[] args)
    {
        dosyayaYaz("uygulama basladi");
        cnfg = new Config();

        if (cnfg.ayarlariOku())
        {
            if (!cnfg.ayarlariKontrolEt())
            {
                dosyayaYaz("ayar dosyasında eksik oldugu icin uygulama kapatıldı");
                System.exit(0);
            }
        }
        else
        {
            dosyayaYaz("ayar dosyası okunurken hata olustugu icin uygulama kapatıldı");
            System.exit(0);
        }

        VTThread vtThread = new VTThread();
        vtThread.start();
    }

    public static void dosyayaYaz(String log)
    {
        PrintWriter out = null;
        try
        {
            out = new PrintWriter(new FileWriter("log.txt", true), true);
            Date date = new Date();
            out.write(dateFormat.format(date) + " :: " + log + "\n");
            out.close();

            System.out.println(dateFormat.format(date) + " :: " + log);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Amws.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            out.close();
        }
    }

    public static Config getCnfg()
    {
        return cnfg;
    }
}
