/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MVC.Controllers;

import POO.Client;
import POO.Poste;
import POO.SMS;
import Utilities.ComponentHandlers.ComponentsHolder;
import Utilities.DataHandlers.DataHolder;
import Utilities.Workers.GSMModemHandler;
import Utilities.Workers.SMSPicker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * FXML Controller class
 *
 * @author MSSM
 */
public class MainStageController implements Initializable {
    
    @FXML private JFXHamburger newClientHambuger;
    @FXML private JFXDrawer newClientDrawer;
    @FXML private JFXComboBox<String> gsmPortComboBox;
    @FXML private JFXTextField ndtTextField,newClientNameTextField,newClientTelTextField;
    @FXML private JFXComboBox<Poste.EtatPoste> postStateComboBox;
    @FXML private TableView<Client> clientsTableView;
    @FXML private JFXButton refreshButton,onOffButton;
    
    @FXML
    private void startApplication(){
        try {
            if(!DataHolder.APPLICATION_RUNNING.getValue()){
                this.updateComponents(true);
                
                DataHolder.LAST_POLL_TIME.setValue(0);
                
                if(DataHolder.POSTE.getNdtPoste() < 1)
                    throw new NumberFormatException();
            
                (new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            DataHolder.MODEM_GSM = new GSMModemHandler(gsmPortComboBox.getSelectionModel()
                                                        .getSelectedItem());
                            DataHolder.SMS_PICKER = new SMSPicker(DataHolder.APPLICATION_FREQUENCY,DataHolder.MODEM_GSM);
                            DataHolder.SMS_PICKER.start();
                        } 
                        catch (SerialPortException ex) {
                            ex.printStackTrace();
                            
                            Platform.runLater(new Runnable(){
                                @Override
                                public void run() { 
                                    Alert alert = new Alert(AlertType.ERROR);
                                          alert.setTitle("Erreur...");
                                          alert.setHeaderText("Erreur d'initialisation du port GSM");
                                          alert.setContentText("Verifiez que ce port n'est pas utilisé par une autre resource ou un autre programme !");
                                          alert.showAndWait();
                                          
                                    updateComponents(false);
                                }
                            });
                        }
                    }})).start();
            }
            else {
                
                (new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            DataHolder.SMS_PICKER.stopWorking();
                            DataHolder.MODEM_GSM.closePort();
                            
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    ndtTextField.setText(DataHolder.POSTE.getNdtPoste() + "");
                                    updateComponents(false);
                                }
                            });
                        } 
                        catch (SerialPortException ex) {
                            ex.printStackTrace();
                            
                            Platform.runLater(new Runnable(){
                                @Override
                                public void run() {
                                    Alert alert = new Alert(AlertType.ERROR);
                                          alert.setTitle("Erreur...");
                                          alert.setHeaderText("Erreur de fermeture du port GSM");
                                          alert.setContentText("Veuillez réessayer !");
                                          alert.showAndWait();
                                    
                                    updateComponents(false);
                                }
                            });
                        }
                    }
                })).start();
            }
        }
        catch(NumberFormatException ex){
            ex.printStackTrace();
            
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Erreur...");
                  alert.setHeaderText("Erreur de configuration");
                  alert.setContentText("Veuillez entrer un nombre valide dans le champs de saisi du numero du dernier ticket");
                  alert.showAndWait();
            
            updateComponents(false);
        }
    }
    
    @FXML
    private void addNewClient(){
        try{
            String nom = this.newClientNameTextField.getText();
            String numTel = this.newClientTelTextField.getText();
            
            if(nom == null || nom.equals(""))
                throw new NullPointerException();
            
            Client client = new Client(nom, numTel, DataHolder.POSTE.getNdtPoste(), 
                LocalDate.now(), LocalTime.now());

            DataHolder.POSTE.incrementNdtPoste();
            DataHolder.POSTE.addClient(client);
            DataHolder.SESSION_FACTORY_HANDLER.insertEntities(client);
            DataHolder.SESSION_FACTORY_HANDLER.updateEntities(DataHolder.POSTE);
            
            this.newClientNameTextField.setText("");
            this.newClientTelTextField.setText("");
        }
        catch(NullPointerException ex){
            ex.printStackTrace();
            
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Erreur...");
                  alert.setHeaderText("Erreur d'ajout d'un nouveau client");
                  alert.setContentText("Veuillez réessayer en entrant un nom !");
                  alert.showAndWait();
        }
    }
    
    @FXML
    private void defilerClient(){
        if(!DataHolder.POSTE.isFileAttendeEmpty()){
            long t = System.currentTimeMillis();
            long deltaT = t - DataHolder.LAST_POLL_TIME.getValue();
        
            if(/*deltaT >= DataHolder.POLLING_INTERVAL.getValue()*/ true){
                Alert alert = new Alert(AlertType.CONFIRMATION);
                      alert.setTitle("Confirmation de defilement");
                      alert.setHeaderText("Defilement d'un client:");
                      alert.setContentText("Voulez vous vraiment défiler un client ?");
                      alert.showAndWait();
                  
                if(alert.getResult() == ButtonType.OK){
                    DataHolder.POSTE.pollClient();
                    DataHolder.POSTE.incrementNtsPoste();
                    DataHolder.LAST_POLL_TIME.setValue(t);
                    DataHolder.SESSION_FACTORY_HANDLER.updateEntities(DataHolder.POSTE);
                    
                    if(DataHolder.POSTE.getFileAttente().size() >= DataHolder.NOTIFICATION_DELIMITER)
                        this.notifyClient();
                }
            }
            else {
                Alert alert = new Alert(AlertType.ERROR);
                      alert.setTitle("Erreur...");
                      alert.setHeaderText("Erreur lors du défilement");
                      alert.setContentText("Vous devez attendre " + ((DataHolder.POLLING_INTERVAL.getValue() - deltaT) / 1000) + " secondes avant de pouvoir re défiler un client.");
                      alert.showAndWait();
            }
        }
    }
    
    @FXML
    private void refreshGsmPortList(){
        this.gsmPortComboBox.getItems().clear();
        this.gsmPortComboBox.getItems().addAll(SerialPortList.getPortNames());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.initPostState();
        this.initComponents();
        this.initClientsTable();
        this.initHamburger();
        this.initTextFields();
        this.loadUnpassedClients();
    }
    
    private void initPostState(){
        List<Poste> posteTodayStates = (List<Poste>) (Object) DataHolder.SESSION_FACTORY_HANDLER.getListOfEntities("from Poste where stateDay = ?", LocalDate.now());
        
        if(posteTodayStates.isEmpty()){
            DataHolder.POSTE = new Poste();
            DataHolder.SESSION_FACTORY_HANDLER.insertEntities(DataHolder.POSTE);
        }
        else DataHolder.POSTE = posteTodayStates.get(0);
    }
    
    private void loadUnpassedClients(){
        List<Client> remainingClients = (List<Client>) (Object)
                DataHolder.SESSION_FACTORY_HANDLER.getListOfEntities("from Client where dateClient = ? and ticketClient >= ?", LocalDate.now(), DataHolder.POSTE.getNtsPoste());
        for(Client client: remainingClients)
            DataHolder.POSTE.addClient(client);
    }
    
    private void initComponents(){
        this.gsmPortComboBox.getItems().addAll(SerialPortList.getPortNames());
        DataHolder.APPLICATION_RUNNING.bind(
                this.gsmPortComboBox.disableProperty());
        
        this.postStateComboBox.getItems().addAll(Poste.EtatPoste.values());
        this.postStateComboBox.getSelectionModel().select(0);
        DataHolder.POSTE.etatPosteProperty().bind(
                this.postStateComboBox.getSelectionModel().selectedItemProperty());
    }
    
    private void updateComponents(boolean update){
        this.gsmPortComboBox.setDisable(update);
        this.ndtTextField.setDisable(update);
        this.refreshButton.setDisable(update);
        
        if(update)
            ((ImageView)(this.onOffButton.getGraphic())).setImage(new Image(
                    getClass().getResource("/MVC/Fxmls/Images/stopApp.png").toString()));
        else 
            ((ImageView)(this.onOffButton.getGraphic())).setImage(new Image(
                    getClass().getResource("/MVC/Fxmls/Images/startApp.png").toString()));
    }
    
    private void initClientsTable(){
        ((TableColumn<Client,Integer>)(this.clientsTableView.getColumns().get(0))).setCellValueFactory(
            cellData -> cellData.getValue().numeroTicketProperty().asObject());
        ((TableColumn<Client,String>)(this.clientsTableView.getColumns().get(1))).setCellValueFactory(
            cellData -> cellData.getValue().nomProperty());
        ((TableColumn<Client,String>)(this.clientsTableView.getColumns().get(2))).setCellValueFactory(
            cellData -> cellData.getValue().numeroTelephoneProperty());
        
        this.clientsTableView.setItems((ObservableList<Client>) DataHolder.POSTE.getFileAttente());
    }
    
    private void initHamburger(){
        HamburgerSlideCloseTransition hamburgerAnimator = new HamburgerSlideCloseTransition(this.newClientHambuger);
                                      hamburgerAnimator.setRate(-1);

        this.newClientHambuger.setAnimation(hamburgerAnimator);
        this.newClientHambuger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)-> {
            this.newClientHambuger.getAnimation().setRate(this.newClientHambuger.getAnimation().getRate() * -1);
            this.newClientHambuger.getAnimation().play();
            if(this.newClientDrawer.isShown())
                this.newClientDrawer.close();
            else this.newClientDrawer.open();
        });
    }
    
    private void initTextFields(){
        this.newClientNameTextField.setOnKeyReleased((event) -> {
            if(event.getCode() == KeyCode.ENTER)
                addNewClient();
        });
        this.newClientTelTextField.setOnKeyReleased((event) -> {
            if(event.getCode() == KeyCode.ENTER)
                addNewClient();
        });
        ComponentsHolder.NDT_TEXT_FIELD = this.ndtTextField;
        ComponentsHolder.NDT_TEXT_FIELD.setText("" + DataHolder.POSTE.getNdtPoste());
    }
    
    private synchronized void notifyClient(){
        Client notifiedClient = DataHolder.POSTE.getFileAttente()
                        .get(DataHolder.NOTIFICATION_DELIMITER - 1);
        SMS notification = new SMS(notifiedClient.getNumeroTelephoneClient(),"Il ne reste plu que "
                        + DataHolder.NOTIFICATION_DELIMITER + " personnes avant votre tour !");
        DataHolder.MODEM_GSM.sendMessage(notification);
    }
}
