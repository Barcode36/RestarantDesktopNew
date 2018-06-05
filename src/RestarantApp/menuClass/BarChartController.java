package RestarantApp.menuClass;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

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
    String[] months,week,days;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        rootPane.getStylesheets().add(css);

        months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
        monthlyBar.setTitle("Monthly Report");
        monthNames.addAll(Arrays.asList(months));
        xAxis.setCategories(monthNames);
        setPersonData();

        weekly_report.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                week = DateFormatSymbols.getInstance(Locale.ENGLISH).getWeekdays();
                monthlyBar.setTitle("Weekly Report");
                monthNames.clear();
                monthNames.addAll(week);
                xAxis.setCategories(monthNames);
            }
        });
        monthly_report.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
                monthNames.clear();
                monthlyBar.setTitle("Monthly Report");
                monthNames.addAll(Arrays.asList(months));
                xAxis.setCategories(monthNames);
            }
        });

        daily_report.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                days = DateFormatSymbols.getInstance(Locale.ENGLISH).getShortWeekdays();
                monthNames.clear();
                monthlyBar.setTitle("Monthly Report");
                monthNames.addAll(Arrays.asList(days));
                xAxis.setCategories(monthNames);
            }
        });
    }

    private XYChart.Series<String, Integer> createMonthDataSeries(int[] monthCounter) {
        XYChart.Series<String,Integer> series = new XYChart.Series<String,Integer>();
        for (int i = 0; i < monthCounter.length; i++) {
            XYChart.Data<String, Integer> monthData = new XYChart.Data<String,Integer>(monthNames.get(i), monthCounter[i]);
            series.getData().add(monthData);
        }

        return series;
    }

    public void setPersonData() {
        int[] monthCounter = new int[12];
        monthCounter[0] = 100;
        monthCounter[1] = 200;
        monthCounter[2] = 300;
        monthCounter[3] = 400;
        monthCounter[4] = 500;
        monthCounter[5] = 600;
        monthCounter[6] = 700;
        monthCounter[7] = 800;
        monthCounter[8] = 900;

        XYChart.Series<String, Integer> series = createMonthDataSeries(monthCounter);
        monthlyBar.getData().add(series);
    }

}
