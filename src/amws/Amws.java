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

    private static DateFormat dateFormat;

    public static void main(String[] args)
    {
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dosyayaYaz("uygulama basladi");

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

}
