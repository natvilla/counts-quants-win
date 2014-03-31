package random_ArasuManku_Window;

import java.io.Serializable;
/*
 * Implementation of a skicky triple described in the paper which
 * this implementation is based from
 * 
 * Programmer:
 * Robert Christensen
 * 
 */


public class Sticky_Triple<T> implements Serializable{
	/** randomly generated serial UID number **/
	private static final long serialVersionUID = -726616990674368635L;
	private final T m_e;
	private int m_f;
	private int m_i;
	
	public Sticky_Triple(T element)
	{
		m_e = element;
		m_f = 1;
		m_i = 0;
	}
	
	public Sticky_Triple(T element, int i)
	{
		m_e = element;
		m_f = 1;
		m_i = i;
	}
	
	public final T get_e()
	{
		return m_e;
	}
	
	public int get_f()
	{
		return m_f;
	}
	
	public int get_i()
	{
		return m_i;
	}
	
	public void set_f(int new_f)
	{
		m_f = new_f;
	}
	
	public void incr_f()
	{
		m_f++;
	}
	
	public void set_i(int new_i)
	{
		m_i = new_i;
	}
}
