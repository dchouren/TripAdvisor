import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser 
{
	/**
	 * Reads hotel ratings from file into an ArrayList.
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static List<Double> readHotelRatings(String file) 
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
	
	
	public static List<ArrayList<Integer>> readUsers(String file) 
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
	
	public static List<ArrayList<Integer>> readVisitedHotels(String file) 
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
	        	int newUserIndex = Integer.parseInt(splitLine[0]);
	        	int hotelVisited = Integer.parseInt(splitLine[1]);
	        	
	        	// new user
	        	if (newUserIndex != oldUserIndex && oldUserIndex != -1) {

	        		userHotels.add(oldUserIndex, thisUserHotels);

		        	oldUserIndex = newUserIndex;
		        	
	        		thisUserHotels = new ArrayList<Integer>();
	        	}
	        	thisUserHotels.add(hotelVisited);
	        	
	            line = br.readLine();
	        }
	    }
		
		return userHotels;
	}
	
	public static void main(String[] args) 
			throws FileNotFoundException, IOException
	{
		/*List<Double> hotelRatings = readHotelRatings("hotels.txt");
		for (double rating : hotelRatings) {
			System.out.println(rating);
		}*/
		
		/*List<ArrayList<Integer>> users = readUsers("users.txt");
		for (ArrayList<Integer> user : users) {
			for (int stat : user) {
				System.out.print(stat + "\t");
			}
			System.out.println();
		}*/
		
		List<ArrayList<Integer>> userHotels = readVisitedHotels("activity.txt");
		for (ArrayList<Integer> user : userHotels) {
			for (int hotel : user) {
				System.out.print(hotel + "\t");
			}
			System.out.println();
		}
		
	}
}