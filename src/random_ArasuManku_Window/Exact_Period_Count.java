package random_ArasuManku_Window;

import java.util.Date;

public class Exact_Period_Count {
		String m_word;
		int    m_count;
		
		Date   m_minDate;
		Date   m_maxDate;
	
	
	public Exact_Period_Count(String word, Date min, Date max) {
		m_word    = word;
		m_minDate = min;
		m_maxDate = max;
		m_count   = 0;
	}
	
	public void Add_ifInRangeAndWordMatch(String word, Date date_of_occurence)
	{
		if(m_word.equalsIgnoreCase(word))
			Add_ifInRange(date_of_occurence);
	}
	
	public void Add_ifInRange(Date date_of_occurence)
	{
		if(date_of_occurence.compareTo(m_minDate) >= 0 && date_of_occurence.compareTo(m_maxDate) <= 0)
			m_count++;
	}

	public int get_Count()
	{
		return m_count;
	}
	
	public String get_word()
	{
		return m_word;
	}
	
	public Date get_minDate()
	{
		return m_minDate;
	}
	
	public Date get_maxDate()
	{
		return m_maxDate;
	}
	
	public String toString()
	{
		return m_word + " : (" + m_count + ") " + m_minDate + " -> " + m_maxDate;
	}
}
