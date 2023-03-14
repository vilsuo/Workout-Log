
package com.mycompany.dao;

import com.mycompany.domain.ExerciseSet;
import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExerciseSetDaoImplTest {
    
    private ExerciseSetDao database;
    private String databaseFile;
    
    @BeforeEach
    public void init() {
        this.databaseFile = "workoutLog-database-test-" + UUID.randomUUID().toString().substring(0, 6);
        this.database = new ExerciseSetDaoImpl("jdbc:h2:./" + databaseFile);
    }
    
    @AfterEach
    public void tearDown() {
        try {
            new File(databaseFile + ".mv.db").delete();
            new File(databaseFile + ".trace.db").delete();
        } catch (Exception e) {

        }
    }
    
    @Test
    public void createItemTest() throws SQLException {
        assertTrue(database.getItem(0) == null);
        
        int workingSets = 2;
        int repetitions = 10;
        int workingWeight = 60;
        int orderNumber = 1;
        
        int id = database.createItem(workingSets, repetitions, workingWeight, orderNumber);
        ExerciseSet item = new ExerciseSet(id, workingSets, repetitions, workingWeight, orderNumber);
        assertTrue(id != -1);
        
        ExerciseSet itemFetched = database.getItem(item.getId());
        assertTrue(itemFetched.equals(item));
        
        assertTrue(itemFetched.getId() == id);
        assertTrue(itemFetched.getWorkingSets() == workingSets);
        assertTrue(itemFetched.getRepetitions() == repetitions);
        assertTrue(itemFetched.getWorkingWeight() == workingWeight);
        assertTrue(itemFetched.getOrderNumber() == orderNumber);
        
        assertTrue(database.getItem(item.getId() + 1) == null);
    }
    
    @Test
    public void updateItemTest() throws SQLException {
        int workingSets1 = 3;
        int repetitions1 = 11;
        int workingWeight1 = 61;
        int orderNumber1 = 2;
        int id1 = database.createItem(workingSets1, repetitions1, workingWeight1, orderNumber1);
        ExerciseSet item1 = new ExerciseSet(id1, workingSets1, repetitions1, workingWeight1, orderNumber1);
        
        int workingSets2 = 4;
        int repetitions2 = 12;
        int workingWeight2 = 62;
        int orderNumber2 = 4;
        int id2 = database.createItem(workingSets2, repetitions2, workingWeight2, orderNumber2);
        ExerciseSet item2 = new ExerciseSet(id2, workingSets2, repetitions2, workingWeight2, orderNumber2);
        
        int newWorkingSets = 4;
        int newRepetitions = 12;
        int newWorkingWeight = 62;
        item1.setWorkingSets(newWorkingSets);
        item1.setRepetitions(newRepetitions);
        item1.setWorkingWeight(newWorkingWeight);
        
        database.updateItem(item1.getId(), newWorkingSets, newRepetitions, newWorkingWeight);
        
        // correct changes in items that were updated
        ExerciseSet updatedItem = database.getItem(id1);
        assertTrue(updatedItem.equals(item1));
        
        // no changes in items that were not updated
        ExerciseSet itemFetched2 = database.getItem(id2);
        assertTrue(itemFetched2.equals(item2));
    }
    
    @Test
    public void updateItemOrderNumberTest() throws SQLException {
        int workingSets1 = 3;
        int repetitions1 = 11;
        int workingWeight1 = 61;
        int orderNumber1 = 2;
        int id1 = database.createItem(workingSets1, repetitions1, workingWeight1, orderNumber1);
        ExerciseSet item1 = new ExerciseSet(id1, workingSets1, repetitions1, workingWeight1, orderNumber1);
        
        int workingSets2 = 4;
        int repetitions2 = 12;
        int workingWeight2 = 62;
        int orderNumber2 = 4;
        int id2 = database.createItem(workingSets2, repetitions2, workingWeight2, orderNumber2);
        ExerciseSet item2 = new ExerciseSet(id2, workingSets2, repetitions2, workingWeight2, orderNumber2);
        
        int newOrderNumber = 3;
        item1.setOrderNumber(newOrderNumber);
        
        database.updateItemOrderNumber(item1.getId(), newOrderNumber);
        
        // updated items order changes
        ExerciseSet updatedItem = database.getItem(id1);
        assertTrue(updatedItem.equals(item1));
        
        // order does not change for items that were not updated
        ExerciseSet itemFetched2 = database.getItem(id2);
        assertTrue(itemFetched2.equals(item2));
    }
    
    @Test
    public void updateItemsOrderNumbersTest() throws SQLException {
        int workingSets1 = 3;
        int repetitions1 = 11;
        int workingWeight1 = 61;
        int orderNumber1 = 1;
        int id1 = database.createItem(workingSets1, repetitions1, workingWeight1, orderNumber1);
        ExerciseSet item1 = new ExerciseSet(id1, workingSets1, repetitions1, workingWeight1, orderNumber1);
        
        int workingSets2 = 4;
        int repetitions2 = 12;
        int workingWeight2 = 62;
        int orderNumber2 = 2;
        int id2 = database.createItem(workingSets2, repetitions2, workingWeight2, orderNumber2);
        ExerciseSet item2 = new ExerciseSet(id2, workingSets2, repetitions2, workingWeight2, orderNumber2);
        
        int workingSets3 = 5;
        int repetitions3 = 13;
        int workingWeight3 = 63;
        int orderNumber3 = 3;
        int id3 = database.createItem(workingSets3, repetitions3, workingWeight3, orderNumber3);
        ExerciseSet item3 = new ExerciseSet(id3, workingSets3, repetitions3, workingWeight3, orderNumber3);
        
        int invalidId = id1 + id2 + id2;
        List<Integer> idList = Arrays.asList(id1, id2, invalidId);
        
        int item1NewOrderNumber = 4;
        int item2NewOrderNumber = 5;
        item1.setOrderNumber(item1NewOrderNumber);
        item2.setOrderNumber(item2NewOrderNumber);
        List<Integer> orderNumberList = Arrays.asList(item1NewOrderNumber, item2NewOrderNumber, 6);
        
        database.updateItemsOrderNumbers(idList, orderNumberList);
        
        // orders changed
        assertTrue(database.getItem(id1).equals(item1));
        assertTrue(database.getItem(id2).equals(item2));
        
        // order not changed
        assertTrue(database.getItem(id3).equals(item3));
    }
    
    @Test
    public void removeItemTest() throws SQLException {
        int workingSets1 = 3;
        int repetitions1 = 11;
        int workingWeight1 = 61;
        int orderNumber1 = 1;
        int id1 = database.createItem(workingSets1, repetitions1, workingWeight1, orderNumber1);
        ExerciseSet item1 = new ExerciseSet(id1, workingSets1, repetitions1, workingWeight1, orderNumber1);
        
        int workingSets2 = 4;
        int repetitions2 = 12;
        int workingWeight2 = 62;
        int orderNumber2 = 2;
        int id2 = database.createItem(workingSets2, repetitions2, workingWeight2, orderNumber2);
        
        assertTrue(database.getItem(id1) != null);
        assertTrue(database.getItem(id2) != null);
        
        database.removeItem(id2);
        
        // only the specified item gets removed
        assertTrue(database.getItem(id1) != null);
        assertTrue(database.getItem(id2) == null);
        
        assertTrue(database.getItem(id1).equals(item1));
    }
    
    @Test
    public void removeItemsTest() throws SQLException {
        int workingSets1 = 3;
        int repetitions1 = 11;
        int workingWeight1 = 61;
        int orderNumber1 = 1;
        int id1 = database.createItem(workingSets1, repetitions1, workingWeight1, orderNumber1);
        ExerciseSet item1 = new ExerciseSet(id1, workingSets1, repetitions1, workingWeight1, orderNumber1);
        
        int workingSets2 = 4;
        int repetitions2 = 12;
        int workingWeight2 = 62;
        int orderNumber2 = 2;
        int id2 = database.createItem(workingSets2, repetitions2, workingWeight2, orderNumber2);
        ExerciseSet item2 = new ExerciseSet(id2, workingSets2, repetitions2, workingWeight2, orderNumber2);
        
        int workingSets3 = 5;
        int repetitions3 = 13;
        int workingWeight3 = 63;
        int orderNumber3 = 3;
        int id3 = database.createItem(workingSets3, repetitions3, workingWeight3, orderNumber3);
        ExerciseSet item3 = new ExerciseSet(id3, workingSets3, repetitions3, workingWeight3, orderNumber3);
        
        int invalidId = id1 + id2 + id3;
        
        // removeItems item1 and item2
        database.removeItems(Arrays.asList(item1.getId(), item2.getId(), invalidId));
        
        assertTrue(database.getItem(item1.getId()) == null);
        assertTrue(database.getItem(item2.getId()) == null);
        
        // item3 does not getItems removed
        assertTrue(database.getItem(item3.getId()) != null);
    }
    
    @Test
    public void getItemsTest() throws SQLException {
        int workingSets1 = 3;
        int repetitions1 = 11;
        int workingWeight1 = 61;
        int orderNumber1 = 1;
        int id1 = database.createItem(workingSets1, repetitions1, workingWeight1, orderNumber1);
        ExerciseSet item1 = new ExerciseSet(id1, workingSets1, repetitions1, workingWeight1, orderNumber1);
        
        int workingSets2 = 4;
        int repetitions2 = 12;
        int workingWeight2 = 62;
        int orderNumber2 = 2;
        int id2 = database.createItem(workingSets2, repetitions2, workingWeight2, orderNumber2);
        ExerciseSet item2 = new ExerciseSet(id2, workingSets2, repetitions2, workingWeight2, orderNumber2);
        
        int workingSets3 = 5;
        int repetitions3 = 13;
        int workingWeight3 = 63;
        int orderNumber3 = 3;
        int id3 = database.createItem(workingSets3, repetitions3, workingWeight3, orderNumber3);
        ExerciseSet item3 = new ExerciseSet(id3, workingSets3, repetitions3, workingWeight3, orderNumber3);
        
        int invalidId = id1 + id2 + id3;
        
        // getItems item1 and item2
        List<ExerciseSet> exerciseSetList = database.getItems(Arrays.asList(item1.getId(), item2.getId(), invalidId));
        
        // items1 and item2 are in the list
        assertTrue(exerciseSetList.size() == 2);
        assertTrue(exerciseSetList.contains(item1));
        assertTrue(exerciseSetList.contains(item2));
        
        // item3 is not in the list
        assertFalse(exerciseSetList.contains(item3));
    }
}
