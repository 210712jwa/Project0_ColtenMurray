package com.revature.controller;

import java.util.List;

import com.revature.dto.AddOrEditAccountDTO;
import com.revature.dto.AddOrEditClientDTO;
import com.revature.model.Account;
import com.revature.model.Client;
import com.revature.service.AccountService;

import io.javalin.Javalin;
import io.javalin.http.Handler;


public class AccountController implements Controller {

	private AccountService accountService;
	
	public AccountController() {
		this.accountService = new AccountService();
	}
	
	private Handler addAccountToClient = (ctx) -> {
		AddOrEditAccountDTO accountToAdd = ctx.bodyAsClass(AddOrEditAccountDTO.class);
		
		Account addedAccount = accountService.addAccount(accountToAdd);
		ctx.status(200);
		ctx.json(addedAccount);
		
	};
	
	private Handler getAccountFromClient = (ctx) -> {
		String clientId = ctx.pathParam("clientid");
		
		String lessThan = ctx.queryParam("amountLessThan");
		String greaterThan = ctx.queryParam("amountGreaterThan");
		
		List<Account> accountsFromClient = accountService.getAllAccountsFromClient(clientId, lessThan, greaterThan);
		ctx.status(200);
		ctx.json(accountsFromClient);
	};
	
	
	private Handler getAccountById = (ctx) -> {
		String clientId = ctx.pathParam("clientid");
		String accountId = ctx.pathParam("accountid");
		
		
		Account accountById = accountService.getAccountById(clientId,accountId);
		ctx.status(200);
		ctx.json(accountById);
	};
	
	
	private Handler editAccount = (ctx) -> {
		AddOrEditAccountDTO accountToEdit = ctx.bodyAsClass(AddOrEditAccountDTO.class);
		
		String clientId = ctx.pathParam("clientid");
		
		String accountId = ctx.pathParam("accountid");
		
		Account editedAccount = accountService.editAccount(clientId, accountId, accountToEdit);
		ctx.status(200);
		ctx.json(editedAccount);
		
	};
	
	private Handler deleteAccount = (ctx) -> {
		String clientId = ctx.pathParam("clientid");
		
		String accountId = ctx.pathParam("accountid");
		ctx.status(200);
		accountService.deleteAccount(clientId,accountId);
	};
	
	
	@Override
	public void mapEndpoints(Javalin app) {
		app.post("/client/:clientid/account", addAccountToClient);
		app.get("/client/:clientid/account", getAccountFromClient);
		//app.get("/client/:clientid/account/amountLTE2000GTE400", getAccountLTE2000GTE400);
		app.get("/client/:clientid/account/:accountid", getAccountById);
		app.put("/client/:clientid/account/:accountid", editAccount);
		app.delete("/client/:clientid/account/:accountid", deleteAccount);
		
	}

}
