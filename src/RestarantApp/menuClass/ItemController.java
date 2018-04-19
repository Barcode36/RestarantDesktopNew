package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import com.google.gson.JsonArray;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.CheckComboBox;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemController {

    @FXML
    CheckComboBox checkCombo,verietyCombo;
    private int i = 0;

    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;
    ArrayList<RequestAndResponseModel.cat_list> cat_listArrayList;
    ArrayList<RequestAndResponseModel.variety_id_list> variety_listArrayList;
    String outputImage;
    BufferedImage bufferedImage;
    @FXML
    ImageView imgItemIamge,imgUpload;
    @FXML
    TextField txtItem,itemDes,txtPrice,txtItemId;
    ArrayList itemId = new ArrayList();
    ArrayList varietyId = new ArrayList();

    public void initialize() {
        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);

        txtPrice.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    txtPrice.setText(oldValue);
                }
            }
        });
        getData();
        getVarietyList();
        imgUpload.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                try {
                    File file = UtilsClass.selectImage();
                    double bytes = file.length();
                    double kilobytes = (bytes / 1024);
                    System.out.println("file size-->"+String.valueOf(kilobytes));
                    if (kilobytes < 250) {
                        bufferedImage = ImageIO.read(file);
                        if (bufferedImage != null) {
                            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                            imgItemIamge.setImage(image);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        txtItemId.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtItemId.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

    }

    private void getVarietyList() {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);

        Call<RequestAndResponseModel> call = retrofitClient.listVaiety();
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    if (requestAndResponseModel.getSuccessCode().equals(Constants.Success)) {
                        variety_listArrayList = requestAndResponseModel.getVariety_id_list();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                setVarietyPane();
                            }
                        });


                    }else
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }


    public void btnUploadAction(ActionEvent actionEvent) {


    }

    public void btnAddCategory(ActionEvent actionEvent) {

        ObservableList getIndex =  checkCombo.getCheckModel().getCheckedIndices();
        ObservableList getVareityIndex =  verietyCombo.getCheckModel().getCheckedIndices();

        ArrayList checkItem = new ArrayList();
        ArrayList checkVarityItem = new ArrayList();
        for (int i=1;i<getIndex.size();i++)
        {
            int index = (int) getIndex.get(i);


            checkItem.add(itemId.get(index));
        }

        for (int i=1;i<getVareityIndex.size();i++)
        {
            int index = (int) getVareityIndex.get(i);


            checkVarityItem.add(varietyId.get(index));
        }



        sendItemDetails(checkItem,checkVarityItem);
    }

    private void sendItemDetails(ArrayList checkItem,ArrayList variety_listArrayList) {

        ArrayList itemList = new ArrayList();
        ArrayList vareityArrayList = new ArrayList();
        for(int j= 0 ; j < checkItem.size() ; j ++)
        {
            String item = String.valueOf(j) +":" +checkItem.get(j);
            itemList.add(item);

        }
        String list = itemList.toString();
        list = list.substring(1, list.length()-1);
        list = "{"+list+"}";


        for(int j= 0 ; j < variety_listArrayList.size() ; j ++)
        {
            String item = String.valueOf(j) +":" +variety_listArrayList.get(j);
            vareityArrayList.add(item);

        }
        String varietyList = vareityArrayList.toString();
        varietyList = varietyList.substring(1, varietyList.length()-1);
        varietyList = "{"+varietyList+"}";

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        outputImage = UtilsClass.encodeToString(bufferedImage,"png");
        try {
            JSONObject item = new JSONObject(list);
            JSONObject varietyItem = new JSONObject(varietyList);
            jsonObject.put("item_name",txtItem.getText());
            jsonObject.put("short_code",txtItemId.getText());
            jsonObject.put("item_desc",itemDes.getText());
            jsonObject.put( "item_image",outputImage);
            jsonObject.put("item_price",txtPrice.getText());
            jsonObject.put("item_cat_list",item);
            jsonObject.put("item_variety_list",varietyItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> addItemcall = retrofitClient.sendItemDetails(jsonObject);
        addItemcall.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println("select index------->"+requestAndResponseModel.getStatus_message());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
                        }
                    });

                    if (requestAndResponseModel.getStatus_code().equals(Constants.Success))
                    {



                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    checkCombo.getCheckModel().clearChecks();
                                    txtItem.setText("");
                                    itemDes.setText("");
                                    txtPrice.setText("");
                                    imgItemIamge.setImage(null);
                                    checkCombo.getCheckModel().check(0);
                                    txtItemId.setText("");
                                    verietyCombo.getCheckModel().check(0);
                                }
                            });


                    }
                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }


    private void getData() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);

        Call<RequestAndResponseModel> call = retrofitClient.categoryList();
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    if (requestAndResponseModel.getStatus_code().equals(Constants.Success)) {
                        cat_listArrayList = requestAndResponseModel.getCat_list();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                setGridPane();
                            }
                        });


                    }else
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });


    }

    private void setGridPane() {

        checkCombo.getItems().add("Select Catagory");
        itemId.add("-1");

        for (int i=0;i<cat_listArrayList.size();i++)
        {
            RequestAndResponseModel.cat_list cat_list = cat_listArrayList.get(i);
            checkCombo.getItems().add(cat_list.getCat_name());
            itemId.add(cat_list.getCat_id());
            System.out.println(cat_list.getCat_name());
        }

        checkCombo.getCheckModel().check(0);

        checkCombo.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {

                System.out.println(checkCombo.getCheckModel().getCheckedItems());
            }
        });



    }

    private void setVarietyPane() {

        verietyCombo.getItems().add("Select Variety");
        varietyId.add("-1");

        for (int i=0;i<variety_listArrayList.size();i++)
        {
            RequestAndResponseModel.variety_id_list cat_list = variety_listArrayList.get(i);
            verietyCombo.getItems().add(cat_list.getVariety_name());
            varietyId.add(cat_list.getVariety_id());
            System.out.println(cat_list.getVariety_name());
        }

        verietyCombo.getCheckModel().check(0);

        verietyCombo.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {

                System.out.println(verietyCombo.getCheckModel().getCheckedItems());
            }
        });



    }

}
