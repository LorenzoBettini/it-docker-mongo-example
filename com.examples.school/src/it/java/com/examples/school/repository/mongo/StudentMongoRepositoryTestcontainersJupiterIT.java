package com.examples.school.repository.mongo;

import static com.examples.school.repository.mongo.StudentMongoRepository.SCHOOL_DB_NAME;
import static com.examples.school.repository.mongo.StudentMongoRepository.STUDENT_COLLECTION_NAME;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.examples.school.model.Student;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Some integration tests for the
 * {@link com.examples.school.mongo.StudentMongoRepository}, relying on
 * Testcontainers.
 * 
 * These tests do not necessarily make sense: they are meant to be a
 * demonstration of Testcontainers.
 * 
 * @author Lorenzo Bettini
 *
 */
@Testcontainers
class StudentMongoRepositoryTestcontainersJupiterIT {

	@Container
	static final MongoDBContainer mongo =
		new MongoDBContainer("mongo:4.4.3");

	private MongoClient client;
	private StudentMongoRepository studentRepository;
	private MongoCollection<Document> studentCollection;

	@BeforeEach
	void setup() {
		client = new MongoClient(
			new ServerAddress(
				mongo.getContainerIpAddress(),
				mongo.getFirstMappedPort()));
		studentRepository = new StudentMongoRepository(client);
		MongoDatabase database = client.getDatabase(SCHOOL_DB_NAME);
		// make sure we always start with a clean database
		database.drop();
		studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
	}

	@AfterEach
	void tearDown() {
		client.close();
	}

	@Test
	void testFindAll() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");
		assertThat(studentRepository.findAll())
			.containsExactly(
				new Student("1", "test1"),
				new Student("2", "test2"));
	}

	@Test
	void testFindById() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");
		assertThat(studentRepository.findById("2"))
			.isEqualTo(new Student("2", "test2"));
	}

	@Test
	void testSave() {
		Student student = new Student("1", "added student");
		studentRepository.save(student);
		assertThat(readAllStudentsFromDatabase())
			.containsExactly(student);
	}

	@Test
	void testDelete() {
		addTestStudentToDatabase("1", "test1");
		studentRepository.delete("1");
		assertThat(readAllStudentsFromDatabase())
			.isEmpty();
	}

	private void addTestStudentToDatabase(String id, String name) {
		studentCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}

	private List<Student> readAllStudentsFromDatabase() {
		return StreamSupport.
			stream(studentCollection.find().spliterator(), false)
				.map(d -> new Student(""+d.get("id"), ""+d.get("name")))
				.collect(Collectors.toList());
	}
}
