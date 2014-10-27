import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UHMatrix
{
	private Map<Integer, ArrayList<Integer>> memberHotelMap;
	private Map<Integer, ArrayList<Integer>> hotelMemberMap;
	private double[][] matrix;
	private int numHotels;
	private int numUsers;

	public UHMatrix()
	{
		memberHotelMap = new HashMap<Integer, ArrayList<Integer>>();
		hotelMemberMap = new HashMap<Integer, ArrayList<Integer>>();
	}
	
	/*public UHMatrix(String file) throws FileNotFoundException, IOException
	{
		UHMatrix matrix = new UHMatrix();
		initialize(file);
	}*/

	/**
	 * Initialize UHMatrix object.
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void initialize(String file) throws FileNotFoundException, IOException
	{
		readVisitedHotels(file);
		numHotels = hotelMemberMap.size();
		numUsers = memberHotelMap.size();
		matrix = new double[numUsers][numHotels];

		fillMatrix();
		hotelTfIdf();
	}

	/**
	 * Reads hotel ratings from file.
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private List<Double> readHotelRatings(String file) 
			throws FileNotFoundException, IOException
	{
		List<Double> hotelRatings = new ArrayList<Double>();

		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String firstLine = br.readLine();  // want to skip first line

			String line = br.readLine();

			while (line != null) {
				String[] splitLine = line.split("\t");
				int hotelNumber = Integer.parseInt(splitLine[0]);
				double rating = Double.parseDouble(splitLine[1]);
				hotelRatings.add(hotelNumber - 1, rating);

				line = br.readLine();
			}
		}

		return hotelRatings;
	}

	/**
	 * Reads users from file.
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private List<ArrayList<Integer>> readUsers(String file) 
			throws FileNotFoundException, IOException
	{
		List<ArrayList<Integer>> users = new ArrayList<ArrayList<Integer>>();

		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String firstLine = br.readLine();  // want to skip first line

			String line = br.readLine();

			while (line != null) {
				String[] splitLine = line.split("\t");
				int userIndex = Integer.parseInt(splitLine[0]);
				int continent = Integer.parseInt(splitLine[1]);

				// assign gender as 0 for male, 1 for female
				String gender = splitLine[2];
				int genderInt = gender.charAt(0) == 'm' ? 0 : 1;

				ArrayList<Integer> thisUser = new ArrayList<Integer>();
				thisUser.add(continent); 
				thisUser.add(genderInt);

				users.add(userIndex - 1, thisUser);

				line = br.readLine();
			}
		}

		return users;
	}

	/**
	 * Read hotel visit info from file.
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private List<ArrayList<Integer>> readVisitedHotels(String file) 
			throws FileNotFoundException, IOException
	{
		List<ArrayList<Integer>> userHotels =
				new ArrayList<ArrayList<Integer>>();

		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String firstLine = br.readLine();  // want to skip first line

			String line = br.readLine();
			int oldUserIndex = -1;
			ArrayList<Integer> thisUserHotels = new ArrayList<Integer>();

			while (line != null) {
				String[] splitLine = line.split("\t");
				
				// use -1 to switch from 1-based indexing to 0-based indexing
				int newUserIndex = Integer.parseInt(splitLine[0]) - 1;
				int hotelVisited = Integer.parseInt(splitLine[1]) - 1;

				// new user
				if (newUserIndex != oldUserIndex) {

					userHotels.add(thisUserHotels);

					if (oldUserIndex != -1) {

						// update which hotels this user has been to
						memberHotelMap.put(oldUserIndex, thisUserHotels);
					}

					oldUserIndex = newUserIndex;

					thisUserHotels = new ArrayList<Integer>();
				}
				thisUserHotels.add(hotelVisited);

				// update maps
				// update which users have been to this hotel
				if (this.hotelMemberMap.containsKey(hotelVisited)) {
					this.hotelMemberMap.get(hotelVisited).add(newUserIndex);
				}
				else {
					ArrayList<Integer> users = new ArrayList<Integer>();
					users.add(newUserIndex);
					this.hotelMemberMap.put(hotelVisited, users);
				}

				line = br.readLine();
			}

			// make sure we get the last user
			// last user change is not caught in the while loop
			memberHotelMap.put(oldUserIndex, thisUserHotels);
		}

		return userHotels;
	}


	/**
	 * Fill matrix with visited hotels.
	 */
	public void fillMatrix()
	{
		for (int i = 0; i < this.matrix.length; i++) {
			List<Integer> theseHotels = memberHotelMap.get(i);

			// mark hotels that have been visited by user
			for (int j = 0; j < theseHotels.size(); j++) {
				matrix[i][theseHotels.get(j)] = 1.0;
			}
		}
	}

	/**
	 * Helper function to help calculate tf-idf score.
	 * @param termFrequency
	 * @param inverseDocumentFrequency
	 * @return
	 */
	private static double tfIdf(double termFrequency,
			double inverseDocumentFrequency)
	{
		double tf = 0;
		if (!Epsilon.areEqual(0, termFrequency)) {
			tf = 1;
		}
		double idf = Math.log(inverseDocumentFrequency);

		return tf * idf;
	}

	/**
	 * Calculate tfIdf scores for the UHMatrix.
	 */
	private void hotelTfIdf()
	{
		int numCols = this.hotelMemberMap.size();
		int numRows = this.memberHotelMap.size();

		double[][] newMatrix = new double[numRows][numCols];

		for (int i = 0; i < numRows; i++) {

			double[] rowHotels = matrix[i];
			int totalHotels = SetUtils.getSize(rowHotels);

			for (int j = 0; j < numCols; j++) {
				double termFreq = (double) rowHotels[j] / totalHotels; 
				int hotelUsers = this.hotelMemberMap.get(j).size();

				double inverseDocFreq = (double) numRows / hotelUsers;

				newMatrix[i][j] = tfIdf(termFreq, inverseDocFreq);
			}
		}

		this.matrix = newMatrix;
	}

	/**
	 * Calculate a modified Jaccard similarity between two arrays.
	 * @param array1
	 * @param array2
	 * @return
	 */
	private static double jaccard(double[] array1, double[] array2)
	{
		int intersection = SetUtils.additiveIntersection(array1, array2);
		int union = SetUtils.union(array1, array2);

		if (union == 0)
			return 1;

		double similarity = (double) intersection / union;

		return similarity;
	}

	/**
	 * Calculate similarity between a user specified with an index and the
	 * other users.
	 * @param index
	 * @return
	 */
	private ArrayList<Double> getSimilarityScores(int index)
	{
		ArrayList<Double> similarityScores = new ArrayList<Double>();
		double[] thisRow = this.matrix[index];

		for (int i = 0; i < numUsers; i++) {
			double[] compareRow = this.matrix[i];
			double thisSimilarityScore = jaccard(thisRow, compareRow);
			similarityScores.add(i, thisSimilarityScore);
		}

		return similarityScores;
	}

	/**
	 * Find the total similarity-weighted hotel preferences from all users.
	 * @param similarityScores
	 * @return
	 */
	private double[] getSumWeights(ArrayList<Double> similarityScores)
	{
		double[] sumWeightedScores = new double[numHotels];

		for (int i = 0; i < numUsers; i++) {
			double[] tfIdfScores = this.matrix[i];
			double userSimilarityWeight = similarityScores.get(i);

			for (int j = 0; j < numHotels; j++) {
				sumWeightedScores[j] += userSimilarityWeight * tfIdfScores[j];
			}
		}
		/*for (int i = 0; i < sumWeightedScores.length; i++) {
			System.out.println("i: " + i);
			System.out.println(similarityScores.get(i));
			System.out.println(sumWeightedScores[i]);
		}*/
//		System.out.println();
//		System.out.println("These are our sum weights: ");
//		System.out.println(Arrays.toString(sumWeightedScores));

		return sumWeightedScores;
	}

	/**
	 * Return the best hotel choice.
	 * @param sumWeights
	 * @param index
	 * @return
	 */
	private int getBestHotel(int index)
	{
		ArrayList<Double> similarityScores = getSimilarityScores(index);
		double[] sumWeights = getSumWeights(similarityScores);
		
		ArrayList<Integer> visited = this.memberHotelMap.get(index);

//		System.out.println();
//		System.out.println("Eliminate these choices");
//		for(int i = 0; i < visited.size(); i++) {
//			System.out.println(visited.get(i));
//		}
//		System.out.println();

		double maxWeight = Double.NEGATIVE_INFINITY;
		int bestHotelIndex = -1;

		for (int i = 0; i < numHotels; i++) {
			if (sumWeights[i] > maxWeight) {
				if (!visited.contains(i)) { 
					maxWeight = sumWeights[i];
					bestHotelIndex = i;
				}
			}
		}

		int bestHotel = bestHotelIndex + 1;
		return bestHotel;
	}

	/**
	 * Return a 0-indexed list of hotel preferences. User 1's hotel preference
	 * is at index 0.
	 * @return
	 */
	private ArrayList<Integer> getBestHotels() 
	{
		ArrayList<Integer> bestHotels = new ArrayList<Integer>();
		
		for (int i = 0; i < numUsers; i++) {
			bestHotels.add(i, getBestHotel(i));
		}
		
		return bestHotels;
	}


	/**
	 * Iterate through a map's keys and values.
	 * @param map
	 */
	private static void iterateMap(Map<Integer, ArrayList<Integer>> map) 
	{
		int maxHotels = -1;
		int minHotels = Integer.MAX_VALUE;

		for (Map.Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
			Integer key = entry.getKey();
			ArrayList<Integer> value = entry.getValue();

			int numHotels = value.size();
			if (numHotels > maxHotels) {
				maxHotels = numHotels;
			}
			if (numHotels < minHotels) {
				minHotels = numHotels;
			}
			//		    System.out.println(value.size());
		}

		System.out.println(maxHotels);
		System.out.println(minHotels);

		//		System.out.println(map.get(3).size());
	}


	public static void writeCSV(ArrayList<Integer> results, String file) 
			throws IOException
	{
		String delimiter = "\t";
		String columns = "Users" + delimiter + "Hotel" + "\n";
		
		BufferedWriter out = null;
		try  
		{
		    FileWriter fstream = new FileWriter(file, false); 
		    out = new BufferedWriter(fstream);
		    out.write(columns);
		    for (int i = 0; i < results.size(); i++) {
		    	out.write((i+1) + delimiter + results.get(i) + "\n");
		    }
		}
		catch (IOException e)
		{
		    System.err.println("Error: " + e.getMessage());
		}
		finally
		{
		    if(out != null) {
		        out.close();
		    }
		}
	}
	
	public static void main(String[] args) 
			throws FileNotFoundException, IOException
	{
		UHMatrix matrix = new UHMatrix();
		matrix.initialize("activity.txt");

		int indexToTest = 0;

//		for (int i = 0; i < matrix.matrix.length; i++) {
//			System.out.println(Arrays.toString(matrix.matrix[i]));
//		}
		 
		//		for (int i = 0; i < similarityScores.size(); i++) {
		//			System.out.println(similarityScores.get(i));
		//		}
//		
//		int bestHotel = matrix.getBestHotel(indexToTest);
//		System.out.println("best " + bestHotel);

		ArrayList<Integer> bestHotels = matrix.getBestHotels();
		/*for (Integer integer : bestHotels) {
			System.out.println(integer);
		}*/
		
		writeCSV(bestHotels, "results.txt");
		//XXX: TODO: return most popular value for visitors that have nothing
		// in common with other visitors
	}
}












