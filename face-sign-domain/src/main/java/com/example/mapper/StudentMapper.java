package com.example.mapper;

import com.example.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {
    @Select("SELECT * FROM student WHERE student_id = #{studentId}")
    Student getStudentById(@Param("studentId") Long studentId);

    @Select("SELECT * FROM student WHERE student_no = #{studentNo}")
    Student getStudentByStudentNo(@Param("studentNo") String studentNo);

    @Insert("INSERT INTO student (name, class_id, student_no, face_image_path, created_at, updated_at) " +
            "VALUES (#{name}, #{classId}, #{studentNo}, #{faceImagePath}, #{createdAt}, #{updatedAt})")
    int addStudent(Student student);

    @Update("UPDATE student SET name = #{name}, class_id = #{classId}, face_image_path = #{faceImagePath}, " +
            "updated_at = #{updatedAt} WHERE student_id = #{studentId}")
    int updateStudent(Student student);

    @Delete("DELETE FROM student WHERE student_id = #{studentId}")
    int deleteStudent(@Param("studentId") Long studentId);

    @Select("SELECT * FROM student")
    List<Student> getAllStudents();
}