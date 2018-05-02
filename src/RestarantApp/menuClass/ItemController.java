package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import RestarantApp.model.SubCatagoryList;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemController {

    @FXML
    CheckComboBox verietyCombo,subCatCombo;
    @FXML
    ComboBox<String> checkCombo;
    private int i = 0;
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;
    ArrayList<RequestAndResponseModel.cat_list> cat_listArrayList;
    ArrayList<RequestAndResponseModel.variety_id_list> variety_listArrayList;
    String outputImage,getSelectedCatId;
    BufferedImage bufferedImage;
    @FXML
    ImageView imgItemIamge,imgUpload;
    @FXML
    TextField txtItem,itemDes,txtPrice,txtItemId;
    ArrayList itemId = new ArrayList();
    ArrayList varietyId = new ArrayList();
    HashMap<String,String>getSubList ;
    @FXML
    ProgressIndicator progressItem;

    public void initialize() {
        System.out.println("Call Initialize");
        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);
        progressItem.setVisible(false);
        progressItem.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        txtPrice.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    txtPrice.setText(oldValue);
                }
            }
        });
        getData();
        getSubCatagory();
        getVarietyList();

        checkCombo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

               int selectedIndex =   checkCombo.getSelectionModel().getSelectedIndex();
                System.out.println(itemId.get(selectedIndex));
                if (!itemId.get(selectedIndex).equals("-1"))
                {
                    getSelectedCatId = String.valueOf(itemId.get(selectedIndex));
                    getSubCatagoryList(String.valueOf(itemId.get(selectedIndex)));

                }
            }
        });

        imgUpload.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                try {
                    File file = UtilsClass.selectImage();
                    if (file != null) {
                        double bytes = file.length();
                        double kilobytes = (bytes / 1024);
                        System.out.println("file size-->" + String.valueOf(kilobytes));
                        if (kilobytes < 250) {
                            bufferedImage = ImageIO.read(file);
                            if (bufferedImage != null) {
                                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                                imgItemIamge.setImage(image);
                            }
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

    private void getSubCatagory() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        Call<RequestAndResponseModel> call = retrofitClient.getSubCatagoryList();
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful())
                {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    ArrayList getItemDetils = requestAndResponseModel.getCat_list();
                    getSubList = new HashMap<>();
                    for (int i = 0; i < getItemDetils.size(); i++) {
                        RequestAndResponseModel.cat_list list = (RequestAndResponseModel.cat_list) getItemDetils.get(i);
                        getSubList.put(list.getSub_Cat_id(),list.getSub_Cat_name());

                    }
                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

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
                    progressItem.setVisible(false);
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


        ObservableList getVareityIndex =  verietyCombo.getCheckModel().getCheckedIndices();
        ObservableList getSubCatIndex =  subCatCombo.getCheckModel().getCheckedItems();

        String valueFromMap =(String) itemId.get(checkCombo.getSelectionModel().getSelectedIndex());
        valueFromMap = "{0:"+getSelectedCatId+"}";

//        ArrayList checkItem = new ArrayList();
        ArrayList checkVarityItem = new ArrayList();
        ArrayList checkSubCatList = new ArrayList();
       /* for (int i=1;i<getIndex.size();i++)
        {
            int index = (int) getIndex.get(i);


            checkItem.add(itemId.get(index));
        }*/

        for (int i=1;i<getVareityIndex.size();i++)
        {
            int index = (int) getVareityIndex.get(i);


            checkVarityItem.add(varietyId.get(index));
        }

        for (int i=1;i<getSubCatIndex.size();i++)
        {
            String subItemId =(String) UtilsClass.getKeyFromValue(getSubList,getSubCatIndex.get(i));
            checkSubCatList.add(subItemId);
           /* int index = (int) getSubCatIndex.get(i);


            checkSubCatList.add(itemId.get(index));*/
        }

       sendItemDetails(valueFromMap,checkVarityItem,checkSubCatList);
    }

    private void sendItemDetails(String catList,ArrayList variety_listArrayList,ArrayList subCatList) {

        ArrayList itemList = new ArrayList();
        ArrayList vareityArrayList = new ArrayList();
        ArrayList subArrayList = new ArrayList();
      /*  for(int j= 0 ; j < checkItem.size() ; j ++)
        {
            String item = String.valueOf(j) +":" +checkItem.get(j);
            itemList.add(item);

        }
        String list = itemList.toString();
        list = list.substring(1, list.length()-1);
        list = "{"+list+"}";*/


        for(int j= 0 ; j < variety_listArrayList.size() ; j ++)
        {
            String item = String.valueOf(j) +":" +variety_listArrayList.get(j);
            vareityArrayList.add(item);

        }
        String varietyList = vareityArrayList.toString();
        varietyList = varietyList.substring(1, varietyList.length()-1);
        varietyList = "{"+varietyList+"}";

        for(int j= 0 ; j < subCatList.size() ; j ++)
        {
            String item = String.valueOf(j) +":" +subCatList.get(j);
            subArrayList.add(item);

        }
        String subList = subArrayList.toString();
        subList = subList.substring(1, subList.length()-1);
        subList = "{"+subList+"}";

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        outputImage = UtilsClass.encodeToString(bufferedImage,"png");
        try {
            JSONObject itemCatList = new JSONObject(catList);
            JSONObject varietyItem = new JSONObject(varietyList);
            JSONObject subCatItem = new JSONObject(subList);
            jsonObject.put("item_name",txtItem.getText());
            jsonObject.put("short_code",txtItemId.getText());
            jsonObject.put("item_desc",itemDes.getText());
            jsonObject.put( "item_image",outputImage);
            jsonObject.put("item_price",txtPrice.getText());
            jsonObject.put("item_cat_list",itemCatList);
            jsonObject.put("item_variety_list",varietyItem);
            jsonObject.put("item_sub_cat_list",subCatItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressItem.setVisible(true);
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
                                    txtItem.setText("");
                                    itemDes.setText("");
                                    txtPrice.setText("");
                                    imgItemIamge.setImage(null);
                                    checkCombo.getSelectionModel().selectFirst();
                                    txtItemId.setText("");
                                    verietyCombo.getCheckModel().clearChecks();
                                    subCatCombo.getCheckModel().clearChecks();
                                    verietyCombo.getCheckModel().check(0);
                                    progressItem.setVisible(false);
                                }
                            });


                    }
                }else
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            progressItem.setVisible(false);
                            jfxSnackbar.show(response.raw().networkResponse().message() + " Please Resize a Image....",5000);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }


    private void getData() {
        progressItem.setVisible(true);
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
        checkCombo.getSelectionModel().selectFirst();
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

    public void getSubCatagoryList(String cat_id)
    {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("parent_cat_id",cat_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<SubCatagoryList> call = retrofitClient.getSubCatagoryList(jsonObject);
        call.enqueue(new Callback<SubCatagoryList>() {
            @Override
            public void onResponse(Call<SubCatagoryList> call, Response<SubCatagoryList> response) {
                if (response.isSuccessful()) {
                    SubCatagoryList subCatagoryList = response.body();
                    if (subCatagoryList.getStatus_code().equals(Constants.Success)) {
                        ArrayList<SubCatagoryList.sub_cat_list> subCatList = subCatagoryList.getSub_cat_list();
                        subCatCombo.getItems().clear();
                        subCatCombo.getItems().add("Select Sub Catagory");
                        for (int i = 0 ; i < subCatList.size() ; i++)
                        {
                            SubCatagoryList.sub_cat_list sub_cat_list = subCatList.get(i);
                            subCatCombo.getItems().add(getSubList.get(sub_cat_list.getSub_cat_id()));
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                subCatCombo.getCheckModel().check(0);

                            }
                        });

                    }else
                    {
                        subCatCombo.getItems().clear();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                jfxSnackbar.show("No item found",5000);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<SubCatagoryList> call, Throwable throwable) {

            }
        });
    }
}
