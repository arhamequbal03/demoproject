package com.arham.demo_project.Services;
import com.arham.demo_project.Model.Report;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomMessage {

    public Map<String,Object> GenerateCustomReport(List<Report> reports){
        Map<String,Object> response= new LinkedHashMap<>();
        if(reports.isEmpty()){
            response.put("message","No record found");
            return response;
        }
        response.put("message","Records found");
        List<Map<String, Object>> reportList = new ArrayList<>();
        for (Report report : reports) {
            Map<String, Object> reportMap = new LinkedHashMap<>();
            reportMap.put("id", report.getBorrow_id());
            reportMap.put("book_id", report.getBook_id());
            reportMap.put("member_id", report.getMember_id());
            reportMap.put("issue_date", report.getIssue_date());

            if (report.getReturn_date() != null) {
                reportMap.put("return_date", report.getReturn_date());
                reportMap.put("fine_amount", report.getFine_amount());
            }
            reportList.add(reportMap);
        }

        response.put("Report",reportList);
        return response;
    }

    public Map<String,Object> GenerateIssueMessage(Report r1){
        Map<String ,Object> response= new LinkedHashMap<>();
        response.put("message","Book issued successfully");
        response.put("body",r1);
        return response;
    }

    public Map<String,Object> GenerateReturnMessage(Long MemberId,Long BookId){
        Map<String ,Object> response= new LinkedHashMap<>();
        response.put("message","Book returned successfully");
        response.put("body","member_id:"+MemberId+", book_id : "+ BookId) ;
        return response;
    }

}