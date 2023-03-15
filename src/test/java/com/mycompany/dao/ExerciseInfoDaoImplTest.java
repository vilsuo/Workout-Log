
package com.mycompany.dao;

import com.mycompany.domain.ExerciseInfo;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExerciseInfoDaoImplTest {
    
    private ExerciseInfoDao database;
    private String databaseFile;
    
    @BeforeEach
    public void init() {
        this.databaseFile = "workoutLog-database-test-" + UUID.randomUUID().toString().substring(0, 6);
        this.database = new ExerciseInfoDaoImpl("jdbc:h2:./" + databaseFile);
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
        ExerciseInfo item = database.getItem(0);
        assertTrue(item == null);
        
        String name1 = "Bench";
        String category1 = "Chest";
        
        int id1 = database.createItem(name1, category1);
        ExerciseInfo item1 = new ExerciseInfo(id1, name1, category1);
        assertTrue(item1.getId() != -1);
        
        ExerciseInfo item1Fetched = database.getItem(item1.getId());
        assertTrue(item1Fetched.equals(item1));
    }
    
    @Test
    public void createItemTestDublicateNamesAreNotAdded() throws SQLException {
        String name1 = "Bench";
        String category1 = "Chest";
        
        int id1 = database.createItem(name1, category1);
        assertTrue(id1 != -1);
        ExerciseInfo item1 = new ExerciseInfo(id1, name1, category1);
        
        String category2 = "Legs";
        
        int id2 = database.createItem(name1, category2);
        assertTrue(id2 == -1);
        ExerciseInfo item2 = new ExerciseInfo(id2, name1, category2);
        
        List<ExerciseInfo> itemList = database.getItems(
            Arrays.asList(id1, id2)
        );
        
        assertTrue(itemList.contains(item1));
        assertFalse(itemList.contains(item2));
    }
    
    @Test
    public void updateItemNameTest() throws SQLException {
        String name1 = "Bench";
        String category1 = "Chest";
        int id1 = database.createItem(name1, category1);
        ExerciseInfo item1 = database.getItem(id1);
        
        String name2 = "Dip";
        String category2 = "Chest";
        int id2 = database.createItem(name2, category2);
        ExerciseInfo item2 = database.getItem(id2);
        
        String newNameNotDuplicate = "Pullover";
        item1.setName(newNameNotDuplicate);
        assertTrue(database.updateItemName(item1.getId(), newNameNotDuplicate));
        
        // name gets updated
        ExerciseInfo itemFetched1 = database.getItem(item1.getId());
        assertTrue(itemFetched1.equals(item1));
        
        // not updated
        ExerciseInfo itemFetched2 = database.getItem(item2.getId());
        assertTrue(itemFetched2.equals(item2));
    }
    
    @Test
    public void updateItemNameTestNameNotUpdatedIfNewAlredyNameExists() throws SQLException {
        String name1 = "Bench";
        String category1 = "Chest";
        int id1 = database.createItem(name1, category1);
        ExerciseInfo item1 = new ExerciseInfo(id1, name1, category1);
        
        String name2 = "Dip";
        String category2 = "Chest";
        int id2 = database.createItem(name2, category2);
        ExerciseInfo item2 = new ExerciseInfo(id2, name2, category2);
        
        item1.setName(item2.getName());
        assertFalse(database.updateItemName(item1.getId(), item2.getName()));
        
        // not updated: name already exists
        ExerciseInfo item1Fetched = database.getItem(id1);
        assertFalse(item1Fetched.equals(item1));
        assertTrue(item1Fetched.getName().equals(name1));
        
        // not updated
        ExerciseInfo item2Fetched = database.getItem(id2);
        assertTrue(item2Fetched.getName().equals(name2));
        assertTrue(item2Fetched.getCategory().equals(category2));
    }
    
    @Test
    public void updateItemCategoryTest() throws SQLException {
        String name1 = "Bench";
        String category1 = "Chest";
        int id1 = database.createItem(name1, category1);
        
        String name2 = "Ohp";
        String category2 = "Shoulders";
        int id2 = database.createItem(name2, category2);
        
        String newCategory = "Legs";
        database.updateItemCategory(id1, newCategory);
        
        // category updated
        ExerciseInfo item1 = database.getItem(id1);
        assertTrue(item1.getCategory().equals(newCategory));
        assertTrue(item1.getName().equals(name1));
        
        // category not updated
        ExerciseInfo item2 = database.getItem(id2);
        assertTrue(item2.getCategory().equals(category2));
        assertTrue(item2.getName().equals(name2));
    }
    
    @Test
    public void removeItemTest() throws SQLException {
        String name1 = "Bench";
        String category1 = "Chest";
        int id1 = database.createItem(name1, category1);
        
        String name2 = "Ohp";
        String category2 = "Shoulders";
        int id2 = database.createItem(name2, category2);
        
        database.removeItem(id1);
        
        List<ExerciseInfo> itemList = database.getAllItems();
        
        assertFalse(itemList.contains(new ExerciseInfo(id1, name1, category1)));
        assertTrue(itemList.contains(new ExerciseInfo(id2, name2, category2)));
    }
    
    @Test
    public void getItemsTest() throws SQLException {
        List<ExerciseInfo> itemList1 = database.getItems(new ArrayList<>());
        assertTrue(itemList1.isEmpty());
        
        List<ExerciseInfo> itemList2 = database.getItems(Arrays.asList(1));
        assertTrue(itemList2.isEmpty());
        
        String name1 = "Dip";
        String category1 = "Chest";
        int id1 = database.createItem(name1, category1);
        
        String name2 = "Pull up";
        String category2 = "Back";
        int id2 = database.createItem(name2, category2);
        
        String name3 = "Squat";
        String category3 = "Legs";
        int id3 = database.createItem(name3, category3);
        
        int invalidId = id1 + id2 + id3;
        List<ExerciseInfo> itemList3 = database.getItems(
            Arrays.asList(id1, id2, invalidId)
        );
        
        assertTrue(itemList3.size() == 2);
        assertTrue(itemList3.contains(new ExerciseInfo(id1, name1, category1)));
        assertTrue(itemList3.contains(new ExerciseInfo(id2, name2, category2)));
        assertFalse(itemList3.contains(new ExerciseInfo(id3, name3, category3)));
    }
    
    @Test
    public void getAllItemsTest() throws SQLException {
        assertTrue(database.getAllItems().isEmpty());
        
        String name1 = "Bench";
        String category1 = "Chest";
        int id1 = database.createItem(name1, category1);
        ExerciseInfo item1 = new ExerciseInfo(id1, name1, category1);
        
        assertTrue(database.getAllItems().size() == 1);
        assertTrue(database.getAllItems().contains(item1));
        
        String name2 = "Ohp";
        String category2 = "Shoulders";
        int id2 = database.createItem(name2, category2);
        ExerciseInfo item2 = new ExerciseInfo(id2, name2, category2);
        
        assertTrue(database.getAllItems().size() == 2);
        assertTrue(database.getAllItems().contains(item1));
        assertTrue(database.getAllItems().contains(item2));
    }
    
    @Test
    public void getAllItemsByCategoryTest() throws SQLException {
        String category1 = "Back";
        List<ExerciseInfo> itemsList = database.getAllItemsByCategory(category1);
        
        assertTrue(itemsList.size() == 0);
        
        String name1 = "Chin up";
        int id1 = database.createItem(name1, category1);
        ExerciseInfo item1 = new ExerciseInfo(id1, name1, category1);
        
        String name2 = "Pull up";
        int id2 = database.createItem(name2, category1);
        ExerciseInfo item2 = new ExerciseInfo(id2, name2, category1);
        
        itemsList = database.getAllItemsByCategory(category1);
        assertTrue(itemsList.size() == 2);
        
        String name3 = "Squat";
        String category2 = "Legs";
        int id3 = database.createItem(name3, category2);
        ExerciseInfo item3 = new ExerciseInfo(id3, name3, category2);
        
        itemsList = database.getAllItemsByCategory(category1);
        assertTrue(itemsList.size() == 2);
        assertTrue(itemsList.contains(item1));
        assertTrue(itemsList.contains(item2));
        assertFalse(itemsList.contains(item3));
    }
    
}
