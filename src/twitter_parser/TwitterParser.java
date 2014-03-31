package twitter_parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * Parses a twitter entry from the test data dump.  Each line will be in the format:
 * (id)(tab)(date)(tab)(twitter entry)
 * This parser will read the id, date, and tweet.  The date will be in java date format
 * and the twitter will be cleaned of unnecessary punctuation and symbols.  Also,
 * the tweets will be in a string array where each word is an element of the array.
 * 
 * To use the parser, give the entire line of the tweet to the constructor. To get
 * the interesting related contents from the tweet us the appropriate getters.  The
 * list of interesting words and hash tags can get retrieved from the ArrayList<String>
 * returned by get_words()
 * 
 */


public class TwitterParser {
	private Date m_date;
	private long m_id;
	private ArrayList<String> m_tweetWords;
	
	public TwitterParser(String tweet) throws Exception
	{
		String[] split1 = tweet.split("\t");
		m_tweetWords = new ArrayList<String>();
		
		if(split1.length != 3)
			throw new Exception("Malformed String to parse " + tweet);
		
		// get the id number
		try {
			m_id = Long.parseLong(split1[0]);
		} catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		
		// get the date
		DateFormat formatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		try {
			m_date = (Date)(formatter.parse(split1[1]));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//String filtered_string = this.removeAll(split1[2], ".,:;\'\"<!?[]{}()/\\");
		String filtered_string = split1[2].replaceAll("[-]", " ");
		       filtered_string = filtered_string.replaceAll("[\\W&&[^ #]]", "");
		String[] split2 = filtered_string.split(" ");
		
		for(String word : split2)
		{
			String potential_insert = word.toLowerCase();
			
			if(!StopListDictionary.s_query(potential_insert))
			{
				m_tweetWords.add(potential_insert);
			}
		}
	}
	
	public Date get_date()
	{
		return m_date;
	}
	
	public long get_id()
	{
		return m_id;
	}
	
	public ArrayList<String> get_words()
	{
		return m_tweetWords;
	}
}
