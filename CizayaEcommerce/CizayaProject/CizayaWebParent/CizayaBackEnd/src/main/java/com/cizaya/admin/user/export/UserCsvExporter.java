package com.cizaya.admin.user.export;

import com.cizaya.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserCsvExporter {

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormatter.format(new Date());
        String fileName ="users_" + timestamp + ".csv";
        //dinh dang csv
        response.setContentType("text/csv");
        //khoa tieu de
        String headerKey = "Content-Dispositon";
        //tep dinh kem + ten
        String headerValue = "attachment; fileName=" + fileName;
        //trinh duyet se luon tai voi dinh dang key tieu de va noi dung + ten file
        response.setHeader(headerKey,headerValue);

        //ham nay de cover sang csv
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        //khai bao chuoi string chua thong tin tieu de cua file in ra
        String[] csvHeader = {"User Id","Email","FirstName","LastName","Roles","Enabled"};
        String[] filedMapping = {"id","email","firstName","lastName","roles","enabled"};
        csvWriter.writeHeader(csvHeader);
        for (User user: listUsers) {
            csvWriter.write(user, filedMapping);
        }
        csvWriter.close();
    }
}
