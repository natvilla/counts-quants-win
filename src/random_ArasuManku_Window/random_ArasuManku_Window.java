package random_ArasuManku_Window;
/*
 * This implements the Randomized, Bounded-window sketch described in
 * section 6 of the paper found at: http://research.microsoft.com/pubs/77611/quantiles.pdf
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;

public class random_ArasuManku_Window {
	private final int m_W;
	private final double m_epsilon;
	private final double m_delta;
	private final int m_r;
	private final int m_blockSize;
	
	private int m_insertedElements;  // the number of elements which have entered by calling 'insert'
	private int m_trackedElements;   // the edge of the used slots when looking in the array m_summery
	private int m_nextSampleIndex;   // the index of the next item which will be sampled
	
	private Random m_rgen;
	
	private boolean debug_flag;
	
	private ArrayList<Sticky_Triple<String>> m_summery;
	
	private Hashtable<String, LinkedList<Sticky_Triple<String>>> m_lookup;
	
	public random_ArasuManku_Window(int W, double epsilon, double delta)
	{		
		m_W          = W;
		m_epsilon    = epsilon;
		m_delta      = delta;
		m_r          = (int) log(0.0 + m_W / ( 1.0 / m_epsilon * log(1.0 / (m_epsilon + m_delta), 2)), 2);
		m_blockSize = (int) Math.pow(2, m_r);
		
		m_insertedElements = 0;
		
		int max_elements =  (int) (1.0 / m_epsilon * log(1.0 / (m_epsilon + m_delta), 2));
		
		m_rgen = new Random();
		
		// build the array and setup so all the locations are null
		m_summery = new ArrayList<Sticky_Triple<String>>(max_elements);
		for(int i=0; i< max_elements; i++)
			m_summery.add(null);
		
		// create the array with slightly more capacity then we will ever need
		m_lookup = new Hashtable<String, LinkedList<Sticky_Triple<String>>>((int) (max_elements * 1.25));
		
		debug_flag = false;
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
			
			m_trackedElements += 1;
			// TODO: account for overflow
			
			// insert the element into the table
			m_summery.set(m_trackedElements, to_insert);
			// insert the element into the lookup hash table
			if(!m_lookup.contains(e))
			{
				// create a new linked list if necessary
				m_lookup.put(e, new LinkedList<Sticky_Triple<String>>());				
			}
			m_lookup.get(e).addFirst(to_insert);
			
			return true;
		}
		return false;
	}
	
	public void insert(String e)
	{
		// check to see if it is inserted as a sample.  If so we have already
		// accounted for the item, otherwise we need to update the structure
		if(!sampleMaintanace(e))
		{
			// if the item exists in the lookup table, we should increase the frequency
			// of the most recently sampled item
			if(m_lookup.contains(e))
			{
				m_lookup.get(e).getFirst().incr_f();
			}
		}
		
		m_insertedElements++;
	}
	
}
