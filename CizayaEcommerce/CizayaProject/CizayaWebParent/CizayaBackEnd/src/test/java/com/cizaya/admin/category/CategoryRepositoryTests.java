package com.cizaya.admin.category;

import static org.assertj.core.api.Assertions.assertThat;
import com.cizaya.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository repo;

    //test cho danh muc me - parent gom co fashins va shoes
    @Test
    public void testCreateRootCategory(){
        Category category = new Category("Shoes");
        Category savedCategory =  repo.save(category);

        assertThat(savedCategory.getId()).isGreaterThan(0);
    }
    //test cho 1 danh muc con
    @Test
    public void testCreateSubCategory(){
        Category parent = new Category(7);
        Category subCategory = new Category("Doll Shoe",parent);
        Category savedCategory = repo.save(subCategory);

        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    //test cho 1 list danh muc con
    @Test
    public void testCreateSubListCategory(){
        Category parent = new Category(2);
        Category shoeMan = new Category("ShoeMan",parent);
        Category shoeWoman = new Category("ShoeWoman",parent);
        repo.saveAll(List.of(shoeMan,shoeWoman));

    }
    //test cho vong co trong phu kien
    @Test
    public void testCreateAccessoryCategory(){
        Category parent = new Category(5);
        Category necklace = new Category("necklace",parent);
        Category savedNecklace = repo.save(necklace);
        assertThat(savedNecklace.getId()).isGreaterThan(0);
    }
    //test lay du lieu cua cac children voi id cua parent
    @Test
    public void testGetCategory(){
        Category category = repo.findById(2).get();
        System.out.println(category.getName());
        Set<Category> children =  category.getChildren();
        for(Category subCategory : children){
            System.out.println(subCategory.getName());
        }
        assertThat(children.size()).isGreaterThan(0);
    }

    //lay All du lieu khi ko co du lieu tu cha me
    @Test
    public void testPrintHierarchicalCategories(){
        Iterable<Category> categories = repo.findAll();

    }

    @Test
    public void testListCategories(){
        List<Category> rootCategories = repo.findRootCategories();
        rootCategories.forEach(cat -> System.out.println(cat.getName()));
    }

}
