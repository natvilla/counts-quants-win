/**
 * 
 */
package random_ArasuManku_Window;

import java.util.Calendar;
import java.util.Date;

/**
 * @author robertc
 *
 */
public class random_ArasuManku_Window_withDate extends random_ArasuManku_Window {

	/** randomly generated serial UID number **/
	private static final long serialVersionUID = -6886353949795093035L;

	Date_Index_Table m_dateTable;
	
	Calendar m_nextTimeRecord;
	Date     m_nextTimeRecordDate;
	
	int m_updateRate;
	
	/**
	 * @param W
	 * @param epsilon
	 * @param delta
	 */
	public random_ArasuManku_Window_withDate(int W, double epsilon, double delta, int seconds_per_update) {
		super(W, epsilon, delta);
		m_dateTable = new Date_Index_Table(10000);
		m_updateRate = seconds_per_update;
	}
	
	public void insertWDate(String item, Date timeStamp)
	{
		if(m_nextTimeRecord == null)
		{
			m_nextTimeRecord = Calendar.getInstance();
			m_nextTimeRecord.add(Calendar.SECOND, m_updateRate);
			m_nextTimeRecord.setTime(timeStamp);
			m_dateTable.insertElement(timeStamp, this.get_insertedElements());
		}
		else if(timeStamp.after(m_nextTimeRecord.getTime()))
		{
			// do a while loop in case we need to update multiple times.  This should
			// not have to loop many times
			while(timeStamp.after(m_nextTimeRecord.getTime()))
				m_nextTimeRecord.add(Calendar.SECOND, m_updateRate);
			
			m_dateTable.insertElement(m_nextTimeRecord.getTime(), this.get_insertedElements());
		}
		
		this.insert(item);
	}
	
	public int query(String item, Date startDate, Date endDate)
	{
		if(m_dateTable == null)
			return 0;
		
		int lower = m_dateTable.find_lower_date(startDate);
		int upper = m_dateTable.find_upper_date(endDate);
		
		return this.query(item, lower, upper);
	}
	
	public void dumpDebug()
	{
		m_dateTable.dump_all_Recorded_Dates();
	}
	
	public Date get_smallestGuaranteedDate()
	{
		int smallest_index = this.get_smallestIValue();
		return m_dateTable.find_lower_index(smallest_index);
	}
}
