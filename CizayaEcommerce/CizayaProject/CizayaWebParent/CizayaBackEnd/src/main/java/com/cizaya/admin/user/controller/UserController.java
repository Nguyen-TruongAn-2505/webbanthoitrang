package com.cizaya.admin.user.controller;

import java.io.IOException;

import java.util.List;

import com.cizaya.admin.user.UserNotFoundException;
import com.cizaya.admin.user.UserService;
import com.cizaya.admin.user.export.UserCsvExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cizaya.admin.FileUploadUtil;
import com.cizaya.common.entity.Role;
import com.cizaya.common.entity.User;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    // load ra list user theo duong link /users
    @GetMapping("/users")
    public String listFirstPage(Model model) {
        //them "firstName","asc" de sap xep theo ten
        return listByPage(1, model, "firstName", "asc", null);
    }

    //ham hien thi phan trang, khi nguoni dung kich vao 1 nut chuyen trang, se goi den ham nay
    // dau vao nhan duoc 1 duong link chua  pageNumber va can co 1 model de chuyen trang thai hay la phan trang
    //
    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword) {
        System.out.println("sortField" + sortField);
        System.out.println("sortOrder" + sortDir);

        Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
        List<User> listUsers = page.getContent();

        long startCount = (pageNum - 1) * UserService.USER_PER_PAGE + 1;
        long endCount = startCount + UserService.USER_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        // neu sortDir ko la asc thi se la desc
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        //cac thuoc tinh de chuyen du lieu qua cac view
        //1. phan trang
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCode", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);
        //2.sap xep
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        //3.tim kiem
        model.addAttribute("keyword", keyword);
        return "users/user";
    }


    // load ra thong tin de them moi theo duong link /users/new
    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> listRoles = service.listRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }

    // luu thong tin vao db theo duong link users/save
    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {
        // khi user form submited len server
        // neu file anh ko null thi can set nhung thu sau
        if (!multipartFile.isEmpty()) {
            // 1 don dep file anh
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            // 2 dat truong anh cua nguoi dung voi ten tep
            user.setPhotos(fileName);
            // 3 duy tri thong tin chi tiet ve ng dung bang cach luu vao database
            User savedUser = service.save(user);
            // 4 tu doi tuong user da ton tai lay id user de tao thu muc id nguoi dung , de
            // luu tru hinh anh
            String uploadDir = "user-photos/" + savedUser.getId();
            // lam sach file
            FileUploadUtil.cleanDir(uploadDir);

            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            service.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has bean saved successfully");

        //refactor -> extracmethod
        return getRedirectURLtoAffectedUser(user);
    }

    // return "redirect:/users"; chuyen doi url co dinh va them mot vai tham so nhu sap xep, keyword, ...
    // va chi hien thi nguoi dung bi anh huong voi tu khoa la phan dau tien cua email
    private  String getRedirectURLtoAffectedUser(User user) {
        String firstPartOrEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOrEmail;
    }

    // hien thi form update theo id
    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            List<Role> listRoles = service.listRoles();
            User user = service.get(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (ID:" + id + ")");
            model.addAttribute("listRoles", listRoles);
            return "users/user_form";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }

    }

    // xoa theo id
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("message", "The user ID" + id + "has bean deleted successfully");
            return "redirect:/users";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }
    }

    // vo hieu hoa trang thai
    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        service.updateUserEnbledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String mesagess = "The user ID " + id + "has been " + status;
        redirectAttributes.addFlashAttribute("message", mesagess);

        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public  void exportToCsv(HttpServletResponse response) throws IOException {
        List<User>  listUsers = service.listAll();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(listUsers,response);
    }
}
