package RestarantApp.Billing;

import RestarantApp.Network.APIService;
import RestarantApp.Network.NetworkChangeListener;
import RestarantApp.Network.NetworkConnection;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.*;
import RestarantApp.chat.GetFromServerListener;
import RestarantApp.chat.rabbitmq_server.RabbitmqServer;
import RestarantApp.chat.rabbitmq_stomp.Listener;
import RestarantApp.database.SqliteConnection;
import RestarantApp.menuClass.ViewCategoryController;
import RestarantApp.model.*;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.print.Printer;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.joda.time.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BillingController implements Initializable, ItemSelectedListener, GetFromServerListener, NetworkChangeListener{

    @FXML
    AnchorPane billingRootPane;
    @FXML
    ProgressIndicator itemLoadProgres;
    @FXML
    AutoCompleteTextField txtFieldId;
    @FXML
    AutoCompleteItemTextField txtFieldName;
    AutoCompleteTextField  autoCompleteTextField;
    AutoCompleteItemTextField  autoCompleteItemTextField;
    Set<String> possibleWordSet = new HashSet<>();
    private AutoCompletionBinding<String> autoCompletionBinding;
    APIService retrofitService;
    Set<String> itemName = new HashSet<>();
    Set<String> itemId =  new HashSet<>();
    Double intTaxPrice;
    ArrayList<ItemListRequestAndResponseModel.item_list> billingItemDetails = new ArrayList<>();
    LoginRequestAndResponse loginRequestAndResponse = LoginRequestAndResponse.getInstance();
    @FXML
    TableView<BillingModel> tableBill;
    @FXML
    TableColumn colSno,colItem,colQty,colRate,colAmount,colAddNotes,kotNo;
    @FXML
    TextField txtQty,txtTotalAmount,txtFiledDiscount,txtFiledDiscountAmount,txtFileldGross,txtGstPercent,txtTotal,txtRounding,txtNetAmount,txtCashReceived,txtCashBalance;
    ObservableList<BillingModel> modelObservableList = FXCollections.observableArrayList();
    ObservableList<BillingModel> treeItemList = FXCollections.observableArrayList();
    ArrayList<BillingModel> sendKotList = new ArrayList<>();
    ObservableList<BillingSaveModel> offlineModeList = FXCollections.observableArrayList();
    ItemListRequestAndResponseModel.item_list selectedItem;
    @FXML
    ChoiceBox comboPaymentMethod;
    double subTotal;
    int serialNo = 1;
    boolean isConnectedNetwork;
    ArrayList<Integer> itemIdList = new ArrayList<>();
    HashMap<Integer,Double> getTaxListDetails = new HashMap<>();
    ObservableList<String> tableList = FXCollections.observableArrayList();
    HashMap<String,ObservableList<BillingModel>> tableListValue = new HashMap<>();
    String selectedTable,from;
//    @FXML
//    ListView<String> listTableList;
    AddNewItemListener addNewItemListener;
    String customer_id;
    Connection connection;
    SqliteConnection sqliteConnection;
    ObservableList<Integer> selectedCheckedItems = FXCollections.observableArrayList();
    Alert alert;
    HashMap<String,Boolean> getSendKot = new HashMap<>();
    @FXML
    ImageView imgConectionStatus,imgPlaceOrder,btnAddItem;
    BillingSaveModel billingSaveModel = BillingSaveModel.getInstance();
    String tax1,tax2,tax_1,tax_2;
    ArrayList<String> checkBoxIndex = new ArrayList<>();
    String kotLastDate;
    ArrayList<String> kotList = new ArrayList();
    @FXML
    Button btnLogout;
    JFXSnackbar jfxSnackbar;
    int tableNo = 0,countEnter=0;

    XSSFSheet spreadsheet = null;
    XSSFWorkbook workbook= null;
    XSSFRow row;
    FileOutputStream out;
    File sheetFile;
    FileInputStream fileInputStream;
    String sheetName = "";
    ArrayList<String>tableNameList = new ArrayList<>();
    ArrayList<String> kotNoList = new ArrayList<>();
    @FXML
    TreeView<TableTreeItem>listTreeItem;
    HashMap<String,ArrayList<String>> treeListMap = new HashMap<>();

    //treeView
    ArrayList<String>rootItem = new ArrayList<>();
    HashMap<String,ArrayList<String>> kotHashMapList = new HashMap<>();
    String selectedRoot = "";
    CheckBox cb;
    @Override
    public void initialize(URL location, ResourceBundle resources) {



        //database connecttivity
        Thread t = new Thread(new Runnable() {
            public void run() {
                connection = SqliteConnection.connector();
                     if (connection == null)
                             {
                                 System.out.println("Connection not successfull");
                             }else
                                 {
                                System.out.println("Connection  successfull");
                                     sqliteConnection = new SqliteConnection();
                                     kotLastDate = sqliteConnection.getLastKOTDATE();
                                     getTableList();
            }
            }
        });
        t.start();

        itemLoadProgres.setVisible(false);
        itemLoadProgres.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        String css = BillingController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        billingRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(billingRootPane);

        KeyCombination ctrlX = KeyCodeCombination.keyCombination("Ctrl+N");
        KeyCombination ctrlH = KeyCodeCombination.keyCombination("Ctrl+H");
        billingRootPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (ctrlX.match(event)) {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/Billing/add_new_item.fxml"));
                    Parent root1 = null;
                    try {
                        root1 = (Parent) fxmlLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    AddNewItemController addNewItemController = new AddNewItemController();
                    addNewItemController.setAddItemListener(addNewItemListener);
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(new Scene(root1));
                    if (!stage.isShowing())
                        stage.show();
                }else if (ctrlH.match(event)){
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/Billing/search.fxml"));
                    Parent root1 = null;
                    try {
                        root1 = (Parent) fxmlLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(new Scene(root1));
                    if (!stage.isShowing())
                        stage.show();
                }
            }
        });



        new RabbitmqServer(this).execute();
        setTableDetails();
       NetworkConnection networkConnection = new NetworkConnection(BillingController.this);
         isConnectedNetwork =  networkConnection.isInternetReachable();
     /*   FileInputStream input = null;
       if (isConnectedNetwork) {

            try {
                input = new FileInputStream("RestarantApp/images/online.png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else
        {
            try {
                input = new FileInputStream("RestarantApp/images/offline.png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Image image = new Image(input);
        imgConectionStatus.setImage(image);*/
        Image image2;
        if (isConnectedNetwork)
        {
             image2 = new Image(BillingController.class.getResourceAsStream("/RestarantApp/images/online.png"));

        }else
        {
             image2 = new Image(BillingController.class.getResourceAsStream("/RestarantApp/images/offline.png"));

        }
        imgConectionStatus.setImage(image2);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getData();

                taxList();

            }
        });

//


        autoCompleteTextField = new AutoCompleteTextField(this);
        autoCompleteItemTextField = new AutoCompleteItemTextField(this);

        txtQty.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*") ) {

                    txtQty.setText(newValue.replaceAll("[^\\d]", ""));

                }else if ( !newValue.matches("^([1-9][0-9]{0,2}|1000)$"))
                {
                    txtQty.setText("1");
                }
            }
        });

        txtCashReceived.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*") ) {

                    txtCashReceived.setText(newValue.replaceAll("[^\\d]", ""));

                }else if ( !newValue.matches("^([1-9][0-9]{0,2}|1000)$"))
                {

                }
            }
        });

        txtCashReceived.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                 if (event.getCode().equals(KeyCode.ENTER)) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            double getNetAmount = Double.parseDouble(txtNetAmount.getText());
                            double cashReceiveAmount = Double.parseDouble(txtCashReceived.getText());
                            if (getNetAmount < cashReceiveAmount)
                                getBalance();
                            else
                                txtCashBalance.setText("");
                        }
                    });
                }
            }
        });
        txtCashBalance.setDisable(true);


      /*  txtFieldId.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*") ) {

                    txtFieldId.setText(newValue.replaceAll("[^\\d]", ""));

                }
            }
        });*/

        txtFieldId.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode().equals(KeyCode.TAB))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtFieldName.requestFocus();
                        }
                    });
                }else if (event.getCode().equals(KeyCode.ENTER)) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (!txtFieldId.getText().isEmpty())
                                addItemWhenEnter();
                        }
                    });
                }
            }
        });

        txtFieldName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.TAB))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtQty.requestFocus();
                        }
                    });
                }else if (event.getCode().equals(KeyCode.ENTER)) {

                    if(!txtFieldName.getText().isEmpty())

                        addItemWhenEnter();

                }
            }
        });

        txtQty.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.TAB))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnAddItem.requestFocus();

                        }
                    });
                }else if (event.getCode().equals(KeyCode.ENTER))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (selectedTable != null )
                            {
                                addItem();
                            }else
                            {
                                Constants.showAlert(Alert.AlertType.WARNING,"Warning","Warning","Please Select Table");
                            }
                        }
                    });
                }
            }
        });
        btnAddItem.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (selectedTable != null )
                            {
                                addItem();
                            }else
                            {
                                Constants.showAlert(Alert.AlertType.WARNING,"Warning","Warning","Please Select Table");
                            }
                        }
                    });
                }
            }
        });



        txtQty.setText("1");


        txtFiledDiscount.textProperty().addListener(addDiscountPercentage);//discount percentage
        txtFiledDiscountAmount.textProperty().addListener(addDiscountAmount);//discount amount

        listTreeItem.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<TableTreeItem>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<TableTreeItem>> observable, TreeItem<TableTreeItem> oldValue, TreeItem<TableTreeItem> newValue) {

                if (newValue != null) {
                    String selectedValue = String.valueOf(newValue.getValue());

                    if (newValue != null) {
                        if (modelObservableList.size() != 0) {
                            modelObservableList = tableListValue.get(selectedValue);
                        }
                        if (tableListValue.containsKey(selectedValue)) {

                            modelObservableList = tableListValue.get(selectedValue);
                            tableBill.setItems(modelObservableList);
                            itemIdList = new ArrayList<>();
                            if (modelObservableList.size() != 0) {
                                for (int k = 0; k < modelObservableList.size(); k++) {
                                    BillingModel billingModel = modelObservableList.get(k);
                                    billingModel.setS_no(k+1);
                                    itemIdList.add(Integer.parseInt(billingModel.getItem_id()));
                                    customer_id = billingModel.getCustomer_id();
                                }
                                setSubTotal();
                            }
                            tableBill.refresh();
                            setIsPlaced();
                        } else {
                            if (tableListValue.size() != 0) {
                                Set<String> getKeyFromHashMap = tableListValue.keySet();
                                ObservableList<BillingModel> billingList = FXCollections.observableArrayList();
                                ArrayList<String> keyList = new ArrayList<>();
                                ArrayList<ObservableList<BillingModel>> valuesList = new ArrayList<>();
                                Iterator<String> iterator = getKeyFromHashMap.iterator();
                                while (iterator.hasNext()) {
                                    String name = iterator.next();
                                    keyList.add(name);
                                }

                                for (int i = 0; i < keyList.size(); i++) {
                                    valuesList.add(tableListValue.get(keyList.get(i)));
                                }

                                for (int j = 0; j < valuesList.size(); j++) {
                                    ObservableList<BillingModel> billList = valuesList.get(j);
                                    int count = 1;
                                    for (int i = 0; i < billList.size(); i++) {
                                        BillingModel billingModel = billList.get(i);
                                        String kotNo = String.valueOf(billingModel.getKot_no());
                                        if (kotNo.equals(selectedValue)) {
                                            billingModel.setS_no(count);
                                            count = count + 1;
                                            billingList.add(billingModel);
                                        }
                                    }
                                }

                                modelObservableList = FXCollections.observableArrayList();
                                modelObservableList = billingList;
                                tableBill.setItems(modelObservableList);
                                itemIdList = new ArrayList<>();
                                if (modelObservableList.size() != 0) {
                                    for (int k = 0; k < modelObservableList.size(); k++) {
                                        BillingModel billingModel = modelObservableList.get(k);
                                        itemIdList.add(Integer.parseInt(billingModel.getItem_id()));
                                        customer_id = billingModel.getCustomer_id();
                                    }
                                    setSubTotal();
                                }
                                tableBill.refresh();
                                setIsPlaced();
                            }else {
                                modelObservableList = FXCollections.observableArrayList();
                                selectedCheckedItems = FXCollections.observableArrayList();
                                tableBill.setItems(modelObservableList);
                                itemIdList = new ArrayList<>();
                                setIsPlaced();
                            }

                        }


                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtFieldId.requestFocus();
                        }
                    });

                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem deleteItem = new MenuItem();
                    deleteItem.textProperty().bind(Bindings.format("Delete \"%s\"", newValue.getValue()));
                    deleteItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation Dialog");
                            alert.setHeaderText(null);
                            alert.setContentText("Are you want to Cancel this Order?");

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK){
                                TreeItem<TableTreeItem> selected = listTreeItem.getSelectionModel().getSelectedItem();
                                selected.getParent().getChildren().remove(selected);
                                rootItem.remove(selectedValue);
                                modelObservableList.clear();
                                checkBoxIndex = new ArrayList<>();
                                tableList.remove(selectedTable);

                            } else  {
                                alert.close();

                            }
                        }
                    });

                    MenuItem printItem = new MenuItem();
                    printItem.textProperty().bind(Bindings.format("Print Kot \"%s\"", newValue.getValue()));
                    printItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            String kotNumber =String.valueOf( newValue.getValue());
                            printKotBased(kotNumber);

                        }
                    });
                    if (rootItem.contains(selectedValue))
                    {
                        System.out.println("Selected Text yes:----> " + newValue.getValue());
                        contextMenu.getItems().add(deleteItem);
                    }else{
                        System.out.println("Selected Text No:----> " + newValue.getValue());
                        contextMenu.getItems().add(printItem);
                    }

                    listTreeItem.setContextMenu(contextMenu);
                }



            }
        });





        tableBill.setRowFactory(tv -> new TableRow<BillingModel>() {
            @Override
            public void updateItem(BillingModel item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (item.getKot_no() != 0) {
                    setStyle("-fx-background-color: #ffcad0;");
                } else {
                    setStyle("");
                }
            }
        });

        addNewItemListener = new AddNewItemListener() {
            @Override
            public void addNewItem() {

                if (tableList.contains(loginRequestAndResponse.getName()))
                {
                    tableNo = tableNo + 1;
                    String con = String.valueOf(tableNo);
                    con = loginRequestAndResponse.getName()+"("+con+")";
                    tableList.add(con);

                    rootItem.add(con);
                    selectedTable = con;
                }else {
                    tableList.add(loginRequestAndResponse.getName());
                    rootItem.add(loginRequestAndResponse.getName());

                    selectedTable = loginRequestAndResponse.getName();
                }
//                listTableList.setItems(tableList);
                addTreeView();
                modelObservableList = FXCollections.observableArrayList();
                serialNo = 1;
                from = "parcel";
                customer_id = loginRequestAndResponse.getCustomer_id();

                addItemWhenSelected(selectedTable);
                NetworkConnection networkConnection = new NetworkConnection(BillingController.this);

//                tableListValue.put(loginRequestAndResponse.getCustomer_id(),modelObservableList);
            }
        };

        comboPaymentMethod.getItems().add("Cash");
        comboPaymentMethod.getItems().add("Card");
        comboPaymentMethod.getItems().add("Paytm");
        comboPaymentMethod.getItems().add("Others");
        comboPaymentMethod.getSelectionModel().selectFirst();




        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Please Wait");
        alert.setHeaderText("Save to Server");
        alert.setContentText("Your OFFline data save to server is processing");
        alert.getButtonTypes().remove(0);



    }

    private void printKotBased(String kotNumber) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        Date date = new Date();
        Date time = new Date();
        String currentDate = dateFormat.format(date);
        String currentTime = timeFormat.format(time);
        String header =
                "       **** Prawn And Crab ****       \n\n"
                        +"                  KOT                 \n\n"
                        +" KOT No:"+String.valueOf(kotNumber)+"                Date:"+currentDate+"\n"
                        +" Table No:"+selectedTable+"          Time:"+currentTime +"\n\n";


        String  listItem = "            List Of Items            \n"
                +"            -------------             \n";

        String item_header = "  S.No         Item Name        Qty\n\n";

        String item = "";
         int totalQty = 0;
         int ssNo =0;
        for (int i = 0; i<modelObservableList.size();i++)
        {
            BillingModel billingModel = modelObservableList.get(i);
            String item_name = billingModel.getItem_name();
            String qty = billingModel.getQuantity();
            String notes = billingModel.getNotes();

             ssNo = ssNo + 1;
             totalQty = totalQty + Integer.parseInt(qty);
             item = item +"   "+ ssNo + "        "+item_name + "          "+qty+"\n";
             if (!notes.equals("notes")) {
                    item =item + "           [ "+notes+" ]\n";
                }



        }

        String line = " ------------------------------------\n";
        String total_item = "                  Total Item(s)  "+ totalQty;
        header = header+listItem+item_header+ item +line+total_item+"\n\n\n\n\n\n\n";

        System.out.println(header);
    }


    public void addItemWhenSelected(String selectedValue)
    {


            if (selectedValue != null) {
                selectedTable = selectedValue;
                if (modelObservableList.size() != 0) {
                    modelObservableList = tableListValue.get(selectedValue);
                }
                if (tableListValue.containsKey(selectedValue)) {

                    modelObservableList = tableListValue.get(selectedValue);
                    tableBill.setItems(modelObservableList);
                    itemIdList = new ArrayList<>();
                    if (modelObservableList.size() != 0) {
                        for (int k = 0; k < modelObservableList.size(); k++) {
                            BillingModel billingModel = modelObservableList.get(k);
                            billingModel.setS_no(k+1);
                            itemIdList.add(Integer.parseInt(billingModel.getItem_id()));
                            customer_id = billingModel.getCustomer_id();
                        }
                        setSubTotal();
                    }
                    tableBill.refresh();
                    setIsPlaced();
                } else {
                    if (tableListValue.size() != 0) {
                        Set<String> getKeyFromHashMap = tableListValue.keySet();
                        ObservableList<BillingModel> billingList = FXCollections.observableArrayList();
                        ArrayList<String> keyList = new ArrayList<>();
                        ArrayList<ObservableList<BillingModel>> valuesList = new ArrayList<>();
                        Iterator<String> iterator = getKeyFromHashMap.iterator();
                        while (iterator.hasNext()) {
                            String name = iterator.next();
                            keyList.add(name);
                        }

                        for (int i = 0; i < keyList.size(); i++) {
                            valuesList.add(tableListValue.get(keyList.get(i)));
                        }

                        for (int j = 0; j < valuesList.size(); j++) {
                            ObservableList<BillingModel> billList = valuesList.get(j);
                            int count = 1;
                            for (int i = 0; i < billList.size(); i++) {
                                BillingModel billingModel = billList.get(i);
                                String kotNo = String.valueOf(billingModel.getKot_no());
                                if (kotNo.equals(selectedValue)) {
                                    billingModel.setS_no(count);
                                    count = count + 1;
                                    billingList.add(billingModel);
                                }
                            }
                        }

                        modelObservableList = FXCollections.observableArrayList();
                        modelObservableList = billingList;
                        tableBill.setItems(modelObservableList);
                        itemIdList = new ArrayList<>();
                        if (modelObservableList.size() != 0) {
                            for (int k = 0; k < modelObservableList.size(); k++) {
                                BillingModel billingModel = modelObservableList.get(k);
                                itemIdList.add(Integer.parseInt(billingModel.getItem_id()));
                                customer_id = billingModel.getCustomer_id();
                            }
                            setSubTotal();
                        }
                        tableBill.refresh();
                        setIsPlaced();
                    }else {
                        modelObservableList = FXCollections.observableArrayList();
                        selectedCheckedItems = FXCollections.observableArrayList();
                        tableBill.setItems(modelObservableList);
                        itemIdList = new ArrayList<>();
                        setIsPlaced();
                    }

                }


            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    txtFieldId.requestFocus();
                }
            });

    }
    private void getBalance() {
        double getNetAmount = Double.parseDouble(txtNetAmount.getText());
        double cashReceiveAmount = Double.parseDouble(txtCashReceived.getText());
        double balanceAmount = cashReceiveAmount - getNetAmount;
        String remainingAmt = String.format ("%.1f", balanceAmount);
        txtCashBalance.setText(remainingAmt);

    }

    private void addItemWhenEnter() {

            for (int j = 0; j < billingItemDetails.size(); j++) {
                ItemListRequestAndResponseModel.item_list item_list = billingItemDetails.get(j);
                if (item_list.getItem_name().equals(txtFieldId.getText().trim())) {
                    selectedItem = item_list;

                    txtFieldId.setText(item_list.getShort_code());
                    txtFieldId.hidePopUp();

                } else if (item_list.getShort_code().equals(txtFieldId.getText().trim())) {
                    selectedItem = item_list;

                    txtFieldName.setText(item_list.getItem_name());
                    txtFieldName.hidePopUp();


                }
            }

            if (selectedTable != null) {
                addItem();
            } else {
                Constants.showAlert(Alert.AlertType.WARNING, "Warning", "Warning", "Please Select Table");
            }


    }


    public void nertworkChanged()
    {
        FileInputStream input = null;
        if (isConnectedNetwork) {

            try {
                input = new FileInputStream("src/RestarantApp/images/online.png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else
        {
            try {
                input = new FileInputStream("src/RestarantApp/images/offline.png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        Image image = new Image(input);
        imgConectionStatus.setImage(image);
    }

    public void setIsPlaced()
    {
        if (modelObservableList.size() != 0) {
            BillingModel billingModel = modelObservableList.get(0);
            billingModel.isPlacedSale();
            if (billingModel.isPlacedSale()) {
                tableBill.setEditable(false);
                FileInputStream input = null;

                try {
                    input = new FileInputStream("src/RestarantApp/images/closesale.png");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Image image = new Image(input);
                imgPlaceOrder.setImage(image);
                btnAddItem.setOnMouseClicked(null);
            } else {
                tableBill.setEditable(true);
                FileInputStream input = null;

                try {
                    input = new FileInputStream("src/RestarantApp/images/placeorder.png");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Image image = new Image(input);
                imgPlaceOrder.setImage(image);
                btnAddItem.setOnMouseClicked(this::btnAddItem);
            }
        }else
        {
            tableBill.setEditable(true);
            FileInputStream input = null;

            try {
                input = new FileInputStream("src/RestarantApp/images/placeorder.png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Image image = new Image(input);
            imgPlaceOrder.setImage(image);
            btnAddItem.setOnMouseClicked(this::btnAddItem);
            txtTotalAmount.setText("");
            txtFiledDiscount.setText("");
            txtNetAmount.setText("");
            txtRounding.setText("");
            txtFileldGross.setText("");
            txtTotal.setText("");
            comboPaymentMethod.getSelectionModel().selectFirst();
        }
    }

    private void setTableDetails() {

        tableBill.setEditable(true);
        colSno.setResizable(false);
        colSno.setCellValueFactory(new PropertyValueFactory<BillingModel,Integer>("s_no"));
        cb = new CheckBox();
        cb.setOnAction(handleSelectAllCheckbox());
//        cb.setUserData(this.colSno);
        this.colSno.setGraphic(cb);

        colSno.setCellFactory(param -> {
            return new TableCell<BillingModel,Integer>()
            {
                CheckBox checkBox = new CheckBox();
                  @Override
                    public void updateItem(Integer item, boolean empty) {
                      super.updateItem(item,empty);
                        if (empty)
                        {
                            setGraphic(null);
                        }else
                        {
//                        checkBox.setSelected(true);
                            BillingModel billingModel = modelObservableList.get(item-1);
                            checkBox.setText(String.valueOf(billingModel.getS_no()));
                            if (billingModel.isSendKot)
                            {
                                checkBox.setSelected(false);
                            }
//                            checkBox.setSelected(billingModel.isSendKot());

                            if (billingModel.isSelected())
                            {
                                checkBox.setSelected(true);
                            }else
                            {
                                checkBox.setSelected(false);
                            }
                            setGraphic(checkBox);

                            checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (modelObservableList != null) {
                                        BillingModel billingModel = modelObservableList.get(item - 1);
                                        String index = String.valueOf(item -1);
                                        if (newValue)
                                        {
                                           if (!checkBoxIndex.contains(index))
                                           {
                                               checkBoxIndex.add(index);
                                           }
                                        }else
                                        {
                                            if (checkBoxIndex.contains(index))
                                            {
                                                checkBoxIndex.remove(index);
                                            }
                                        }

                                        if (!billingModel.isSendKot())
                                            billingModel.setSendKot(newValue);
                                    }
                                }
                            });
                        }



                    }



            };

        });
        colSno.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {

            }
        });

        colSno.setPrefWidth( 60 );

        kotNo.setCellValueFactory(new PropertyValueFactory<BillingModel,Integer>("kot_no"));
        kotNo.setPrefWidth( 60 );


        colItem.setCellValueFactory(
                new PropertyValueFactory<BillingModel,String>("item_name")
        );
        colItem.setPrefWidth( 200 );
        colQty.setCellValueFactory(
                new PropertyValueFactory<BillingModel,String>("quantity")
        );
        colQty.setPrefWidth( 70 );
        tableBill.setEditable( true );
        javafx.util.Callback<TableColumn, TableCell> cellFactory =
                new javafx.util.Callback<TableColumn, TableCell>() {
                    @Override
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };
        colQty.setCellFactory(cellFactory);

        colQty.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {

                BillingModel billingModel = modelObservableList.get(event.getTablePosition().getRow());
                double amt = Double.valueOf(billingModel.getQuantity());
                double price = Double.valueOf(billingModel.getRate());
                amt = amt * price ;
                String amount = String.valueOf(amt);
                billingModel.setAmount(amount);
                modelObservableList.set(event.getTablePosition().getRow(),billingModel);
                setSubTotal();
                if (billingModel.getFrom().equals("mobile"))
                    sendToMobile(selectedTable);
            }
        });


        colAddNotes.setCellValueFactory(
                new PropertyValueFactory<BillingModel,String>("notes")
        );
        colAddNotes.setPrefWidth( 150 );
        javafx.util.Callback<TableColumn, TableCell> notescellFactory =
                new javafx.util.Callback<TableColumn, TableCell>() {

                    @Override
                    public TableCell call(TableColumn p) {
                        return new NotesEditingCell();
                    }
                };
        colAddNotes.setCellFactory(notescellFactory);

        colAddNotes.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {

              /*  BillingModel billingModel = modelObservableList.get(event.getTablePosition().getRow());

                modelObservableList.set(event.getTablePosition().getRow(),billingModel);*/
            }
        });


        colRate.setCellValueFactory(
                new PropertyValueFactory<BillingModel,String>("rate")
        );
        colRate.setPrefWidth( 100 );
        colAmount.setCellValueFactory(
                new PropertyValueFactory<BillingModel,String>("amount")
        );

        colAmount.setPrefWidth( 110 );


        tableBill.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableBill.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableBill.setFixedCellSize(35);



        TableColumn<BillingModel, BillingModel> unfriendCol = new TableColumn<>("Action");
        unfriendCol.setMinWidth(40);
        unfriendCol.setPrefWidth( 100 );
        unfriendCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        unfriendCol.setCellFactory(param -> {
            return new TableCell<BillingModel,BillingModel>() {

                Image image = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/delete.png"));
                ImageView deleteButton = new ImageView(image);
                Button button1 = new Button("", deleteButton);

                @Override
                protected void updateItem(BillingModel billingModel, boolean empty) {
                    super.updateItem(billingModel, empty);

                    if (billingModel == null) {
                        setGraphic(null);
                        return;
                    }
                    String TOOLTIP = "deleteTip",EDITTIP = "editTip";

                    Tooltip deleteTip = new Tooltip("Delete Item");
                    deleteButton.getProperties().put(TOOLTIP, deleteTip);
                    Tooltip.install(deleteButton, deleteTip);

                    Tooltip editTip = new Tooltip("Edit Item");


                    HBox pane = new HBox( button1);
                    pane.setAlignment(Pos.CENTER);
                    pane.setSpacing(5);
                    pane.setPadding(new Insets(10, 0, 10, 10));

                    button1.setStyle("-fx-background-color: transparent;");
                    setGraphic(pane);
                    button1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {


                            showAlert(billingModel);

                        }
                    });

                    deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();

                        }
                    });

                }
            };

        });
        tableBill.getColumns().addAll(unfriendCol);

        listTreeItem.setCellFactory(new javafx.util.Callback<TreeView<TableTreeItem>, TreeCell<TableTreeItem>>() {

            @Override
            public TreeCell<TableTreeItem> call(TreeView<TableTreeItem> param) {

                CheckBoxTreeCell<TableTreeItem> treeItemCheckBoxTreeCell = new CheckBoxTreeCell<>();

                return treeItemCheckBoxTreeCell;
            }
        });


    }

    private EventHandler<ActionEvent> handleSelectAllCheckbox() {


        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                TableColumn column = (TableColumn) cb.getUserData();
                checkBoxIndex = new ArrayList<>();
                if (cb.isSelected()) {


                   for (int i = 0; i < modelObservableList.size() ; i ++)
                   {
                       BillingModel  billingModel  = modelObservableList.get(i);
                       System.out.println("check box selected---->"+billingModel.isSendKot());
                       if (!billingModel.isSendKot()) {
                           billingModel.setSelected(true);
                           checkBoxIndex.add(String.valueOf(i));
                           billingModel.setSendKot(true);
                       }
                   }
                } else {

                    for (BillingModel billingModel : modelObservableList) {
                        billingModel.setSelected(false);
                        if (billingModel.isSendKot()){
                            billingModel.setSendKot(true);
                        }else{
                            billingModel.setSendKot(false);
                        }
                    }
                    checkBoxIndex = new ArrayList<>();
                }

                tableBill.refresh();
            }
        };
    }

    private void getData() {
        itemLoadProgres.setVisible(true);
        retrofitService = RetrofitClient.getClient().create(APIService.class);
        Call<ItemListRequestAndResponseModel> getItemList = retrofitService.getItemList();
        getItemList.enqueue(new Callback<ItemListRequestAndResponseModel>() {
            @Override
            public void onResponse(Call<ItemListRequestAndResponseModel> call, Response<ItemListRequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    ItemListRequestAndResponseModel listRequestAndResponseModel = response.body();
                    for (int j= 0 ; j < listRequestAndResponseModel.getItem_list().size() ; j++)
                    {
                        ItemListRequestAndResponseModel.item_list item_list = listRequestAndResponseModel.getItem_list().get(j);
                        itemName.add(item_list.getItem_name());
                        itemId.add(item_list.getShort_code());
                        billingItemDetails.add(item_list);

                    }
//                    TextFields.bindAutoCompletion(txtFieldName,itemName);
                    txtFieldName.getEntries().addAll(itemName);
                    txtFieldId.getEntries().addAll(itemId);
                    itemLoadProgres.setVisible(false);

                    isConnectedNetwork = true;
                    nertworkChanged();

                }
            }

            @Override
            public void onFailure(Call<ItemListRequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }

    public void textFiledNamePressed(javafx.scene.input.KeyEvent keyEvent) {
        switch (keyEvent.getCode())
        {
            case ENTER:
                learnWord(txtFieldName.getText());
                break;
        }
    }

    public void learnWord(String text)
    {
        possibleWordSet.add(text);
        if (autoCompletionBinding != null)
        {
            autoCompletionBinding.dispose();
        }
        autoCompletionBinding =  TextFields.bindAutoCompletion(txtFieldName,possibleWordSet);

    }


    public void addItem()
    {
        Platform.runLater(new Runnable(){

            @Override
            public void run() {
                if (selectedItem != null) {
                    BillingModel billingModel;
                    if (itemIdList.size() != 0) {
                        int itemId = Integer.parseInt(selectedItem.getShort_code());

                        if (itemIdList.contains(itemId)) {

                            for (int j = 0; j < modelObservableList.size(); j++) {
                                BillingModel billingModelList = modelObservableList.get(j);
                                if (billingModelList.getItem_id().equals(selectedItem.getShort_code())) {
                                    int alreadyValue = Integer.parseInt(billingModelList.getQuantity());
                                    int newQty = Integer.parseInt(txtQty.getText());
                                    newQty = newQty + alreadyValue;
                                    double amt = Double.valueOf(newQty);
                                    double price = Double.valueOf(selectedItem.getPrice());
                                    amt = amt * price;
                                    String amount = String.valueOf(amt);
                                    serialNo = billingModelList.getS_no();
                                    billingModel = new BillingModel(serialNo, selectedItem.getItem_name(), "notes" ,String.valueOf(newQty), selectedItem.getPrice(), amount, selectedItem.getShort_code(),customer_id,from,false,false);
                                    if (getSendKot.containsKey(selectedTable))
                                    {
                                        changeSNo();
                                        double amtChenged = Double.valueOf(txtQty.getText());
                                        double priceChenged = Double.valueOf(selectedItem.getPrice());
                                        amtChenged = amtChenged * priceChenged;
                                        String amountChenged = String.valueOf(amtChenged);
                                        billingModel = new BillingModel(serialNo, selectedItem.getItem_name(), "notes",txtQty.getText(), selectedItem.getPrice(), amountChenged, selectedItem.getShort_code(),customer_id,from,false,false);
                                        modelObservableList.add( billingModel);
                                        setSubTotal();
                                        break;
                                    }else
                                    {
                                        modelObservableList.set(j, billingModel);
                                        setSubTotal();
                                        break;
                                    }


                                }
                            }
                        } else {
                            double amt = Double.valueOf(txtQty.getText());
                            double price = Double.valueOf(selectedItem.getPrice());
                            amt = amt * price;
                            String amount = String.valueOf(amt);
                            billingModel = new BillingModel(serialNo, selectedItem.getItem_name(), "notes",txtQty.getText(), selectedItem.getPrice(), amount, selectedItem.getShort_code(),customer_id,from,false,false);
                            modelObservableList.add(billingModel);
                            tableBill.setItems(modelObservableList);
                            itemIdList.add(Integer.parseInt(selectedItem.getShort_code()));
                            setSubTotal();
                        }
                    } else {
                        double amt = Double.valueOf(txtQty.getText());
                        double price = Double.valueOf(selectedItem.getPrice());
                        amt = amt * price;
                        String amount = String.valueOf(amt);
                        billingModel = new BillingModel(serialNo, selectedItem.getItem_name(), "notes",txtQty.getText(), selectedItem.getPrice(), amount, selectedItem.getShort_code(),customer_id,from,false,false);
                        modelObservableList.add(billingModel);
                        tableBill.setItems(modelObservableList);
                        itemIdList.add(Integer.parseInt(selectedItem.getShort_code()));
                        setSubTotal();
                    }
                    if (from.equals("mobile")) {

                        sendToMobile(selectedTable);


                    }else
                    {
                        tableListValue.put(loginRequestAndResponse.getName(),modelObservableList);
                    }

                    txtFieldId.clear();
                    txtFieldName.clear();


            }

                serialNo++;

                txtFieldId.requestFocus();
            }


        });
    }
    public void btnAddItem(MouseEvent mouseEvent) {
        if (selectedTable != null )
        {

            addItem();
            addTreeView();
        }else
        {
            Constants.showAlert(Alert.AlertType.WARNING,"Warning","Warning","Please Select Table");
        }

    }

    private void addTreeView() {

        TableTreeItem layer1 = new TableTreeItem("Root", false);
        TreeItem<TableTreeItem> rootList = new TreeItem<>(layer1);

        rootList.setExpanded(true);


       for (int j= 0 ; j < rootItem.size() ; j ++) {
           TableTreeItem tableTreeItem = new TableTreeItem(rootItem.get(j),false);
           CheckBoxTreeItem<TableTreeItem> treeItem = new CheckBoxTreeItem<TableTreeItem>(tableTreeItem);
           treeItem.selectedProperty().addListener((obs, oldVal, newVal) -> {
               System.out.println(treeItem.getValue() + " selection state: " + newVal);
               selectedRoot = String.valueOf(treeItem.getValue());

           });
           String strRootItem = rootItem.get(j);

               ObservableList<BillingModel> billingModels = tableListValue.get(strRootItem);
               ArrayList<String> kotNoList = new ArrayList<>();
               if (billingModels != null) {
                   for (int k = 0; k < billingModels.size(); k++) {
                       BillingModel billingModel = billingModels.get(k);
                       String kotNo = String.valueOf(billingModel.getKot_no());
                       if (!kotNoList.contains(kotNo) && !kotNo.equals("0")) {
                           kotNoList.add(kotNo);
                       }
                   }

                   ArrayList<String> selectedKot = new ArrayList<>();
                   for (int i = 0 ; i < kotNoList.size() ; i ++) {
                       TableTreeItem tableChildTreeItem = new TableTreeItem(kotNoList.get(i), false);
                       CheckBoxTreeItem<TableTreeItem> treeChildItem = new CheckBoxTreeItem<TableTreeItem>(tableChildTreeItem);

                       treeChildItem.selectedProperty().addListener((obs, oldVal, newVal) -> {
                            if (newVal && !selectedKot.contains(String.valueOf(treeChildItem.getValue())))
                                selectedKot.add(String.valueOf(treeChildItem.getValue()));
                            else if (!newVal)
                            {
                                String value = String.valueOf(treeChildItem.getValue());
                                if (selectedKot.contains(value))
                                {
                                    selectedKot.remove(value);
                                }
                            }

                            String tableName =String.valueOf( treeItem.getValue());
                            kotHashMapList.put(tableName,selectedKot);
                            selectedRoot = String.valueOf(treeItem.getValue());
                           System.out.println(treeItem.getValue() +"----->"+ treeChildItem.getValue() + " selection child1 state: " + newVal);
                           System.out.println(kotHashMapList);
                       });
                        treeItem.getChildren().add(treeChildItem);
                   }

               }

           rootList.getChildren().add(treeItem);
       }


        listTreeItem.setRoot(rootList);
        listTreeItem.setShowRoot(false);



    }


    public void setSubTotal()
    {
        subTotal = 0.0;
        for (int j=0;j<modelObservableList.size();j++)
        {
            BillingModel billingModel = modelObservableList.get(j);
            double price = Double.parseDouble(billingModel.getAmount());
            subTotal = subTotal + price;
        }

        String subtotal = String.valueOf(subTotal);
        txtTotalAmount.setText(subtotal);
        double totalAmount = Double.valueOf(txtTotalAmount.getText());
        if (!txtFiledDiscount.getText().isEmpty())
        {


            double getValue = Double.valueOf(txtFiledDiscount.getText());
            getValue = getValue/100;
            getValue = getValue*totalAmount;
            txtFiledDiscountAmount.setText(String.valueOf(getValue));
            txtFileldGross.setText(String.valueOf(totalAmount - getValue));

            double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
            double getGst = fromGst/100;
            getGst = getGst * totalAmount;
            txtTotal.setText(String.valueOf(getGst + totalAmount - getValue));
            roundValue();

        }else if (!txtFiledDiscountAmount.getText().isEmpty())
        {

            double getValue = Double.valueOf(txtFiledDiscountAmount.getText());
            txtFileldGross.setText(String.valueOf(totalAmount - getValue));

            double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
            double getGst = fromGst/100;
            getGst = getGst * totalAmount;
            txtTotal.setText(String.valueOf(getGst + totalAmount - getValue));
            roundValue();
        } else
        {
            txtFileldGross.setText(String.valueOf(totalAmount));
            double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
            double getGst = fromGst/100;
            getGst = getGst * totalAmount;
            txtTotal.setText(String.valueOf(getGst + totalAmount));
            roundValue();
        }


    }

    @Override
    public void getSelectedResult(String result) {

        for (int j= 0 ; j < billingItemDetails.size() ; j ++)
        {
            ItemListRequestAndResponseModel.item_list item_list = billingItemDetails.get(j);
            if (item_list.getItem_name().equals(result))
            {
                selectedItem = item_list;
                txtFieldId.setText(item_list.getShort_code());
                txtFieldId.hidePopUp();
            }else if (item_list.getShort_code().equals(result))
            {
                selectedItem = item_list;
                txtFieldName.setText(item_list.getItem_name());
                txtFieldName.hidePopUp();
            }
        }

    }

    void showAlert(BillingModel list)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you want to Delete this "+list.getItem_name() + " Item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            double deletedItemPrice = Double.parseDouble(list.getRate());
            double deletedItemQty = Double.parseDouble(list.getQuantity());
            deletedItemPrice = deletedItemPrice * deletedItemQty ;
            subTotal = subTotal - deletedItemPrice;
            String subtotal = String.valueOf(subTotal);

            txtTotalAmount.setText(subtotal);
            modelObservableList.remove(list);
            int itemIdDelete = Integer.parseInt(list.getItem_id());


            itemIdList.remove(itemIdList.indexOf(itemIdDelete));
            changeSNo();
            if (from.equals("mobile")) {
                sendToMobile(selectedTable);
            }
        } else  {
            alert.close();

        }

    }


    private void changeSNo()
    {
        serialNo = 0;
        for (int i=0;i<modelObservableList.size();i++)
        {
            BillingModel billingModel = modelObservableList.get(i);
            billingModel.setS_no(i+1);
            modelObservableList.set(i,billingModel);
        }
        serialNo = modelObservableList.size()+1;

    }



    public void taxList()
    {
        retrofitService = RetrofitClient.getClient().create(APIService.class);

        Call<TaxModel> getTaxCall = retrofitService.getTaxList1();
        getTaxCall.enqueue(new Callback<TaxModel>() {
            @Override
            public void onResponse(Call<TaxModel> call, Response<TaxModel> response) {
                if (response.isSuccessful()) {
                    TaxModel requestAndResponseModel = response.body();

                    HashMap<Integer,String> taxNAme = new HashMap<>();
                    for (int i=0;i < requestAndResponseModel.getList().size() ; i ++)
                    {
                        TaxModel.list list = requestAndResponseModel.getList().get(i);
                        getTaxListDetails.put(Integer.valueOf(list.getId()),list.getValue());
                        taxNAme.put(Integer.valueOf(list.getId()),list.getName());
                        if (list.getActive() == 1)
                        {
                            if (list.getComp1() != 0&& list.getComp2() != 0)
                            {
                                int comb1 = list.getComp1();
                                int comb2 = list.getComp2();

                                tax1 = taxNAme.get(comb1)+"("+getTaxListDetails.get(comb1) + "%)";
                                tax2 =  taxNAme.get(comb2)+"("+getTaxListDetails.get(comb2) + "%)";
                                intTaxPrice = getTaxListDetails.get(comb1)+getTaxListDetails.get(comb2);
                                tax_1 = String.valueOf(getTaxListDetails.get(comb1));
                                tax_2 = String.valueOf(getTaxListDetails.get(comb2));

                                txtGstPercent.setText(String.valueOf(intTaxPrice)+"%");
                                txtGstPercent.setEditable(false);
                            }
                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<TaxModel> call, Throwable throwable) {

            }
        });
    }

    public void roundValue()
    {
        String totalValue = txtTotal.getText();
        String[] totalValueSplit = totalValue.split("\\.");
        if (totalValueSplit[1] != null)
        {
            String firstChar = String.valueOf(totalValueSplit[1].charAt(0));
            int split2 = Integer.parseInt(firstChar);
            if (split2 != 0)
            {
                if (split2 > 5)
                {

                    int roundValue = Integer.parseInt(totalValueSplit[0]);
                    int actulaValue = roundValue + 1;
                    txtNetAmount.setText(String.valueOf(actulaValue));

                    double rounding = Double.valueOf(txtTotal.getText());

                    String roundValueText = String.format("%.2f",rounding - actulaValue);
                    txtRounding.setText(roundValueText);
                    String round = String.valueOf(rounding - actulaValue);
                    if (round.contains("-"))
                    {
                        roundValueText = "+ "+roundValueText.substring(1);
                        txtRounding.setText(roundValueText);
                    }else
                    {
                        roundValueText = "- "+roundValueText;
                        txtRounding.setText(roundValueText);
                    }


                }else if (split2 < 5)
                {

                    int roundValue = Integer.parseInt(totalValueSplit[0]);
                    int actulaValue = roundValue ;
                    txtNetAmount.setText(String.valueOf(actulaValue));

                    double rounding = Double.valueOf(txtTotal.getText());

                    String round = String.valueOf(rounding - actulaValue);
                    String roundValueText = String.format("%.2f",rounding - actulaValue);
//                    txtRounding.setText(round);
                    if (round.contains("-"))
                    {
                        roundValueText = "+ "+roundValueText.substring(1);
                        txtRounding.setText(roundValueText);
                    }else
                    {
                        roundValueText = "- "+roundValueText.substring(1);
                        txtRounding.setText(roundValueText);
                    }

                }else
                {


                    int roundValue = Integer.parseInt(totalValueSplit[0]);
                    int actulaValue = roundValue + 1;
                    txtNetAmount.setText(String.valueOf(actulaValue));

                    double rounding = Double.valueOf(txtTotal.getText());

                    txtRounding.setText(String.valueOf(rounding - actulaValue));
                    String round = String.valueOf(rounding - actulaValue);
                    String roundValueText = String.format("%.2f",rounding - actulaValue);
                    if (round.contains("-"))
                    {
                        roundValueText = "+ "+roundValueText.substring(1);
                        txtRounding.setText(roundValueText);
                    }else
                    {
                        txtRounding.setText(roundValueText);
                    }
                }
            }else
            {

                txtNetAmount.setText(totalValueSplit[0]);
                txtRounding.setText("0");
            }
        }

    }

    public void calculatePergentage(String discountValue)
    {
        double totalAmount = Double.valueOf(txtTotalAmount.getText());
        double givenValue = Double.valueOf(discountValue);
        double percentage = givenValue / totalAmount;
        percentage = percentage * 100;

        String percent = String.valueOf(percentage);
        if (percent.contains("."))
        {
            String[] split= percent.split("//.");
            txtFiledDiscount.setText(split[0]);

        }else {
            txtFiledDiscount.setText(percent);
        }

    }

    ChangeListener<String> addDiscountPercentage = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {



            double totalAmount = Double.valueOf(txtTotalAmount.getText());

            if (!txtTotalAmount.getText().equals("0.0"))
            {
                double getValue = 0.0;

                if (newValue.isEmpty())
                {
                    txtFileldGross.setText(String.valueOf(totalAmount));
                    double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
                    double getGst = fromGst/100;
                    getGst = getGst * totalAmount ;
                    txtTotal.setText(String.valueOf(getGst + totalAmount));
                    roundValue();
                }else {
                    getValue = Double.valueOf(newValue);
                }



                getValue = getValue/100;
                getValue = getValue*totalAmount;

                txtFiledDiscountAmount.setText(String.valueOf(getValue));

                txtFileldGross.setText(String.valueOf(totalAmount - getValue));

                if (!newValue.isEmpty()) {
                    txtFileldGross.setText(String.valueOf(totalAmount - getValue));
                    double fromGross = Double.valueOf(txtFileldGross.getText());
                    double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
                    double getGst = fromGst/100;
                    getGst = getGst * fromGross;
                    txtTotal.setText(String.valueOf(getGst + fromGross));
                    roundValue();
                }
            }
        }
    };

    ChangeListener<String> addDiscountAmount = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

            if (!newValue.isEmpty())
                calculatePergentage(newValue);
            if (!txtTotalAmount.getText().equals("0.0"))
            {

                double totalAmount = Double.valueOf(txtTotalAmount.getText());

                double getValue = 0.0;
                if (newValue.isEmpty())
                {
                    txtFileldGross.setText(String.valueOf(totalAmount));
                    double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
                    double getGst = fromGst/100;
                    getGst = getGst * totalAmount ;
                    txtTotal.setText(String.valueOf(getGst + totalAmount));
                    roundValue();
                }else {
                    getValue = Double.valueOf(newValue);
                }
                if (!newValue.isEmpty()) {
                    txtFileldGross.setText(String.valueOf(totalAmount - getValue));
                    double fromGross = Double.valueOf(txtFileldGross.getText());
                    double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
                    double getGst = fromGst/100;
                    getGst = getGst * fromGross;
                    txtTotal.setText(String.valueOf(getGst + fromGross));
                    roundValue();
                }
            }
        }
    };


    @Override
    public void getFromServer(String body) {
        try {
            JSONObject itemList = new JSONObject(body);
            if (itemList.has("table"))
            {
                String table = itemList.getString("table");

                if (!tableList.contains(table))
                {
                    Platform.runLater(new Runnable(){

                        @Override
                        public void run() {
                            tableList.add(table);
                    modelObservableList =  FXCollections.observableArrayList();
                    itemIdList = new ArrayList<>();
                    rootItem.add(table);
//                    listTableList.setItems(tableList);
                            try {
                                if (itemList.getString("from").equals("mobile")) {

                                    serialNo = 1;
                                    if (itemList.has("Item_list")) {
                                        JSONArray itemListArray = itemList.getJSONArray("Item_list");
                                        String customer_id_from = itemList.getString("cus_id");
                                        for (int j = 0; j < itemListArray.length(); j++) {
                                            JSONObject item = itemListArray.getJSONObject(j);
                                            serialNo = serialNo + j;
                                            String item_name = item.getString("item_name");
                                            String qty = item.getString("qty");
                                            String rate = item.getString("price");
                                            String shortCode = item.getString("short_code");
                                            String notes = item.getString("notes");

                                            if (notes.isEmpty())
                                            {
                                                notes = "notes";
                                            }
                                            from = "mobile";
                                            double amt = Double.valueOf(qty);
                                            double price = Double.valueOf(rate);
                                            amt = amt * price;
                                            String amount = String.valueOf(amt);
                                            BillingModel billingModel = new BillingModel(serialNo, item_name, notes,qty, rate, amount, shortCode,customer_id_from,"mobile",false,false);
                                            modelObservableList.add(billingModel);
                                            /*Platform.runLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (selectedTable.equals(table))
                                                    {
                                                        tableBill.getItems().clear();
                                                        tableBill.setItems(modelObservableList);
                                                        tableBill.refresh();
                                                    }
                                                }
                                            });*/


                              /* tableBill.setItems(modelObservableList);
                               itemIdList.add(Integer.parseInt(selectedItem.getShort_code()));
                               setSubTotal();  */
                                        }
                                        customer_id = customer_id_from;
                                        tableListValue.put(table, modelObservableList);
                                        playMusic(table);
                                        addTreeView();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    });

                }else //already contains a table
                {

                            tableListValue.remove(table);
                            modelObservableList = FXCollections.observableArrayList();
                            try {
//                                if (itemList.getString("from").equals("mobile")) {

                                    if (itemList.has("Item_list")) {
                                        JSONArray itemListArray = itemList.getJSONArray("Item_list");
                                        String customer_id_from = itemList.getString("cus_id");
                                        serialNo = 1;
                                        for (int j = 0; j < itemListArray.length(); j++) {
                                            JSONObject item = itemListArray.getJSONObject(j);
                                            serialNo = serialNo + j;
                                            String item_name = item.getString("item_name");
                                            String qty = item.getString("qty");
                                            String rate = item.getString("price");
                                            String shortCode = item.getString("short_code");
                                            String notes = item.getString("notes");
                                            if (notes.isEmpty())
                                            {
                                                notes = "notes";
                                            }
                                            double amt = Double.valueOf(qty);
                                            double price = Double.valueOf(rate);
                                            amt = amt * price;
                                            String amount = String.valueOf(amt);
                                            from = "mobile";
                                            BillingModel billingModel = new BillingModel(serialNo, item_name,notes, qty, rate, amount, shortCode,customer_id_from,from,false,false);
                                            modelObservableList.add(billingModel);
                                        }
                                        tableListValue.put(table, modelObservableList);
                                        customer_id = customer_id_from;
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                playMusic(table);

                                            }
                                        });

                                        if (selectedTable.equals(table))
                                        {
                                            tableBill.getItems().clear();
                                            tableBill.setItems(modelObservableList);
                                            tableBill.refresh();
                                            setSubTotal();
                                        }
                                    }

//                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public  void playMusic(String table)
    {
        InputStream music = null;

        try {
            music = new FileInputStream(new File("src/RestarantApp/sound/beep.wav"));
            AudioStream audio =new AudioStream(music);
            AudioPlayer.player.start(audio);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        Constants.showAlert(Alert.AlertType.INFORMATION,"From Table",null,"Got a Order From"+table);




    }


    @Override
    public void Networkchanged(boolean isConnected) {

//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
                FileInputStream input = null;

                if (isConnected) {

            isConnectedNetwork = isConnected;
            try {
                input = new FileInputStream("src/RestarantApp/images/online.png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (sqliteConnection != null) {
                int tableCount = sqliteConnection.tableCount();
                if (tableCount != 0) {
                    saveToServer();
                }
            }

        }else
        {
            isConnectedNetwork = isConnected;
            try {
                input = new FileInputStream("src/RestarantApp/images/offline.png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        Image image = new Image(input);
        imgConectionStatus.setImage(image);

//
//            }
//        });
    }

    private void saveToServer() {


        if (!alert.isShowing()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    alert.showAndWait();

                }
            });

            ArrayList<BillingSaveModel> billingSaveModels = sqliteConnection.getAllData();
            ArrayList<Integer> orderID = new ArrayList();
            for (int i = 0; i < billingSaveModels.size(); i++) {
                BillingSaveModel billingSaveModel = billingSaveModels.get(i);
                int order_id = billingSaveModel.getOreder_id();
                if (!orderID.contains(order_id)) {
                    orderID.add(order_id);
                }
            }

            for (int j = 0; j < orderID.size(); j++) {
                offlineModeList = FXCollections.observableArrayList();
                int order_id = orderID.get(j);
                for (int i = 0; i < billingSaveModels.size(); i++) {
                    BillingSaveModel billingSaveModel = billingSaveModels.get(i);
                    if (order_id == billingSaveModel.getOreder_id()) {
                        offlineModeList.add(billingSaveModel);
                    }
                }
                placedOrder("offline");
               int k = j;

               Platform.runLater(new Runnable() {
                   @Override
                   public void run() {

                       if (offlineModeList.size()-1 == k) {
                           alert.setResult(ButtonType.CANCEL);
                           alert.hide();
                           sqliteConnection.deletOrderTable();
                           sqliteConnection.deletOrderItemAountTable();
                           sqliteConnection.deletOrderItemTable();
                       }
                   }
               });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }


        }

    }

    public void sendToMobile(String selectedTable)
    {
        ObservableList<BillingModel> tempModelList = modelObservableList;
        System.out.println("item  size----->"+modelObservableList.size());
        ArrayList<ItemListRequestAndResponseModel.item_list> item_lists = billingItemDetails;
        String message;
        JSONObject json = new JSONObject();
        try {
        json.put("table", selectedTable);
        json.put("from", "Desktop");
        json.put("cus_id",customer_id);
        JSONArray itemArray = new JSONArray();
     for (int i= 0; i< tempModelList.size();i++)
     {
         BillingModel billingModel = tempModelList.get(i);
         for (int j=0 ; j < item_lists.size() ; j++)
         {
             ItemListRequestAndResponseModel.item_list item_list = item_lists.get(j);
             if (billingModel.getItem_id().equals(item_list.getShort_code()))
             {
                 JSONObject item = new JSONObject();
                 item.put("item_name",item_list.getItem_name());
                 item.put("qty",billingModel.getQuantity());
                 item.put("item_id",item_list.getItem_id());
                 item.put("price",billingModel.getRate());
                 item.put("short_code",item_list.getShort_code());
                 item.put("des",item_list.getDescription());
                 item.put("notes", billingModel.getNotes());
                 item.put("image", Constants.ITEM_BASE_URL + item_list.getImage());
                 itemArray.put(item);
             }
         }
     }
            json.put("Item_list", itemArray);

            message = json.toString();
            sendMsg(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
      /*  String message;
        JSONObject json = new JSONObject();
        try {

            for (int i=0;i<modelObservableList.size();i++)
            {
                BillingModel  item_list = modelObservableList.get(i);
                JSONObject item = new JSONObject();
                item.put("item_name",item_list.getItem_name());
                item.put("qty",item_list.getQuantity());
                item.put("item_id",item_list.getItem_id());
                item.put("price",item_list.getRate());
                item.put("short_code",item_list.getItem_id());
                itemArray.put(item);
            }
            json.put("Item_list", itemArray);

            message = json.toString();

            sendMsg(message);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    public void sendMsg(String msg) {

        HashMap headers = new HashMap();
        headers.put("content-type", "text/plain");
        if (RabbitmqServer.client!= null) {
            RabbitmqServer.client.send("/topic/resturantApp", msg, headers);
            RabbitmqServer.client.addErrorListener(new Listener() {

                @Override
                public void message(Map headers, String body) {

                }
            });
        }else
        {
        }
    }

    public void btnCloseOrder(MouseEvent mouseEvent) {
        if (isConnectedNetwork)
        {
            BillingModel billingModel = modelObservableList.get(0);
            if (!billingModel.isPlacedSale()) {
                placedOrder("online");
            }else
            {

//                listTableList.getItems().remove(listTableList.getSelectionModel().getSelectedItem());
                modelObservableList.clear();
                modelObservableList =FXCollections.observableArrayList();
                FileInputStream input = null;

                try {
                    input = new FileInputStream("src/RestarantApp/images/placeorder.png");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Image image = new Image(input);
                imgPlaceOrder.setImage(image);
                btnAddItem.setOnMouseClicked(this::btnAddItem);
                selectedTable = null;

            }
        }else
        {


            BillingModel billingModel = modelObservableList.get(0);
            if (!billingModel.isPlacedSale()) {
                insertIntoDb();
            }else
            {

//                listTableList.getItems().remove(listTableList.getSelectionModel().getSelectedItem());
                modelObservableList.clear();
                modelObservableList =FXCollections.observableArrayList();
                FileInputStream input = null;

                try {
                    input = new FileInputStream("src/RestarantApp/images/placeorder.png");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Image image = new Image(input);
                imgPlaceOrder.setImage(image);
                btnAddItem.setOnMouseClicked(this::btnAddItem);
                selectedTable = null;

            }

        }



      /*  if (isPlacedOrder)
        {
            placedOrder();
        }*/
    }

    private void placedOrder(String from) {
        itemLoadProgres.setVisible(true);
        JSONObject jsonObject = new JSONObject();
        retrofitService = RetrofitClient.getClient().create(APIService.class);

        if (from.equals("offline"))
        {
            ArrayList listArray = new ArrayList();
            for (int j = 0; j < offlineModeList.size(); j++) {
                BillingSaveModel billingSaveModel = offlineModeList.get(j);
                String billItem = billingSaveModel.getItem_id() + ":" + billingSaveModel.getQty();
                listArray.add(billItem);
            }
            String list = listArray.toString();
            list = list.substring(1, list.length() - 1);
            list = "{" + list + "}";

            BillingSaveModel billingSaveModel = offlineModeList.get(0);
            JSONObject itemList = null;
            try {
                itemList = new JSONObject(list);
                jsonObject.put("customer_id",loginRequestAndResponse.getCustomer_id());
                jsonObject.put("customer_name",loginRequestAndResponse.getName());
                jsonObject.put("customer_phone",loginRequestAndResponse.getMobile_num());
                jsonObject.put("customer_email",loginRequestAndResponse.getCustomer_email());
                jsonObject.put("customer_address",loginRequestAndResponse.getCustomer_address());
                jsonObject.put("item_list",itemList);
                jsonObject.put("net_amount",billingSaveModel.getNet_amt());
                jsonObject.put("tax_amount",billingSaveModel.getTax_amt());
                jsonObject.put("gross_amount",billingSaveModel.getGross_amt());
                jsonObject.put("table_no",billingSaveModel.getTable_no());
                jsonObject.put("payment_method",billingSaveModel.getPayment_method());
                jsonObject.put("discount_amount",billingSaveModel.getDiscount_amt());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Call<RequestAndResponseModel> placeOrder = retrofitService.placeOrder(jsonObject);
            placeOrder.enqueue(new Callback<RequestAndResponseModel>() {
                @Override
                public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                    itemLoadProgres.setVisible(false);
                    if (response.isSuccessful())
                    {
                        RequestAndResponseModel requestAndResponseModel = response.body();



                    }
                }

                @Override
                public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

                }
            });
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText("Are you want place this order ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){


            String payment_method = String.valueOf(comboPaymentMethod.getSelectionModel().getSelectedItem());
            String method = "";
            if (payment_method.equals("Cash")) {
                method = "1";
            } else if (payment_method.equals("Card")) {
                method = "2";
            } else if (payment_method.equals("Paytm")) {
                method = "3";
            } else if (payment_method.equals("Others")) {
                method = "4";
            }
            ArrayList listArray = new ArrayList();
            for (int j = 0; j < modelObservableList.size(); j++) {
                BillingModel billingModel = modelObservableList.get(j);
                String billItem = billingModel.getItem_id() + ":" + billingModel.getQuantity();
                listArray.add(billItem);
            }
            String list = listArray.toString();
            list = list.substring(1, list.length() - 1);
            list = "{" + list + "}";
            JSONObject itemList = null;

            String getFrom;
            if (from.equals("parcel")) {
                getFrom = "Parcel";
            } else {
                getFrom = selectedTable;
            }
            double total_amount = Double.parseDouble(txtTotal.getText());
            double gross_amount = Double.parseDouble(txtFileldGross.getText());
            double tax_amount = total_amount - gross_amount;
            String tax = String.valueOf(tax_amount);
            String dis_amount = " ";
            if (!txtFiledDiscountAmount.getText().isEmpty())
                dis_amount = txtFiledDiscountAmount.getText();
            try {
                itemList = new JSONObject(list);
                jsonObject.put("customer_id", loginRequestAndResponse.getCustomer_id());
                jsonObject.put("net_amount", txtNetAmount.getText().trim());
                jsonObject.put("tax_amount", tax);
                jsonObject.put("gross_amount", txtFileldGross.getText().trim());
                jsonObject.put("payment_method", method);
                jsonObject.put("item_list", itemList);
                jsonObject.put("discount_amount", dis_amount);
                jsonObject.put("table_no", getFrom);
            } catch (JSONException e) {
                e.printStackTrace();
            }
                Call<RequestAndResponseModel> placeOrder = retrofitService.placeOrder(jsonObject);
                placeOrder.enqueue(new Callback<RequestAndResponseModel>() {
                    @Override
                    public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {

                        if (response.isSuccessful())
                        {
                            RequestAndResponseModel requestAndResponseModel = response.body();
                            if (requestAndResponseModel.getSuccessCode().equals(Constants.Success))
                            {
                                for (int j = 0; j < modelObservableList.size() ; j++)
                                {
                                    BillingModel billingModel = modelObservableList.get(j);
                                    billingModel.setPlacedSale(true);
                                }

                                tableBill.setEditable(false);
                                final FileInputStream[] input = {null};

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

//                                try {

//                                    input[0] = new FileInputStream("/RestarantApp/images/closesale.png");

                                    Image image2 = new Image(BillingController.class.getResourceAsStream("/RestarantApp/images/closesale.png"));
                                    imgPlaceOrder.setImage(image2);


                                        try {
                                            fileInputStream = new FileInputStream(sheetFile);
                                            workbook = new XSSFWorkbook(fileInputStream);
                                            spreadsheet =workbook.getSheet(sheetName);
                                            for (int i= 0; i < kotList.size() ; i ++) {
                                                Row selectedRow = UtilsClass.findCell(spreadsheet, kotList.get(i));
                                                ArrayList<Row> getRow = UtilsClass.findCellsInarray(spreadsheet,kotList.get(i));
                                                for (int j = 0 ; j<getRow.size();j++)
                                                {
                                                    Row row = getRow.get(j);
                                                    row.createCell(4).setCellValue(requestAndResponseModel.getOrder_id());
                                                    UtilsClass.makeDefaultText(workbook,row);
                                                }

                                            }
                                            fileInputStream.close();
                                                FileOutputStream outFile = new FileOutputStream(sheetFile);
                                            workbook.write(outFile);
                                            outFile.close();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


//                                    jfxSnackbar.show("Image show",5000);
                              /*  } catch (FileNotFoundException e) {
                                    e.printStackT1race();
                                    jfxSnackbar.show("Image not show",5000);
                                }*/
                                /*Image image = new Image(input[0]);
                                imgPlaceOrder.setImage(image);*/
                                btnAddItem.setOnMouseClicked(null);
                                itemLoadProgres.setVisible(false);
                                kotList = new ArrayList<>();
                                    }
                                });

                            }



                        }
                    }

                    @Override
                    public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

                    }
                });

            } else {
                alert.close();
            }
        }

    }


    public void imgAddNewOrder(MouseEvent mouseEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/Billing/add_new_item.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AddNewItemController addNewItemController = new AddNewItemController();
        addNewItemController.setAddItemListener(addNewItemListener);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root1));
        stage.show();
    }


    public void insertIntoDb()
    {
        if (sqliteConnection != null)
        {
          int tableCount =  sqliteConnection.tableCount();
          LoginRequestAndResponse loginRequestAndResponse = LoginRequestAndResponse.getInstance();
            if (tableCount == 0) {
                sqliteConnection.insertDataToOrderMaster(1001, customer_id,tableCount,loginRequestAndResponse.getMobile_num(),loginRequestAndResponse.getName(),loginRequestAndResponse.getCustomer_email(),loginRequestAndResponse.getCustomer_address());
            }else
            {
                sqliteConnection.insertDataToOrderMaster(1001, customer_id,tableCount,loginRequestAndResponse.getMobile_num(),loginRequestAndResponse.getName(),loginRequestAndResponse.getCustomer_email(),loginRequestAndResponse.getCustomer_address());
            }
            for (int i =0 ; i < modelObservableList.size() ; i++)
            {
                BillingModel billingModel = modelObservableList.get(i);
                sqliteConnection.insertDataToOrderITEMMaster(sqliteConnection.getLastRow(),billingModel.getItem_id(),billingModel.getQuantity());
            }
            String payment_method = String.valueOf(comboPaymentMethod.getSelectionModel().getSelectedItem());
            String method = "";
            if (payment_method.equals("Cash"))
            {
                method = "1";
            }else if (payment_method.equals("Card"))
            {
                method = "2";
            }else if (payment_method.equals("Paytm"))
            {
                method = "3";
            }else if (payment_method.equals("Others"))
            {
                method = "4";
            }
            String getFrom;
            if (from.equals("parcel"))
            {
                getFrom = "Parcel";
            }else
            {
                getFrom = selectedTable;
            }
            double total_amount = Double.parseDouble(txtTotal.getText().trim());
            double gross_amount = Double.parseDouble(txtFileldGross.getText().trim());
            double tax_amount = total_amount - gross_amount;
            String tax = String.valueOf(tax_amount);
            String dis_amount = " ";
            if (!txtFiledDiscountAmount.getText().isEmpty())
                dis_amount = txtFiledDiscountAmount.getText();


            sqliteConnection.insertDataToOrderAmountMaster(sqliteConnection.getLastRow(),getFrom,txtNetAmount.getText().trim(),tax,txtFileldGross.getText().trim(),dis_amount,method);

            for (int j = 0; j < modelObservableList.size() ; j++)
            {
                BillingModel billingModel = modelObservableList.get(j);
                billingModel.setPlacedSale(true);
            }

            tableBill.setEditable(false);
            FileInputStream input = null;

            try {
                input = new FileInputStream("src/RestarantApp/images/closesale.png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Image image = new Image(input);
            imgPlaceOrder.setImage(image);
            btnAddItem.setOnMouseClicked(null);


        }

    }

    public void btnPrintKot(MouseEvent mouseEvent) {

        printKot();


    }


    public void printKot()
    {

        int lastKot_number = sqliteConnection.getLastKOTNumber();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        //get current date time with Date()
        Date date = new Date();
        Date time = new Date();
        String currentDate = dateFormat.format(date);
        String currentTime = timeFormat.format(time);

        DateFormat dateFormat_1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date_1 = new Date();
        if (kotLastDate == null)
        {

            sqliteConnection.insertKot(100,dateFormat_1.format(date_1));
            kotLastDate = sqliteConnection.getLastKOTDATE();

        }else
        {
            String dateStart =sqliteConnection.getLastKOTDATE();
            String dateStop = dateFormat_1.format(date_1);




        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


            Calendar cal = Calendar.getInstance();

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) ;
            int currDate = cal.get(Calendar.DATE);
            cal.set(year,month,currDate,6,0,0);

            Date setTime = cal.getTime();

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            DateTime dt1 = new DateTime(setTime);
            DateTime dt2 = new DateTime(d2);

            int get_hour = Hours.hoursBetween(dt2, dt1).getHours() % 24 ;

            if (get_hour < 24 || get_hour == 0)
            {

                sqliteConnection.insertKot(lastKot_number+1,dateFormat_1.format(date_1));

            }else
            {
                sqliteConnection.deleteKotMaster();
                sqliteConnection.insertKot(100,dateFormat_1.format(date_1));
                kotLastDate = sqliteConnection.getLastKOTDATE();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        }

        String header =
                "       **** Prawn And Crab ****       \n\n"
               +"                  KOT                 \n\n"
               +" KOT No:"+String.valueOf(lastKot_number +1)+"                Date:"+currentDate+"\n"
               +" Table No:"+selectedTable+"          Time:"+currentTime +"\n\n";


        String  listItem = "            List Of Items            \n"
                          +"            -------------             \n";

        String item_header = "  S.No         Item Name        Qty\n\n";
        final String[] item = {""};
        final int[] totalQty = {0};
        final int[] ssNo = {0};

        if (modelObservableList.size() != 0)
        {

            if (!getSendKot.containsKey(selectedTable))
            {
                getSendKot.put(selectedTable,true);
                billingSaveModel.setGetSetKot(getSendKot);
            }
            for (int i = 0; i<checkBoxIndex.size();i++)
            {
                int index = Integer.parseInt(checkBoxIndex.get(i));
                BillingModel billingModel = modelObservableList.get(index);
                String item_name = billingModel.getItem_name();
                String qty = billingModel.getQuantity();
                String notes = billingModel.getNotes();

                if (billingModel.isSendKot())
                {
                    ssNo[0] = ssNo[0] + 1;
                    totalQty[0] = totalQty[0] + Integer.parseInt(qty);
                    item[0] = item[0] +"   "+ ssNo[0] + "        "+item_name + "          "+qty+"\n";
                    if (!notes.equals("notes"))
                    {
                        item[0] =item[0] + "           [ "+notes+" ]\n";
                    }
                    billingModel.setSendKot(true);
                    billingModel.setSelected(false);
                    kotList.add(String.valueOf(lastKot_number+1));
                    billingModel.setGetKotList(kotList);
                    billingModel.setKot_no(lastKot_number+1);

                    tableNameList.add(selectedTable);
                    kotNoList.add(String.valueOf(lastKot_number+1));

                }


            }
            checkBoxIndex = new ArrayList<>();
            cb.setSelected(false);
            tableBill.refresh();
        }




        addTreeView();
        String line = " ------------------------------------\n";
        String total_item = "                  Total Item(s)  "+ totalQty[0];
        header = header+listItem+item_header+ item[0] +line+total_item+"\n\n\n\n\n\n\n";


        tableBill.setRowFactory(tv -> new TableRow<BillingModel>() {
            @Override
            public void updateItem(BillingModel item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (item.getKot_no() != 0) {


                    setStyle("-fx-background-color: #ffcad0;");


                } else {
                    setStyle("");
                }
            }
        });

        generateXsl();

        /*ChoiceDialog dialog = new ChoiceDialog(Printer.getDefaultPrinter(), Printer.getAllPrinters());
        dialog.setHeaderText("Choose the printer!");
        dialog.setContentText("Choose a printer from available printers");
        dialog.setTitle("Printer Choice");
        PrinterService printerService = new PrinterService();

        Optional<Printer> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            Printer printer = opt.get();
            //print some stuff
            printerService.printString(printer.getName(),header);
            // cut that paper!
            byte[] cutP = new byte[] { 0x1d, 'V', 1 };

            printerService.printBytes("RP3150 STAR(U) 1", cutP);


            tableBill.setRowFactory(tv -> new TableRow<BillingModel>() {
                @Override
                public void updateItem(BillingModel item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if (item == null) {
                        setStyle("");
                    } else if (item.isSendKot()) {


                            setStyle("-fx-background-color: #ffcad0;");


                    } else {
                        setStyle("");
                    }
                }
            });

        }else
        {

        }*/


    }



    public void printBill()
    {
        ArrayList<String> getPrintItem = kotHashMapList.get(selectedRoot);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        //get current date time with Date()
        Date date = new Date();
        Date time = new Date();
        String currentDate = dateFormat.format(date);
        String currentTime = timeFormat.format(time);
        String item = "";
        int totalQty = 0;
        int ssNo = 0;
        String header =
                "       **** Prawn And Crab ****       \n"
                        +"       R.S.No 92/7, Plot No.14 & 15, " +
                        "        Sri Gokulam Nagar,Korukkumedu Road, " +
                        "        Thavalakuppam, Pondicherry-605007.\n\n"
                        +" GST NO:34GWGPS3087B1ZE                  \n"
                        +" Date:"+currentDate+"       Time:"+currentTime+"\n"
                        +" Table No:"+selectedTable+                "\n\n";
//                        +" Table No:"+selectedTable+"       KOT:"+String.join(",",s)+         "\n\n";


        String  listItem = "            List of Items            \n"
                +"            -------------             \n";

        String item_header =String.format("%2s %-17s %4s %6s %7s\n","S.No","Item Name","Qty","Rate","Amount");
        String line2 = "------------------------------------------\n";
        ArrayList<String> itemId =new ArrayList<>();

        ObservableList<BillingModel> tempBillingList = modelObservableList;
      /*  for (int i = 0; i < tempBillingList.size() ; i++)
        {
            BillingModel billingModel = tempBillingList.get(i);

            int Qty = 0;
            if (!itemId.contains(billingModel.getItem_id())) {
                itemId.add(billingModel.getItem_id());
                for (int j = 0; j < tempBillingList.size(); j++) {
                    BillingModel billingModel1 = tempBillingList.get(j);
                    if (billingModel.getItem_id().equals(billingModel1.getItem_id())) {

                        Qty = Qty + Integer.parseInt(billingModel.getQuantity());
                    }
                }
                System.out.println("qty---->" + Qty);
            }

        }*/

        for (int i = 0; i<modelObservableList.size();i++)
        {

            BillingModel billingModel = modelObservableList.get(i);
            if (getPrintItem.contains(String.valueOf(billingModel.getKot_no()))){
            String item_name = billingModel.getItem_name();
            String qty = billingModel.getQuantity();
                ssNo = ssNo + 1;
                totalQty = totalQty + Integer.parseInt(qty);
                String rate = billingModel.getRate();
                String amount = billingModel.getAmount();
                int Qty = 0;
            if (!itemId.contains(billingModel.getItem_id())) {
                itemId.add(billingModel.getItem_id());
                for (int j = 0; j < modelObservableList.size(); j++) {
                    BillingModel billingModel1 = tempBillingList.get(j);
                    if (billingModel.getItem_id().equals(billingModel1.getItem_id())) {

                        Qty = Qty + Integer.parseInt(billingModel1.getQuantity());
                    }
                }


                double rateValue = Double.parseDouble(billingModel.getRate());
                double amt1 = rateValue * Qty;
                item = item + String.format("%2s %-16s %5s %7s %5.2f\n", ssNo, item_name, Qty, rate, amt1);
                System.out.println(item);
             }

            }


//                item = item +" "+ ssNo +"   "+item_name + "       "+qty+"    "+rate+"    "+amount+"\n";
//                billingModel.setSendKot(true);



        }
        double gst = Double.parseDouble(txtFileldGross.getText());
        double tax_value = Double.parseDouble(tax_1);
        double gstValue = (tax_value/100);
        gstValue = gstValue * gst;


        double gst_1 = Double.parseDouble(txtFileldGross.getText());
        double tax_value_1 = Double.parseDouble(tax_2);
        double gstValue_1 = (tax_value_1/100);
        gstValue_1 = gstValue_1 * gst_1;

        String val = String.valueOf(gstValue);
        String val1 = String.valueOf(gstValue_1);

        double totalAmount = Double.parseDouble(txtTotalAmount.getText());
        double netAmount =Double.parseDouble( txtFileldGross.getText());
        double total = Double.parseDouble(txtTotal.getText());
        double discountAmount=0.0;
        if (!txtFiledDiscountAmount.getText().isEmpty()) {
             discountAmount = Double.parseDouble(txtFiledDiscountAmount.getText());
        }


        String line = "------------------------------------------\n";
        String subTotal=String.format("%32s : %6.2f\n","Sub Total",totalAmount);
        String percentage=String.format("%26s(%2s%%): %6.2f\n","Discount",txtFiledDiscount.getText(),discountAmount);
        String netTotal = String.format("%32s : %6.2f\n","Net Total",netAmount);
        String cgst = String.format("%32s : %6.2f\n",tax1,gstValue);
        String scgst = String.format("%32s : %6.2f\n",tax2,gstValue_1);
        String line1 = String.format("%40s\n","--------------------");
        String grantTotl = String.format("%32s : %6.2f\n\n","Grand Total",total);
       String thnk_you =  "       Thank You, Visit Again !!!         \n";
        if (!txtFiledDiscountAmount.getText().isEmpty()) {
            header = header + listItem + item_header +line2+ item + line + subTotal + percentage + netTotal+cgst+scgst+line1+grantTotl+thnk_you+"\n\n\n\n";
        }else
        {
            header = header + listItem + item_header + line2 +item + line + subTotal  + netTotal+cgst+scgst+line1+grantTotl+thnk_you+"\n\n\n\n";
        }


        System.out.println("kot list-------->"+header);
        ChoiceDialog dialog = new ChoiceDialog(Printer.getDefaultPrinter(), Printer.getAllPrinters());
        dialog.setHeaderText("Choose the printer!");
        dialog.setContentText("Choose a printer from available printers");
        dialog.setTitle("Printer Choice");
        PrinterService printerService = new PrinterService();

        Optional<Printer> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            Printer printer = opt.get();
            //print some stuff
            printerService.printString(printer.getName(),header);
            // cut that paper!
            byte[] cutP = new byte[] { 0x1d, 'V', 1 };

            printerService.printBytes("RP3150 STAR(U) 1", cutP);
        }

    }

    private void getTableList() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);

        Call<ItemListRequestAndResponseModel> call = retrofitClient.getTableList();
        call.enqueue(new Callback<ItemListRequestAndResponseModel>() {
            @Override
            public void onResponse(Call<ItemListRequestAndResponseModel> call, Response<ItemListRequestAndResponseModel> response) {
                if (response.isSuccessful()) {

                    ItemListRequestAndResponseModel itemListRequestAndResponseModel = response.body();

                    ArrayList getItemDetils = itemListRequestAndResponseModel.getList();
                    for (int i = 0; i < getItemDetils.size(); i++) {
                        ItemListRequestAndResponseModel.list list = (ItemListRequestAndResponseModel.list) getItemDetils.get(i);

                        if (list.getActive().equals("1"))
                        {
                            int status = Integer.parseInt(list.getActive());
                           if (sqliteConnection.checkDatabase(list.getName()) == 0)
                           {
                               sqliteConnection.insertTableData(list.getName(),status);
                           }else
                           {
                              int statusFromDb = sqliteConnection.getTableStatus(list.getName());
                              if (statusFromDb != status)
                              {
                                  sqliteConnection.updateTableStatus(list.getName(),status);
                              }
                           }
                        }

                    }


                }
            }

            @Override
            public void onFailure(Call<ItemListRequestAndResponseModel> call, Throwable throwable) {

            }
        });


    }

    public void btnprintBill(MouseEvent mouseEvent) {
        printBill();
    }


    public void imgRefreshItem(MouseEvent mouseEvent) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getData();
                getTableList();
                taxList();
            }
        });
    }

    public void btnLogOut(ActionEvent actionEvent) {
        Stage stage;
        stage=(Stage) btnLogout.getScene().getWindow();
        Parent   root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/RestarantApp/Login/Login.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Prawn And Crabs");
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        stage.setScene(new Scene(root, visualBounds.getWidth(), visualBounds.getHeight()));
        stage.show();

    }


    public void btnAddNewCustomer(MouseEvent mouseEvent) {

        LoginRequestAndResponse loginRequestAndResponse =LoginRequestAndResponse.getInstance();
        loginRequestAndResponse.setFromWhere(true);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/menuFxml/add_new_customer.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void generateXsl()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {


                String[] monthName = { "January", "February", "March", "April", "May", "June", "July",
                        "August", "September", "October", "November", "December" };
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = new Date();
                Calendar cl = Calendar. getInstance();
                cl.setTime(date);
                String month = monthName[cl.get(Calendar.MONTH)];
/*
                System.out.println("today is a "+cl.get(Calendar.WEEK_OF_MONTH) +"year" + cl.get(Calendar.YEAR) +"week of the month");
                System.out.println("Current Month is : " + month);
*/

                String file_name = month+cl.get(Calendar.YEAR)+"_"+cl.get(Calendar.WEEK_OF_MONTH)+"Week";


                sheetName = dateFormat.format(date);
                sheetFile = new File("d:\\prawn_crab\\"+file_name+".xlsx");
                boolean containsSheet = false;
                if (sheetFile.exists())
                {
                    try {

                        fileInputStream = new FileInputStream(sheetFile);
                        workbook = new XSSFWorkbook(fileInputStream);

                        int no_sheets = workbook.getNumberOfSheets();
                        for (int i= 0 ; i < no_sheets ; i ++)
                        {
                            String sheetName= workbook.getSheetName(i);
                            if (sheetName.equals(sheetName))
                            {
                                containsSheet = true;

                            }
                        }
                        if (containsSheet)
                        {
                            spreadsheet = workbook.getSheet(sheetName);
                        }else
                        {
                            spreadsheet = workbook.createSheet( sheetName);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (EmptyFileException e)
                    {
                        e.printStackTrace();
                    }
                }else
                {

                        workbook = new XSSFWorkbook();
                        spreadsheet = workbook.createSheet( sheetName);


                }

                Map < String, Object[] > item_info = new TreeMap < String, Object[] >();
                int rowCount = spreadsheet.getLastRowNum();

                if (!sheetFile.exists() || !containsSheet)
                    item_info.put( String.valueOf(rowCount), new Object[] {
                        "SHORT ID", "ITEM NAME" ,"QTY" ,"KOT NUMBER","ORDER NUMBER"});

              /*  for (int i = 0 ; i < modelObservableList.size() ;i++)
                {
                    BillingModel billingModel = modelObservableList.get(i);
                    if (billingModel.isSendKot() ) {
                        rowCount = rowCount + 1;
                        item_info.put(String.valueOf(rowCount), new Object[]{billingModel.getItem_id(), billingModel.getItem_name(),
                                billingModel.getQuantity(), String.valueOf(billingModel.getKot_no())});
                    }

                }*/

                for (int i = 0; i<checkBoxIndex.size();i++)
                {
                    int index = Integer.parseInt(checkBoxIndex.get(i));
                    BillingModel billingModel = modelObservableList.get(index);
                    if (billingModel.isSendKot() ) {
                        rowCount = rowCount + 1;
                        item_info.put(String.valueOf(rowCount), new Object[]{billingModel.getItem_id(), billingModel.getItem_name(),
                                billingModel.getQuantity(), String.valueOf(billingModel.getKot_no())});
                    }
                }

        //Iterate over data and write to sheet
        Set < String > keyid = item_info.keySet();
        int rowid = rowCount;
        for (String key : keyid) {

            row = spreadsheet.createRow(rowid++);

            Object [] objectArr = item_info.get(key);
            int cellid = 0;

            for (Object obj : objectArr){
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String)obj);
                if (key.equals("0")) {
                    UtilsClass.makeRowBold(workbook, row);
                }else
                {
                    UtilsClass.makeDefaultText(workbook,row);
                }
            }
        }


                for (int i=0; i<10; i++){
                    spreadsheet.autoSizeColumn(i);
                }


                try {
             out =  new FileOutputStream(sheetFile);
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


            }
        });
    }


    public void btnSearchHistory(MouseEvent mouseEvent) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/Billing/search.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root1));
        if (!stage.isShowing())
            stage.show();
    }
}
