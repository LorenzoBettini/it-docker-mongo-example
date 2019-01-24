package com.examples.school.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class StudentMongoRepository implements StudentRepository {

	public static final String STUDENT_COLLECTION_NAME = "student";
	public static final String SCHOOL_DB_NAME = "school";
	private MongoCollection<Document> studentCollection;

	public StudentMongoRepository(MongoClient client) {
		studentCollection = client
			.getDatabase(SCHOOL_DB_NAME)
			.getCollection(STUDENT_COLLECTION_NAME);
	}

	@Override
	public List<Student> findAll() {
		return StreamSupport.
				stream(studentCollection.find().spliterator(), false)
				.map(d -> new Student(""+d.get("id"), ""+d.get("name")))
				.collect(Collectors.toList());
	}

	@Override
	public Student findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Student student) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub

	}

}
