package com.face.sign.user.qo;

import lombok.Data;

/**
 * @author carrocean
 * @date 2025/5/5 12:45
 */
@Data
public class UserQo {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
