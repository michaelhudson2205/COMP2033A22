package degreePlanningGraphApp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DegreeApp
{
	public static void main(String[] args)
	{
		// Set up for getting user input
		String path = "C:\\Users\\micha\\Dropbox\\Uni_SA\\COMP2033\\Assessments\\A_2_2\\";
		Scanner scanner = new Scanner(System.in);
		
		// Ask the student for the name of the input file
		System.out.print("Enter the name of the input file: ");
		String fileName = scanner.nextLine();
		path += fileName;
		
		// Ask the student for the maximum number of units they can study concurrently
		int maxUnits = 0;
		while (maxUnits < 1 || maxUnits > 4)
		{
			System.out.print("Enter the maximum number of units you can study concurrently (1-4): ");
			maxUnits = scanner.nextInt();
			if (maxUnits < 1 || maxUnits > 4)
			{
				System.out.println("Invalid input. Please enter a value between 1 and 4.");
			}
		}
		scanner.close();
		
		// Create data storage structures for processing of input file
		List<String> allUnits = new ArrayList<>();
		Map<String, List<String>> unitPrerequisites = new HashMap<>();
		Map<String, List<String>> adjacencyList = new HashMap<>();
		
		// Read and prepare input file
		try (BufferedReader br = new BufferedReader(new FileReader(path)))
		{
			String line;
			boolean firstLine = true;
			
			while ((line = br.readLine()) != null)
			{
				if (firstLine)
				{
					// Capture the first line containing all units
					allUnits = Arrays.stream(line.split(","))
							.map(String::trim)
							.collect(Collectors.toList());
					firstLine = false;
					continue; // Skip to next iteration to avoid processing the first line again
				}
				
				// Split the line by commas to separate the unit code and its prerequisites
				String[] parts = line.split(",");
				String unitCode = parts[0].trim();
				
				List<String> prerequisites = Arrays.stream(parts)
						.skip(1) // Skip the unit code
						.map(String::trim) // Trim the leading and trailing spaces
						.collect(Collectors.toList());
				
				// Store in the HashMap
				unitPrerequisites.put(unitCode, prerequisites);
			}
		} // >>>>>>>>>>end of try block<<<<<<<<<<
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Initialise the adjacency list for all units
		for (String unit : allUnits)
		{
			adjacencyList.put(unit, new ArrayList<>());
		}
		
		// Populate the adjacency list based on the unitPrerequisites map
		for (Map.Entry<String, List<String>> entry : unitPrerequisites.entrySet())
		{
			String unit = entry.getKey();
			for (String prerequisite : entry.getValue())
			{
				adjacencyList.get(prerequisite).add(unit);
			}
		}
		
		// Perform topological sort
		List<String> topologicalOrder = topologicalSort(allUnits, adjacencyList);
		if (topologicalOrder != null)
		{
			System.out.println("\nTopological Order:");
			for (String unit : topologicalOrder)
			{
				System.out.println(unit);
			}
			
			// Print the adjacency list
			System.out.println("\nAdjacency List:");
			for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet())
			{
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
			
			// Find and print the longest paths
			List<List<String>> longestPaths = findAllLongestPaths(topologicalOrder, adjacencyList);
			
			for (int i = 0; i < longestPaths.size(); i++)
			{
				System.out.println("\nLongest Path " + (i + 1) + ":");
				for (String unit : longestPaths.get(i))
				{
					System.out.println(unit);
				}
			}
			
			// Allocate units to study periods
			List<List<String>> studyPeriods = allocateUnitsToStudyPeriods(topologicalOrder, adjacencyList, maxUnits, longestPaths);
			
			// Ensure the order of the longest path units is maintained
			for (List<String> longestPath : longestPaths)
			{
				ensureLongestPathOrder(studyPeriods, longestPath);
			}
			
			// Print study periods
			System.out.println("\nStudy Periods:");
			for (int i = 0; i < studyPeriods.size(); i++)
			{
				System.out.println("Study Period " + (i + 1) + ": " + studyPeriods.get(i));
			}
			
		} // >>>>>>>>>>end of topological sort actions<<<<<<<<<<
		else
		{
			System.out.println("The graph contains a cycle and cannot be topologically sorted");
		}
		
	} // >>>>>>>>>>end of psvm<<<<<<<<<<
	
	private static List<String> topologicalSort(List<String> allUnits, Map<String, List<String>> adjacencyList)
	{
		Map<String, Integer> inDegree = new HashMap<>();
		for (String unit : allUnits)
		{
			inDegree.put(unit, 0);
		}
		
		// Populate in-degree map
		for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet())
		{
			for (String neighbour : entry.getValue())
			{
				inDegree.put(neighbour, inDegree.get(neighbour) + 1);
			}
		}
		
		// Initialise queue with units having zero in-degrees
		Queue<String> queue = new LinkedList<>();
		for (Map.Entry<String, Integer> entry : inDegree.entrySet())
		{
			if (entry.getValue() == 0)
			{
				queue.add(entry.getKey());
			}
		}
		
		// Process the queue
		List<String> topologicalOrder = new ArrayList<>();
		while (!queue.isEmpty())
		{
			String current = queue.poll();
			topologicalOrder.add(current);
			
			for (String neighbour : adjacencyList.get(current))
			{
				inDegree.put(neighbour, inDegree.get(neighbour) - 1);
				if (inDegree.get(neighbour) == 0)
				{
					queue.add(neighbour);
				}
			}
		}
		
		// Check if topological order contains all units (i.e., no cycles)
		if (topologicalOrder.size() != allUnits.size())
		{
			return null;
		}
		
		return topologicalOrder;
		
	} // >>>>>>>>>>end of topologicalSort method<<<<<<<<<<
	
	private static List<List<String>> findAllLongestPaths(List<String> topologicalOrder, Map<String, List<String>> adjacencyList)
	{
		Map<String, Integer> longestDistances = new HashMap<>();
		Map<String, String> predecessors = new HashMap<>();
		List<List<String>> longestPaths = new ArrayList<>();
		
		for (String startUnit : topologicalOrder)
		{
			// Initialise distances to negative infinity
			for (String unit : topologicalOrder)
			{
				longestDistances.put(unit, Integer.MIN_VALUE);
			}
			longestDistances.put(startUnit, 0);
			
			// Calculate the longest paths from startUnit
			for (String unit : topologicalOrder)
			{
				if (longestDistances.get(unit) == Integer.MIN_VALUE)
				{
					longestDistances.put(unit, 0);
				}
				
				for (String neighbour : adjacencyList.get(unit))
				{
					if (longestDistances.get(neighbour) < longestDistances.get(unit) + 1)
					{
						longestDistances.put(neighbour, longestDistances.get(unit) + 1);
						predecessors.put(neighbour, unit);
					}
				}
			}
			
			// Find the end of the longest path
			String end = startUnit;
			for (String unit : topologicalOrder)
			{
				if (longestDistances.get(unit) > longestDistances.get(end))
				{
					end = unit;
				}
			}
			
			// Reconstruct the longest path
			List<String> longestPath = new LinkedList<>();
			for (String at = end; at != null; at = predecessors.get(at))
			{
				longestPath.add(at);
			}
			Collections.reverse(longestPath);
			
			// Add to the list of longest paths if it is not already present
			if (!longestPath.isEmpty() && !longestPaths.contains(longestPath))
			{
				longestPaths.add(longestPath);
			}
		}
		
		return longestPaths;
		
	} // >>>>>>>>>>end of findAllLongestPaths method<<<<<<<<<<
	
	private static List<List<String>> allocateUnitsToStudyPeriods(List<String> topologicalOrder, Map<String, List<String>> adjacencyList, int maxUnits, List<List<String>> longestPaths)
	{
		List<List<String>> studyPeriods = new ArrayList<>();
		Map<String, Integer> unitPeriod = new HashMap<>();
		
		for (String unit : topologicalOrder)
		{
			int period = 0;
			// Find the earliest period in which this unit can be placed
			for (String prerequisite : adjacencyList.get(unit))
			{
				if (unitPeriod.containsKey(prerequisite))
				{
					period = Math.max(period, unitPeriod.get(prerequisite) + 1);
				}
			}
			
			// Ensure no period exceeds maxUnits and prerequisites are met
			while (studyPeriods.size() <= period || (studyPeriods.size() > period && studyPeriods.get(period).size() >= maxUnits))
			{
				if (studyPeriods.size() <= period)
				{
					studyPeriods.add(new ArrayList<>());
				}
				else
				{
					period++;
				}
			}
			
			// Ensure no unit is placed in the same period as its prerequisites
			while (studyPeriods.size() > period && studyPeriods.get(period).stream().anyMatch(prereq -> adjacencyList.get(prereq).contains(unit)))
			{
				period++;
				if (studyPeriods.size() <= period)
				{
					studyPeriods.add(new ArrayList<>());
				}
			}
			
			// Allocate the unit to the found period
			studyPeriods.get(period).add(unit);
			unitPeriod.put(unit, period);
			
			// Reallocate units if the maxUnits constraint is violated
			while (studyPeriods.get(period).size() > maxUnits)
			{
				String movedUnit = studyPeriods.get(period).remove(studyPeriods.get(period).size() - 1);
				if (studyPeriods.size() <= period + 1)
				{
					studyPeriods.add(new ArrayList<>());
				}
				studyPeriods.get(period + 1).add(movedUnit);
				unitPeriod.put(movedUnit, period + 1);
			}
		}
		
		return studyPeriods;
		
	}// >>>>>>>>>>end of allocateUnitsToStudyPeriods method<<<<<<<<<<
	
	private static void ensureLongestPathOrder(List<List<String>> studyPeriods, List<String> longestPath)
	{
		Map<String, Integer> unitToPeriod = new HashMap<>();
		for (int i = 0; i < studyPeriods.size(); i++)
		{
			for (String unit : studyPeriods.get(i))
			{
				unitToPeriod.put(unit, i);
			}
		}
		
		for (int i = 0; i < longestPath.size() - 1; i++)
		{
			String currentUnit = longestPath.get(i);
			String nextUnit = longestPath.get(i + 1);
			
			int currentPeriod = unitToPeriod.get(currentUnit);
			int nextPeriod = unitToPeriod.get(nextUnit);
			
			if (currentPeriod >= nextPeriod)
			{
				studyPeriods.get(nextPeriod).remove(nextUnit); // Remove from the earlier period
				while (studyPeriods.size() <= currentPeriod + 1)
				{
					studyPeriods.add(new ArrayList<>());
				}
				studyPeriods.get(currentPeriod + 1).add(nextUnit);
				unitToPeriod.put(nextUnit, currentPeriod + 1);
			}
		}
	} // >>>>>>>>>>end of ensureLongestPathOrder method<<<<<<<<<<
	
} // >>>>>>>>>>end of class DegreeApp<<<<<<<<<<
