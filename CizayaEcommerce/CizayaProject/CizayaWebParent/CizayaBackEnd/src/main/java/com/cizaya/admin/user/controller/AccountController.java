package com.cizaya.admin.user.controller;

import com.cizaya.admin.FileUploadUtil;
import com.cizaya.admin.security.CizayaUserDetail;
import com.cizaya.admin.user.UserService;
import com.cizaya.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {
    @Autowired
    private UserService service;

    //Trong phương thức viewDetails, chúng ta cần lấy một đối tượng Người dùng đại diện cho người dùng hiện đang đăng nhập.
    //nen can chu thich AuthenticationPrincipal
    // dung CizayaUserDetail trong security de tra ra mot doi tuong moi trong qua trinh xac thuc
    //loggerUser la ten doi tuong  -  tu ten doi tuong lay dc email
    //Model de tham chieu doi tuong Spring mvc
    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal CizayaUserDetail loggerUser , Model model){
        String email =  loggerUser.getUsername();
        // tu day truy xuat thong tin chi tiet nguoi dung hien dang dang nhap tu csdl de dam bao thong tin moi nhat
        User user = service.getByEmail(email);
        model.addAttribute("user",user);
        return "users/account_form";
    }

    @PostMapping("/account/update")
    public String saveDetails(User user, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal CizayaUserDetail loggedUser,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {
        // khi user form submited len server
        // neu file anh ko null thi can set nhung thu sau
        if (!multipartFile.isEmpty()) {
            // 1 don dep file anh
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            // 2 dat truong anh cua nguoi dung voi ten tep
            user.setPhotos(fileName);
            // 3 duy tri thong tin chi tiet ve ng dung bang cach luu vao database
            User savedUser = service.updateAccount(user);
            // 4 tu doi tuong user da ton tai lay id user de tao thu muc id nguoi dung , de
            // luu tru hinh anh
            String uploadDir = "user-photos/" + savedUser.getId();
            // lam sach file
            FileUploadUtil.cleanDir(uploadDir);

            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            service.updateAccount(user);
        }

        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());

        redirectAttributes.addFlashAttribute("message", "your account details have been updated");

        //refactor -> extracmethod
        return "redirect:/account";
    }

}
