package com.revature.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
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
	
	@Test
	public void test_getAllAccountsFromClient_positive() throws BadParameterException, DatabaseException, ClientNotFoundException, SQLException {
		
		when(clientDao.getClientById(eq(10))).thenReturn(new Client(10, "jim", 100));
		
		List<Account> mockAccounts = new ArrayList<>();
		mockAccounts.add(new Account(1, "account1", 30, 10));
		mockAccounts.add(new Account(2, "account2", 33, 10));
		
		when(accountDao.getAllAccountsFromClient(eq(10))).thenReturn(mockAccounts);
		
		List<Account> actualAccounts = accountService.getAllAccountsFromClient("10");
		
		assertEquals(mockAccounts, actualAccounts);
	}
	
	@Test
	public void test_getAllAccountsFromClient_clientDoesNotExist() throws BadParameterException, DatabaseException, ClientNotFoundException, SQLException {
		
		try {
			when(clientDao.getClientById(eq(10))).thenReturn(null);
			
			accountService.getAllAccountsFromClient("10");
			
			fail();
		} catch(ClientNotFoundException e) {
			assertEquals("Client with id 10 was not found", e.getMessage());
		}
		
	}
	
	@Test
	public void test_getAllAccountsFromClient_invalidFormatClientId() throws DatabaseException, ClientNotFoundException {
		try {
			accountService.getAllAccountsFromClient("abc");
			
			fail();
		} catch(BadParameterException e) {
			assertEquals("abc was passed in by the user as the id, but it is not an int", e.getMessage());
		}
	}
	
	@Test(expected = DatabaseException.class)
	public void test_getAllAccountsFromClient_SQLExceptionEncountered_fromClientDao_getClientById() throws SQLException, BadParameterException, DatabaseException, ClientNotFoundException {
		when(clientDao.getClientById(eq(10))).thenThrow(SQLException.class);
		
		accountService.getAllAccountsFromClient("10");
	}
	
	@Test(expected = DatabaseException.class)
	public void test_getAllAccountsFromClient_SQLExceptionEncountered_fromAccountDao_getAllAccountsFromClient() throws SQLException, BadParameterException, DatabaseException, ClientNotFoundException {
		when(clientDao.getClientById(eq(10))).thenReturn(new Client(10, "jim", 25));
		
		when(accountDao.getAllAccountsFromClient(eq(10))).thenThrow(SQLException.class);
		
		accountService.getAllAccountsFromClient("10");
	}
}
