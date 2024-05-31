package tests;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

import degreePlanningGraphApp.DegreeApp;

public class CourseSchedulerTest
{
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
	
	
} // >>>>>>>>>>end of class CourseSchedulerTest<<<<<<<<<<
