package random_ArasuManku_Window;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

public class Test_random_ArasuManku_Window {

	@Test
	public void test() {		
		//fail("Not yet implemented");
	}
	
	@Test
	public void insert_query() {
		random_ArasuManku_Window test_subject = new random_ArasuManku_Window(10000, 0.001, 0.01);
		
		for(int i=0; i<100; i++)
			test_subject.insert("hello");
		
		assertEquals(100, test_subject.query("hello", 0, 100));		
	}
	
	@Test
	public void insert_query_test2(){
		random_ArasuManku_Window test_subject = new random_ArasuManku_Window(10000, 0.001, 0.01);
		
		for(int i=0; i<200; i++)
		{
			test_subject.insert("hello");
			test_subject.insert("world");
		}
		
		assertEquals(50, test_subject.query("hello", 0, 100));
		assertEquals(50, test_subject.query("world", 0, 100));
	}

}
