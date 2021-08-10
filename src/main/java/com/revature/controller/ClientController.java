package com.revature.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.revature.dto.AddOrEditClientDTO;
import com.revature.model.Client;
import com.revature.service.ClientService;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class ClientController implements Controller {

	private ClientService clientService;
	
	public ClientController() {
		this.clientService = new ClientService();
	}
	
	private Handler getAllClients = (ctx) -> {	
		
		List<Client> clients = clientService.getAllClients();
		
		ctx.status(200); // 200 means OK
		ctx.json(clients);
	};
	
	private Handler getClientById = (ctx) -> {
		String clientid = ctx.pathParam("clientid");
		
		Client client = clientService.getClientById(clientid);
		ctx.status(200);
		ctx.json(client);
	};
	
	private Handler addClient = (ctx) -> {
		AddOrEditClientDTO clientToAdd = ctx.bodyAsClass(AddOrEditClientDTO.class);
		
		Client addedClient = clientService.addClient(clientToAdd);
		ctx.status(200);
		ctx.json(addedClient);
	};
	
	private Handler editClient = (ctx) -> {
		AddOrEditClientDTO clientToEdit = ctx.bodyAsClass(AddOrEditClientDTO.class);
		
		String clientId = ctx.pathParam("clientid");
		Client editedClient = clientService.editClient(clientId, clientToEdit);
		ctx.status(200);
		ctx.json(editedClient);
	};
	
	private Handler deleteClient = (ctx) -> {
		String clientId = ctx.pathParam("clientid");
		clientService.deleteClient(clientId);
		ctx.status(200);
	};
	
	@Override
	public void mapEndpoints(Javalin app) {
		app.post("/client", addClient);
		app.get("/client", getAllClients);
		app.get("/client/:clientid", getClientById);
		app.put("/client/:clientid", editClient);
		app.delete("/client/:clientid", deleteClient);
	}

}
