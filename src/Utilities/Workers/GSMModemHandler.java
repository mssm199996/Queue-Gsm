package Utilities.Workers;

import MVC.Controllers.MainStageController;
import POO.SMS;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class GSMModemHandler extends SerialPort implements SerialPortEventListener{
    
    private static final String
            COMMAND_REMISE_A_ZERO = "ATZ",
            COMMAND_SMS_MODE_TEXT = "AT+CMGF=1",
            COMMAND_DETAILED_ERRORS = "AT+CMEE=1",
            COMMAND_SET_UP_MEMORIES = "AT+CPMS=\"MT\",\"MT\",\"MT\"",
            
            COMMAND_LIST_SUPPORTED_STORAGE_MODES = "AT+CPMS=?",
            
            COMMAND_ENVOIE_SMS = "AT+CMGS=",
            
            COMMAND_GET_ALL_SMS = "AT+CMGL=\"ALL\"",
            COMMAND_GET_NEW_SMS = "AT+CMGL=\"REC UNREAD\"",
            
            COMMAND_DELETE_ALL_MESSAGES = "AT+CMGD=0[,4]",
            COMMAND_DELETE_READ_MESSAGES = "AT+CMGD=0[,1]";
            
    private SMSPicker smsPicker = null;
    private String lastResponse = "";
    
    public GSMModemHandler(String port) throws SerialPortException{
        super(port);        
        this.openPort();
        this.setParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
        this.addEventListener(this);
        this.startGsm();    
    }
    
    public synchronized void startGsm() throws SerialPortException{
        this.writeString(GSMModemHandler.COMMAND_REMISE_A_ZERO + "\r\n");
        this.writeString(GSMModemHandler.COMMAND_SMS_MODE_TEXT + "\r\n");
        this.writeString(GSMModemHandler.COMMAND_DETAILED_ERRORS + "\r\n");
        this.writeString(GSMModemHandler.COMMAND_SET_UP_MEMORIES + "\r\n");
    }
    
    public synchronized void sendMessage(SMS sms){
        try{
            if(this.isOpened()){ 
                this.writeString(GSMModemHandler.COMMAND_ENVOIE_SMS + "\"" + sms.getCorrespondantSms() + "\"\r\n");
                this.writeString(sms.getContenuSms() + '\032');
            }
        }
        catch(SerialPortException exp){
            exp.printStackTrace();
        }
    }
    
    public synchronized void readAllMessages(){
        try{
            if(this.isOpened())
                this.writeString(GSMModemHandler.COMMAND_GET_ALL_SMS + "\r\n");
            
        }
        catch(SerialPortException exp){
            exp.printStackTrace();
        }
    }
    
    public synchronized void readUnreadMessages(){
        try{
            if(this.isOpened())
                this.writeString(GSMModemHandler.COMMAND_GET_NEW_SMS + "\r\n");
        }
        catch(SerialPortException exp){
            exp.printStackTrace();
        }
    }
    
    public synchronized void deleteAllMessages(){
        try{
            if(this.isOpened())
                this.writeString(GSMModemHandler.COMMAND_DELETE_ALL_MESSAGES + "\r\n");
        }
        catch(SerialPortException exp){
            exp.printStackTrace();
        }
    }
    
    public synchronized void deleteReadMessages(){
        try{
            if(this.isOpened())
                this.writeString(GSMModemHandler.COMMAND_DELETE_READ_MESSAGES + "\r\n");
        }
        catch(SerialPortException exp){
            exp.printStackTrace();
        }
    }
    
    public synchronized void fermerConnexion(){
        try{
            this.closePort();
        }
        catch(SerialPortException exp){
            exp.printStackTrace();
        }
    }
        
    @Override
    public void serialEvent(SerialPortEvent spe) {
            try {
                this.lastResponse = this.readString();
                
                if(this.lastResponse != null){
                    // if the response contains sms
                    if(this.lastResponse.contains("+CMGL") && this.lastResponse.contains("REC"))
                        this.smsPicker.getResponseQueue().add(this.lastResponse);
                }
        } 
        catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * @return the smsPicker
     */
    public SMSPicker getSmsPicker() {
        return smsPicker;
    }

    /**
     * @param smsPicker the smsPicker to set
     */
    public void setSmsPicker(SMSPicker smsPicker) {
        this.smsPicker = smsPicker;
    }
}
