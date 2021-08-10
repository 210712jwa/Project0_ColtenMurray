package com.revature.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
// You may need to type this import manually to make use of 
// the argument matchers for Mockito, such as eq() or any()
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.revature.dao.AccountDAO;
import com.revature.dao.ClientDAO;
import com.revature.dto.AddOrEditClientDTO;
import com.revature.exception.BadParameterException;
import com.revature.exception.DatabaseException;
import com.revature.exception.ClientNotFoundException;
import com.revature.model.Account;
import com.revature.model.Client;

public class ClientServiceTest {
	
	private ClientService clientService;
	private ClientDAO clientDao;
	private AccountDAO accountDao;
	
	@Before
	public void setUp() throws Exception {
		this.clientDao = mock(ClientDAO.class); // I'm using Mockito to create a fake clientDao object
		this.accountDao = mock(AccountDAO.class);
		
		this.clientService = new ClientService(clientDao, accountDao); // Here I am injecting the mocked object into a ClientService object
	}

	// Positive test case / "happy path"
	@Test
	public void test_getAllClients_positive() throws DatabaseException, SQLException {
		// Because we're not using a real ClientDAO object and instead a mocked ClientDAO, 
		// we need to actually specify what we want the mocked ClientDAO to return whenever we invoke the clientDao.getAllClients() method
		List<Client> mockReturnValues = new ArrayList<>();
		mockReturnValues.add(new Client(1, "Black Pearlssss", 40));
		mockReturnValues.add(new Client(2, "Royal Fortune", 10));
		when(clientDao.getAllClients()).thenReturn(mockReturnValues);
		
		List<Account> blackPearlAccounts = new ArrayList<>();
		blackPearlAccounts.add(new Account(1, "Jack Sparrow", 28, 1));
		blackPearlAccounts.add(new Account(2, "Captain Hook", 60, 1));
		when(accountDao.getAllAccountsFromClient(eq(1))).thenReturn(blackPearlAccounts);
		
		List<Account> royalFortuneAccounts = new ArrayList<>();
		royalFortuneAccounts.add(new Account(10, "test1", 100, 2));
		royalFortuneAccounts.add(new Account(53, "test2", 101, 2));
		when(accountDao.getAllAccountsFromClient(eq(2))).thenReturn(royalFortuneAccounts);
		
		// actual = the real data being returned by the getAllClients method from clientService
		List<Client> actual = clientService.getAllClients();
		
		System.out.println(actual);
		
		// expected = what we expect for the clients List to contain
		List<Client> expected = new ArrayList<>();
		Client s1 = new Client(1, "Black Pearlssss", 40);
		s1.setAccounts(blackPearlAccounts);
		Client s2 = new Client(2, "Royal Fortune", 10);
		s2.setAccounts(royalFortuneAccounts);
		
		expected.add(s1);
		expected.add(s2);
		
		// So, we do an assertEquals, to have these two compared to each other
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_getAllClients_negative() throws SQLException {
		when(clientDao.getAllClients()).thenThrow(SQLException.class);
		// Simulate a situation where clientDao.getAllClients() throws a SQLException
		
		try {
			clientService.getAllClients();
			
			fail(); // We only reach the fail() assertion IF DatabaseException is not thrown and caught
			// So this allows us to test for the DatabaseException occurring.
			// Additionally, in the catch block, we're also checking the exception message itself
		} catch (DatabaseException e) {
			String exceptionMessage = e.getMessage();
			assertEquals("Something went wrong with our DAO operations", exceptionMessage);
		}
		
	}
	
//	@Test(expected = BadParameterException.class)
//	public void test_getClientById_idStringIsNotAnInt() throws DatabaseException, ClientNotFoundException, BadParameterException {
//		clientService.getClientById("asdfsdf");
//	}
	
	@Test
	public void test_getClientById_idStringIsNotAnInt() throws DatabaseException, ClientNotFoundException {
		try {
			clientService.getClientById("asdfasdf");
			
			fail();
		} catch (BadParameterException e) {
			assertEquals("asdfasdf was passed in by the user as the id, but it is not an int", e.getMessage());
		}
	}
	
	@Test
	public void test_getClientById_existingId() throws SQLException, DatabaseException, ClientNotFoundException, BadParameterException {
		when(clientDao.getClientById(eq(1))).thenReturn(new Client(1, "Black Pearl", 1));
		
		Client actual = clientService.getClientById("1");
		
		Client expected = new Client(1, "Black Pearl", 1);
		expected.setAccounts(new ArrayList<>());
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_getClientById_clientDoesNotExist() throws DatabaseException, ClientNotFoundException, BadParameterException {
		try {
			clientService.getClientById("10"); // Because I'm not providing a mock response for getClientId when the int parameter
			// passed into that method is 10, it will by default return a null value
			
			fail();
		} catch (ClientNotFoundException e) {
			assertEquals("Client with id 10 was not found", e.getMessage());
		}
	}
	
	@Test
	public void test_getClientById_sqlExceptionEncountered() throws SQLException, ClientNotFoundException, BadParameterException {
		try {
			when(clientDao.getClientById(anyInt())).thenThrow(SQLException.class);
			
			clientService.getClientById("1");
			
			fail();
		} catch (DatabaseException e) {
			assertEquals("Something went wrong with our DAO operations", e.getMessage());
		}
	}
	
	/*
	 * addClient
	 */
	@Test
	public void test_addClient_positivePath() throws SQLException, DatabaseException, BadParameterException {
		
		AddOrEditClientDTO dto = new AddOrEditClientDTO();
		dto.setName("Black Pearl");
		dto.setAge(10);
		
		when(clientDao.addClient(eq(dto))).thenReturn(new Client(1, "Black Pearl", 10));
		
		Client actual = clientService.addClient(dto);
		
		Client expected = new Client(1, "Black Pearl", 10);
		expected.setAccounts(new ArrayList<>());
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_addClient_blankName() throws DatabaseException {
		AddOrEditClientDTO dto = new AddOrEditClientDTO();
		dto.setName("");
		dto.setAge(10);
		
		try {
			clientService.addClient(dto);
			
			fail();
		} catch (BadParameterException e) {
			assertEquals("Client name cannot be blank", e.getMessage());
		}
		
	}
	
	@Test
	public void test_addClient_blankNameWithSpaces() throws DatabaseException {
		AddOrEditClientDTO dto = new AddOrEditClientDTO();
		dto.setName("                        ");
		dto.setAge(10);
		
		try {
			clientService.addClient(dto);
			
			fail();
		} catch (BadParameterException e) {
			assertEquals("Client name cannot be blank", e.getMessage());
		}
	}

	@Test
	public void test_addClient_negativeAge() throws DatabaseException {
		AddOrEditClientDTO dto = new AddOrEditClientDTO();
		dto.setName("Bach's Client");
		dto.setAge(-1);
		
		try {
			clientService.addClient(dto);
			
			fail();
		} catch (BadParameterException e) {
			assertEquals("Client age cannot be less than 0", e.getMessage());
		}
		
	}
	
	@Test
	public void test_addClient_negativeAgeAndBlankName() throws DatabaseException {
		AddOrEditClientDTO dto = new AddOrEditClientDTO();
		dto.setName("");
		dto.setAge(-10);
		
		try {
			clientService.addClient(dto);
			
			fail();
		} catch (BadParameterException e) {
			assertEquals("Client name cannot be blank and age cannot be less than 0", e.getMessage());
		}
		
	}
	
	@Test(expected = DatabaseException.class)
	public void test_addClient_SQLExceptionEncountered() throws SQLException, DatabaseException, BadParameterException {
		when(clientDao.addClient(any())).thenThrow(SQLException.class);
		
		AddOrEditClientDTO dto = new AddOrEditClientDTO();
		dto.setName("Black Pearl");
		dto.setAge(10);
		clientService.addClient(dto);
	}
	
	/*
	 * editClient
	 */
	
	@Test
	public void test_editClient_positivePath() throws DatabaseException, ClientNotFoundException, BadParameterException, SQLException {
		AddOrEditClientDTO dto = new AddOrEditClientDTO();
		dto.setName("Black Pearl");
		dto.setAge(100);
		
		Client clientWithId10 = new Client(10, "Jolly Roger", 5);
		when(clientDao.getClientById(eq(10))).thenReturn(clientWithId10);
		
		when(clientDao.editClient(eq(10), eq(dto))).thenReturn(new Client(10, "Black Pearl", 100));
		
		Client actual = clientService.editClient("10", dto);
		
		Client expected = new Client(10, "Black Pearl", 100);
		expected.setAccounts(new ArrayList<>());
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_editClient_clientDoesNotExist() throws DatabaseException, BadParameterException {
		AddOrEditClientDTO dto = new AddOrEditClientDTO();
		dto.setName("Black Pearl");
		dto.setAge(100);
		
		try {
			clientService.editClient("1000", dto);
			
			fail();
		} catch (ClientNotFoundException e) {
			assertEquals("Client with id 1000 was not found", e.getMessage());
		}
		
	}
	
	@Test(expected = BadParameterException.class)
	public void test_editClient_invalidId() throws DatabaseException, ClientNotFoundException, BadParameterException {
		AddOrEditClientDTO dto = new AddOrEditClientDTO();
		dto.setName("Black Pearl");
		dto.setAge(100);
		
		clientService.editClient("abc", dto);
	}
	
	@Test(expected = DatabaseException.class)
	public void test_editClient_SQLExceptionEncountered() throws SQLException, DatabaseException, ClientNotFoundException, BadParameterException {
		AddOrEditClientDTO dto = new AddOrEditClientDTO();
		dto.setName("Black Pearl");
		dto.setAge(100);
		
		when(clientDao.getClientById(eq(10))).thenReturn(new Client(10, "Jolly Roger", 5));
		when(clientDao.editClient(eq(10), eq(dto))).thenThrow(SQLException.class);
		
		clientService.editClient("10", dto);
	}
	
	/*
	 * Exercise: Create tests for DeleteClient and have full test coverage
	 */
}
