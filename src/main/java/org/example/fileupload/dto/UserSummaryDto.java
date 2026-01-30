package org.example.fileupload.dto;



import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {

    private Integer id;
    private String fullName;
    private String email;
}
