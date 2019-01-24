package com.examples.school.repository;

import java.util.List;

import com.examples.school.model.Student;

public interface StudentRepository {
	public List<Student> findAll();

	public Student findById(String id);

	public void save(Student student);

	public void delete(String id);
}
