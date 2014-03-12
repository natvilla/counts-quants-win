package spark.test;

import java.io.Serializable;

public class HashValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// TODO: have modifiers be randomly generated (with similar seed?)
	// currently they are just a bunch of random prime numbers
	private static final int[] modifiers = {3433, 6547, 9677, 4639, 6142, 9091, 7481, 6869};
	
	//int hash_count;
	
	private int m_hash;
	private int used_hash;
	
	public HashValue(int hash){
		m_hash    = hash;
		used_hash = 0;
	}
	
	public HashValue(Object h){
		m_hash = h.hashCode();
	}
	
//	public static void increase_hashes() throws Exception
//	{
//		System.out.println("INCREASING HASH COUNT TO " + (hash_count+1));
//		if(hash_count < modifiers.length)
//			hash_count++;
//		else
//			throw new Exception("Can't increase hash count any more!");
//	}
//	
//	public static int get_hashCount()
//	{
//		return hash_count;
//	}
	
	/*
	 * get the used hash index
	 */
	public int getUsedHash()
	{	return used_hash;	}
	
	/*
	 * set the hash value that is currently being used
	 */
	public void setUsedHash(int h)
	{	used_hash = h;	}
	
	public void increaseUsedHash(int hash_count)
	{	used_hash = (used_hash+1) % hash_count;		}
	
	
	public int hash() {
		return this.hash(used_hash);
	}
	/*
	 * hash with a given index
	 */
	public int hash(int idx) {
		int t = MurmurHash2.hash(HashValue.intToByteArray(m_hash), modifiers[idx]);
		return t < 0 ? -t : t;
	}
	
	/*
	 * hash with a ceiling
	 */
	public int hash(int idx, int ceil){
		int t = this.hash(idx) % ceil;
		return t;
	}
	
	@Override
	public int hashCode()
	{
		return this.hash(0);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(this == other) return true;
		if( !(other instanceof HashValue)) return false;
		
		HashValue aThat = (HashValue) other;
		
		return this.m_hash == aThat.m_hash;
	}
	
	private static final byte[] intToByteArray(int value) {
	    return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
	}
	
}
