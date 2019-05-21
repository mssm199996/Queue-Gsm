/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities.Workers;

import POO.SMS;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MSSM
 */
public class SMSPicker extends ControlledThread{
    
    private GSMModemHandler modemGsm = null;
    private SMSQueueTreater smsQueueHandler = null;
    private Queue<String> responsesQueue = new LinkedList<String>();
    
    public SMSPicker(double frequency, GSMModemHandler gsmModem){
        super(frequency);
        this.modemGsm = gsmModem;
        this.modemGsm.setSmsPicker(this);
        this.smsQueueHandler = new SMSQueueTreater(frequency,gsmModem);
    }

    @Override
    public void whatToDoBeforeTheLoop(){
        this.smsQueueHandler.start();
                
        try {
            this.wait(2 * this.waitingPeriod.get());
        } catch (InterruptedException ex) {
            Logger.getLogger(SMSPicker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void stopWorking(){
        this.smsQueueHandler.stopWorking();
        super.stopWorking();
    }

    @Override
    public void whatToDoDuringTheLoop() throws NullPointerException{                
        // Sending the sms read request to the gsm modem
        synchronized(this){
            this.modemGsm.readAllMessages();
            this.modemGsm.deleteReadMessages();
        }
        
        // Treating the response in order to extract sms from it
        while(!this.responsesQueue.isEmpty()){
            String longMessage = this.responsesQueue.poll();
            
            if(longMessage != null){
                
                String[] shortMessages = null;
                shortMessages = longMessage.split("\\+CMGL: [0-9]*,\"");
                
                // Si le message ne peut être decoupé => aucun message
                if(shortMessages == null) 
                    continue;
            
                for(String shortMessage: shortMessages){
                    int indexLastOK = shortMessage.lastIndexOf("OK");

                    if(indexLastOK != -1 && shortMessage.contains("REC")) 
                        this.smsQueueHandler.getSmsFifo().add(this.fromStringToSms(shortMessage
                                .substring(0,shortMessage.lastIndexOf("OK")))); // if it is the last sms
                    else if(shortMessage.contains("REC")) // if it is not the last one
                        this.smsQueueHandler.getSmsFifo().add(this.fromStringToSms(shortMessage));
                }
            }
        }        
    }

    private SMS fromStringToSms(String stringSms){
        String[] smsParts = stringSms.split(",");
        
        String correspondantSms = smsParts[1].replaceAll("\"", "");        
        String dateSms = smsParts[3].replace("\"","").replaceAll("/", "-");
        String heureSms = smsParts[4].substring(0,smsParts[4].lastIndexOf("\"")).substring(0, 8);
        String contenuSms = stringSms.substring(stringSms.lastIndexOf("\"") + 3);
        
        LocalDate date = LocalDate.parse("20" + dateSms);
        LocalTime heure = LocalTime.parse(heureSms);
        
        return new SMS(correspondantSms, contenuSms, date, heure);
    }
    
    @Override
    public void whatToDoAfterTheLoop() {
    }

    /**
     * @return the modemGsm
     */
    public GSMModemHandler getModemGsm() {
        return modemGsm;
    }

    /**
     * @param modemGsm the modemGsm to set
     */
    public void setModemGsm(GSMModemHandler modemGsm) {
        this.modemGsm = modemGsm;
    }

    /**
     * @return the smsQueueHandler
     */
    public SMSQueueTreater getSmsQueueHandler() {
        return smsQueueHandler;
    }

    /**
     * @param smsQueueHandler the smsQueueHandler to set
     */
    public void setSmsQueueHandler(SMSQueueTreater smsQueueHandler) {
        this.smsQueueHandler = smsQueueHandler;
    }

    /**
     * @return the response
     */
    public Queue<String> getResponseQueue() {
        return responsesQueue;
    }

    /**
     * @param response the response to set
     */
    public void setResponseQueue(Queue<String> responses) {
        this.responsesQueue = responses;
    }
}
