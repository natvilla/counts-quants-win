package utils;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import twitter_parser.TwitterParser;

/*
 * This is mostly used for testing how accurate the other methods are.
 * In order to use this, one must select which words should be watched and
 * counted exactly for different ranges.  In other words, the query is
 * asked before the data is ever seen (so it is not very useful in the real
 * world, but it is nice when testing data is available to see how close the
 * other methods are to the exact counts).
 * 
 * Before any data is seen, register queries with the word and the time series
 * of the query.  Every element should be inserted into this data structure
 * along with the time (or just pass the all the TwitterParser objects in which contains the
 * date and the word).  At the end One can look at the queries to see what the counts
 * are for those queries.
 */
public class Exact_PreDefined_Query {
	Hashtable<String, List<Exact_Period_Count> > m_table;
	List<Exact_Period_Count> m_queryList;
	
	public Exact_PreDefined_Query() {
		m_table = new Hashtable<String, List<Exact_Period_Count> >();
		m_queryList = new LinkedList<Exact_Period_Count>();
	}
	
	/**
	 * insert the query into the list of queries.
	 * 
	 * @param word the word which matches this query.
	 * @param min the minimum date (the time which we want to start counting)
	 * @param max the maximum date (the time which we don't want to count any more)
	 * @return returns a reference to the query which we just created and inserted into the table
	 */
	public Exact_Period_Count create_query(String word, Date min, Date max)
	{
		Exact_Period_Count query = new Exact_Period_Count(word, min, max);
		if(!m_table.containsKey(word))
			m_table.put(word, new LinkedList<Exact_Period_Count>());
		
		m_table.get(word).add(query);
		m_queryList.add(query);
		return query;
	}

	/**
	 * given a single word and the date which that word occurred, update
	 * the queries of interest so they count this word.
	 * 
	 * @param word the word which was seen
	 * @param time the date the word was seen
	 */
	public void update_query(String word, Date time)
	{
		List<Exact_Period_Count> queries = m_table.get(word);
		if(queries != null)
			for(Exact_Period_Count i : queries)
				i.Add_ifInRange(time);
	}
	
	/**
	 * update all the queries with the words in a TwitterParser.
	 * 
	 * @param tweet the tweet to process with the date and words to be used to update the queries.
	 */
	public void update_query(TwitterParser tweet)
	{
		for(String s : tweet.get_words())
			update_query(s, tweet.get_date());
	}
	
	/**
	 * return an iterator of the queries which have been inserted into this data structure.
	 * The items will be returned in an undefined order.
	 * @return an iterator over the queries which have been inserted in the data structure.
	 */
	public Iterator<Exact_Period_Count> get_QueryIterator()
	{
		return m_queryList.iterator();
	}
}
