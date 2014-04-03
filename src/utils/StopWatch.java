package utils;

/**
 * This class is supposed to create a nice clean way to use
 * a timer in Java.  It works like a stop watch.  You create
 * the timer, start it using .start() stop it using .stop()
 * and you can read how many seconds elapsed.
 * @author robertc
 *
 */
public class StopWatch {
	long m_startTime;
	long m_endTime;
	long m_sum; // used for the continue option
	static Double NS_per_S = 1000000000.0;
	
	
	public StopWatch() {
		// TODO Auto-generated constructor stub
	}
	
	public void start()
	{
		reset();
		m_startTime = System.nanoTime();
	}
	
	public void resume()
	{
		m_sum += (m_endTime - m_startTime);
		m_startTime = System.nanoTime();
	}
	
	public void stop()
	{
		m_endTime = System.nanoTime();
	}
	
	public Double get_elapsed_Seconds()
	{
		// must convert from nanosecond to 
		return (m_endTime - m_startTime + m_sum) / NS_per_S;
	}
	
	public void reset()
	{
		m_startTime = m_endTime = m_sum = 0;
	}

}
