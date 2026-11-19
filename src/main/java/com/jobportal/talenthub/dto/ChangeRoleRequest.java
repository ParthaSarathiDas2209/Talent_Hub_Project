package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.Role;

public record ChangeRoleRequest(
        Role role
) {
}
