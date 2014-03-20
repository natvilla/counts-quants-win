package twitter_parser;

import java.util.HashSet;
import java.util.LinkedList;

/*
 * This stop list is taken from http://norm.al/2009/04/14/list-of-english-stop-words/
 * 
 * This class is a singleton.  After it is built the data structure does not change.
 * 
 * Query the data structure using s_query (for a static query) or query (if an instance is obtained)
 * If it returns true, it means it is in the stop list.
 * 
 * Note: this stop list also has prefix stop words.  This can be used to filter out web sites
 * which start with http
 */
public class StopListDictionary {
	static private StopListDictionary instance;
	
	private HashSet<String> m_dictionary;
	private LinkedList<String> m_prefixDictionary;
	
	static public StopListDictionary get_instance()
	{
		if(instance == null)
			instance = new StopListDictionary();
		return instance;
	}
	
	static public Boolean s_query(String s)
	{
		return get_instance().query(s);
	}
	
	private StopListDictionary()
	{
		m_prefixDictionary = new LinkedList<String>();
		
		m_prefixDictionary.add("http");
		
		m_dictionary = new HashSet<String>();
		
		String stopList = "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your";
		String[] stopListWords = stopList.split(",");
		
		for(String word : stopListWords)
			m_dictionary.add(word);		
	}
	
	// returns true if it is in the stop list
	public Boolean query(String s)
	{
		if(m_dictionary.contains(s)) return true;
		
		for(String prefix : m_prefixDictionary)
			if(s.startsWith(prefix)) return true;
		
		return false;
	}
}
