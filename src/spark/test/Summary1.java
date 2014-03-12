package spark.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;

public class Summary1 {
	public HashTable<String> m_table;
	public int m_k;
	public long inserted;
	
	public Summary1(int k)
	{
		m_table  = new HashTable<String>(k);
		m_k      = k;
		inserted = 0;
	}
	
	public void appendElement(String val)
	{
		inserted++;
		m_table.place(val);
	}
	
	public void mergeWith(Summary1 val)
	{
		m_table = m_table.MergeTable(val.m_table);
		inserted = m_table.getTotalCount();
	}
	
	public void dump() {
		
		Enumeration<HashElement<String>> enu = m_table.getHashElementEnumeration();
		//Enumeration<String> enu = m_table.getCommonElementEnumeration(0.5);
		int i=0;
		
		while(enu.hasMoreElements()) {
			//String val = enu.nextElement();
			HashElement<String> val = enu.nextElement();
			
			String output = "" + i + ": (" + val.getItem() + ") (count=" + val.getCount() + " eps=" + val.getEpsilon() + ")\n";
			//String output = "" + i + ": (" + val + ")\n"; 
			System.out.print(output);
			i++;
		}
		
		Enumeration<String> enu2 = m_table.getCommonElementEnumeration(0.001);
		System.out.println("---Top 0.001 elements---");
		i = 0;
		
		/*while(enu2.hasMoreElements())
		{
			String val = enu2.nextElement();
			String output = "" + i + ": (" + val + ")\n"; 
			System.out.print(output);
			i++;
		}*/
	}
	
	// test merging the table with itself
	public void testMerge1()
	{
		System.out.println("--STARTING SAME MERGE TEST");
		HashTable<String> table2 = m_table.MergeTable(m_table);
		
		Enumeration<HashElement<String>> enu1 = m_table.getHashElementEnumeration();
		Enumeration<HashElement<String>> enu2 =  table2.getHashElementEnumeration();
		
		HashElement<String> item1;
		HashElement<String> item2;
		
		while(enu1.hasMoreElements() && enu2.hasMoreElements())
		{
			item1 = enu1.nextElement();
			item2 = enu2.nextElement();
			
			if(!item1.getItem().equals(item2.getItem()))
				System.err.println("DIFFERENT ORDERING? " + item1.getItem() + "(cnt=" + item1.getCount() + ") vs " + item2.getItem() + "(cnt=" + item2.getCount() + ")");
			if(2 * item1.getCount() != item2.getCount())
				System.err.println("ITEM " + item1.getItem() + " SHOULD HAVE THE 2x COUNTS (should be 2x" + item1.getCount() + " but it is " + item2.getCount() + ")");
			if(2 * item1.getEpsilon() != item2.getEpsilon())
				System.err.println("ITEM " + item1.getItem() + " SHOULD HAVE THE 2x EPS (should be 2x" + item1.getEpsilon() + " but it is " + item2.getEpsilon() + ")");			
		}
		
		Enumeration<String> enu1a = m_table.getCommonElementEnumeration(0.01);
		Enumeration<String> enu2a =  table2.getCommonElementEnumeration(0.01);
		
		String item1a;
		String item2a;
		
		while(enu1a.hasMoreElements() && enu2a.hasMoreElements())
		{
			item1a = enu1a.nextElement();
			item2a = enu2a.nextElement();
			
			if(!item1a.equals(item1a))
				System.err.println("DIFFERENT ORDERING? " + item1a + " vs " + item2a);			
		}
		
		if(enu1.hasMoreElements())
			System.err.println("STILL MORE ELEMENTS IN THIS TABLE");
		if(enu2.hasMoreElements())
			System.err.println("STILL MORE ELEMENTS IN MERGED TABLE");
		if(enu1a.hasMoreElements())
			System.err.println("STILL MORE ELEMENTS IN THIS TABLE");
		if(enu2a.hasMoreElements())
			System.err.println("STILL MORE ELEMENTS IN MERGED TABLE");
		
		if(2*m_table.getTotalCount() != table2.getTotalCount())
			System.err.println("ITEM COUNTS DON'T MATCH (2*" + m_table.getTotalCount() + "!=" + table2.getTotalCount() + ")");
		
		System.out.println("DONE WITH SAME MERGE TEST");
	}
	
	public void testMerge2()
	{
		System.out.println("--STARTING EMPTY MERGE TEST");
		HashTable<String> emptyTable = new HashTable<String>(m_table.getK());
		HashTable<String> table2 = emptyTable.MergeTable(m_table);
		
		Enumeration<HashElement<String>> enu1 = m_table.getHashElementEnumeration();
		Enumeration<HashElement<String>> enu2 =  table2.getHashElementEnumeration();
		
		HashElement<String> item1;
		HashElement<String> item2;
		
		while(enu1.hasMoreElements() && enu2.hasMoreElements())
		{
			item1 = enu1.nextElement();
			item2 = enu2.nextElement();
			
			if(!item1.getItem().equals(item2.getItem()))
				System.err.println("DIFFERENT ORDERING? " + item1.getItem() + " vs " + item2.getItem());
			if(item1.getCount() != item2.getCount())
				System.err.println("ITEM " + item1.getItem() + " SHOULD HAVE THE 2x COUNTS (" + item1.getCount() + " vs " + item2.getCount() + ")");
			if(item1.getEpsilon() != item2.getEpsilon())
				System.err.println("ITEM " + item1.getItem() + " SHOULD HAVE THE 2x EPS (" + item1.getEpsilon() + " vs " + item2.getEpsilon() + ")");			
		}
		
		Enumeration<String> enu1a = m_table.getCommonElementEnumeration(0.01);
		Enumeration<String> enu2a =  table2.getCommonElementEnumeration(0.01);
		
		String item1a;
		String item2a;
		
		while(enu1a.hasMoreElements() && enu2a.hasMoreElements())
		{
			item1a = enu1a.nextElement();
			item2a = enu2a.nextElement();
			
			if(!item1a.equals(item1a))
				System.err.println("DIFFERENT ORDERING? " + item1a + " vs " + item2a);			
		}
		
		if(enu1.hasMoreElements())
			System.err.println("STILL MORE ELEMENTS IN THIS TABLE (" + enu1.nextElement().getItem() + ")");
		if(enu2.hasMoreElements())
			System.err.println("STILL MORE ELEMENTS IN MERGED TABLE");
		if(enu1a.hasMoreElements())
			System.err.println("STILL MORE ELEMENTS IN THIS TABLE (" + enu1a.nextElement() + ")");
		if(enu2a.hasMoreElements())
			System.err.println("STILL MORE ELEMENTS IN MERGED TABLE");
		
		if(m_table.getTotalCount() != table2.getTotalCount())
			System.err.println("ITEM COUNTS DON'T MATCH (2*" + m_table.getTotalCount() + "!=" + table2.getTotalCount() + ")");	
		
		System.out.println("DONE WITH EMPTY MERGE TEST");
		
	}
	
	@SuppressWarnings("unchecked")
	public void testSerial() {
		System.out.println("--STARTING SERIALIZABLE TEST");
		HashTable<String> read_table = null;
		
		
		// first, serialize the object
		try {
			FileOutputStream fileOut = new FileOutputStream("tmp.data");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(m_table);
			fileOut.close();
			
			// second, read the serialized object
			FileInputStream fileIn = new FileInputStream("tmp.data");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			read_table = (HashTable<String>) in.readObject();
			fileIn.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);			
		} finally {
			
		}
		
		// verify the two are the same by iterating through all object.
		// it should be stored in the same order.
		Enumeration<HashElement<String>> enu1 = m_table.getHashElementEnumeration();
		Enumeration<HashElement<String>> enu2 = read_table.getHashElementEnumeration();
		int i=0;
		
		while(enu1.hasMoreElements() && enu2.hasMoreElements())
		{
			HashElement<String> item1 = enu1.nextElement();
			HashElement<String> item2 = enu2.nextElement();
			
			if(!item1.equals(item2))
			{
				System.err.println("FOUND AN ERROR AT " + i + ": (" + item1.getItem() + ") != (" + item2.getItem() + ")");
			}
			i++;
		}
		
		if(enu1.hasMoreElements() || enu2.hasMoreElements())
			System.err.println("DIFFERENT NUMBER OF ELEMENTS FOUND IN EACH ONE");
		
		System.out.println("DONE WITH SERIALIZABLE TEST");
		
	}
	
}
