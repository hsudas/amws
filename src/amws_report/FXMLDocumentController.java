package amws_report;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import jfxtras.scene.control.LocalTimePicker;

public class FXMLDocumentController implements Initializable
{

    @FXML
    private Label label;
    @FXML
    public Button btnRaporIstek;
    @FXML
    public TextArea txtLog;
    @FXML
    public TableView<amws_report.ReportRequestTableView> grid;
    @FXML
    public TableColumn col1;
    @FXML
    public TableColumn col2;
    @FXML
    public TableColumn col3;
    @FXML
    public TableColumn col4;
    @FXML
    public TableColumn col5;
    @FXML
    public TableColumn col6;
    @FXML
    public TableColumn col7;
    @FXML
    public TableColumn col8;
    @FXML
    public TableColumn col9;
    @FXML
    public TableColumn col10;
    @FXML
    public TableColumn col11;
    @FXML
    public ComboBox cbRaporTuru;
    @FXML
    public DatePicker dpBaslangicTarih;
    @FXML
    public DatePicker dpBitisTarih;
    @FXML
    public LocalTimePicker tpBaslangic;
    @FXML
    public LocalTimePicker tpBitis;
    @FXML
    public Button btnTextToDB;
    @FXML
    public TextField txtUUID;

    public FXMLDocumentController()
    {
    }

    //ReportRequestTableView sinifindaki degiskenler tableView ile baglaniyor
    public void initTableView()
    {
        col1.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn1"));
        col2.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn2"));
        col3.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn3"));
        col4.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn4"));
        col5.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn5"));
        col6.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn6"));
        col7.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn7"));
        col8.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn8"));
        col9.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn9"));
        col10.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn10"));
        col11.setCellValueFactory(new PropertyValueFactory<amws_report.ReportRequestTableView, String>("stn11"));
    }

    @FXML
    public void btnRaporIstekTiklandi(ActionEvent event)
    {
        System.out.println("tiklandi");
        //reportRequestKayitEkle();
    }

    @FXML
    public void setLog(String log)
    {
        System.out.println("log : " + log);
        txtLog.setText(log);
    }

    @FXML
    private void handleButtonAction(ActionEvent event)
    {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }

}
