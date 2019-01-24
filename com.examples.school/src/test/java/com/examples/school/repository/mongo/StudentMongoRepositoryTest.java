package com.examples.school.repository.mongo;

import static com.examples.school.repository.mongo.StudentMongoRepository.SCHOOL_DB_NAME;
import static com.examples.school.repository.mongo.StudentMongoRepository.STUDENT_COLLECTION_NAME;
import static org.assertj.core.api.Assertions.*;

import java.net.InetSocketAddress;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.examples.school.model.Student;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class StudentMongoRepositoryTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient client;
	private StudentMongoRepository studentRepository;
	private MongoCollection<Document> studentCollection;

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		// bind on a random local port
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(serverAddress));
		studentRepository = new StudentMongoRepository(client);
		MongoDatabase database = client.getDatabase(SCHOOL_DB_NAME);
		// make sure we always start with a clean database
		database.drop();
		studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(studentRepository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");
		assertThat(studentRepository.findAll())
			.containsExactly(
				new Student("1", "test1"),
				new Student("2", "test2"));
	}

	@Test
	public void testFindByIdNotFound() {
		assertThat(studentRepository.findById("1"))
			.isNull();
	}

	@Test
	public void testFindByIdFound() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");
		assertThat(studentRepository.findById("2"))
			.isEqualTo(new Student("2", "test2"));
	}

	private void addTestStudentToDatabase(String id, String name) {
		studentCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}
}
