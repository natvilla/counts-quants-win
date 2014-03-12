package spark.test;

import java.io.Serializable;

public class CountListItem<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long m_count;
	HashElement<T> m_itemListHead;
	CountList<T> m_master;
	
	private CountListItem<T> my_prev;
	private CountListItem<T> my_next;
	
	public CountListItem(HashElement<T> head, CountList<T> master, long count) {
		m_itemListHead = head;
		head.setCountList(this);
		m_itemListHead.updatePointers(m_itemListHead, m_itemListHead);
		
		m_master = master;
		
		m_count = count;
	}
	
	public void InsertElement(HashElement<T> newItem) {
		// NOTE: I am not currently sorting the elements in the list.
		//  the paper I am referencing for this implementation suggests
		//  the list be sorted.  I don't think it is necessary for a correct
		//  implementation, so I have waited to add the sorted order until later.
		//  (What would I sort by?  Hash value?)
		
		// The list will always have at least one element in the list, so we
		// can just add the new element anywhere in the list, so I will
		// insert the element to the end of the list
		HashElement<T> last_element = m_itemListHead.getPrev();
		
		last_element.updateNextPointer(newItem);
		m_itemListHead.updatePrevPointer(newItem);
		newItem.setCountList(this);
		newItem.updatePointers(last_element, m_itemListHead);
	}
	
	public void RemoveElement(HashElement<T> item) {
		// if it points to itself, we don't need to do anything
		// the appropriate things should just disappear		
		if(!item.isNextSelf())
		{
			if(m_itemListHead == item)
				m_itemListHead = item.getNext();
			item.getPrev().updateNextPointer(item.getNext());
			item.getNext().updatePrevPointer(item.getPrev());
		}
		else
		{
			// if there is nothing in the list, set the head to
			// null so we remember it is empty.  If this counter
			// element is used again it should throw all sorts
			// of exceptions.
			m_itemListHead = null;
		}
	}
	
	public void addCount(HashElement<T> item, long by_this_amount)
	{
		if(by_this_amount <= 0)
			return;
		long desired_count = this.m_count + by_this_amount;
		
		// if it is the only countListItem
		if(my_next == null && my_prev == null)
		{
			if(this.ListLongerThenOne())
			{
				this.RemoveElement(item);
				// create a new object with +1 count
				CountListItem<T> newItem = new CountListItem<T>(item, m_master, this.m_count+by_this_amount);
				newItem.setPrev(this);
				this.setNext(newItem);
				item.setCountList(newItem);
				// the new value will be the new head
				m_master.registerHead(newItem);				
			} else
			{
				// I can just increase the count of this item
				this.m_count += by_this_amount;
			}
		} else
		{
			this.RemoveElement(item);
			
			CountListItem<T> current_pointer = this;
			while(current_pointer.getNext()  != null 
			   && current_pointer.getNext().getCount() <= desired_count)
			{
				current_pointer = current_pointer.getNext();				
			}
			
			// if the found location is = to desired count, just add it to there
			if(current_pointer.getCount() == desired_count)
			{
				current_pointer.InsertElement(item);
			}
			// in this case we will need to create a new CountListItem
			else
			{
				CountListItem<T> newItem = new CountListItem<T>(item, m_master, this.m_count+by_this_amount);
				
				newItem.setNext(current_pointer.getNext());
				newItem.setPrev(current_pointer);
				
				current_pointer.my_next = newItem;
				if(newItem.my_next == null)
					m_master.registerHead(newItem);
				else
					newItem.my_next.setPrev(newItem);
			}
			
			// if this is empty, make sure we can drop it
			if(this.m_itemListHead == null)
			{
				// prev might be null if it is the tail, but next should never be null because
				// we have at least added an element because we are increasing counts.
				if(this.getPrev() == null)
				{
					m_master.registerTail(this.getNext());
					this.getNext().setPrev(null);
				}
				else
				{
					this.getPrev().setNext(this.getNext());  //// MARK
					this.getNext().setPrev(this.getPrev());
				}
			}
		}
	}		

	public void addCount(HashElement<T> item)
	{
		// if this is the only one in the list
		if(my_next == null && my_prev == null)
		{
			// if there is more then one object with this count
			if(this.ListLongerThenOne())
			{
				this.RemoveElement(item);
				// create a new object with +1 count
				CountListItem<T> newItem = new CountListItem<T>(item, m_master, this.m_count+1);
				newItem.setPrev(this);
				this.setNext(newItem);
				item.setCountList(newItem);
				// the new value will be the new head
				m_master.registerHead(newItem);
			} else // this is the only item with this count
			{
				// I can just increase the count of this item
				this.m_count++;
			}			
		}
		// if there are multiple different counts in the list, but this is the highest count
		else if(my_next == null)
		{
			if(this.ListLongerThenOne())
			{
				this.RemoveElement(item);
				CountListItem<T> newItem = new CountListItem<T>(item, m_master, this.m_count+1);
				newItem.setPrev(this);
				this.setNext(newItem);
				item.setCountList(newItem);
				m_master.registerHead(newItem);
			} else
			{
				this.m_count++;
			}
		}
		// if this is the one with the lowest count
		else if(my_prev == null)
		{
			if(this.ListLongerThenOne())
			{
				// if the next highest count has the correct count
				if(my_next.getCount() == this.m_count+1)
				{
					this.RemoveElement(item);
					this.my_next.InsertElement(item);
					//m_master.registerTail(this.my_next);
					//this.my_next.setPrev(null);
					//this.setNext(null); // ?
				}
				else
				{
					this.RemoveElement(item);
					CountListItem<T> newItem = new CountListItem<T>(item, m_master, this.m_count+1);
					newItem.setNext(this.getNext());
					this.getNext().setPrev(newItem);
					newItem.setPrev(this);
					this.setNext(newItem);
					item.setCountList(newItem);
				}
			}
			else
			{
				if(my_next.getCount() == this.m_count+1)
				{
					this.RemoveElement(item);
					this.my_next.InsertElement(item);
					// set the prev pointer to null, because the new element is now smallest
					this.my_next.my_prev = null;
					m_master.registerTail(this.my_next);
					// we can keep the next pointer because this object should just disappear evenutally
				}
				else
				{
					this.m_count++;
				}
			}
		}
		// if this is one of the elements in the middle of the list
		else
		{
			if(this.ListLongerThenOne())
			{
				// if the next highest counter has the correct count
				if(my_next.getCount() == this.m_count+1)
				{
					this.RemoveElement(item);
					this.my_next.InsertElement(item);
				}
				else
				{
					this.RemoveElement(item);
					CountListItem<T> newItem = new CountListItem<T>(item, m_master, this.m_count+1);
					newItem.setNext(this.getNext());
					this.getNext().setPrev(newItem);
					newItem.setPrev(this);
					this.setNext(newItem);
					item.setCountList(newItem);
				}
			}
			else
			{
				if(my_next.getCount() == this.m_count+1)
				{
					// if this is the only element in the list, we need to drop this bucket
					// and move the object to the next bucket
					this.RemoveElement(item);
					this.my_next.InsertElement(item);
					
					// now, drop this bucket
					this.my_next.setPrev(this.my_prev);
					this.my_prev.setNext(this.my_next);
				}
				else
				{
					this.m_count++;
				}
			}
		}
	}
	
	public boolean ListLongerThenOne() {
		return !m_itemListHead.isNextSelf();
	}
	
	public long getCount()
	{	return m_count;	}
	
	public void setNext(CountListItem<T> next)
	{	
		my_next = next;
	}
	
	public CountListItem<T> getNext()
	{	return my_next;	}
	
	public void setPrev(CountListItem<T> prev)
	{	
		my_prev	= prev;
	}
	
	public HashElement<T> getFirstItem()
	{	return m_itemListHead;	}
	
	public CountListItem<T> getPrev()
	{	return my_prev;	}
}
