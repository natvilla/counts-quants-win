package random_ArasuManku_Window;

import java.util.ArrayList;
import java.util.Date;

/*
 * This table is used to keep track of what indexes are aligned with which time
 * or day.  Useful when querying the data structure between specific times.
 * 
 * This is a very basic implementation because I don't have the motivation
 * to implement a good tree structure which would be a better method.
 */


public class Date_Index_Table {
	private ArrayList<Date_Index_Elem> m_data;
	
	
	public Date_Index_Table(int initial_capacity)
	{
		m_data = new ArrayList<Date_Index_Elem>(initial_capacity);
	}
	
	public void insertElement(Date date, int index)
	{
		m_data.add(new Date_Index_Elem(date, index));
	}
	
	public int find_upper(Date date)
	{
		return m_data.get(this.find(date, true)).m_index;
	}
	
	public int find_lower(Date date)
	{
		return m_data.get(this.find(date, false)).m_index;
	}
	
	public void cleanup(Date oldest_new_date)
	{
		int index = this.find(oldest_new_date, false);
		ArrayList<Date_Index_Elem> new_data = new ArrayList<Date_Index_Elem>(m_data.size());
		new_data.addAll(index, m_data);
		m_data = new_data;
	}
	
	private int find(Date date, Boolean upper)
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
				return m_data.get(mid).m_index;
			}
		}
		
		if(upper)
			return Math.max(first, upto);
		else
			return Math.min(first, upto);
	}
}
