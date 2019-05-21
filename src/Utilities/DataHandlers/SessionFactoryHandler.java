/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities.DataHandlers;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

/**
 *
 * @author MSSM
 */
public class SessionFactoryHandler {
    
    private String hibernateMappingFilePath = null;
    private SessionFactory sessionFactory = null;
    
    public SessionFactoryHandler(String hibernateMappingFilePath){
        this.hibernateMappingFilePath = hibernateMappingFilePath;
        this.sessionFactory = new MetadataSources(
            new StandardServiceRegistryBuilder().configure(hibernateMappingFilePath).build())
            .buildMetadata().buildSessionFactory();
    }
    
    public void insertEntities(Object ... entities) throws HibernateException{
        Session session = sessionFactory.openSession();
                session.beginTransaction();
        
        for(Object mssmEntity: entities) 
            session.save(mssmEntity);
        
        session.getTransaction().commit();
        session.close();
    }
    
    public void updateEntities(Object ... entities) throws HibernateException{
        Session session = sessionFactory.openSession();
                session.beginTransaction();
                            
        for(Object mssmEntity: entities) 
            session.update(mssmEntity);
                          
        session.getTransaction().commit();
        session.close();
    }
    
    public void deleteEntities(Object ... entities) throws HibernateException{
        Session session = sessionFactory.openSession();
                session.beginTransaction();
                            
        for(Object mssmEntity: entities) 
            session.delete(mssmEntity);
                   
        session.getTransaction().commit();
        session.close();
    }
    
    public List<Object> getListOfEntities(String stringQuery, Object ... params) throws HibernateException{
        List<Object> result = null;
        
        Session session = sessionFactory.openSession();
                session.beginTransaction();
                                
        Query query = session.createQuery(stringQuery);
        
        if(params != null)
            for (int i = 0; i < params.length; i++)
                if (params[i] != null) 
                    query.setParameter(i, params[i]);

        result = query.getResultList();

        session.getTransaction().commit();
        session.close();        
        
        return result;
    }

    public String getHibernateMappingFilePath() {
        return hibernateMappingFilePath;
    }

    public void setHibernateMappingFilePath(String hibernateMappingFilePath) {
        this.hibernateMappingFilePath = hibernateMappingFilePath;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
