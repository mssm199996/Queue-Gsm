/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package POO;

import Utilities.ComponentHandlers.ComponentsHolder;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.AttributeConverter;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author MSSM
 */
@Entity
@Table(name = "ETAT_POSTE")
public class Poste implements Serializable{
    
    private SimpleIntegerProperty stateId = new SimpleIntegerProperty();
    private transient SimpleObjectProperty<EtatPoste> etatPoste = new SimpleObjectProperty<EtatPoste>(EtatPoste.OUVERTE);
    private SimpleObjectProperty<AtomicInteger> ntsPoste = new SimpleObjectProperty<AtomicInteger>(new AtomicInteger(1)),
                                  ndtPoste = new SimpleObjectProperty<AtomicInteger>(new AtomicInteger(1));
    private SimpleObjectProperty<LocalDate> stateDate = new SimpleObjectProperty<LocalDate>(LocalDate.now());
    private transient ObservableList<Client> fileAttente = FXCollections.observableList(new LinkedList());

    public Poste(){}
    
    @Id
    @Column(name = "ID_ETAT")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public int getStateId(){
        return this.stateId.getValue();
    }
    public void setStateId(int id){
        this.stateId.set(id);
    }
    
    @Transient
    public EtatPoste getEtatPoste(){
        return this.etatPoste.get();
    }
    
    public void setEtat(EtatPoste etat){
        this.etatPoste.setValue(etat);
    }
    
    @Basic
    @Column(name = "NDT_ETAT")
    public synchronized int getNdtPoste(){
        return this.ndtPoste.get().get();
    }
    public synchronized void setNdtPoste(int ndt){
        this.ndtPoste.get().set(ndt);
    }
    public synchronized SimpleObjectProperty<AtomicInteger> ndtProperty(){
        return this.ndtPoste;
    }
        
    @Basic
    @Column(name = "NTS_ETAT")
    public synchronized int getNtsPoste(){
        return this.ntsPoste.get().get();
    }
    
    public synchronized void setNtsPoste(int ntc){
        this.ntsPoste.get().set(ntc);
    }
    public synchronized SimpleObjectProperty<AtomicInteger> ntsProperty(){
        return this.ntsPoste;
    }
    
    @Basic
    @Column(name = "DATE_ETAT")
    @Convert(converter = DateConverter.class)
    public LocalDate getStateDay(){
        return this.stateDate.getValue();
    }
    public void setStateDay(LocalDate stateDay){
        this.stateDate.setValue(stateDay);
    }
    
    public synchronized int incrementNtsPoste(){
        return this.ntsPoste.getValue().incrementAndGet();
    }
    
    public synchronized int incrementNdtPoste(){
        ComponentsHolder.NDT_TEXT_FIELD.setText("" + (this.getNdtPoste() + 1));
        return this.ndtPoste.getValue().incrementAndGet();
    }
    
    public synchronized Client pollClient(){
        return (!this.fileAttente.isEmpty() ? this.fileAttente.remove(0) : null);
    }
    
    public synchronized void addClient(Client client){
        this.fileAttente.add(client);
    }
    
    public synchronized void deleteClient(Client client){
        this.fileAttente.remove(client);
    }
    
    public synchronized void deleteIndexedClient(int i){
        this.fileAttente.remove(i);
    }
    
    public synchronized SimpleObjectProperty<EtatPoste> etatPosteProperty(){
        return this.etatPoste;
    }

    @Transient
    public synchronized ObservableList<Client> getFileAttente(){
        return this.fileAttente;
    }
    
    @Transient
    public synchronized boolean isFileAttendeEmpty(){
        return this.fileAttente.isEmpty();
    }
    
    public enum EtatPoste {
        OUVERTE,
        FERMEE
    }
    
    @Converter
    public static class DateConverter implements AttributeConverter<LocalDate, String>{

        @Override
        public String convertToDatabaseColumn(LocalDate x) {
            return x.toString();
        }

        @Override
        public LocalDate convertToEntityAttribute(String y) {
            return LocalDate.parse(y);
        }
    }
    
    @Converter
    public static class TimeConverter implements AttributeConverter<LocalTime, String>{

        @Override
        public String convertToDatabaseColumn(LocalTime x) {
            return x.toString();
        }

        @Override
        public LocalTime convertToEntityAttribute(String y) {
            return LocalTime.parse(y);
        }
    }
}