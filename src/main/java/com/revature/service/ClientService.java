package com.revature.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.dao.AccountDAO;
import com.revature.dao.AccountDAOImpl;
import com.revature.dao.ClientDAO;
import com.revature.dao.ClientDAOImpl;
import com.revature.dto.AddOrEditClientDTO;
import com.revature.exception.BadParameterException;
import com.revature.exception.DatabaseException;
import com.revature.exception.ClientNotFoundException;
import com.revature.model.Account;
import com.revature.model.Client;

public class ClientService {

	private Logger logger = LoggerFactory.getLogger(ClientService.class);
	
	// ClientDAO is a dependency of ClientService
	private ClientDAO clientDao;
	private AccountDAO accountDao;
	
	// This constructor will instantiate a REAL ClientDAO object making use of the ClientDAOImpl class
	public ClientService() {
		this.clientDao = new ClientDAOImpl();
		this.accountDao = new AccountDAOImpl();
	}
	
	// This constructor is used to "inject" a fake ClientDAO object whenever I am instantiating a ClientService object
	// that I want to perform unit tests on
	public ClientService(ClientDAO mockedClientDaoObject, AccountDAO mockedAccountDaoObject) {
		this.clientDao = mockedClientDaoObject;
		this.accountDao = mockedAccountDaoObject;
	}
	
	// This method is dependent on a ClientDAO object to function
	// Because we're invoking the getAllClients() method from ClientDAO
	public List<Client> getAllClients() throws DatabaseException {
		List<Client> clients;
		try {
			
			clients = clientDao.getAllClients();
			
			for (Client client : clients) {
				List<Account> accounts = accountDao.getAllAccountsFromClient(client.getId());
				client.setAccounts(accounts);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Something went wrong with our DAO operations");
		}
		
		return clients;
	}
	
	public Client getClientById(String stringId) throws DatabaseException, ClientNotFoundException, BadParameterException {
		try {
			int id = Integer.parseInt(stringId);
			
			Client client = clientDao.getClientById(id);
			
			if (client == null) {
				throw new ClientNotFoundException("Client with id " + id + " was not found");
			}
			
			List<Account> accounts = accountDao.getAllAccountsFromClient(id);
			client.setAccounts(accounts);
			
			return client;
		} catch (SQLException e) {
			throw new DatabaseException("Something went wrong with our DAO operations");
		} catch (NumberFormatException e) {
			throw new BadParameterException(stringId + " was passed in by the user as the id, " + "but it is not an int");
		}
	}
	
	public Client addClient(AddOrEditClientDTO client) throws DatabaseException, BadParameterException {
		if (client.getName().trim().equals("") && client.getAge() < 0) {
			throw new BadParameterException("Client name cannot be blank and age cannot be less than 0");
		}
		
		if (client.getName().trim().equals("")) {
			throw new BadParameterException("Client name cannot be blank");
		}
		
		if (client.getAge() < 0) {
			throw new BadParameterException("Client age cannot be less than 0");
		}
		
		try {
			Client addedClient = clientDao.addClient(client);
			addedClient.setAccounts(new ArrayList<>());
			
			return addedClient;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	public Client editClient(String stringId, AddOrEditClientDTO client) throws DatabaseException, ClientNotFoundException, BadParameterException {
		
		try {
			int clientId = Integer.parseInt(stringId);
			
			// Before we can edit a Client, see if the client already exists, and if not, throw an Exception
			if (clientDao.getClientById(clientId) == null) {
				throw new ClientNotFoundException("Client with id " + clientId + " was not found");
			}
			
			// If client exists, we proceed to edit the client
			Client editedClient = clientDao.editClient(clientId, client);
			
			List<Account> accounts = accountDao.getAllAccountsFromClient(clientId);
			editedClient.setAccounts(accounts);
			
			return editedClient;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		} catch (NumberFormatException e) {
			throw new BadParameterException(stringId + " was passed in by the user as the id, " + "but it is not an int");
		}

	}

	public void deleteClient(String clientId) throws BadParameterException, DatabaseException, ClientNotFoundException {
		
		// Check to see if the client exists
		try {
			int id = Integer.parseInt(clientId);
			
			Client client = clientDao.getClientById(id);
			if (client == null) {
				throw new ClientNotFoundException("Trying to delete client with an id of " + id + ", but it does not exist");
			}
			
			clientDao.deleteClient(id);
			
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		} catch (NumberFormatException e) {
			throw new BadParameterException(clientId + " was passed in by the user as the id, " + "but it is not an int");
		}
		
	}

	public void getClientsWithAgeBetween(int i, String queryParam) {
		
		
	}
	
}
