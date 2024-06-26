/**
* File:         CourseSchedulerTest.java
* Description:  Project 2: Degree Planning Graph Applications
* 				Watch the video at: https://www.youtube.com/watch?v=p7dSHRqIu5k
* Author:       Michael Hudson
* Student ID:   110369255
* Email ID:     hudmy010@mymail.unisa.edu.au
* AI Tool Used: ChatGPT4o
* This is my own work as defined by
*    the University's Academic Integrity Policy.
**/
package tests;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

import degreePlanningGraphApp.DegreeApp;

public class CourseSchedulerTest
{
	// This test ensures that the topological sort method correctly sorts units.
	@Test
	public void testTopologicalSort()
	{
		// given
		List<String> allUnits = Arrays.asList("A", "B", "C", "D", "E");
		Map<String, List<String>> adjacencyList = new HashMap<>();
		adjacencyList.put("A", Arrays.asList("B", "C"));
		adjacencyList.put("B", Arrays.asList("D"));
		adjacencyList.put("C", Arrays.asList("D"));
		adjacencyList.put("D", Arrays.asList("E"));
		adjacencyList.put("E", new ArrayList<>());
		
		// when
		List<String> sortedUnits = DegreeApp.topologicalSort(allUnits, adjacencyList);
		
		// then
		assertNotNull(sortedUnits);
		assertEquals(5, sortedUnits.size());
		assertTrue(sortedUnits.indexOf("A") < sortedUnits.indexOf("B"));
		assertTrue(sortedUnits.indexOf("A") < sortedUnits.indexOf("C"));
		assertTrue(sortedUnits.indexOf("B") < sortedUnits.indexOf("D"));
		assertTrue(sortedUnits.indexOf("C") < sortedUnits.indexOf("D"));
		assertTrue(sortedUnits.indexOf("D") < sortedUnits.indexOf("E"));
		
	} // >>>>>>>>>>end of testTopologicalSort<<<<<<<<<<
	
	// This test verifies that the longest path calculation works correctly.
	@Test
	public void testFindAllLongestPaths()
	{
		// given
		List<String> allUnits = Arrays.asList("A", "B", "C", "D", "E");
		Map<String, List<String>> adjacencyList = new HashMap<>();
		adjacencyList.put("A", Arrays.asList("B", "C"));
		adjacencyList.put("B", Arrays.asList("D"));
		adjacencyList.put("C", Arrays.asList("D"));
		adjacencyList.put("D", Arrays.asList("E"));
		adjacencyList.put("E", new ArrayList<>());
		
		// when
		List<String> sortedUnits = DegreeApp.topologicalSort(allUnits, adjacencyList);
		List<List<String>> longestPaths = DegreeApp.findAllLongestPaths(sortedUnits, adjacencyList);
		
		// then
		assertNotNull(longestPaths);
		assertFalse(longestPaths.isEmpty());
		assertEquals(1, longestPaths.size());
		List<String> longestPath = longestPaths.get(0);
		assertEquals(Arrays.asList("A", "B", "D", "E"), longestPath);
		
	} // >>>>>>>>>>end of testFindAllLongestPaths<<<<<<<<<<
	
	// This test ensures that the allocation respects the 'maxUnits' constraint and prerequisites.
	@Test
	public void testAllocateUnitsToStudyPeriods()
	{
		// given
		List<String> allUnits = Arrays.asList("A", "B", "C", "D", "E");
		Map<String, List<String>> adjacencyList = new HashMap<>();
		adjacencyList.put("A", Arrays.asList("B", "C"));
		adjacencyList.put("B", Arrays.asList("D"));
		adjacencyList.put("C", Arrays.asList("D"));
		adjacencyList.put("D", Arrays.asList("E"));
		adjacencyList.put("E", new ArrayList<>());
		
		// when
		List<String> sortedUnits = DegreeApp.topologicalSort(allUnits, adjacencyList);
		List<List<String>> longestPaths = DegreeApp.findAllLongestPaths(sortedUnits, adjacencyList);
		int maxUnits = 1;
		List<List<String>> studyPeriods = DegreeApp.allocateUnitsToStudyPeriods(sortedUnits, adjacencyList, maxUnits, longestPaths);
		
		// then
		assertNotNull(studyPeriods);
		for (List<String> period : studyPeriods)
		{
			assertTrue(period.size() <= maxUnits);
		}
		// Ensure order of units is maintained based on prerequisites
		Map<String, Integer> unitToPeriod = new HashMap<>();
		for (int i = 0; i < studyPeriods.size(); i++)
		{
			for (String unit : studyPeriods.get(i))
			{
				unitToPeriod.put(unit, i);
			}
		}
		assertTrue(unitToPeriod.get("A") < unitToPeriod.get("B"));
		assertTrue(unitToPeriod.get("A") < unitToPeriod.get("C"));
		assertTrue(unitToPeriod.get("B") < unitToPeriod.get("D"));
		assertTrue(unitToPeriod.get("C") < unitToPeriod.get("D"));
		assertTrue(unitToPeriod.get("D") < unitToPeriod.get("E"));
		
	} // >>>>>>>>>>end of testAllocateUnitsToStudyPeriods<<<<<<<<<<
	
	// This test ensures that the longest path order is maintained in the study periods.
	@Test
	public void testEnsureLongestPathOrder()
	{
		// given
		List<List<String>> studyPeriods = new ArrayList<>();
		studyPeriods.add(new ArrayList<>(Arrays.asList("A", "B")));
		studyPeriods.add(new ArrayList<>(Arrays.asList("C", "D")));
		studyPeriods.add(new ArrayList<>(Arrays.asList("E")));
		List<String> longestPath = Arrays.asList("A", "B", "D", "E");
		
		// when
		DegreeApp.ensureLongestPathOrder(studyPeriods, longestPath);
		
		// then
		Map<String, Integer> unitToPeriod = new HashMap<>();
		for (int i = 0; i < studyPeriods.size(); i++)
		{
			for (String unit : studyPeriods.get(i))
			{
				unitToPeriod.put(unit, i);
			}
		}
		assertTrue(unitToPeriod.get("A") < unitToPeriod.get("B"));
		assertTrue(unitToPeriod.get("B") < unitToPeriod.get("D"));
		assertTrue(unitToPeriod.get("D") < unitToPeriod.get("E"));
		
	} // >>>>>>>>>>end of testEnsureLongestPathOrder<<<<<<<<<<
	
	
} // >>>>>>>>>>end of class CourseSchedulerTest<<<<<<<<<<
