package org.example.fileupload.dto;



import lombok.*;
import org.example.fileupload.entity.enums.Role;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {

    private Integer id;
    private String fullName;
    private String email;
    private Role role;
}
