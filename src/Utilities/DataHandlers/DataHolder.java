/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities.DataHandlers;

import POO.Poste;
import Utilities.Workers.GSMModemHandler;
import Utilities.Workers.SMSPicker;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 *
 * @author MSSM
 */
public class DataHolder {
    public static final int APPLICATION_FREQUENCY = 10;
    public static final int NOTIFICATION_DELIMITER = 10;
    public static volatile Poste POSTE = null;
    public static SessionFactoryHandler SESSION_FACTORY_HANDLER = new SessionFactoryHandler("hibernate.cfg.xml");
    public static SimpleBooleanProperty APPLICATION_RUNNING = new SimpleBooleanProperty(false);
    public static SimpleLongProperty POLLING_INTERVAL = new SimpleLongProperty(2 * 60 * 1000);
    public static SimpleLongProperty LAST_POLL_TIME = new SimpleLongProperty(0);
    public static GSMModemHandler MODEM_GSM = null;
    public static SMSPicker SMS_PICKER = null;
}
