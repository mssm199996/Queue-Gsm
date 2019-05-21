/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package POO;

import java.time.LocalDate;
import java.time.LocalTime;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;

/**
 *
 * @author MSSM
 */
@Entity
@Table(name = "SMS_HISTORY")

public class SMS {
    
    private SimpleIntegerProperty idSms = new SimpleIntegerProperty(),
                                  ticketId = new SimpleIntegerProperty(-1);
    private SimpleStringProperty correspondantSms= new SimpleStringProperty(),
                                 contenuSms = new SimpleStringProperty();
    private SimpleObjectProperty<LocalDate> dateSms = new SimpleObjectProperty<LocalDate>();
    private SimpleObjectProperty<LocalTime> heureSms = new SimpleObjectProperty<LocalTime>();
    
    public SMS(){}
    public SMS(String correspondantSms, String contenuSms){
        this.contenuSms.setValue(contenuSms);
        this.correspondantSms.setValue(correspondantSms);
    }
    public SMS(String correspondantSms, String contenuSms, LocalDate dateSms, LocalTime heureSms){
        this(correspondantSms,contenuSms);
        this.dateSms.setValue(dateSms);
        this.heureSms.setValue(heureSms);
    }
    
    @Id
    @Column(name = "ID_SMS")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public int getIdSms(){
        return this.idSms.getValue();
    }
    
    public void setIdSms(int idSms){
        this.idSms.setValue(idSms);
    }
    
    @Basic
    @Column(name = "CORRESPONDANT_SMS")
    public String getCorrespondantSms(){
        return this.correspondantSms.getValue();
    }
    
    public void setCorrespondantSms(String correspondantSms){
        this.correspondantSms.setValue(correspondantSms);
    }
    
    @Basic
    @Column(name = "CONTENU_SMS")
    public String getContenuSms(){
        return this.contenuSms.getValue();
    }
    
    public void setContenuSms(String contenuSms){
        this.contenuSms.setValue(contenuSms);
    }
    
    @Basic
    @Column(name = "DATE_SMS")
    @Convert(converter = Poste.DateConverter.class)
    public LocalDate getDateSms(){
        return this.dateSms.getValue();
    }
    
    public void setDateSms(LocalDate dateSms){
        this.dateSms.setValue(dateSms);
    }
    
    @Basic
    @Column(name = "HEURE_SMS")
    @Convert(converter = Poste.TimeConverter.class)
    public LocalTime getHeureSms(){
        return this.heureSms.getValue();
    }
    
    public void setHeureSms(LocalTime heureSms){
        this.heureSms.setValue(heureSms);
    }
    
    @Basic
    @Column(name = "ID_TICKET")
    public int getTicketId(){
        return this.ticketId.getValue();
    }
    
    public void setTicketId(int ticketId){
        this.ticketId.setValue(ticketId);
    }
    
    @Override
    public String toString(){
        return this.correspondantSms.getValue() + ": " + this.dateSms.getValue().toString() + " " + this.heureSms.getValue().toString() + " => " + this.contenuSms.getValue();
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof SMS)
            return ((SMS) o).correspondantSms.getValue().equals(this.correspondantSms.getValue())
                    && ((SMS) o).dateSms.getValue().equals(this.dateSms.getValue())
                    && ((SMS) o).heureSms.getValue().equals(this.heureSms.getValue());
        return false;
    }
}

/*
  ID_SMS
  CORRESPONDANT_SMS
  CONTENU_SMS
  DATE_SMS
  HEURE_SMS
  ID_TICKET
*/