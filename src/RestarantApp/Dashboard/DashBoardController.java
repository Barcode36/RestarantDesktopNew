package RestarantApp.Dashboard;

import RestarantApp.Main;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

import javafx.stage.Stage;

import java.io.IOException;

public class DashBoardController  {

    public static final ObservableList names =
            FXCollections.observableArrayList();

    @FXML
    TreeView lstMenu;
    @FXML
    AnchorPane subCategory,rootPane;
    String root = "Root",monthly_report ="Monthly Report",category ="Category",sub_category ="Sub Category",item = "Items",add_category = "Add Category",view_category ="View Category",variety ="Variety",add_sub_category = "Add Sub Category",view_sub_category ="View sub Category",add_varity = "Add variety",view_varity ="View variety"
            ,add_Item ="Add Item",list_item = "List Item",tax = "Tax",add_tax = "Add Tax",list_tax = "List Tax",combo = " Combo Items",add_combo = "Add Combo Items",
            combo_item = "Combo Details",table="Table",add_table="Add Table",list_table = "List Table",customer = "Customer",add_customer="Add Customer",view_customer = "View Customer";

    @FXML
    Button btnLogout;
    public void initialize() {

        TreeItem<String> treeItemRoot = new TreeItem<> (root);

        TreeItem<String> monthlyNode = new TreeItem<>(monthly_report);
        TreeItem<String> categoryNode = new TreeItem<>(category);
        TreeItem<String> sub_categoryNode = new TreeItem<>(sub_category);
        TreeItem<String> varityNode = new TreeItem<>(variety);
        TreeItem<String> itemNode = new TreeItem<>(item);
        TreeItem<String> taxNode = new TreeItem<>(tax);
        TreeItem<String> comboNode = new TreeItem<>(combo);
        TreeItem<String> tableNode = new TreeItem<>(table);
        TreeItem<String> customerNode = new TreeItem<>(customer);
        treeItemRoot.getChildren().addAll(monthlyNode, categoryNode,sub_categoryNode,varityNode,itemNode,taxNode,comboNode,tableNode,customerNode);

        TreeItem<String> nodeItemA1 = new TreeItem<>(add_category);
        TreeItem<String> nodeItemA2 = new TreeItem<>(view_category);
        categoryNode.getChildren().addAll(nodeItemA1, nodeItemA2);

        TreeItem<String> nodesubItemA1 = new TreeItem<>(add_sub_category);
        TreeItem<String> nodesubItemA2 = new TreeItem<>(view_sub_category);
        sub_categoryNode.getChildren().addAll(nodesubItemA1, nodesubItemA2);

        TreeItem<String> nodeAddVarity = new TreeItem<>(add_varity);
        TreeItem<String> nodeListVarity = new TreeItem<>(view_varity);
        varityNode.getChildren().addAll(nodeAddVarity, nodeListVarity);

        TreeItem<String> addItem = new TreeItem<>(add_Item);
        TreeItem<String> listItem = new TreeItem<>(list_item);
        itemNode.getChildren().addAll(addItem, listItem);

        TreeItem<String> addTax = new TreeItem<>(add_tax);
        TreeItem<String> listTax = new TreeItem<>(list_tax);
        taxNode.getChildren().addAll(addTax,listTax);

        TreeItem<String> addCombo = new TreeItem<>(add_combo);
        TreeItem<String> comboDetails = new TreeItem<>(combo_item);
        comboNode.getChildren().addAll(addCombo,comboDetails);

        TreeItem<String> addTable = new TreeItem<>(add_table);
        TreeItem<String> tableList = new TreeItem<>(list_table);
        tableNode.getChildren().addAll(addTable,tableList);

        TreeItem<String> addCustomer = new TreeItem<>(add_customer);
        TreeItem<String> viewCustomer = new TreeItem<>(view_customer);
        customerNode.getChildren().addAll(addCustomer,viewCustomer);

        treeItemRoot.setExpanded(true);
        lstMenu.setShowRoot(false);
        lstMenu.setRoot(treeItemRoot);

        lstMenu.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                System.out.println("Selected Text : " + selectedItem.getValue());
                if (selectedItem.getValue().equals(monthly_report))
                {

                }else if (selectedItem.getValue().equals(add_category))
                {
                    changePane("/RestarantApp/menuFxml/CategoryScene.fxml");
                }else if (selectedItem.getValue().equals(view_category))
                {
                    changePane("/RestarantApp/menuFxml/ViewCategoryScene.fxml");
                }else if (selectedItem.getValue().equals(add_Item))
                {
                    changePane("/RestarantApp/menuFxml/ItemScene.fxml");
                }else if (selectedItem.getValue().equals(list_item))
                {
                    changePane("/RestarantApp/menuFxml/item_list_controller.fxml");
                }else if (selectedItem.getValue().equals(add_tax))
                {
                    changePane("/RestarantApp/menuFxml/add_tax_scene.fxml");
                }else if (selectedItem.getValue().equals(list_tax))
                {
                    changePane("/RestarantApp/menuFxml/view_tax.fxml");
                }else if (selectedItem.getValue().equals(add_combo))
                {
                    changePane("/RestarantApp/menuFxml/comboscene.fxml");
                }else if (selectedItem.getValue().equals(combo_item))
                {
                    changePane("/RestarantApp/menuFxml/combo_list_scene.fxml");
                }else if (selectedItem.getValue().equals(add_table))
                {
                    changePane("/RestarantApp/menuFxml/add_table_scene.fxml");
                }else if (selectedItem.getValue().equals(list_table))
                {
                    changePane("/RestarantApp/menuFxml/table_list_scene.fxml");
                }else if (selectedItem.getValue().equals(add_varity))
                {
                    changePane("/RestarantApp/menuFxml/varietyscene.fxml");
                }else if (selectedItem.getValue().equals(view_varity))
                {
                    changePane("/RestarantApp/menuFxml/variety_list_controller.fxml");
                }else if (selectedItem.getValue().equals(add_customer))
                {
                    changePane("/RestarantApp/menuFxml/add_new_customer.fxml");
                }else if (selectedItem.getValue().equals(view_customer))
                {
                    changePane("/RestarantApp/menuFxml/customer_list_scene.fxml");
                }else if (selectedItem.getValue().equals(add_sub_category))
                {
                    changePane("/RestarantApp/menuFxml/subcatagoryscene.fxml");
                }else if (selectedItem.getValue().equals(view_sub_category))
                {
                    changePane("/RestarantApp/menuFxml/view_sub_catagory_scene.fxml");
                }
            }
        });

       /* names.addAll(
                "Monthly Report", "Category", "Items","Category List"
        );
        lstMenu.setItems(names);
        lstMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int menu = lstMenu.getSelectionModel().getSelectedIndex();
                if (menu == 1) {
                    changePane("/RestarantApp/menuFxml/CategoryScene.fxml");
                }else if (menu == 3)
                {
                    changePane("/RestarantApp/menuFxml/ViewCategoryScene.fxml");
                }

            }
        });*/
    }



    public void changePane(String urlPath)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {


        FXMLLoader loader = new FXMLLoader(getClass().getResource(urlPath));
        StackPane cmdPane = null;
        try {
            cmdPane = (StackPane) loader.load();
            cmdPane.setAlignment(Pos.CENTER);
            cmdPane.setPrefWidth(subCategory.getWidth());
            cmdPane.setPrefHeight(subCategory.getHeight());


        } catch (IOException e) {
            e.printStackTrace();
        }
        subCategory.getChildren().setAll(cmdPane);
        System.out.println(subCategory.getWidth());
        System.out.println(subCategory.getHeight());
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
}
