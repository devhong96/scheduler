package com.attendance.scheduler.Config.Authority;

import com.attendance.scheduler.Admin.AdminEntity;
import com.attendance.scheduler.Admin.AdminRepository;
import com.attendance.scheduler.Teacher.TeacherEntity;
import com.attendance.scheduler.Teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("teacherId = {}", username);

        final TeacherEntity teacherEntity = teacherRepository
                .findByUsernameIs(username);
        if(teacherEntity != null){
            return new TeacherDetails(teacherEntity);
        }

        final AdminEntity adminEntity = adminRepository
                .findByUsernameIs(username);
        if(adminEntity != null){
            return new AdminDetails(adminEntity);
        }

        throw new UsernameNotFoundException(username);
    }
}