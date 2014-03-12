package spark.test;

import java.io.Serializable;

public class CountList<T> implements Serializable{
	private CountListItem<T> head;
	private CountListItem<T> tail;
	
	public CountList( ) {
		head = tail = null;
	}
	
	public CountListItem<T> getHead()
	{	return head;	}
	
	public CountListItem<T> getTail()
	{	return tail;	}
	
	public void registerTail(CountListItem<T> newTail)
	{	tail = newTail;	}
	
	public void registerHead(CountListItem<T> newHead)
	{	head = newHead;	}	
	
	public void InsertNewCountListItem(HashElement<T> item)
	{
		if(tail != null && tail.getCount() == 1)
		{
			tail.InsertElement(item);
		}
		else
		{
			CountListItem<T> temp = new CountListItem<T>(item, this, 1);
			if(tail != null)
			{	// if this is not the only element in the list, make it the new tail
				tail.setPrev(temp);
				temp.setNext(tail);
				tail = temp;
			}
			else
			{	// if this is the first element to be inserted, the tail and head point to the same place
				tail = head = temp;
			}
		}
	}
}
