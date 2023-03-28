package com.cizaya.admin.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cizaya.common.entity.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    // checl email
    @Query("SELECT u FROM User u WHERE u.email=:email")
    public User getUserByEmail(@Param("email") String email);

    // tim kiem theo id
    public Long countById(Integer id);

    //phuong thuc nay tra ve mot trang user  co 2 tham so la Keyword vaf pageable
    //%?1% la trinh giu cho cho gia tri cua tham so dau tien
    //ham concat de gan cac chuoi can tim kiem
    @Query("SELECT u FROM User u WHERE CONCAT(u.id,' ',u.email,' ',u.firstName,' '," + " u.lastName) LIKE %?1%")
    public Page<User> findAll(String keyword, Pageable pageable);

    // update trang thai
    // vi la mot ban tuyen bo cap nhap(update statment) nen can co modifying
    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    public void updateEnableStatus(Integer id, boolean enabled);
}
