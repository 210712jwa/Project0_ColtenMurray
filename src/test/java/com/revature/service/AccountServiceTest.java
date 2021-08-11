package com.revature.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
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
import com.revature.dto.AddOrEditAccountDTO;
import com.revature.dto.AddOrEditClientDTO;
import com.revature.exception.BadParameterException;
import com.revature.exception.DatabaseException;
import com.revature.exception.ClientNotFoundException;
import com.revature.model.Account;
import com.revature.model.Client;

public class AccountServiceTest {

	private AccountService accountService;
	private ClientDAO clientDao;
	private AccountDAO accountDao;
	
	@Before
	public void setUp() {
		this.clientDao = mock(ClientDAO.class);
		this.accountDao = mock(AccountDAO.class);
		
		this.accountService = new AccountService(clientDao, accountDao);
	}
	
	/*
	 * getAllAccountsFromClient
	 */
	@Test
	public void test_getAllAccountsFromClient_positive() throws BadParameterException, DatabaseException, ClientNotFoundException, SQLException {
		
		when(clientDao.getClientById(eq(10))).thenReturn(new Client(10, "jim", 100));
		
		List<Account> mockAccounts = new ArrayList<>();
		mockAccounts.add(new Account(1, "account1", 30, 10));
		mockAccounts.add(new Account(2, "account2", 33, 10));
		
		when(accountDao.getAllAccountsFromClient(eq(10))).thenReturn(mockAccounts);
		
		List<Account> actualAccounts = accountService.getAllAccountsFromClient("10", null, null);
		
		assertEquals(mockAccounts, actualAccounts);
	}
	
	@Test
	public void test_getAllAccountsFromClient_clientDoesNotExist() throws BadParameterException, DatabaseException, ClientNotFoundException, SQLException {
		
		try {
			when(clientDao.getClientById(eq(10))).thenReturn(null);
			
			accountService.getAllAccountsFromClient("10", null, null);
			
			fail();
		} catch(ClientNotFoundException e) {
			assertEquals("Client with id 10 was not found", e.getMessage());
		}
		
	}
	
	@Test
	public void test_getAllAccountsFromClient_invalidFormatClientId() throws DatabaseException, ClientNotFoundException {
		try {
			accountService.getAllAccountsFromClient("abc", null, null);
			
			fail();
		} catch(BadParameterException e) {
			assertEquals("abc was passed in by the user as the id, but it is not an int", e.getMessage());
		}
	}
	
	@Test(expected = DatabaseException.class)
	public void test_getAllAccountsFromClient_SQLExceptionEncountered_fromClientDao_getClientById() throws SQLException, BadParameterException, DatabaseException, ClientNotFoundException {
		when(clientDao.getClientById(eq(10))).thenThrow(SQLException.class);
		
		accountService.getAllAccountsFromClient("10", null, null);
	}
	
	@Test(expected = DatabaseException.class)
	public void test_getAllAccountsFromClient_SQLExceptionEncountered_fromAccountDao_getAllAccountsFromClient() throws SQLException, BadParameterException, DatabaseException, ClientNotFoundException {
		when(clientDao.getClientById(eq(10))).thenReturn(new Client(10, "jim", 25));
		
		when(accountDao.getAllAccountsFromClient(eq(10))).thenThrow(SQLException.class);
		
		accountService.getAllAccountsFromClient("10", null, null);
	}
	/*
	 * getAccountById
	 */
	@Test
	public void test_getAccountById_idStringIsNotAnInt() throws DatabaseException, ClientNotFoundException {
		try {
			accountService.getAccountById("asdfasdf", "fdgsdfg");
			fail();
		} catch (BadParameterException e) {
			assertEquals("asdfasdf was passed in by the user as the id, but it is not an int", e.getMessage());
		}
	}
	
	@Test
	public void test_getAccountById_existingId() throws SQLException, DatabaseException, ClientNotFoundException, BadParameterException {
		when(accountDao.getAccountById(eq(1),eq(1))).thenReturn(new Account(1, "checking", 100, 1));
		when(clientDao.getClientById(eq(1))).thenReturn(new Client (1, "bill", 32));
		
		Account actual = accountService.getAccountById("1", "1");
		
		Account expected = new Account(1, "checking", 100, 1);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_getAccountById_accountDoesNotExist() throws DatabaseException, ClientNotFoundException, BadParameterException {
		try {
			accountService.getAccountById("10", "10"); // Because I'm not providing a mock response for getClientId when the int parameter
			// passed into that method is 10, it will by default return a null value
			
			fail();
		} catch (ClientNotFoundException e) {
			assertEquals("Client with id 10 was not found", e.getMessage());
		}
	}
	
	
	/*
	 * addAccount
	 */
	@Test
	public void test_addAccount_positivePath() throws SQLException, DatabaseException, BadParameterException {
		
		AddOrEditAccountDTO dto = new AddOrEditAccountDTO();
		dto.setName("checking");
		dto.setBalance(100);
		dto.setClientId(1);
		
		when(accountDao.addAccount(eq(dto))).thenReturn(new Account(1, "checking", 100, 1));
		
		Account actual = accountService.addAccount(dto);
		
		Account expected = new Account(1, "checking", 100, 1);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_addAccount_blankName() throws DatabaseException {
		AddOrEditAccountDTO dto = new AddOrEditAccountDTO();
		dto.setName("");
		dto.setBalance(100);
		dto.setClientId(1);
		
		try {
			accountService.addAccount(dto);
			
			fail();
		} catch (BadParameterException e) {
			assertEquals("Account name cannot be blank", e.getMessage());
		}
		
	}
	
	@Test
	public void test_addAccount_blankNameWithSpaces() throws DatabaseException {
		AddOrEditAccountDTO dto = new AddOrEditAccountDTO();
		dto.setName("              ");
		dto.setBalance(100);
		dto.setClientId(1);
		
		try {
			accountService.addAccount(dto);
			
			fail();
		} catch (BadParameterException e) {
			assertEquals("Account name cannot be blank", e.getMessage());
		}
	}

	@Test
	public void test_addAccount_negativeBalance() throws DatabaseException {
		AddOrEditAccountDTO dto = new AddOrEditAccountDTO();
		dto.setName("checking");
		dto.setBalance(-1);
		dto.setClientId(1);
		
		try {
			accountService.addAccount(dto);
			
			fail();
		} catch (BadParameterException e) {
			assertEquals("Account balance cannot be less than 0", e.getMessage());
		}
		
	}
	
	@Test
	public void test_addAccount_negativeBalanceAndBlankName() throws DatabaseException {
		AddOrEditAccountDTO dto = new AddOrEditAccountDTO();
		dto.setName("");
		dto.setBalance(-10);
		dto.setClientId(1);
		
		try {
			accountService.addAccount(dto);
			
			fail();
		} catch (BadParameterException e) {
			assertEquals("Account name cannot be blank and balance cannot be less than 0", e.getMessage());
		}
		
	}
	
	@Test(expected = DatabaseException.class)
	public void test_addAccount_SQLExceptionEncountered() throws SQLException, DatabaseException, BadParameterException {
		when(accountDao.addAccount(any())).thenThrow(SQLException.class);
		
		AddOrEditAccountDTO dto = new AddOrEditAccountDTO();
		dto.setName("Black Pearl");
		dto.setBalance(100);
		accountService.addAccount(dto);
	}
	
	/*
	 * editAccount
	 */
	@Test
	public void test_editAccount_positivePath() throws DatabaseException, ClientNotFoundException, BadParameterException, SQLException {
		AddOrEditAccountDTO dto = new AddOrEditAccountDTO();
		dto.setName("checking");
		dto.setBalance(100);
		dto.setClientId(1);
		
		Account accountWithId10 = new Account(10, "checking", 100, 1);
		Client clientWithId10 = new Client (1, "bill", 32);
		when(accountDao.getAccountById(eq(10), eq(1))).thenReturn(accountWithId10);
		when(clientDao.getClientById(eq(10))).thenReturn(clientWithId10);
		
		when(accountDao.editAccount(eq(10),eq(1), eq(dto))).thenReturn(new Account(10, "checking", 100, 1));
		
		Account actual = accountService.editAccount("10", "1", dto);
		
		Account expected = new Account(10, "checking", 100, 1);

		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_editAccount_clientDoesNotExist() throws DatabaseException, BadParameterException {
		AddOrEditAccountDTO dto = new AddOrEditAccountDTO();
		dto.setName("Black Pearl");
		dto.setBalance(100);
		dto.setClientId(1);
		
		try {
			accountService.editAccount("1000", "1", dto);
			
			fail();
		} catch (ClientNotFoundException e) {
			assertEquals("Client with id 1000 was not found", e.getMessage());
		}
		
	}
	
	@Test(expected = BadParameterException.class)
	public void test_editAccount_invalidId() throws DatabaseException, ClientNotFoundException, BadParameterException {
		AddOrEditAccountDTO dto = new AddOrEditAccountDTO();
		dto.setName("checking");
		dto.setBalance(100);
		dto.setClientId(1);
		
		accountService.editAccount("abc", "1", dto);
	}
	
	@Test(expected = DatabaseException.class)
	public void test_editAccount_SQLExceptionEncountered() throws SQLException, DatabaseException, ClientNotFoundException, BadParameterException {
		AddOrEditAccountDTO dto = new AddOrEditAccountDTO();
		dto.setName("checking");
		dto.setBalance(100);
		dto.setClientId(1);
		
		when(accountDao.getAccountById(eq(3), eq(10))).thenReturn(new Account(10, "saving", 500, 3));
		when(clientDao.getClientById(eq(3))).thenReturn(new Client (3, "bill", 32));
		when(accountDao.editAccount(eq(3), eq(10), eq(dto))).thenThrow(SQLException.class);
		
		accountService.editAccount("3", "10", dto);
	}
	
	// deleteAccount
	@Test
	public void test_deleteAccount_clientDoesNotExist() throws DatabaseException, BadParameterException {
		
		try {
			accountService.deleteAccount("1000", "1");
			
			fail();
		} catch (ClientNotFoundException e) {
			assertEquals("Client with id 1000 was not found", e.getMessage());
		}
		
	}
	
	@Test(expected = BadParameterException.class)
	public void test_deleteAccount_invalidId() throws DatabaseException, ClientNotFoundException, BadParameterException {
		
		
		accountService.deleteAccount("abc", "abc");
	}
}
