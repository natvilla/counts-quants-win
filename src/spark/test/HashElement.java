package spark.test;

import java.io.Serializable;

public class HashElement<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T p_item;
	private long p_count;
	private long p_epsilon;
	private HashValue p_hashValue;
	
	private HashElement<T> prev;
	private HashElement<T> next;
	
	CountListItem<T> counter;
	
	public HashElement(T item)
	{
		this(item, 0, 0, null, null, null);
	}
	
	public HashElement(T item, long count, long epsilon)
	{
		this(item, count, epsilon, null, null, null);
	}
	
	public HashElement(T item, long count, long epsilon, HashElement<T> prv, HashElement<T> nxt, CountListItem<T> cnt)
	{
		p_item      = item;
		p_hashValue = new HashValue(p_item);		

		p_count     = count;
		p_epsilon   = epsilon;
		
		counter     = cnt;
		
		if(prv == null)
			prev = this;
		else
			prev = prv;
		
		if(nxt == null)
			next = this;
		else
			next = nxt;
	}
	
	/*
	 * be very careful when calling this method.  It will modify the hash
	 * table elements, so make sure the element is not replaced while
	 * it is still in the hash table.  Otherwise it might make this
	 * element very difficult to find.
	 * 
	 * This also updates the epsilon value
	 * 
	 * TODO: fix this
	 */
	public void ReplaceElement(T item)
	{
		p_item      = item;
		p_hashValue = new HashValue(p_item);
		p_epsilon   = p_count;
	}
	
	public void updatePointers(HashElement<T> prv, HashElement<T> nxt)
	{
		if(prv == null)
			prev = this;
		else
			prev = prv;
		
		if(nxt == null)
			next = this;
		else
			next = nxt;		
	}
	
	public void updatePrevPointer(HashElement<T> prv)
	{
		if(prv == null)
			prev = this;
		else
			prev = prv;		
	}
	
	public void updateNextPointer(HashElement<T> nxt)
	{
		if(nxt == null)
			next = this;
		else
			next = nxt;			
	}
	
	public void increaseCount()
	{	
		p_count++;
		counter.addCount(this);
	}
	
	public void increaseCounters(long count, long eps)
	{
		p_count   += count;
		p_epsilon += eps;
		
		counter.addCount(this, count);
	}
	
	public void setCountList(CountListItem<T> cnt)
	{	counter = cnt;	}
	
	public void removeCountList()
	{
		if(counter != null)
			counter.RemoveElement(this);
	}
	
	public long getCount()
	{	return counter.getCount();	}
	//{	return p_count;	}
	
	public long getEpsilon()
	{	return p_epsilon;	}
	
	public T getItem()
	{	return p_item;	}
	
	public HashElement<T> getNext()
	{	return next;	}
	
	public HashElement<T> getPrev()
	{	return prev;	}
	
	public boolean isNextSelf()
	{	return next == this;	}
	
	public HashValue getHashValue()
	{	return p_hashValue;	}
	
	@Override
	public boolean equals(Object e)
	{
		if(!(e instanceof HashElement<?>))
			return false;
		
		@SuppressWarnings("unchecked")
		HashElement<T> temp = (HashElement<T>) e;
		
		return this.p_hashValue.equals(temp.p_hashValue) && this.p_item.equals(temp.p_item);
	}
	
	@Override
	public int hashCode()
	{	return p_hashValue.hashCode();	}
}
