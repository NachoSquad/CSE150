package nachos.threads;
import nachos.machine.*;
import java.util.LinkedList;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */

//commit test case 
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
    	
    	waitingQueue = new LinkedList<waitingData>(); 
    	
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
    
    	boolean status = Machine.interrupt().disable(); 
    
    	while(!waitingQueue.isEmpty() && waitingQueue.getFirst().wakeTime <= Machine.timer().getTime()) {
    		waitingData currentwaiter = waitingQueue.getFirst(); 
    		currentwaiter.thread.ready();
    		waitingQueue.remove(currentwaiter); 	
    		Lib.assertTrue(currentwaiter.wakeTime <= Machine.timer().getTime());
    } 
    	
    	KThread.yield();
    	Machine.interrupt().restore(status);
  }
    
   
    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	
	long wakeTime = Machine.timer().getTime() + x;
    KThread waitingThread = KThread.currentThread(); 
	boolean status = Machine.interrupt().disable(); 
	
	waitingData waiter = new waitingData(wakeTime,waitingThread); 
	waitingQueue.add(waiter);
	
	
	KThread.sleep(); 
	
	Machine.interrupt().restore(status);
	
	
    }
    
 //   
 private LinkedList<waitingData> waitingQueue;  
    
    private static class waitingData {
    long wakeTime; 
    	KThread thread; 
    	
    	public waitingData(long wakeTime,KThread thread) { 
    		this.wakeTime = wakeTime; 
    		this.thread = thread; 
       	} 	
    }
    
    
}
