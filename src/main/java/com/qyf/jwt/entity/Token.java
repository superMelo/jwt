package com.qyf.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Token {

    private String id;

    private String userId;

    private String token;

    private Date expTime;

    private String ip;
}
