package com.vulkano.desarolloApp.config;

import com.vulkano.desarolloApp.models.user.ERole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import com.vulkano.desarolloApp.models.user.RoleEntity;

@Component
@ReadingConverter
public class StringToRoleEntityConverter implements Converter<String, RoleEntity> {

    @Override
    public RoleEntity convert(String source) {
        RoleEntity role = new RoleEntity();
        role.setName(ERole.valueOf(source));
        return role;
    }
}