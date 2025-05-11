package com.face.sign.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.SecurityUtils;
import com.face.sign.common.util.SnowflakeUtils;
import com.face.sign.common.util.mybatisplus.QueryWrapperUtils;
import com.face.sign.course.bean.dto.ExcelStudentDto;
import com.face.sign.course.entity.ClassEntity;
import com.face.sign.course.mapper.ClassMapper;
import com.face.sign.course.service.IClassService;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.mapper.StudentMapper;
import com.face.sign.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClassServiceImpl extends BaseServiceImpl<ClassEntity, ClassMapper> implements IClassService {

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importStudents(List<ExcelStudentDto> studentDataList) {
        for (ExcelStudentDto studentData : studentDataList) {
            // 检查学生是否已存在
            StudentEntity studentEntity = studentMapper.selectOne(new QueryWrapper<StudentEntity>().eq("student_number", studentData.getStudentNumber()));
            if (studentEntity != null) {
                continue;
            }
            UserEntity user = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("account", studentData.getStudentNumber()));
            if (user != null) {
                continue;
            }

            // 检查班级是否存在，不存在则新建
            ClassEntity classEntity = classMapper.selectOne(new QueryWrapper<ClassEntity>().eq("class_name", studentData.getClassName()));
            if (classEntity == null) {
                classEntity = new ClassEntity();
                classEntity.setClassName(studentData.getClassName());
                classMapper.insert(classEntity);
                classEntity = classMapper.selectOne(new QueryWrapper<ClassEntity>().eq("class_name", studentData.getClassName()));
            }

            UserEntity userEntity = new UserEntity();
            userEntity.setAccount(studentData.getStudentNumber());
            userEntity.setPassword(SecurityUtils.encodePassword(studentData.getStudentNumber()));
            userEntity.setRole("student");
            Long userId = Long.valueOf(SnowflakeUtils.createId());
            userEntity.setId(userId);
            userEntity.setStatus(1);
            userMapper.insert(userEntity);

            // 创建学生实体
            StudentEntity student = new StudentEntity();
            student.setName(studentData.getName());
            student.setStudentNumber(studentData.getStudentNumber());
            student.setClassId(classEntity.getId());
            student.setPhone(studentData.getPhone());
            student.setEmail(studentData.getEmail());
            student.setUserId(userId);
            // 保存学生信息
            studentMapper.insert(student);
        }
    }

    @Override
    public List<ExcelStudentDto> queryStudents(Map<String, Object> conditions, String token) {
        QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtils.buildLikeQueryWrapper(queryWrapper, conditions);
        queryWrapper.orderByDesc("add_time");
        List<StudentEntity> studentEntities = studentMapper.selectList(queryWrapper);

        List<ExcelStudentDto> studentDtos = new ArrayList<>();
        for (StudentEntity studentEntity : studentEntities) {
            ExcelStudentDto studentDto = BeanUtil.copyProperties(studentEntity, ExcelStudentDto.class);

            // 查询班级名称
            ClassEntity classEntity = classMapper.selectById(studentEntity.getClassId());
            if (classEntity != null) {
                studentDto.setClassName(classEntity.getClassName());
            }

            studentDtos.add(studentDto);
        }


        return studentDtos;
    }
}