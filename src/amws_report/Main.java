package amws_report;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.scene.control.LocalTimePicker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main extends Application
{
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static Config cnfg;
    private static Controller cntrl;//arayüz sınıfı nesnesi
    private static TableView tableView;//fxml tableView bileseni
    private static List listTableView;//tableView doldurmak için liste
    private static ComboBox cbRaporTuru;//fxml combobox bileseni
    private static Button btnRaporIstek;//fxml combobox bileseni
    private static List<YeniRaporIstek> listeRaporIstek;//arayüzden yapılan rapor isteklerini tutmak için liste
    private static YeniRaporIstek yri;
    private static DatePicker dpBaslangicTarih;
    private static DatePicker dpBitisTarih;
    private static LocalTimePicker tpBaslangic;
    private static LocalTimePicker tpBitis;

    public static void main(String[] args)
    {
        listTableView = new ArrayList();
        cntrl = new Controller();
        cnfg = new Config();
        listeRaporIstek = new ArrayList<>();
        yri = new YeniRaporIstek();

        launch(args);
        dosyayaYaz("uygulama bitti");
        System.exit(0);
    }

    public static void dosyayaYaz(String log)
    {
        PrintWriter out = null;
        Date date = new Date();
        try
        {
            out = new PrintWriter(new FileWriter("log.txt", true), true);
            out.write(dateFormat.format(date) + " :: " + log + "\n");
            out.close();

            System.out.println(dateFormat.format(date) + " :: " + log);
            //cntrl.setLog(dateFormat.format(date) + " :: " + log + "\n");
            //txt.appendText(dateFormat.format(date) + " :: " + log + "\n");
        }
        catch (IOException ex)
        {
            System.out.println(dateFormat.format(date) + " :: dosyaya yazarken hata olustu : " + ex.getMessage());
            //Logger.getLogger(Amws.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            out.close();
        }
    }

    /**
     * tableView guncellemesinden once data listesini siliyor
     */
    public static void listTableViewTemizle()
    {
        listTableView.clear();
    }

    /**
     * TableViewa satir ekler
     */
    public static void tableViewSatirEkle(String stn1, String stn2, String stn3, String stn4, String stn5, String stn6, String stn7, String stn8, String stn9)
    {
        listTableView.add(new ReportRequestTableView(stn1, stn2, stn3, stn4, stn5, stn6, stn7, stn8, stn9));
        ObservableList data = FXCollections.observableList(listTableView);
        tableView.setItems(data);
    }

    /**
     * Config verilerine ulasabilmek icin nesne
     *
     * @return
     */
    public static Config getCnfg()
    {
        return cnfg;
    }

    public void reportRequestKayitEkle()
    {
        yri.setTip(((RaporTuru) cbRaporTuru.getSelectionModel().getSelectedItem()).getCirkin());
        yri.setBaslangicTarihi(dpBaslangicTarih.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + tpBaslangic.getLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + ":00");
        yri.setBitisTarihi(dpBitisTarih.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + tpBitis.getLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + ":00");

        VtInsertThread vtInsertThread = new VtInsertThread(yri);
        vtInsertThread.start();
    }

    /**
     * rapor turlerini combobox a ekler
     */
    public void cbRaporTuruDoldur()
    {
        RaporTuru rt = new RaporTuru();
        List listeRaporTuru = rt.getListeRaporTuru();
        ObservableList options = FXCollections.observableArrayList(listeRaporTuru);
        cbRaporTuru.setItems(options);
        cbRaporTuru.getSelectionModel().selectFirst();

        //combobox a RaporTuru nesnesi ekleniyor. burada item toString() deki deger e guncelleniyor
        cbRaporTuru.setCellFactory(new Callback<ListView<RaporTuru>, ListCell<RaporTuru>>()
        {
            @Override
            public ListCell<RaporTuru> call(ListView<RaporTuru> p)
            {
                final ListCell<RaporTuru> cell = new ListCell<RaporTuru>()
                {
                    @Override
                    protected void updateItem(RaporTuru t, boolean bln)
                    {
                        super.updateItem(t, bln);

                        if (t != null)
                        {
                            setText(t.toString());//combobox ta gozukecek yazi
                        }
                        else
                        {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });

        //item secimi yapılınca buraya geliyor
        cbRaporTuru.valueProperty().addListener(new ChangeListener()
        {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1)
            {
                RaporTuru rt = (RaporTuru) cbRaporTuru.getSelectionModel().getSelectedItem();
                System.out.println("crin : " + rt.getCirkin());
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        dosyayaYaz("uygulama basladi");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("amws_report.fxml"));
        Parent root = fxmlLoader.load();
        cntrl = fxmlLoader.getController();
        tableView = (TableView) root.lookup("#grid");
        cbRaporTuru = (ComboBox) root.lookup("#cbRaporTuru");
        dpBaslangicTarih = (DatePicker) root.lookup("#dpBaslangicTarih");
        dpBitisTarih = (DatePicker) root.lookup("#dpBitisTarih");
        tpBaslangic = (LocalTimePicker) root.lookup("#tpBaslangic");
        tpBitis = (LocalTimePicker) root.lookup("#tpBitis");
        btnRaporIstek = (Button) root.lookup("#btnRaporIstek");
        dpBaslangicTarih.setValue(LocalDate.now());
        dpBitisTarih.setValue(LocalDate.now());

        btnRaporIstek.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                System.out.println("tiklandi : " + mouseEvent);
                reportRequestKayitEkle();
            }
        });

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(300);
        primaryStage.show();

        cntrl.initTableView();
        cbRaporTuruDoldur();

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

        VtMainThread vtMainThread = new VtMainThread();
        vtMainThread.start();
    }
}
