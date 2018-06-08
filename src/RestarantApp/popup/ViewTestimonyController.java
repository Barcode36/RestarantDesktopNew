package RestarantApp.popup;

import RestarantApp.model.Constants;
import RestarantApp.model.TestimonyDetails;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewTestimonyController implements Initializable {
    @FXML
    Label labCustomerNAme,labCustomerMessage,labIsApproved;
    @FXML
    ImageView imgCustomer,imgClose;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setItemsDetails(TestimonyDetails item) {
        labCustomerMessage.setText(item.getTestimonyMessage());
        labCustomerNAme.setText(item.getCustomerName());
        if (item.getTestimonyApproved().equals("0"))
        {
            labIsApproved.setText("No");
        }else {
            labIsApproved.setText("Yes");
        }
        System.out.println(Constants.TESTIMONY_BASE_URL + item.getTestimonyImage());
        javafx.scene.image.Image image = new Image(Constants.TESTIMONY_BASE_URL + item.getTestimonyImage());
        imgCustomer.setImage(image);
    }

    public void imgClosed(MouseEvent mouseEvent) {
        Stage stage = (Stage) imgClose.getScene().getWindow();
        stage.close();
    }
}
