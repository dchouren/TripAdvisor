/**
 * Helper class for double comparisons.
 * @author dchouren
 */
public class Epsilon {
	
	private final static double EPSILON = 1E-6;
	
	/**
	 * Epsilon double comparison. 
	 * @param value
	 * @return
	 */
	public static boolean areEqual(double value1, double value2) 
	{
		return Math.abs(value1 - value2) < EPSILON;
	}
	

	/**
	 * Helper method for double 0 comparison.
	 * @param value
	 * @return
	 */
	public static boolean isZero(double value) 
	{
		return areEqual(value, 0.0);
	}
}
