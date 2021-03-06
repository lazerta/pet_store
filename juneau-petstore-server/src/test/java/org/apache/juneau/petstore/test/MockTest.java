package org.apache.juneau.petstore.test;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.juneau.petstore.App;
import org.apache.juneau.petstore.repository.UserRepository;
import org.apache.juneau.petstore.rest.PetStoreResource;
import org.apache.juneau.rest.mock2.MockRest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings("javadoc")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { App.class })
@SpringBootTest
public class MockTest {

	@Autowired
	PetStoreResource petStoreResource;
	MockRest petStoreRest;
	UserRepository userRepository;

	@Before
	public void setup() {

		petStoreRest = MockRest.create(petStoreResource).simpleJson().build();

	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Pets
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private String createTestPet() throws AssertionError, ServletException, IOException {

		String petId = petStoreRest
				.post("/pet", "{name:'Sunshine',tags:['nice'], price:100.0,species:'BIRD'}")
				.execute()
				.assertStatus(200)
				.getBodyAsString();

		return petId;
	}

	private void deleteTestPets() throws AssertionError, ServletException, IOException {

		petStoreRest
		.delete("/pets")
		.execute()
		.assertStatus(200);

	}

	// Delete pet by Id

	@Test
	public void testDeletePet() throws Exception {

		String petId = createTestPet();
		petStoreRest
		.delete("/pet/" + petId)
		.execute()
		.assertStatus(200);

	}

	// Getting all pets

	@Test
	public void testGettingPets() throws Exception {

		String petId = createTestPet();

		petStoreRest
		.get("/pet")
		.execute()
		.assertStatus(200)
		.assertBody(
				"[{id:" + petId + ",species:'BIRD',name:'Sunshine',tags:['nice'],price:100.0,status:'AVAILABLE'}]");
		deleteTestPets();
	}

	// Posting pet

	@Test
	public void testPostPet() throws Exception {

		petStoreRest
		.post("/pet", "{name:'Sunshine',tags:['nice'], price:100.0,species:'BIRD'}")
		.execute()
		.assertStatus(200);

		deleteTestPets();
	}

	// Find pet by Id

	@Test
	public void testfindPet() throws Exception {

		String petId = createTestPet();

		petStoreRest
		.get("/pet/" + petId)
		.execute()
		.assertStatus(200)
		.assertBody(
				"{id:" + petId + ",species:'BIRD',name:'Sunshine',tags:['nice'],price:100.0,status:'AVAILABLE'}");

		deleteTestPets();

	}

	// Find pet by status

	@Test
	public void testfindPetByStatus() throws Exception {

		String petId = createTestPet();

		petStoreRest
		.get("/pet/findByStatus?status=AVAILABLE")
		.execute()
		.assertStatus(200)
		.assertBody(
				"[{id:" + petId + ",species:'BIRD',name:'Sunshine',tags:['nice'],price:100.0,status:'AVAILABLE'}]");

		deleteTestPets();
	}

	// Updating pet

	@Test
	public void testUpdatePet() throws Exception {

		String petId = createTestPet();

		petStoreRest
				.put("/pet/" + petId,
						"{id: " + petId
								+ ",name:'Daisy1',price:1000.0,species:'BIRD'tags:['nice'], status:'AVAILABLE' }")
				.execute()
				.assertStatus(200);

		deleteTestPets();

	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Users
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void deleteTestUsers() throws AssertionError, ServletException, IOException {

		petStoreRest
		.delete("/users/")
		.execute()
		.assertStatus(200);

	}

	private String createTestUser(String username) throws AssertionError, ServletException, IOException {

		petStoreRest
				.post("/user", "{username:" + username + ",firstName: 'Tom',lastName: 'Simon', userStatus: 'ACTIVE'}")
				.execute()
				.assertStatus(200);

		return username;
	}

	// Create user

	@Test
	public void testCreateUser() throws Exception {

		petStoreRest
		.post("/user", "{username:'catlover',firstName: 'Tom',lastName: 'Simon', userStatus: 'ACTIVE'}")
		.execute()
		.assertStatus(200);

		deleteTestUsers();
	}

	// Delete user

	@Test
	public void testDeleteUser() throws Exception {

		petStoreRest
		.delete("/user/" + "catlover1")
		.execute()
		.assertStatus(200);

	}

	// Create list of users

	@Test
	public void testCreateUsers() throws Exception {

		petStoreRest
				.post("/user/createWithArray",
						"[{username:'billy',firstName: 'Billy',lastName: 'Bob', userStatus: 'ACTIVE'},"
								+ "{username:'peter',firstName: 'Peter',lastName: 'Adams', userStatus: 'ACTIVE'}]")
				.execute()
				.assertStatus(200);

		deleteTestUsers();
	}

	// Getting all users

	@Test
	public void testGettingUsers() throws Exception {

		createTestUser("doglover");
		petStoreRest
		.get("/user")
		.execute()
		.assertStatus(200)
		.assertBody("[{username:'doglover',firstName:'Tom',lastName:'Simon',userStatus:'ACTIVE'}]");

		deleteTestUsers();
	}

	// Get user by user name

	@Test
	public void testFindUserByName() throws Exception {

		createTestUser("garfield");
		petStoreRest
		.get("/user/garfield")
		.execute()
		.assertStatus(200)
		.assertBody("{username:'garfield',firstName:'Tom',lastName:'Simon',userStatus:'ACTIVE'}");

		deleteTestUsers();
	}

	// Updating user

	@Test
	public void testUpdateUser() throws Exception {

		createTestUser("snoopy");
		petStoreRest
		.put("/user/snoopy", "{username:'snoopy',phone: '34562345'}")
		.execute()
		.assertStatus(200);

		deleteTestUsers();
	}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Orders
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void deleteTestOrders() throws AssertionError, ServletException, IOException {

		petStoreRest
		.delete("/orders")
		.execute()
		.assertStatus(200);

	}

	private String createTestOrder() throws AssertionError, ServletException, IOException {

		String petId = createTestPet();
		String orderId = petStoreRest
				.post("/store/order", "{petId:" + petId + " + ,username: 'catlover'}")
				.execute()
				.assertStatus(200)
				.getBodyAsString();

		return orderId;
	}

	// Posting order

	@Test
	public void testPostOrder() throws Exception {

		petStoreRest
		.post("/store/order", "{petId:'1',username: 'snoopy'}")
		.execute()
		.assertStatus(200);

		deleteTestOrders();

	}

	// Getting all orders

	@Test
	public void testGettingOrders() throws Exception {

		String orderId = createTestOrder();
		petStoreRest
		.get("/store/order")
		.execute()
		.assertStatus(200)
		.assertBody("[{id:" + orderId + ",petId:0,status:'PLACED'}]");

		deleteTestOrders();

	}

	// Find order by Id

	@Test
	public void testfindOrder() throws Exception {

		String orderId = createTestOrder();
		petStoreRest
		.get("/store/order/" + orderId)
		.execute()
		.assertStatus(200)
		.assertBody("{id:" + orderId + ",petId:0,status:'PLACED'}");

		deleteTestOrders();
	}

	// Delete order by Id

	@Test
	public void testDeleteOrder() throws Exception {

		String orderId = createTestOrder();
		petStoreRest
		.delete("/store/order/" + orderId)
		.execute()
		.assertStatus(200);

	}
}