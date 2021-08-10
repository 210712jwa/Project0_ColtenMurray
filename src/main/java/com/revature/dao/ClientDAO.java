package com.revature.dao;

import java.sql.SQLException;
import java.util.List;

import com.revature.dto.AddOrEditClientDTO;
import com.revature.model.Client;

public interface ClientDAO {

	public abstract List<Client> getAllClients() throws SQLException;
	
	/**
	 * This method returns a Client from the database
	 * 
	 * @param id is an int that represents the id
	 * @return Client a representation of client, or null if none was found
	 */
	public abstract Client getClientById(int id) throws SQLException;
	
	
	public abstract Client addClient(AddOrEditClientDTO client) throws SQLException;
	
	public abstract Client editClient(int clientId, AddOrEditClientDTO client) throws SQLException;
	
	public abstract void deleteClient(int clientId) throws SQLException;
}
