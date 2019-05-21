package Utilities.Workers;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author MSSM
 */
public abstract class ControlledThread extends Thread{
    
    protected AtomicBoolean workable = null;
    protected AtomicLong waitingPeriod = null;
    
    public ControlledThread(double frequency){
        super();
        this.workable = new AtomicBoolean(true);
        this.waitingPeriod = new AtomicLong(((long)(1000 / frequency)));
    }
    
    @Override
    public synchronized void run() {
        this.whatToDoBeforeTheLoop();
        while(this.workable.get()){
            try{
                this.whatToDoDuringTheLoop();
                this.wait(this.waitingPeriod.get());
            }
            catch(InterruptedException exp){
                exp.printStackTrace();
            }
        }
        this.whatToDoAfterTheLoop();
    }
    
    public void stopWorking(){
        this.workable.set(false);
    }
    
    public synchronized boolean isWorking(){
        return this.workable.get();
    }
    
    public abstract void whatToDoBeforeTheLoop();
    public abstract void whatToDoDuringTheLoop();
    public abstract void whatToDoAfterTheLoop();
}
