package spark.test;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class HashTable<T> implements Serializable{
	/**
	 * 
	 */
	transient private Object m_table[];
	transient private CountList<T> m_countlist;		

	private long totalCount;
	transient private long insertedElements;
	private int m_k;
	private int cycle_count;
	private int table_hashes_used;	
	
	
	public HashTable(int k)
	{
		totalCount       = 0;
		cycle_count      = 0;
		insertedElements = 0;
		m_k              = k;
		table_hashes_used= 2;
		m_countlist = new CountList<T>();
		m_table = new Object[2*k];
	}
	
	public long getTotalCount()
	{	return totalCount;	}
	
	public int getK()
	{	return m_k;	}
	
	public boolean contains(T val)
	{
		return this.getValue(val) != null;
	}
	
	private void placeExistingElement(HashElement<T> val)
	{
		HashElement<T> fval = this.getValue(val.getItem());
		
		// if the element exists in the table, just update the count
		if(fval != null)
		{
			fval.increaseCounters(val.getCount(), val.getEpsilon());
		}
		// otherwise, we need to insert the element at the tail end
		else
		{
			// here be assume the table will always have enough to insert
			// into the table, since we are currently only calling this
			// function when merging.
			placeNewElement(val.getItem(), val.getCount(), val.getEpsilon());
			// the previous line does the work of the following lines
			//HashElement<T> newItem = insertNew(val.getItem());
			//newItem.increaseCounters(val.getCount()-newItem.getCount(), val.getEpsilon());			
		}
	}
	
	private void placeNewElement(T item, long count, long epsilon)
	{
	  HashElement<T> newItem = insertNew(item);
	  newItem.increaseCounters(count - newItem.getCount(), epsilon);
	}
	
	public void place(T val)
	{
		totalCount++;
		HashElement<T> fval = this.getValue(val);
		
		// in this case, it is in the table
		if(fval != null)
		{
			fval.increaseCount();
		}
		// in this case, it it not in the table, so we must add to to the table
		else
		{
			// if the table is not 'full'
			if(insertedElements < m_k)
			{
				insertNew(val);
			}
			// if the table is full, we must remove the value with the smallest count
			else
			{
				// remove item the table first
				HashElement<T> temp = m_countlist.getTail().getFirstItem();
				remove(temp);
				temp.ReplaceElement(val);
				temp.increaseCount();
				insert(temp);				
			}
		}
	}
	
	private void remove(HashElement<T> val)
	{
		insertedElements--;
		// to remove, just replace that location with a null pointer
		m_table[val.getHashValue().hash()%m_table.length] =  null;
		// also, remove from the count list items
		//val.removeCountList();
	}
	
	private HashElement<T> insertNew(T val)
	{
		HashElement<T> newItem = new HashElement<T>(val, 1, 0, null, null, null);
		m_countlist.InsertNewCountListItem(newItem);
		newItem.setCountList(m_countlist.getTail());
		insert(newItem);
		return newItem;
	}
	
	@SuppressWarnings("unchecked")
	private void insert(HashElement<T> val)
	{
		insertedElements++;		
		
		boolean found_spot = false;
		for(int i=0; i<this.table_hashes_used && !found_spot; i++)
		{
			// if it found an open available spot to place the element
			if(m_table[val.getHashValue().hash(i, m_table.length)] == null)
			{
				m_table[val.getHashValue().hash(i, m_table.length)] = val;
				val.getHashValue().setUsedHash(i);
				found_spot = true;
			}
		}
		
		// if a location was not found, we must rotate until a spot is available
		if(!found_spot)
		{
			HashElement<T> toInsert;
			HashElement<T> tmp = val;
			int c = 0;
			
			do
			{
				// setup to place in the next location
				toInsert = tmp;
				toInsert.getHashValue().increaseUsedHash(this.table_hashes_used);
				// grab the item in the location to insert to
				tmp = (HashElement<T>) m_table[toInsert.getHashValue().hash()% m_table.length];
				
				m_table[toInsert.getHashValue().hash()%m_table.length] = toInsert;
				c++;
			} while(tmp != null && !(tmp == val && c > 1000)); 
					//!(tmp == val && val.getHashValue().getUsedHash() > HashValue.get_hashCount()));
			
			if(tmp == val)
			{
				try {
					this.table_hashes_used++;
					insertedElements--;
					insert(val);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
			else
			{
				cycle_count += 1;
			}
		}
	}
	
	private HashElement<T> getValue(T val)
	{
		HashElement<T> to_ret = null;
		HashValue hval = new HashValue(val);
		for(int i=0; i<this.table_hashes_used && to_ret == null; i++)
		{
			// we are checking equivalence by testing if the whole have value is the same.
			// If they are exactly the same they will hash to exactly the same spot.  The
			// hash table I am using struggles if the hash is exactly the same
			int hashIdx = hval.hash(i)%m_table.length;
			@SuppressWarnings("unchecked")
			HashElement<T> temp = (HashElement<T>) m_table[hashIdx];
			if(temp != null && temp.getHashValue().equals(hval))
				to_ret = temp;
		}		
		return to_ret;
	}
	
	public HashTable<T> MergeTable(HashTable<T> table2)
	{
		Enumeration<HashElement<T>> enu1 =   this.getHashElementEnumeration();
		Enumeration<HashElement<T>> enu2 = table2.getHashElementEnumeration();
		
		// create a large table guaranteed to have enough space for both 
		HashTable<T> tempTable = new HashTable<T>(this.m_k + table2.m_k);
		
		HashElement<T> item1 = enu1.hasMoreElements() ? enu1.nextElement() : null;
		HashElement<T> item2 = enu2.hasMoreElements() ? enu2.nextElement() : null;
		
		while(item1 != null && item2 != null)
		{
			if(item1.getCount() >= item2.getCount())
			{
				tempTable.placeExistingElement(item1);
				item1 = enu1.hasMoreElements() ? enu1.nextElement() : null;
			}
			else
			{
				tempTable.placeExistingElement(item2);
				item2 = enu2.hasMoreElements() ? enu2.nextElement() : null;
			}
		}
		
		// need to add the remaining elements in whatever list still has elements
		while(item1 != null)
		{
			tempTable.placeExistingElement(item1);
			item1 = enu1.hasMoreElements() ? enu1.nextElement() : null;
		}
		while(item2 != null)
		{
			tempTable.placeExistingElement(item2);
			item2 = enu2.hasMoreElements() ? enu2.nextElement() : null;
		}
		
		// We only take the top(k) elements in the temporary table and insert it into
		// the new table
		
		// technically, I think both summaries should have the same k value,
		// but just in case I will select the min.
		int newk = this.m_k < table2.m_k ? this.m_k : table2.m_k;
		
		HashTable<T> toRetTable = new HashTable<T>(newk);
		
		Enumeration<HashElement<T>> enu3 = tempTable.getHashElementEnumeration();
		for(int i=0; i<newk && enu3.hasMoreElements(); i++)
		{
			item1 = enu3.nextElement();
			toRetTable.placeExistingElement(item1);
		}
		
		toRetTable.totalCount = this.totalCount + table2.totalCount;
		
		return toRetTable;
	}
	
	public Enumeration<HashElement<T> > getHashElementEnumeration()
	{
		return new HashTableEnumeration(this);
	}
	
	public Enumeration<T> getCommonElementEnumeration(double phi)
	{
		return new CommonElementEnumeration(this, phi);
	}
	
	class CommonElementEnumeration implements Enumeration<T>
	{
		double my_phi;
		HashTable<T> m_table;
		HashTableEnumeration internal_enum;
		boolean guaranteed;
		
		T next_element_to_return;
		
		Boolean is_guaranteed()
		{
			return guaranteed;
		}
		
		CommonElementEnumeration(HashTable<T> table, double phi)
		{
			m_table = table;
			my_phi  = phi;
			internal_enum = new HashTableEnumeration(table);
			guaranteed = true;
			
			this.findNextElement();
		}
		
		private void findNextElement()
		{
			next_element_to_return = null;
			
			while(next_element_to_return == null 
			   && internal_enum.hasMoreElements())
			{
				HashElement<T> tmp = internal_enum.nextElement();
				
				// check to see if the count is above the desired threshold
				if(tmp.getCount() <= Math.ceil(my_phi * m_table.getTotalCount()))
					break;
				
				next_element_to_return = tmp.getItem();
				
				if(tmp.getCount() - tmp.getEpsilon() < Math.ceil(my_phi * m_table.getTotalCount()))
				{
					System.out.println("No longer guaranteed");
					guaranteed = false;
				}
			}
		}
		
		@Override
		public boolean hasMoreElements() {
			return next_element_to_return!=null;
		}
		
		@Override
		public T nextElement() {
			if(this.hasMoreElements() == false)
				throw new NoSuchElementException();
			
			T to_ret = next_element_to_return;
			
			this.findNextElement();
			
			return to_ret;
		}
		
	}
	
	class HashTableEnumeration implements Enumeration<HashElement<T>>
	{
		CountListItem<T> current_counter;
		HashElement<T> current_element;
		HashElement<T> starting_element;
		HashTable<T> my_table;
		
		HashTableEnumeration(HashTable<T> table)
		{
			my_table        = table;
			current_counter = table.m_countlist.getHead();
			if(current_counter != null)
			{
				current_element = current_counter.m_itemListHead;
				starting_element= current_element;
			}
			else
			{
				current_element  = null;
				starting_element = null;
			}
		}

		@Override
		public boolean hasMoreElements() {
			return current_element!=null;
		}

		@Override
		public HashElement<T> nextElement() {		
			if(!hasMoreElements())
				throw new NoSuchElementException();
			
			HashElement<T> to_ret = current_element;			
			
			if(current_element.getNext() == starting_element 
			&& current_counter.getPrev() == null)
			{
				current_element = null;
				return to_ret;
			}
			else if(current_element.getNext() == starting_element)
			{
				current_counter = current_counter.getPrev();
				current_element = current_counter.m_itemListHead;
				starting_element= current_element;
				return to_ret;
			}
			
			current_element = current_element.getNext();			
			return to_ret;
		}
	}
	
	// code for serializing the linked list structure of the table
	private static final long serialVersionUID = 858165168416L;
	
	class HashTable_archiveData<T> implements Serializable
	{
		private static final long serialVersionUID = 1L;
		HashTable_archiveData(T i, long c, long e)
		{
		  item    = i;
		  count   = c;
		  epsilon = e;
		}
	  	public T item;
	  	public long count;
	  	public long epsilon;
	}
	
	private void writeObject(java.io.ObjectOutputStream s)
		throws java.io.IOException {
		// Write out any hidden serialization magic
		s.defaultWriteObject();
		
		// write out internal data
		s.writeLong(insertedElements);
		
		Enumeration<HashElement<T>> enu = getHashElementEnumeration();
		while(enu.hasMoreElements())
		{
		  HashElement<T> data = enu.nextElement();
		  s.writeObject(new HashTable_archiveData<T>(data.getItem(), data.getCount(), data.getEpsilon()));
		}
	}
	
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden serialization magic
		s.defaultReadObject();
		
		// read in integral data
		long insertedElements = s.readLong();
		
		m_countlist = new CountList<T>();
		m_table = new Object[2*m_k];
		
		for(int i=0; i<insertedElements; i++)
		{
			@SuppressWarnings("unchecked")
			HashTable_archiveData<T> temp = (HashTable_archiveData<T>) s.readObject();
			this.placeNewElement(temp.item, temp.count, temp.epsilon);
		}
	}
}
