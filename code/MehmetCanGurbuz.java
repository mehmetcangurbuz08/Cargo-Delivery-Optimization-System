import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Application to find the shortest route between given locations.
 * @author Mehmet Can Gürbüz , Student ID: 2022400177
 * @since Date: 04.04.2024
 */
public class MehmetCanGurbuz {
    private static final int MAX_ITERATIONS = 500; //Iteration number for the ant colony optimization approach
    private static final int NUM_ANTS = 50; // Number of ant circulate in each iteration

    public static void main(String[] args) {
        int chosenMethod = 1;  // set to 1 to use bruteforce , 2 to use Ant Colony Optimization
        int chosenGraph = 0;  // Set to 1 to see the shortest path , 2 to see pheromone density if you use ACO
        String inputFile = "input01.txt"; // choose a file to work on

        if (chosenMethod == 1) { // if chosen method = 1 use brute-force method
            double startTime = System.currentTimeMillis(); // create a variable for starting time
            ArrayList<ArrayList<Double>> locations = new ArrayList<>(); // create an arraylist for locations
            int migrosIdx = 0; // our migros is in the first line of the files

            try {
                File file = new File(inputFile);
                Scanner scanner = new Scanner(file);
                //Read the file line by line and add locations to the arraylist named locations
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    ArrayList<Double> coordinates = new ArrayList<>();
                    coordinates.add(Double.parseDouble(parts[0]));
                    coordinates.add(Double.parseDouble(parts[1]));
                    locations.add(coordinates);

                }
                scanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred while reading the file.");
                return;
            }
            // Create an initial route for comparisons and other important processes
            Integer[] initialRoute = new Integer[locations.size() - 1];
            int index = 0;
            for (int i = 0; i < locations.size(); i++) {
                if (i != migrosIdx) {
                    initialRoute[index++] = i;
                }
            }
            //create a bestRoute array to hold the shortest Route for brute-force
            Integer[] bestRoute = new Integer[initialRoute.length];
            permute( initialRoute, 0,bestRoute,locations,migrosIdx); // Find the shortest path bay using my permute method

            //Print our best route text by iteration
            String bestRouteText = "";
            for (int i = 0; i < bestRoute.length; i++) {
                bestRouteText += (bestRoute[i] + 1);
                if (i < bestRoute.length - 1) {
                    bestRouteText += " , ";
                }
            }
            if(chosenMethod == 1){ //Print method name
                System.out.println("Method: Brute-Force Method");
            }
            System.out.println("Shortest Distance: " + minDistance);// print the distance
            System.out.println("Shortest Route: " + "[1, " + bestRouteText + ", 1] ");

            // Drawing our shortest path by using Std.Draw for brute force
            int canvasWidth = 800;
            int canvasHeight = 800;
            StdDraw.setCanvasSize(canvasWidth, canvasHeight);
            StdDraw.setXscale(0, 1);
            StdDraw.setYscale(0, 1);
            double circleRadius = 0.02;// Set circle radius
            StdDraw.clear(StdDraw.WHITE);// Set colors
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.005);

            ArrayList<Double> firstLocation = locations.get(migrosIdx);// create an arraylist to hold first locations
            double locationX = firstLocation.get(0);
            double locationY = firstLocation.get(1);
            for (int idx : bestRoute) {// iterate the best route to drawing connections
                ArrayList<Double> nextLocation = locations.get(idx); //create an arraylist to hold second locations
                double nextLocationX = nextLocation.get(0);
                double nextLocationY = nextLocation.get(1);
                StdDraw.line(locationX, locationY, nextLocationX, nextLocationY);
                locationX = nextLocationX;
                locationY = nextLocationY;
            }
            StdDraw.line(locationX, locationY, firstLocation.get(0), firstLocation.get(1)); // draw last connection to the  starting location
            for (int i = 0; i < locations.size(); i++) { // use for loop to iterate locations array and hold x and y values of the locations
                ArrayList<Double> location = locations.get(i);
                if (i == migrosIdx) {
                    StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE); // set migros circle to orange
                } else {
                    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);// set other circles to gray
                }
                StdDraw.filledCircle(location.get(0), location.get(1), circleRadius);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.text(location.get(0), location.get(1), Integer.toString(i + 1));// write number of locations on the circle
            }
            StdDraw.show();
            double endTime = System.currentTimeMillis(); // create a variable to hold end time
            double duration = (endTime - startTime) / 1000.; // create a variable to hold duration time in seconds
            System.out.println("Time it takes to find the shortest path: " + duration + " seconds"); // print the duration time
        }
        if (chosenMethod == 2) {//Ant Colony Optimization
            long startTime = System.currentTimeMillis();// create a variable to hold starting time

            // Initialize myAnts object from AntColonyOptimization class
            AntColonyOptimization myAnts = new AntColonyOptimization(inputFile, NUM_ANTS, MAX_ITERATIONS,chosenGraph);
            myAnts.solve(); // use solve method

            long endTime = System.currentTimeMillis();// create a variable to hold end time
            double duration = (endTime - startTime) / 1000.0;
            System.out.println("Time it takes to find the shortest path: " + duration + " seconds");// print the duration time
        }
    }
    public static double minDistance = Double.MAX_VALUE; // create public static variable to hold minDistance for bruteforce
    // create a permute method which takes 5 arguments
    // It uses all permutations of an arr(initial route) array and find the shortest route by calculating distances of the routes
    private static void permute( Integer[] arr, int k , Integer[] bestRoute , ArrayList<ArrayList<Double>> locations, int migrosIdx) {
        if (k == arr.length) { // checking which route is shortest
            double distance = calculateRouteDistance(arr, locations, migrosIdx);
            if(distance < minDistance){
                minDistance = distance;
                System.arraycopy(arr,0,bestRoute,0,arr.length);
            }
        } else {// recursive permutation part
            for (int i = k; i < arr.length; i++) {
                Integer temp = arr[i];
                arr[i] = arr[k];
                arr[k] = temp;
                permute( arr, k + 1,bestRoute,locations,migrosIdx);
                temp = arr[k];
                arr[k] = arr[i];
                arr[i] = temp;
            }
        }
    }
    // create calculateRouteDistance method for calculating distances along the path by using for-each loop
    private static double calculateRouteDistance(Integer[] route, ArrayList<ArrayList<Double>> locations, int migrosIdx) {
        double distance = 0;//create a distance variable for returning
        int prevIdx = migrosIdx;
        for (int locationIdx : route) {
            distance += calculateDistance(locations.get(prevIdx), locations.get(locationIdx));
            prevIdx = locationIdx;
        }
        distance += calculateDistance(locations.get(prevIdx), locations.get(migrosIdx));// add distance of last index to migros index to distance variable
        return distance; // return distances
    }
    // create calculateDistance method to calculate distance between to location
    // it takes two arraylist arguments and use their first and second indexes as x and y values of the locations
    private static double calculateDistance(ArrayList<Double> loc1, ArrayList<Double> loc2) {
        return Math.sqrt(Math.pow(loc1.get(0) - loc2.get(0), 2) + Math.pow(loc1.get(1) - loc2.get(1), 2)); // return distances
    }
}
class AntColonyOptimization {// create a AntColonyOptimization class for calculate best route and take faster results
    private static final int chosenMethod = 2; // if we use this class our chosen method must be 2
    private static final double ALPHA = 0.7; // Pheromone importance
    private static final double BETA = 2.5; // Distance importance
    private static final double DEG = 0.6; // Pheromone degradation rate
    private static final double Q = 0.0001; // Constant
    private ArrayList<ArrayList<Double>> locations; // Create this variable to hold locations
    private int migrosIdx = 0; // Migros index is always 0 because of our first location on the files is migros
    private int numAnts; // Number of ants to circulate in each iteration
    private int maxIterations; // Iteration number
    private Integer[] bestRoute; // Create a variable to hold the shortest route
    private int chosenGraph; // Set chosen graph to 1 for seeing the shortest path , set to 2 for seeing pheromone density
    // create a constructor which takes 4 arguments
    public AntColonyOptimization(String filename, int numAnts, int maxIterations,int chosenGraph) {
        this.chosenGraph = chosenGraph;
        this.numAnts = numAnts;
        this.maxIterations = maxIterations;
        locations = new ArrayList<>();// create an empty arraylist to put in the locations
        readFile(filename);// use readFile method to hold locations from the file
    }
    // create a readFile method to hold locations on the file and put them into the locations arraylist
    private void readFile(String filename) {
        try {
            File file = new File(filename);// //Read the file line by line and add locations to the arraylist named locations
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                ArrayList<Double> coordinates = new ArrayList<>();
                coordinates.add(Double.parseDouble(parts[0]));
                coordinates.add(Double.parseDouble(parts[1]));
                locations.add(coordinates);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the file.");
        }
    }
    // create a method named solve to find solution and print the screen
    public void solve() {
        double minDistance = Double.MAX_VALUE;// create a minDistance variable for ACO
        bestRoute = null;

        double[][] pheromones = initializePheromones(); // create a matrix for pheromones between every location by using two-dimensional array

        for (int iteration = 0; iteration < maxIterations; iteration++) { // Iterate maxIteration times by using for loop
            double[][] deltaPheromones = new double[locations.size()][locations.size()]; // create deltaPheromones array for every iteration

            for (int ant = 0; ant < numAnts; ant++) { // Each iteration circulate numAnts number of ants
                Integer[] route = constructSolution(pheromones); // Use construct solution method to take routes from it and assign it to route array
                double distance = calculateRouteDistance(route); // Use calculateRouteDistance method to calculate the route's distance

                if (distance < minDistance) { // compare distances to find the shortest path
                    minDistance = distance;
                    bestRoute = route; // assign route to best route because in this iteration route is the shortest
                }
                updateDeltaPheromones(deltaPheromones, route, distance); // use this method the update delta pheromones in each ant circulation
            }
            updatePheromones(pheromones, deltaPheromones); // use this method the update pheromones in each iteration of 50 ants
        }
        if(chosenGraph== 1){ // if chosenGraphs is 1 draw shortest path
            drawSolution(locations,bestRoute,migrosIdx);
        } else if (chosenGraph==2) { // if chosenGraphs is 2 draw pheromones density
            drawPheromoneDensity(pheromones,locations);
        }
        //Print our best route text by iteration
        String bestRouteText = "";
        for (int i = 0; i < bestRoute.length; i++) {
            bestRouteText += (bestRoute[i] + 1);
            if (i < bestRoute.length - 1) {
                bestRouteText += " , ";
            }
        }
        if(chosenMethod == 2){ // Print method name
            System.out.println("Method: Ant Colony Optimization");
        }
        System.out.println("Shortest Distance: " + minDistance); // Print the distance
        System.out.println("Shortest Route: " + "[" + bestRouteText + ", 1] ");
    }
    // create a method named initializePheromones to assign our two-dimensional pheromones array to initial values
    private double[][] initializePheromones() {
        double[][] pheromones = new double[locations.size()][locations.size()];
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones.length; j++) {
                pheromones[i][j] = 0.1; // Initialize with arbitrary value to every value in the array
            }
        }
        return pheromones; // return our two-dimensional array
    }
    // create a method named drawPheromoneDensity to draw final pheromone density on the canvas
    private static void drawPheromoneDensity(double[][] pheromones, ArrayList<ArrayList<Double>> locations) {
        // Set the canvas and pen features
        int canvasWidth = 800;
        int canvasHeight = 800;
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        double circleRadius = 0.02;
        StdDraw.clear(StdDraw.WHITE);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setFont(new Font("Serif", Font.BOLD, 14));

        // use for loops to achieve values of the pheromones
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0 ; j < pheromones.length; j++) {
                double thickness = pheromones[i][j]  ; //proportioning the thickness to the pheromones values
                if (thickness > 0) {
                    ArrayList<Double> startLocation = locations.get(i); // create an arraylist to hold x and y values
                    ArrayList<Double> endLocation = locations.get(j); // create an arraylist to hold x and y values
                    StdDraw.setPenColor(Color.BLACK); // Set color
                    StdDraw.setPenRadius(thickness * 4 ); // Set pen radius
                    StdDraw.line(startLocation.get(0), startLocation.get(1), endLocation.get(0), endLocation.get(1)); // Draw lines between every location
                }
            }
        }
        // Draw all location circles
        for (int i = 0; i < locations.size(); i++) {
            ArrayList<Double> location = locations.get(i); // create an arraylist to hold x and y values
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.filledCircle(location.get(0), location.get(1), circleRadius); //Draw circles
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(location.get(0), location.get(1), Integer.toString(i + 1));
        }
    }
    // create a method named constructSolution to find the routes and return them
    private Integer[] constructSolution(double[][] pheromones) {
        Integer[] route = new Integer[locations.size()]; // create an array to hold routes
        int index = 0; // create an index variable
        int currentCity = migrosIdx;
        boolean[] visited = new boolean[locations.size()]; // create a boolean array to hold which location is visited and which is not
       // Use while loop to create route
        while (index < route.length) {
            int nextCity = selectNext(currentCity, visited, pheromones); // use selectNext method to select next location
            route[index++] = nextCity;
            visited[nextCity] = true; // assign true to every visited location
            currentCity = nextCity;
        }
        return route; // return route for other methods usage
    }
    // create a method named selectNext to select next location probabilistically
    private int selectNext(int currentCity, boolean[] visited, double[][] pheromones) {
        double[] edgeValues = new double[pheromones.length]; // create an edgeValues array for hold probability of every road
        double total = 0.0; // initialize a total variable
        // use for loop to assign values to te edgeValues
        for (int i = 0; i < edgeValues.length; i++) {
            if (!visited[i]) { // check the next location is visited or not
                // assign values to edgeValues by using ALPHA and BETA constants
                edgeValues[i] = Math.pow(pheromones[currentCity][i], ALPHA) *
                        Math.pow(1.0 / calculateDistance(locations.get(currentCity), locations.get(i)), BETA);
                total += edgeValues[i];
            }
        }
        // roulette wheel selection algorithm
        double rand = Math.random() * total; // create a variable rand and assign it to random double value
        double sum = 0.0; // initialize sum variable
        for (int i = 0; i < edgeValues.length; i++) {
            if (!visited[i]) { // check the city is visited or not
                sum += edgeValues[i];
                if (sum >= rand) {
                    return i; // if sum is bigger than our random number return this location as next location
                }
            }
        }
        throw new RuntimeException("No city selected");// there can be a runtime exception
    }
    // create a method named updateDeltaPheromones to update an array after each ant circulation
    private void updateDeltaPheromones(double[][] deltaPheromones, Integer[] route, double distance) {
        for (int i = 0; i < route.length -1; i++) { // Use for loop to assign new values to the deltaPheromones array
            int from = route[i];
            int to = route[i + 1];
            deltaPheromones[from][to] += Q / distance; // Use Q constant to update the values
            deltaPheromones[to][from] += Q / distance; // Use Q constant to update the values
        }
    }
    // create a method named updatePheromones to assign new pheromone values to the array after each iteration of the ant colony
    private void updatePheromones(double[][] pheromones, double[][] deltaPheromones) {
        // Use for loops to assign new values
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones.length; j++) {
                pheromones[i][j] = DEG * pheromones[i][j] + deltaPheromones[i][j]; //Use degradation constant and deltaPheromones to update the values of an array
            }
        }
    }
    // Create a method named calculateRouteDistance to calculate route distance
    private double calculateRouteDistance(Integer[] route) {
        double distance = 0; // initialize variable
        // use for loop to iterate between every location on the route
        for (int i = 0 ; i< route.length-1 ; i++) {
            distance += calculateDistance(locations.get(route[i]), locations.get(route[i+1])); // Use calculateDistance method to add distances to the distance variable
        }
        distance += calculateDistance(locations.get(route[route.length-1]), locations.get(migrosIdx)); // Add distance between last index of the route and migros index
        return distance;
    }
    // create calculateDistance method to calculate distance between to location
    // it takes two arraylist arguments and use their first and second indexes as x and y values of the locations
    private double calculateDistance(ArrayList<Double> loc1, ArrayList<Double> loc2) {
        return Math.sqrt(Math.pow(loc1.get(0) - loc2.get(0), 2) + Math.pow(loc1.get(1) - loc2.get(1), 2)); // return distances
    }
    // create a method named drawSolution to drawing the shortest path and location on the canvas
    private static void drawSolution(ArrayList<ArrayList<Double>> locations, Integer[] bestRoute, int migrosIdx) {
        // Drawing our shortest path by using Std.Draw for ACO
        // Set the canvas
        int canvasWidth = 800;
        int canvasHeight = 800;
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        double circleRadius = 0.02; // set circle radius
        StdDraw.clear(StdDraw.WHITE);
        StdDraw.setPenColor(StdDraw.BLACK); // Set pen color
        StdDraw.setFont(new Font("Serif", Font.BOLD, 14)); // Set font

        // Draw connections between locations
        ArrayList<Double> firstLocation = locations.get(migrosIdx);// Create an arraylist to hold x and y values of the migros
        double locationX = firstLocation.get(0);
        double locationY = firstLocation.get(1);
        for (int idx : bestRoute) { // Use for loop to draw all connections between locations
            ArrayList<Double> nextLocation = locations.get(idx); // Create an arraylist to hold x and y values of the locations
            double nextLocationX = nextLocation.get(0);
            double nextLocationY = nextLocation.get(1);
            StdDraw.setPenRadius(0.007);
            StdDraw.line(locationX, locationY, nextLocationX, nextLocationY); // Draw connection lines between the locations
            locationX = nextLocationX;
            locationY = nextLocationY;
        }
        StdDraw.line(locationX, locationY, firstLocation.get(0), firstLocation.get(1)); // Draw line between last index of the route and migros
        // Draw locations by using circles
        for (int i = 0; i < locations.size(); i++) {
            ArrayList<Double> location = locations.get(i); // Create an arraylist to hold x and y values of the locations
            if (i == migrosIdx) {
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE); // Draw migros circle to Orange
            } else {
                StdDraw.setPenColor(StdDraw.LIGHT_GRAY); // Draw other circles to gray
            }
            StdDraw.filledCircle(location.get(0), location.get(1), circleRadius);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(location.get(0), location.get(1), Integer.toString(i + 1)); // Write number on the circles
        }
        StdDraw.show();
    }
}