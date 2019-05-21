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
import javax.persistence.Convert;
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
@Table(name = "CLIENT_HISTORY")
public class Client {
    
    private SimpleStringProperty nomClient = new SimpleStringProperty(),
                                 numeroTelephoneClient = new SimpleStringProperty();
    private SimpleIntegerProperty ticketClient = new SimpleIntegerProperty(),
                                  idClient = new SimpleIntegerProperty();
    private SimpleObjectProperty<LocalDate> dateClient = new SimpleObjectProperty<LocalDate>();
    private SimpleObjectProperty<LocalTime> heureClient = new SimpleObjectProperty<LocalTime>();
    
    public Client(){}
    public Client(String nom,String numeroTelephone,int ticket, LocalDate date, LocalTime heure){
        this.nomClient.setValue(nom);
        this.numeroTelephoneClient.setValue(numeroTelephone);
        this.ticketClient.set(ticket);
        this.dateClient.set(date);
        this.heureClient.set(heure);
    }

    @Basic
    @Column(name = "NOM_CLIENT")
    public String getNomClient() {
        return nomClient.getValue();
    }

    public void setNomClient(String nomClient) {
        this.nomClient.setValue(nomClient);
    }

    @Basic
    @Column(name = "NUMERO_TELEPHONE_CLIENT")
    public String getNumeroTelephoneClient() {
        return numeroTelephoneClient.getValue();
    }

    public void setNumeroTelephoneClient(String numeroTelephoneClient) {
        this.numeroTelephoneClient.setValue(numeroTelephoneClient);
    }

    @Basic
    @Column(name = "NUMERO_TICKET_CLIENT")
    public int getTicketClient() {
        return ticketClient.get();
    }

    public void setTicketClient(int ticketClient) {
        this.ticketClient.set(ticketClient);
    }
        
    public SimpleStringProperty nomProperty(){
        return this.nomClient;
    }
    
    public SimpleStringProperty numeroTelephoneProperty(){
        return this.numeroTelephoneClient;
    }
    
    public SimpleIntegerProperty numeroTicketProperty(){
        return this.ticketClient;
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof Client)
            return this.getNumeroTelephoneClient().equals(((Client) o).getNumeroTelephoneClient());
        return false;
    }
    
    @Override
    public String toString(){
        return this.nomClient.getValue();
    }

    @Basic
    @Column(name = "DATE_CLIENT")
    @Convert(converter = Poste.DateConverter.class)
    public LocalDate getDateClient() {
        return dateClient.getValue();
    }

    public void setDateClient(LocalDate dateClient) {
        this.dateClient.setValue(dateClient);
    }

    @Basic
    @Column(name = "HEURE_CLIENT")
    @Convert(converter = Poste.TimeConverter.class)
    public LocalTime getHeureClient() {
        return heureClient.getValue();
    }

    public void setHeureClient(LocalTime heureClient) {
        this.heureClient.setValue(heureClient);
    }

    @Id
    @Column(name = "ID_CLIENT")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public int getIdClient() {
        return idClient.getValue();
    }

    public void setIdClient(int idClient) {
        this.idClient.setValue(idClient);
    }
}
