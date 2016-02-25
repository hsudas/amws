package amws_report;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller
{

    @FXML
    public Button btnRequestReport;
    @FXML
    public TextArea txtLog;
    @FXML
    public TableView<ReportRequestTableView> grid;
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

    public Controller()
    {
    }

    //ReportRequestTableView sinifindaki degiskenler tableView ile baglaniyor
    public void initTableView()
    {
        col1.setCellValueFactory(new PropertyValueFactory<ReportRequestTableView, String>("stn1"));
        col2.setCellValueFactory(new PropertyValueFactory<ReportRequestTableView, String>("stn2"));
        col3.setCellValueFactory(new PropertyValueFactory<ReportRequestTableView, String>("stn3"));
        col4.setCellValueFactory(new PropertyValueFactory<ReportRequestTableView, String>("stn4"));
        col5.setCellValueFactory(new PropertyValueFactory<ReportRequestTableView, String>("stn5"));
        col6.setCellValueFactory(new PropertyValueFactory<ReportRequestTableView, String>("stn6"));
        col7.setCellValueFactory(new PropertyValueFactory<ReportRequestTableView, String>("stn7"));
        col8.setCellValueFactory(new PropertyValueFactory<ReportRequestTableView, String>("stn8"));
        col9.setCellValueFactory(new PropertyValueFactory<ReportRequestTableView, String>("stn9"));
    }

    @FXML
    public void btnRequestReportTiklandi(ActionEvent event)
    {
        System.out.println("tiklandi");
    }

    @FXML
    public void setLog(String log)
    {
        System.out.println("log : " + log);
        txtLog.setText(log);
    }
}
