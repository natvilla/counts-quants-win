package random_ArasuManku_Window;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Scanner;

import twitter_parser.TwitterParser;


public class Driver {

	public static void main(String[] args) {
		try
		{
			int i = 0;
			BufferedReader reader = new BufferedReader(new FileReader("test.txt"));
			random_ArasuManku_Window_withDate window = new random_ArasuManku_Window_withDate(100000, 0.001, 0.0001, 10);
			String line;
			while((line = reader.readLine()) != null && i++ < 100)
			{
				TwitterParser tweet_line = new TwitterParser(line);
				System.out.println(tweet_line.get_words());
				for(String s : tweet_line.get_words())
					window.insertWDate(s, tweet_line.get_date());
			}
			
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
			
			reader.close();
		}
		catch(Exception e)
		{
			System.err.println("Opps looks like we have a really bad error");
			e.printStackTrace();
		}

	}

}
