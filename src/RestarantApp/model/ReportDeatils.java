package RestarantApp.model;

import java.util.ArrayList;

public class ReportDeatils {
    String Status_code;
    ArrayList<Report_details> Report_details = new ArrayList<>();

    public String getStatus_code() {
        return Status_code;
    }

    public void setStatus_code(String status_code) {
        Status_code = status_code;
    }

    public ArrayList<ReportDeatils.Report_details> getReport_details() {
        return Report_details;
    }

    public void setReport_details(ArrayList<ReportDeatils.Report_details> report_details) {
        Report_details = report_details;
    }

    public class Report_details{
        String date,Week,Month;
        int sale;

        public String getDate() {
            return date;
        }

        public String getWeek() {
            return Week;
        }

        public void setWeek(String week) {
            Week = week;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getSale() {
            return sale;
        }

        public void setSale(int sale) {
            this.sale = sale;
        }

        public String getMonth() {
            return Month;
        }

        public void setMonth(String month) {
            Month = month;
        }
    }
}
