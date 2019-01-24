package com.examples.school.controller;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.examples.school.view.StudentView;

public class SchoolController {
	private StudentView studentView;
	private StudentRepository studentRepository;

	public SchoolController(StudentView studentView, StudentRepository studentRepository) {
		this.studentView = studentView;
		this.studentRepository = studentRepository;
	}

	public void allStudents() {
		studentView.showAllStudents(studentRepository.findAll());
	}

	public void newStudent(Student student) {
		Student existingStudent = studentRepository.findById(student.getId());
		if (existingStudent != null) {
			studentView.showError("Already existing student with id " + student.getId(),
					existingStudent);
			return;
		}

		studentRepository.save(student);
		studentView.studentAdded(student);
	}

	public void deleteStudent(Student student) {
		if (studentRepository.findById(student.getId()) == null) {
			studentView.showError("No existing student with id " + student.getId(),
					student);
			return;
		}

		studentRepository.delete(student.getId());
		studentView.studentRemoved(student);
	}

}
