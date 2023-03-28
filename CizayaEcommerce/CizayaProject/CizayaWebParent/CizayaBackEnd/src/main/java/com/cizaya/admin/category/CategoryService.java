package com.cizaya.admin.category;

import com.cizaya.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repo;


    //tgoi  repo.findRootCategories(); de lay du lieu tu trong db de tra ra ca danh muc cap cao nhat
    public List<Category> listAll() {
        List<Category> rootCategories =  repo.findRootCategories();
        return listHierarchicalCategories(rootCategories);
    }

    //nhan tat ca cac danh muc con theo cach de quy
    private List<Category> listHierarchicalCategories(List<Category> rootCategories){
        List<Category> hierarchicalCategories  = new ArrayList<>();
        for(Category rootCategory : rootCategories){
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children  = rootCategory.getChildren();
            for (Category subCategory : children){
                String name =  "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory,name));
                listSubHierarchicalCategories(hierarchicalCategories,subCategory,1);
            }
        }
        return  hierarchicalCategories;
    }
    //su dung phuong thuc copy full cua lop category de tao cac ban sao cua tung danh muc voi tat ca cac truong cua no
    //bao gom ca cha va con
    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories , Category parent, int subLevel){
        Set<Category> children  = parent.getChildren();
        int newSubLevel = subLevel + 1;
        for (Category subCategory:children) {
            String name  = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            hierarchicalCategories.add(Category.copyFull(subCategory,name));
            listSubHierarchicalCategories(hierarchicalCategories,subCategory,newSubLevel);
        }
    }
    //luu mot danhmuc vao csdl
    public Category save(Category category){
        return repo.save(category);
    }

    //trả về danh sách các danh mục có thể được sử dụng trong một biểu mẫu.
    //Nó gọi phương thức findAll của CategoryRepository để lấy tất cả các danh mục,
    // sau đó sử dụng phương thức listSubCategoriesUserdInForm để nhận tất cả các danh mục con theo cách đệ quy.
    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesDB = repo.findAll();

        for (Category category : categoriesDB) {
            if (category.getParent() == null) {
               categoriesUsedInForm.add(Category.copyIdAndName(category));

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    String name =  "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
                    listSubCategoriesUserdInForm(categoriesUsedInForm, subCategory, 1);
                }
            }
        }
        return categoriesUsedInForm;
    }

    //phuong thuc cho thuat toan de quy
    //lấy danh sách các danh mục, danh mục chính và Cấp con và nhận đệ quy tất cả các danh mục con có thể được sử dụng trong một biểu mẫu
    private void listSubCategoriesUserdInForm(List<Category> categoriesUsedInForm ,Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();
        for (Category subCategory : children) {
            String name  = "";
            for (int i = 0; i < newSubLevel; i++) {
               name += "--";
            }
            name += subCategory.getName();

            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

            listSubCategoriesUserdInForm(categoriesUsedInForm,subCategory, newSubLevel);
        }
    }


}
