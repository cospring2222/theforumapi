package com.theforum.api;
/**
 * @author Uliana and David
 */

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.theforum.dao.UserManager;
import com.theforum.dao.UserManagerImpl;
import com.theforum.entities.Users;
import com.theforum.json.PaginationWrapper;
import com.theforum.json.UserRegisterWrapper;
import com.theforum.json.UserWrapper;
import com.theforum.util.Role;

//Rest API for User tasks
@Path("/users")
public class UsersRestApi {
	//Tools for work with DB:
	UserManager userManager = new UserManagerImpl();

	//API creating new User with received date from registration form
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response createUser(UserRegisterWrapper uw) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		if (uw.getUsername() == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("username  is mandatory").build());
		}
		// if (uw.getEmail() == null) {
		// throw new WebApplicationException(
		// Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("email is
		// mandatory").build());
		// }
		if (uw.getPassword() == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("password  is mandatory").build());
		}

		// check if username alredy exist
		String uw_un = uw.getUsername();
		Users old_user = userManager.findByUserName(uw_un);
		boolean userExsists = (old_user != null);

		if (userExsists) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("username already in use").build());

		}
		// if username is avilible create new
		Users u = new Users();
		u.setUserRole(Role.USER);
		u.setUserFirstName(uw.getFirstName());
		u.setUserSecondName(uw.getLastName());
		u.setUsername(uw.getUsername());
		u.setUserPassword(uw.getPassword());
		u.setAvator(uw.getAvator());
		// u.setUserEmail(uw.getEmail());

		userManager.saveOrUpdateUser(u);

		return Response.status(200).entity(jsonObject.toString()).build();
	}

	
	
	

	
	//API return list of all Users
	@Path("/list/all")
	@GET
	@Produces("application/json")
	public Response getAllUsers() throws JSONException {

		List<Users> users = userManager.loadAllUsers();

		List<UserWrapper> uw_list = new ArrayList<UserWrapper>();
		for (Users item : users) {
			UserWrapper uw = new UserWrapper(item.getUserId(), item.getUsername(), item.getUserRole().name(),
					item.getUserFirstName(), item.getUserSecondName(), item.getUserPassword(), "");
			uw.setAvator(item.getAvator());
			uw_list.add(uw);
		}
		return Response.status(200).entity(uw_list).build();
	}

	//API return list of all Users with pagination
	@Path("/list/page")
	@POST
	@Produces("application/json")
	public Response getAllUsersPagination(PaginationWrapper pgw) throws JSONException {

		List<Users> users = userManager.loadAllUsers();

		List<UserWrapper> uw_list = new ArrayList<UserWrapper>();
		int counter = 0;
		for (Users item : users) {
			counter++;
			if (pgw.getPageIndex() * pgw.getPageSize() <= counter
					&& counter < (pgw.getPageIndex() + 1) * pgw.getPageSize()) 
			{
				UserWrapper uw = new UserWrapper(item.getUserId(), item.getUsername(), item.getUserRole().name(),
						item.getUserFirstName(), item.getUserSecondName(), item.getUserPassword(), "");
				uw.setAvator(item.getAvator());
				uw_list.add(uw);
			}
		}

		return Response.status(200).entity(uw_list).build();
	}



	//API delete User by giving  ID
	@DELETE
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	// public Response delete(@QueryParam("userID") Long userID)
	public Response deleteUser(@QueryParam("userID") Long userID) throws JSONException {

		JSONObject jsonObject = new JSONObject();

		// Long userID = 1;
		Users cur_u = userManager.findUserById(userID);
		if (cur_u != null) {
			userManager.deleteUser(cur_u);

		} else {
			jsonObject.put("status", "failed");
			jsonObject.put("message", "Username is not exists.");

		}

		return Response.status(200).entity(jsonObject.toString()).build();
	}

	//API get edit method return User by giving  ID for editing
	@GET
	@Path("/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response editUser(@QueryParam("userID") Long userID) throws JSONException {

		JSONObject jsonObject = new JSONObject();

		Users cur_u = userManager.findUserById(userID);
		if (cur_u != null) {
			return Response.status(200).entity(cur_u).build();
		} else {
			jsonObject.put("status", "failed");
			jsonObject.put("message", "Username is not exists.");

		}

		return Response.status(200).entity(jsonObject.toString()).build();
	}
	
	//API post edit method to edit User by giving  date
	@POST
	@Path("/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response updateUser(UserWrapper uw) throws JSONException {

		JSONObject jsonObject = new JSONObject();

		if (uw.getId() == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("id  is mandatory").build());
		}

		if (uw.getUsername() == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("username  is mandatory").build());
		}

		if (uw.getPassword() == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("password  is mandatory").build());
		}

		Users cur_u = userManager.findUserById(uw.getId());

		if (cur_u == null) {
			jsonObject.put("status", "failed");
			jsonObject.put("message", "Username is not exists.");
			return Response.status(200).entity(jsonObject.toString()).build();
		}
		
		// check if new username is alredy taked
		String prevUserName = cur_u.getUsername();
		String newUserName = uw.getUsername();
		if (newUserName != prevUserName) {
			Users check_new_username_exist = userManager.findByUserName(newUserName);
			if (check_new_username_exist != null) {
				jsonObject.put("status", "failed");
				jsonObject.put("message", "User name" + cur_u.getUsername() + "is alredy in use, select other one.");
				return Response.status(200).entity(jsonObject.toString()).build();
			}

		}
		
		cur_u.setUsername(newUserName);
	    cur_u.setUserRole(Role.valueOf(uw.getRole())); 
		cur_u.setUserFirstName(uw.getFirstname());
		cur_u.setUserSecondName(uw.getLastname());
		cur_u.setUserPassword(uw.getPassword());
		cur_u.setAvator(uw.getAvator());

		userManager.saveOrUpdateUser(cur_u);

		return Response.status(200).entity(jsonObject.toString()).build();
	}

}