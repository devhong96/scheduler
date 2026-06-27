package com.attendance.scheduler.infra.config.security.User;

import com.attendance.scheduler.teacher.domain.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class TeacherDetails implements UserDetails {

    private final Teacher teacherEntity;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
        return collection;
    }

    @Override
    public String getPassword() {
        return teacherEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return teacherEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return teacherEntity.isApproved();
    }
}