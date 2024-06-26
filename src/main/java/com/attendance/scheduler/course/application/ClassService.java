package com.attendance.scheduler.course.application;


import com.attendance.scheduler.course.dto.ClassDTO;
import com.attendance.scheduler.course.dto.StudentClassDTO;
import com.attendance.scheduler.student.dto.ClassListDTO;

import java.util.List;
import java.util.Optional;

public interface ClassService {

      List<ClassDTO> findStudentClassList();

      ClassListDTO findTeachersClasses(String studentName);

      // 수업 유무 조회
      Optional<StudentClassDTO> findStudentClasses(String studentName);

      void saveClassTable(ClassDTO classDTO) throws InterruptedException;

      // 수업 삭제
      void deleteClass(String studentName);

}
