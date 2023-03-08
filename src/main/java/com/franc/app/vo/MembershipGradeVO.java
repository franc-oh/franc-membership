package com.franc.app.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = {"mspId", "mspGradeCd"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MembershipGradeVO {

    private String mspId;
    private String mspGradeCd;
    private Integer gradeUpPointFr;
    private Integer gradeUpPointTo;
    private Integer accumRat;
    private Integer discRat;
    private LocalDateTime insertDate;
    private String insertUser;
    private LocalDateTime updateDate;
    private String updateUser;



}
