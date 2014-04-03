package random_ArasuManku_Window;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;

import com.vladium.utils.ObjectProfiler;

import twitter_parser.TwitterParser;
import utils.Exact_Period_Count;
import utils.Exact_PreDefined_Query_Collection;
import utils.StopWatch;


public class Driver {
	public static ArrayList<TwitterParser> buffered_data;
	
	public static Exact_PreDefined_Query_Collection exact_queries;
	
	public static void initalize() throws Exception
	{
		// start with a large capacity so we don't have to grow the data structure as much
		buffered_data = new ArrayList<TwitterParser>(1000000);
		
		exact_queries = new Exact_PreDefined_Query_Collection();
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		String [] query_words = {"love",
				                 "desktop",
				                 "believe",
				                 "twitter",
				                 "facebook",
				                 "microsoft", 
				                 "apple", 
				                 "google", 
				                 "marathon", 
				                 "gaga", 
				                 "#ladygaga", 
				                 "boston"};
		
		// for each query word we create four queries, one which covers the entire month, another for half the month
		// another for just the end of the month and the last for just the last day.
		for(String S : query_words)
		{
			exact_queries.create_query(S, formatter.parse("2013-03-01 01:00"), formatter.parse("2013-03-31 23:59"));
			exact_queries.create_query(S, formatter.parse("2013-03-15 00:00"), formatter.parse("2013-03-31 23:59"));
			exact_queries.create_query(S, formatter.parse("2013-03-25 00:00"), formatter.parse("2013-03-31 23:59"));
		}
		
		
		// read in the files of interest in order
		String [] files = {
				           //"2013-02_clean.txt",
				           "2013-03_clean.txt",
				           //"2013-04_clean.txt",
				           //"2013-05_clean.txt",
				           //"2013-06_clean.txt",
		                  };
		
		// we keep reading files until we find a file greater then or equal to this value, then we kick out.
		// the reason we are doing this is so because the files are no cleanly separated (because the twitter
		// time is not given in MST, but the division between the files is in MST).
		Date read_until = formatter.parse("2013-04-01 00:00");
		
		for(String filename : files)
		{
			String line;
		    BufferedReader reader = new BufferedReader(new FileReader(filename));
		    while((line = reader.readLine()) != null)
		    {
		    	TwitterParser item = new TwitterParser(line);
		    	if(item.get_date().compareTo(read_until) > 0)
		    		continue;
		    	
		    	exact_queries.update_query(item);
		    	buffered_data.add(new TwitterParser(line));
		    }
		    reader.close();
		}
	}
	
	// this returns a string (for output to console where each line is the estimated value of the query)
	// that shows the accuracy of each of the queries.  Each of the values returned is the percent error
	public static String acuracy_of_queries(random_ArasuManku_Window_withDate query)
	{
		String ret_val = "";
		boolean first = true;
		Date min_q_date = query.get_smallestGuaranteedDate();
		Date max_q_date = query.get_largestGuaranteedDate();
		for(Exact_Period_Count qry : exact_queries)
		{
			int exact_count = qry.get_Count();
			int est_count = 0;
			// only do the query if it is within the range of the dates.  If it is not within the range
			// it will result in a count of zero.
			if(qry.get_minDate().after(min_q_date) && qry.get_maxDate().before(max_q_date))
					est_count = query.query(qry.get_word(), qry.get_minDate(), qry.get_maxDate());
			ret_val += (first ? "" : "\t") + Math.abs(((0.0 + est_count - exact_count) / exact_count));
			first = false;
		}
		return ret_val;
	}
	
	public static void VaryWindowSize_timeASize() throws Exception
	{
		int WindowSizesToTest[] = {10000, 50000, 100000, 500000, 1000000, 5000000, 10000000};
		double epsilon = 0.001;
		double delta   = 0.001;
		int seconds    = 60 * 5; // every five minutes
		int trials     = 1;
		String filename = "2013-03_clean.txt";
		BufferedReader reader;
		String line;
		
		StopWatch overhead = new StopWatch();
		
		
		// test the time to read and parse the data
		reader = new BufferedReader(new FileReader(filename));
		overhead.start();
		for(TwitterParser tweet_line : buffered_data)
		{
			tweet_line.get_id();
		}
		overhead.stop();
		reader.close();
		
		System.out.println("Overhead: " + overhead.get_elapsed_Seconds() + "sec");
		
		System.out.print("W\tinserted\traw time\tcorrected time\tsize (bytes)\tLeast recent date\tMost recent date");
		for(int i=0; i<exact_queries.queries_registered(); i++)
			System.out.print("\tquery " + i);
		
		System.out.println();
		
		for(int W : WindowSizesToTest)
		{
			StopWatch time = new StopWatch();
			time.reset();
			int size_sum = 0;
			Date small_date = null;
			Date large_date = null;
			String acruacy = "";
			
			for(int i=0; i<trials; i++)
			{
			reader = new BufferedReader(new FileReader(filename));
			random_ArasuManku_Window_withDate window = new random_ArasuManku_Window_withDate(W, epsilon, delta, seconds);
			
			time.resume();
			for(TwitterParser tweet_line : buffered_data)
				for(String s : tweet_line.get_words())
					window.insertWDate(s, tweet_line.get_date());
			
			time.stop();
			
			window.garbage_collect_dates();
			size_sum += ObjectProfiler.sizeof(window);
			// we will only keep the last entry
			small_date = window.get_smallestGuaranteedDate();
			large_date = window.get_largestGuaranteedDate();
			acruacy = acuracy_of_queries(window);
			}
			
			System.out.println(W + "\t" +
						        buffered_data.size() + "\t" +
							   (time.get_elapsed_Seconds() / trials) + "\t" +
					           ((time.get_elapsed_Seconds() - overhead.get_elapsed_Seconds() * trials) / trials) + "\t" +
							   (size_sum / trials) + "\t" +
							   small_date + "\t" +
							   large_date + "\t" +
							   acruacy);
		}
	}
	
	public static void VaryDeltaSize_timeASize() throws Exception
	{
		//int WindowSizesToTest[] = {10000, 50000, 100000, 500000, 1000000, 5000000, 10000000};
		int W = 1000000;
		double epsilon = 0.001;
		double deltaSizesToTest[]  = {0.5, 0.25, 0.1, 0.05, 0.01, 0.005, 0.001, 0.0005, 0.0001};
		int seconds    = 60 * 5; // every five minutes
		int trials     = 2;
		String filename = "2013-03_clean.txt";
		BufferedReader reader;
		String line;
			
		StopWatch overhead = new StopWatch();
			
			
		// test the time to read and parse the data
		reader = new BufferedReader(new FileReader(filename));
		overhead.start();
		while((line = reader.readLine()) != null)
		{
			TwitterParser tweet_line = new TwitterParser(line);
			tweet_line.get_id();
		}
		overhead.stop();
		reader.close();
		
		System.out.println("Overhead: " + overhead.get_elapsed_Seconds() + "sec");
		
		System.out.println("delta\traw time\tcorrected time\tsize (bytes)\tLeast recent date\tMost recent date");
		
		for(double delta : deltaSizesToTest)
		{
			StopWatch time = new StopWatch();
			time.reset();
			int size_sum = 0;
			Date small_date = null;
			Date large_date = null;
			
			for(int i=0; i<trials; i++)
			{
			reader = new BufferedReader(new FileReader(filename));
			random_ArasuManku_Window_withDate window = new random_ArasuManku_Window_withDate(W, epsilon, delta, seconds);
				
			time.resume();
			while((line = reader.readLine()) != null)
			{
				TwitterParser tweet_line = new TwitterParser(line);
				for(String s : tweet_line.get_words())
					window.insertWDate(s, tweet_line.get_date());
			}
			time.stop();
				
			window.garbage_collect_dates();
			size_sum += ObjectProfiler.sizeof(window);
			// we will only keep the last entry
			small_date = window.get_smallestGuaranteedDate();
			large_date = window.get_largestGuaranteedDate();
			}
			
			System.out.println(delta + "\t" +
							   (time.get_elapsed_Seconds() / trials) + "\t" +
					           ((time.get_elapsed_Seconds() - overhead.get_elapsed_Seconds() * trials) / trials) + "\t" +
							   (size_sum / trials) + "\t" +
							   small_date + "\t" +
							   large_date);
		}
	}

	public static void VaryEpsilonSize_timeASize() throws Exception
	{
		//int WindowSizesToTest[] = {10000, 50000, 100000, 500000, 1000000, 5000000, 10000000};
		int W = 1000000;
		double epsilonSizesToTest[] = {0.5, 0.25, 0.1, 0.05, 0.01, 0.005, 0.001, 0.0005, 0.0001};
		double delta   = 0.001;
		int seconds    = 60 * 5; // every five minutes
		int trials     = 2;
		String filename = "2013-03_clean.txt";
		BufferedReader reader;
		String line;
			
		StopWatch overhead = new StopWatch();
			
			
		// test the time to read and parse the data
		reader = new BufferedReader(new FileReader(filename));
		overhead.start();
		while((line = reader.readLine()) != null)
		{
			TwitterParser tweet_line = new TwitterParser(line);
			tweet_line.get_id();
		}
		overhead.stop();
		reader.close();
		
		System.out.println("Overhead: " + overhead.get_elapsed_Seconds() + "sec");
		
		System.out.println("epsilon\traw time\tcorrected time\tsize (bytes)\tLeast recent date\tMost recent date");
		
		for(double epsilon : epsilonSizesToTest)
		{
			StopWatch time = new StopWatch();
			time.reset();
			int size_sum = 0;
			Date small_date = null;
			Date large_date = null;
			
			for(int i=0; i<trials; i++)
			{
			reader = new BufferedReader(new FileReader(filename));
			random_ArasuManku_Window_withDate window = new random_ArasuManku_Window_withDate(W, epsilon, delta, seconds);
				
			time.resume();
			while((line = reader.readLine()) != null)
			{
				TwitterParser tweet_line = new TwitterParser(line);
				for(String s : tweet_line.get_words())
					window.insertWDate(s, tweet_line.get_date());
			}
			time.stop();
				
			window.garbage_collect_dates();
			size_sum += ObjectProfiler.sizeof(window);
			// we will only keep the last entry
			small_date = window.get_smallestGuaranteedDate();
			large_date = window.get_largestGuaranteedDate();
			}
			
			System.out.println(epsilon + "\t" +
							   (time.get_elapsed_Seconds() / trials) + "\t" +
					           ((time.get_elapsed_Seconds() - overhead.get_elapsed_Seconds() * trials) / trials) + "\t" +
							   (size_sum / trials) + "\t" +
							   small_date + "\t" +
							   large_date);
		}
	}	
	
	public static void main(String[] args) throws Exception {
		initalize();
		
		VaryWindowSize_timeASize();
		
		//VaryDeltaSize_timeASize();
		
		//VaryEpsilonSize_timeASize();
		
		/*try
		{
			int i = 0;
			BufferedReader reader = new BufferedReader(new FileReader("test.txt"));
			//random_ArasuManku_Window_withDate window = new random_ArasuManku_Window_withDate(100000, 0.001, 0.0001, 10);
			String line;
			while((line = reader.readLine()) != null && i++ < 100)
			{
				TwitterParser tweet_line = new TwitterParser(line);
				System.out.println(tweet_line.get_words());
				for(String s : tweet_line.get_words())
					//window.insertWDate(s, tweet_line.get_date());
			}
			
			//System.out.println("Size in bytes: " +  ObjectProfiler.sizeof(window));
			
			
			String s_query = "";
			Scanner in = new Scanner(System.in);
			s_query = in.nextLine();
			while(!s_query.equals("e"))
			{
				if(twitter_parser.StopListDictionary.s_query(s_query))
					System.out.println(" \'" + s_query + "\' : is in the stop list");
				else if(s_query.equals("--LIST--"))
				{
					Enumeration<String> S = window.get_all_tracked();
					while(S.hasMoreElements())
						System.out.println(" " + S.nextElement());
				} else if(s_query.equals("--DATE--"))
				{
					window.dumpDebug();
				}
				else
					System.out.println(" \'" + s_query + "\' : " + window.query(s_query, 0, window.get_insertedElements()));
				
				s_query = in.nextLine();
			}
			
			in.close();
			reader.close();
		}
		catch(Exception e)
		{
			System.err.println("Opps looks like we have a really bad error");
			e.printStackTrace();
		}*/

	}

}
