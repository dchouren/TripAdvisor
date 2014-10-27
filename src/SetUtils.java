/**
 * Utility class for basic set operations.
 * @author dchouren
 *
 */
public class SetUtils 
{
	/**
	 * Return the number of nonzero elements in array.
	 * @param array
	 * @return
	 */
	public static int getSize(int[] array)
	{
		int size = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != 0)
				size++;
		}
		
		return size;
	}
	public static int getSize(double[] array)
	{
		int size = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != 0)
				size++;
		}
		
		return size;
	}
	
	/**
	 * Return the sum of the values in array.
	 * @param array
	 * @return
	 */
	public static double additiveSize(double[] array) 
	{
		double size = 0;
		for (int i = 0; i < array.length; i++) {
			size += array[i];
		}
		
		return size;
	}
	
	/**
	 * Return the intersection of nonzero values in array1 and array2.
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static int intersection(double[] array1, double[] array2) 
	{
		assert(array1.length == array2.length);
		
		int intersection = 0;
		for (int i = 0; i < array1.length; i++) {
			if (!Epsilon.isZero(array1[i]) && !Epsilon.isZero(array2[i]))
				intersection += 1;
		}
		
		return intersection;
	}
	
	
	/**
	 * Return the additive intersection of nonzero values in array1 and array2.
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static int additiveIntersection(double[] array1, double[] array2) 
	{
		assert(array1.length == array2.length);
		
		int intersection = 0;
		for (int i = 0; i < array1.length; i++) {
			if (!Epsilon.isZero(array1[i]) && !Epsilon.isZero(array2[i]))
				intersection += 2 * (array1[i] + array2[i]);
		}
		
		return intersection;
	}
	
	
	/**
	 * Return the union of nonzero values in array1 and array2.
	 */
	public static int union(double[] array1, double[] array2) 
	{
		assert(array1.length == array2.length);
		
		int union = 0;
		for (int i = 0; i < array1.length; i++) {
			if (!Epsilon.isZero(array1[i]) || !Epsilon.isZero(array2[i]))
				union += 1;
		}
		
		return union;
	}
}















