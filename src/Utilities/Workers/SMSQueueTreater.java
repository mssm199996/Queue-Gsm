/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities.Workers;

import POO.Client;
import POO.SMS;
import Utilities.DataHandlers.DataHolder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author MSSM
 */
public class SMSQueueTreater extends ControlledThread{
    
    private SimpleObjectProperty<Queue<SMS>> smsFifo = new SimpleObjectProperty<Queue<SMS>>();
    private GSMModemHandler gsmModem = null;
    
    public SMSQueueTreater(double frequency,GSMModemHandler gsmModem) {
        super(frequency);
        this.smsFifo.setValue(new LinkedList());
        this.gsmModem = gsmModem;
    }

    @Override
    public void whatToDoBeforeTheLoop() {
    }

    @Override
    public void whatToDoDuringTheLoop() {
        while(!this.smsFifo.getValue().isEmpty()){
            synchronized(this){
                System.out.println("--------------------------------------------------------");
                SMS sms = this.getSmsFifo().poll();
                
                System.out.println("Before Response: \n" + sms.getContenuSms());
                if(!isItAlreadySaved(sms)){
                    sms.setContenuSms(this.removeUnwantedCharacters(sms.getContenuSms()));
                    
                    SMS responseSms = new SMS(sms.getCorrespondantSms(), 
                            this.interpreeteCommand(sms));

                    if(responseSms.getContenuSms().startsWith("Votre ticket = ")){
                        Client client = new Client(sms.getContenuSms().substring(1), sms.getCorrespondantSms(),
                                DataHolder.POSTE.getNdtPoste(), LocalDate.now(), LocalTime.now());
                        
                        DataHolder.POSTE.incrementNdtPoste();
                        DataHolder.POSTE.addClient(client);
                        DataHolder.SESSION_FACTORY_HANDLER.insertEntities(client);
                        DataHolder.SESSION_FACTORY_HANDLER.updateEntities(DataHolder.POSTE);
                        
                        sms.setTicketId(client.getTicketClient());
                    }

                    System.out.println("Response: \n" + responseSms.getContenuSms());
                    
                    this.gsmModem.sendMessage(responseSms);
                    
                    DataHolder.SESSION_FACTORY_HANDLER.insertEntities(sms);
                }
            }
        }
    }
    
    @Override
    public void whatToDoAfterTheLoop(){
    }

    /**
     * @return the smsFifo
     */
    public Queue<SMS> getSmsFifo() {
        return smsFifo.getValue();
    }

    /**
     * @param smsFifo the smsFifo to set
     */
    public void setSmsFifo(Queue<SMS> smsFifo) {
        this.smsFifo.setValue(smsFifo);
    }
    
    private synchronized String interpreeteCommand(SMS sms){
        String response = "Commande non reconnue, essayez ceci:\n"
                + "INFO: Connaitre l'etat de la poste, le numero du ticket suivant, numero du dernier ticket.\n"
                + "@VotreNom: Demander un ticket.";
                
        if(sms.getContenuSms().equals("INFO"))
            response = "Etat actuel de la poste: " + DataHolder.POSTE.getEtatPoste().toString() + ".\n"
                + "Numero du ticket suivant = " + DataHolder.POSTE.getNtsPoste() + "\n"
                + "Numero du dernier ticket = " + DataHolder.POSTE.getNdtPoste();
        
        else if(sms.getContenuSms().startsWith("@") && !this.alreadyClaimedTicket(sms))
            response = "Votre ticket = " + DataHolder.POSTE.getNdtPoste();
        else if(sms.getContenuSms().startsWith("@"))
            response = "Vous avez déja prie un ticket !";
        else if(sms.getContenuSms().equals("MT")){
            int remainingClientBeforeMe = this.getPositionOfClient(sms);
            
            if(remainingClientBeforeMe == -1)
                return "Vous ne disposez pas encore de ticket.";
            return "Il reste : " + remainingClientBeforeMe + " personnes avant votre tour.";
        }
        
        return response;
    }
    
    private synchronized String removeUnwantedCharacters(String command){
        String performedCommand = command.replaceAll("\\r\\n", "").replace("OKAT+CMGD=0[,1]", "").replaceAll("\\n", "").toUpperCase();
        
        if(performedCommand.contains("INFO"))
            performedCommand = performedCommand.replaceAll(" ", "");
        
        // Switch all spaces at the end to blanks (remove them)
        while(performedCommand.lastIndexOf(" ") == performedCommand.length() - 1)
            performedCommand = performedCommand.substring(0,performedCommand.length() - 1);
        
        // Switch all double spaces by 1 space
        performedCommand = performedCommand.replaceAll("  ", " ");
        
        return performedCommand;
    }
    
    private synchronized boolean alreadyClaimedTicket(SMS sms){
        // On considère qu'il a pri de ticket si il en a prie et si son numero du ticket N n'a pas encore expiré (N > nds)
        if(sms.getContenuSms().startsWith("@")){
            
            System.out.println("AlreadyClaimedDescription:");
            System.out.println("smsCorres: " + sms.getCorrespondantSms());
            System.out.println("smsDate: " + sms.getDateSms());
            // Verifions s'il en a déja prie
            List<SMS> smsSet = (List<SMS>) (Object) DataHolder.SESSION_FACTORY_HANDLER.getListOfEntities(
                    "from SMS where correspondantSms = ? and dateSms = ? and contenuSms like ? and ticketId != ?", sms.getCorrespondantSms(), sms.getDateSms(), "@%", -1);
            
            if(smsSet.isEmpty()) 
                return false;
            
            // Prendre l'enregistrement du dernier ticket qu'il a commandé le jour J
            SMS targetedSms = smsSet.get(smsSet.size() - 1); // Parce que c'est ordonné par ordre chronologique
            
            System.out.println("Numero Ticket de la personne: " + targetedSms.getTicketId());
            System.out.println("Numero du ticket suivant: " + DataHolder.POSTE.getNtsPoste());
                    
            return (targetedSms.getTicketId() > DataHolder.POSTE.getNtsPoste());
        }
        
        return false;
    }
    
    private synchronized boolean isItAlreadySaved(SMS sms){
        return !(((List<SMS>) (Object) DataHolder.
                        SESSION_FACTORY_HANDLER.getListOfEntities("from SMS where "
                                + "correspondantSms = ? and dateSms = ? and heureSms = ?", sms.getCorrespondantSms(),
                                    sms.getDateSms(), sms.getHeureSms())).isEmpty());
    }
    
    private synchronized int getPositionOfClient(SMS clientSms){        
        Iterator<Client> iterator = DataHolder.POSTE.getFileAttente().iterator();
        int result = 0;
        
        while(iterator.hasNext())
            if(iterator.next().getNumeroTelephoneClient().equals(clientSms.getCorrespondantSms()))
                return result;
            else result++;
        return -1;
    }
}

