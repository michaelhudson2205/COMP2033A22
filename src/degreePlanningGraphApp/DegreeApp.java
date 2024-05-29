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
		
		
		
	} // >>>>>>>>>>end of psvm<<<<<<<<<<
} // >>>>>>>>>>end of class DegreeApp<<<<<<<<<<
