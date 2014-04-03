package random_ArasuManku_Window;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/*
 * This table is used to keep track of what indexes are aligned with which time
 * or day.  Useful when querying the data structure between specific times.
 * 
 * This is a very basic implementation because I don't have the motivation
 * to implement a good tree structure which would be a better method.
 */


public class Date_Index_Table implements Serializable {
	/** randomly generated serial UID number **/
	private static final long serialVersionUID = -7224815207611656136L;
	private ArrayList<Date_Index_Elem> m_data;
	
	
	public Date_Index_Table(int initial_capacity)
	{
		m_data = new ArrayList<Date_Index_Elem>(initial_capacity);
	}
	
	public void insertElement(Date date, int index)
	{
		m_data.add(new Date_Index_Elem(date, index));
	}
	
	public int find_upper_date(Date date)
	{
		return m_data.get(this.find_date(date, true)).m_index;
	}
	
	public int find_lower_date(Date date)
	{
		return m_data.get(this.find_date(date, false)).m_index;
	}
	
	public Date find_upper_index(int index)
	{
		return m_data.get(this.find_index(index, true)).m_date;
	}
	
	public Date find_lower_index(int index)
	{
		return m_data.get(this.find_index(index, false)).m_date;		
	}
	
	public void cleanup(Date oldest_new_date)
	{
		int index = this.find_date(oldest_new_date, false);
		ArrayList<Date_Index_Elem> new_data = new ArrayList<Date_Index_Elem>(m_data.size() - index);
		for(int i=index; i<m_data.size(); i++)
			new_data.add(m_data.get(i));
		//new_data.addAll(index, m_data);
		m_data = new_data;
	}
	
	/**
	 * find the index in the data array for the index value provided
	 * @param index The index to search for
	 * @param upper true if an upper bound is being searched
	 * @return
	 */
	private int find_index(int index, Boolean upper)
	{
		int first = 0;
		int upto = m_data.size();
		
		while(first < upto) {
			int mid = (first + upto) / 2;
			if(m_data.get(mid).m_index > index)
			{
				upto = mid;
			}
			else if (m_data.get(mid).m_index < index)
			{
				first = mid + 1;
			} else {
				return mid;
			}
		}
		
		if(upper)
			return Math.max(first, upto);
		else
			return Math.min(first, upto);		
	}
	
	/**
	 * return the index for the element which is closest to a given date
	 * @param date the date to search for
	 * @param upper if you want an upper bound or a lower bound for the searched date
	 * @return the index to the data structure for the element closest to the date
	 */
	private int find_date(Date date, Boolean upper)
	{
		int first = 0;
		int upto = m_data.size();
		
		while(first < upto) {
			int mid = (first + upto) / 2;
			if(m_data.get(mid).compareTo(date) > 0)
			{
				upto = mid;
			}
			else if (m_data.get(mid).compareTo(date) < 0)
			{
				first = mid + 1;
			} else {
				return mid;
			}
		}
		
		if(upper)
			return Math.max(first, upto);
		else
			return Math.min(first, upto);
	}
	
	public void dump_all_Recorded_Dates()
	{
		for(Date_Index_Elem e : m_data)
		{
			if(e != null)
				System.out.println( "" + e.m_date.toString());
		}
	}
	
	public Date MostRecentlyInserted()
	{
		return m_data.get(m_data.size() - 1).m_date;
	}
}
