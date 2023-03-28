package com.cizaya.admin.user.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.cizaya.admin.user.controller.UserRepository;
import com.cizaya.common.entity.Role;
import com.cizaya.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    // test save user voi entitymanager test de chon role (1 user 1 role)
    @Test
    public void testCreateNewUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User userAnnt = new User("Antao6125@gmail.com", "Annt6125", "An", "Nguyen Truong");
        userAnnt.addRole(roleAdmin);

        User savedUser = repo.save(userAnnt);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    // 1user 2role
    @Test
    public void testCreateNewUserWithTwoRole() {
        User userBaolq = new User("Baolq1020@gmail.com", "Bao2020", "Bao", "Luong Quoc");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);
        userBaolq.addRole(roleEditor);
        userBaolq.addRole(roleAssistant);

        User savedUser = repo.save(userBaolq);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    // test list all
    @Test
    public void testListAllUser() {
        Iterable<User> listUser = repo.findAll();
        listUser.forEach(user -> System.out.println(user));

    }

    // test find by id
    @Test
    public void testGetById() {
        User userAnnt = repo.findById(1).get();
        System.out.println(userAnnt);
        assertThat(userAnnt).isNotNull();
    }

    // test thong tin user
    @Test
    public void testUpdateUserDetails() {
        User userAnnt = repo.findById(1).get();
        userAnnt.setEnabled(true);
        userAnnt.setEmail("Anntph18823@fpt.edu.vn");

        repo.save(userAnnt);
    }

    // test role
    @Test
    public void testUpdateUserRole() {
        User userBao = repo.findById(3).get();
        Role roleEditor = new Role(3);
        Role roleSaleperson = new Role(2);
        userBao.getRoles().remove(roleEditor);
        userBao.addRole(roleSaleperson);

        repo.save(userBao);
    }

    // test delete
    @Test
    public void testDeleteUser() {
        Integer userId = 3;
        repo.deleteById(userId);

    }

    // test Email
    // test xem co tim duoc email theo ko neu ko co thi tra ve null neu co thi test
    // true
    @Test
    public void testGetUserByEmail() {
        String email = "AnntPh18823@fpt.edu.vn";
        User user = repo.getUserByEmail(email);
        assertThat(user).isNotNull();
    }

    // test delete theo id
    @Test
    public void testCountById() {
        Integer id = 19;
        Long countById = repo.countById(id);
        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    //an trang thai
    @Test
    public void testDisableUser() {
        Integer id = 1;
        repo.updateEnableStatus(id, false);
    }

    //hien trang thai
    @Test
    public void testEnableUser() {
        Integer id = 1;
        repo.updateEnableStatus(id, true);
    }

    //test phan trang
    // tao mot doi tuong pageable tu thu vien Pageable no se quet lay so trang va so luong o moi trang
    // sau do findAll ra theo User,
    // goi mot listUser khac gan nhung cai gia tri trong page vao bang phuong thuc page.content
    @Test
    public void testListFirstPage() {
        int pageNumber = 1;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(pageable);

        List<User> listUser = page.getContent();
        listUser.forEach(user -> System.out.println(user));
        assertThat(listUser.size()).isEqualTo(pageSize);
    }

    @Test
    public void textSearchUsers(){
        String keyword = "Bruce";

        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(keyword,pageable);

        List<User> listUser = page.getContent();
        listUser.forEach(user -> System.out.println(user));
        assertThat(listUser.size()).isGreaterThan(0);
    }

}
