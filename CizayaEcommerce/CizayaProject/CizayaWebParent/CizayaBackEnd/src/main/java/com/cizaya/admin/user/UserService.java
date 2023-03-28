package com.cizaya.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import com.cizaya.admin.user.controller.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cizaya.common.entity.Role;
import com.cizaya.common.entity.User;

// them transactional de dung cho ham enabled
@Service
@Transactional
public class UserService {
    public static final int USER_PER_PAGE = 4;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    //phuong thuc tra ve doi tuong nguoi dung dua tren email
    public User getByEmail(String email){
        return userRepo.getUserByEmail(email);
    }

    // goi ra list User tu userRepo
    public List<User> listAll() {
        return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
    }

    // goi ra list Role tu roleRepo
    public List<Role> listRoles() {
        return (List<Role>) roleRepo.findAll();
    }

    //Goi Ham listByPage , gia tri dau vao la int page number, dau ra se in ra list  pageNum -1 voi dieu kien USER_PER_PAGE
    // them  String sortField, String sortDir de sap xep

    // goi them gia tri String keywork de tim kiem
    public Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        //sap xep tang dan neu ko tang dan se giam dan
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        //sau do can upload lai controller
        Pageable pageable = PageRequest.of(pageNumber - 1, USER_PER_PAGE, sort);
        //neu keyword null thi goi den ham findAll co chua keyword va pageable
        //nguoc lai chi tra ra list chua pageable
        if (keyword != null) {
            return userRepo.findAll(keyword, pageable);
        }
        return userRepo.findAll(pageable);
    }

    // save user
    public User save(User user) {
        // gia tri boolean de kiem tra nguoi dung co dang cap nhap hay ko
        // not null la dang update
        boolean isUpdatetingUser = (user.getId() != null);

        // truy xuat thong tin nguoi dung
        if (isUpdatetingUser) {
            User existingUser = userRepo.findById(user.getId()).get();
            // neu mat khau la trong nghia la quan tri vien muon giu nguyen mat khau nguoi
            // dung
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                // neu mat khau trong bieu mau nguoi dung ko trong thi ma hoa mat khau
                encodePassword(user);
            }
        } else {
            encodePassword(user);
        }

        return userRepo.save(user);
    }

    // ma hoa mat khau , truyen vao user, set lai mat khau cho no qua ham
    // passwordEncoder sau do gan lai vao ham save
    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public  User updateAccount(User userInForm){
        User userInDB =  userRepo.findById(userInForm.getId()).get();
        if(!userInForm.getPassword().isEmpty()){
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }

        if(userInForm.getPhotos()!= null){
            userInDB.setPhotos(userInForm.getPhotos());
        }
        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());

        return userRepo.save(userInDB);
    }

    // neu tra ve true email nguoi dung la duy nhat
    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepo.getUserByEmail(email);
        // neu user by email laf null thi tra ve true trong phuong thuc nay vi ko tim
        // thay nguoi dung nao trong phuong thuc da cho
        if (userByEmail == null)
            return true;

        // cho bieu thuc boolean , neu id la null nghia la bieu mau dang o che do moi,
        // mat khac id ko null nghia la nguoi dung dang duoc chinh sua
        boolean isCreatingNew = (id == null);

        // voi ham userByEmail thi luon tra ve null <=> ko co email do trong db
        // neu ham userByEmail ma khac null <=> co du lieu trong db , thi phai tra ve
        // false
        // vi da co 1 nguoi dung khac co email nay. nen tinh duy nhat cua email nay la
        // sai
        if (isCreatingNew) {
            if (userByEmail != null)
                return false;
        } else {
            if (userByEmail.getId() != id) {
                return false;
            }
        }
        return true;
    }

    // tim kiem theo id tra ve 1 doi tuong
    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new UserNotFoundException("Cound not find any user with id " + id);
        }

    }

    // tim theo id de kiem tra su ton tai cua ng dung
    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepo.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Cound not find any user with id " + id);
        }
        userRepo.deleteById(id);
    }

    //update Trang thai (enable)
    public void updateUserEnbledStatus(Integer id, boolean enabled) {
        userRepo.updateEnableStatus(id, enabled);
    }


}
