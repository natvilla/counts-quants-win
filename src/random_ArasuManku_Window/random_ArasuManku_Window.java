package random_ArasuManku_Window;
/*
 * This implements the Randomized, Bounded-window sketch described in
 * section 6 of the paper found at: http://research.microsoft.com/pubs/77611/quantiles.pdf
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class random_ArasuManku_Window implements Serializable {
	/** randomly generated serial UID number **/
	private static final long serialVersionUID = -943983685168737789L;
	private final int m_W;
	private final double m_epsilon;
	private final double m_delta;
	private final int m_r;
	private final int m_blockSize;
	
	private int m_insertedElements;  // the number of elements which have entered by calling 'insert'
	private int m_trackedElementCount;   // the edge of the used slots when looking in the array m_summery
	private int m_nextSampleIndex;   // the index of the next item which will be sampled
	
	private Random m_rgen;
	
	private ArrayList<Sticky_Triple<String>> m_summery;
	
	private Hashtable<String, LinkedList<Sticky_Triple<String>>> m_lookup;
	
	public random_ArasuManku_Window(int W, double epsilon, double delta)
	{		
		m_W          = W;
		m_epsilon    = epsilon;
		m_delta      = delta;
		m_r          = (int) Math.max(log(0.0 + m_W / ( 1.0 / m_epsilon * log(1.0 / (m_epsilon + m_delta), 2)), 2), 0);
		m_blockSize  = (int) Math.pow(2, m_r);
		
		m_insertedElements = 0;
		
		int max_elements =  (int) (1.0 / m_epsilon * log(1.0 / (m_epsilon + m_delta), 2));
		
		m_rgen = new Random();
		
		// build the array and setup so all the locations are null
		m_summery = new ArrayList<Sticky_Triple<String>>(max_elements);
		for(int i=0; i< max_elements; i++)
			m_summery.add(null);
		
		// create the array with slightly more capacity then we will ever need
		m_lookup = new Hashtable<String, LinkedList<Sticky_Triple<String>>>((int) (max_elements * 1.25));
	}
	
	private static double log(double x, double base)
	{
		return Math.log(x) / Math.log(base);
	}
	
	/*
	 * This will perform the sample (if it is time)
	 * It will also update the index for the next sample (if it is time)
	 * 
	 * returns true if it sampled the element
	 */
	private boolean sampleMaintanace(String e)
	{
		// if we are starting a new block, we randomly select the next element which will be sampled
		if(m_insertedElements % m_blockSize == 0 )
			m_nextSampleIndex = m_insertedElements + this.m_rgen.nextInt(m_blockSize);
		
		// if we are looking at the next element we are sampling
		if(m_insertedElements == m_nextSampleIndex)
		{
			Sticky_Triple<String> to_insert = new Sticky_Triple<String>(e, m_insertedElements);
			
			// insert the element into the table
			place_item(to_insert);
			// insert the element into the lookup hash table
			if(!m_lookup.containsKey(e))
			{
				// create a new linked list if necessary
				m_lookup.put(e, new LinkedList<Sticky_Triple<String>>());				
			}
			m_lookup.get(e).addFirst(to_insert);
			
			m_trackedElementCount += 1;
			
			return true;
		}
		return false;
	}

	private void place_item(Sticky_Triple<String> to_insert) {
		int insert_location = m_trackedElementCount % m_summery.size();
		Sticky_Triple<String> toRemove = m_summery.get(insert_location);
		if(toRemove != null)
		{
			m_lookup.get(toRemove.get_e()).remove(toRemove);
			// if this removed all the entries in the table, remove it from the table
			if(m_lookup.get(toRemove.get_e()).size() == 0)
				m_lookup.remove(toRemove.get_e());
		}
		
		m_summery.set(insert_location, to_insert);
	}
	
	// insert several elements into the data structure in the order
	// they appear in the enumeration
	public void insert(Enumeration<String> e)
	{
		while(e.hasMoreElements())
		{
			this.insert(e.nextElement());
		}			
	}
	
	// insert a single element into the data structure
	public void insert(String e)
	{
		// check to see if it is inserted as a sample.  If so we have already
		// accounted for the item, otherwise we need to update the structure
		if(!sampleMaintanace(e))
		{
			// if the item exists in the lookup table, we should increase the frequency
			// of the most recently sampled item
			if(m_lookup.containsKey(e))
			{
				m_lookup.get(e).getFirst().incr_f();
			}
		}
		
		m_insertedElements++;
	}
	
	public int query(String e, int min_idx, int max_idx)
	{
		if(min_idx >= max_idx)
			return 0;
		
		int count = 0;
		if(m_lookup.containsKey(e))
		{
			int last_insertion = m_insertedElements;
			Sticky_Triple<String> current_element;
			Iterator<Sticky_Triple<String>> itr = m_lookup.get(e).iterator();
			current_element = itr.next();
			while(current_element.get_i() > max_idx && itr.hasNext())
			{
				last_insertion = current_element.get_i();
				current_element = itr.next();
			}
			
			count += current_element.get_f();			
			
			// estimate the number of elements in the upper section of the range
			// (here I assume an even distribution of elements in the range
			count -= (int) ((0.0 + last_insertion - max_idx) * current_element.get_f() / (last_insertion - current_element.get_i()));
			
			// until we reach the other side, we can just keep adding the elements to count
			while(current_element.get_i() > min_idx && itr.hasNext())
			{
				last_insertion = current_element.get_i();
				current_element = itr.next();
				count += current_element.get_f();
			}
			
			count -= (int) ((0.0 + min_idx - current_element.get_i()) * current_element.get_f() / (last_insertion - current_element.get_i()));
		}
		
		return count;
	}
	
	public int get_smallestIValue()
	{
		// if the data structure has not overwritten any of the elements, it will be at
		// index 0.  Otherwise, it will be at the next location which will be overwritten.
		int index = 0;
		if(m_trackedElementCount > m_summery.size())
			index = (m_trackedElementCount) % m_summery.size();
		return m_summery.get(index).get_i();
	}
	
	public int get_largestIValue()
	{
		return m_summery.get((m_trackedElementCount - 1) % m_summery.size()).get_i();
	}
	
	public int get_insertedElements()
	{
		return m_insertedElements;
	}
	
	public Enumeration<String> get_all_tracked()
	{
		return m_lookup.keys();
	}
	
	public int get_r()
	{
		return m_r;
	}
	
	public int get_blockSize()
	{
		return m_blockSize;
	}
}
