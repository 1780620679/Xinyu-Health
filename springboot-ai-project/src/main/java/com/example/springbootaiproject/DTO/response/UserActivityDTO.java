package com.example.springbootaiproject.DTO.response;

import lombok.Data;

/**
 * 用户活跃度
 */
@Data
public class UserActivityDTO {

    private String date;

    private Long activeUsers;

    private Long newUsers;

    private Long diaryUsers;

    private Long consultationUsers;
}
