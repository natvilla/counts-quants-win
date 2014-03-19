package random_ArasuManku_Window;

import java.util.Date;

public class Date_Index_Elem implements  Comparable<Date>{
	public final Date m_date;
	public final int m_index;
	
	public Date_Index_Elem(Date date, int index)
	{
		m_date = date;
		m_index = index;
	}

	public int compareTo(Date_Index_Elem arg0) {
		return this.m_date.compareTo(arg0.m_date);
	}
	
	@Override
	public int compareTo(Date arg0) {
		return this.m_date.compareTo(arg0);
	}
}
