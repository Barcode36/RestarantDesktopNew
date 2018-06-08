package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.Constants;
import RestarantApp.model.ReportDeatils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.*;

public class BarChartController implements Initializable {

    @FXML
    BarChart<String,Integer> monthlyBar;
    @FXML
    Hyperlink monthly_report,weekly_report,daily_report;
    private ObservableList<String> monthNames = FXCollections.observableArrayList();
    @FXML
    private CategoryAxis xAxis;
    @FXML
    StackPane rootPane;
    APIService retrofitClient;
    String from = "";
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        rootPane.getStylesheets().add(css);


        weekly_report.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {


                getDailyData("weekly");
            }
        });
        monthly_report.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                getDailyData("monthly");
            }
        });

        daily_report.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {


                getDailyData("daily");
            }
        });

        getDailyData("monthly");
    }

    private void getDailyData(String from) {
        retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("period",from);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ReportDeatils>reportDeatilsCall = retrofitClient.getReportBasedOnDaily(jsonObject);
        reportDeatilsCall.enqueue(new Callback<ReportDeatils>() {
            @Override
            public void onResponse(Call<ReportDeatils> call, Response<ReportDeatils> response) {
                if (response.isSuccessful()){
                    ReportDeatils reportDeatils = response.body();
                    if (reportDeatils.getStatus_code().equals(Constants.Success)){

                        ArrayList<ReportDeatils.Report_details> report_details = reportDeatils.getReport_details();
                        XYChart.Series<String,Integer> dataSeries = new XYChart.Series<String,Integer>();
                        dataSeries.setName("2014");

                        ObservableList<String> dayValue = FXCollections.observableArrayList();
                        for (int i= 0 ; i < report_details.size() ; i ++){
                            ReportDeatils.Report_details reportDetails = report_details.get(i);
                            XYChart.Data<String, Integer> monthData = null ;
                            if (from.equals("daily")) {
                                dayValue.add(reportDetails.getDate());
                                if (reportDetails.getSale() == 0) {
                                    monthData = new XYChart.Data<String, Integer>(reportDetails.getDate(), 0);
                                } else {
                                    monthData = new XYChart.Data<String, Integer>(reportDetails.getDate(), reportDetails.getSale());
                                }

                            }else if (from.equals("weekly")){
                                dayValue.add(reportDetails.getWeek());

                                if (reportDetails.getSale() == 0) {
                                    monthData = new XYChart.Data<String, Integer>(reportDetails.getWeek(), 0);
                                } else {
                                    monthData = new XYChart.Data<String, Integer>(reportDetails.getWeek(), reportDetails.getSale());
                                }
                            }else  if (from.equals("monthly")){
                                dayValue.add(reportDetails.getMonth());

                                if (reportDetails.getSale() == 0) {
                                    monthData = new XYChart.Data<String, Integer>(reportDetails.getMonth(), 0);
                                } else {
                                    monthData = new XYChart.Data<String, Integer>(reportDetails.getMonth(), reportDetails.getSale());
                                }
                            }
                            dataSeries.getData().add(monthData);
                        }
                        System.out.println(dataSeries.getData().size());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                monthlyBar.getData().clear();
                                xAxis.getCategories().clear();
                                monthlyBar.getXAxis().setTickLabelRotation(-90);
                                xAxis.setCategories(dayValue);
                                monthlyBar.getData().add(dataSeries);
                            }
                        });


                    }
                }

            }

            @Override
            public void onFailure(Call<ReportDeatils> call, Throwable throwable) {

            }
        });
    }


}
