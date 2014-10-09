package contact.resource;

import java.net.URI;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import contact.annotations.PATCH;
import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.mem.MemDaoFactory;


/**
 * ContactResource provide operations for saving and retrieving contacts via HTTP request
 * using JAX-RS managed request mapping. This class is intended to be invoked by a JAX-RS
 * framework, not directly from other Java code.
 * 
 * @author wutichai
 */
@Path("/contacts")
@Singleton
public class ContactResource {
	

	ContactDao dao;
	
	public ContactResource() {
		dao = MemDaoFactory.getInstance().getContactDao();
		System.out.println("Created a new ContactResource");
	}

	/**
	 * Get all contacts, , or contact by title if title specified.
	 * @param title Contact title
	 * @return Contact corresponding to title. If no title provide, return all contacts
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getContacts(@QueryParam("title") String title) {

		System.out.println("GET is called.");
		System.out.println("GET: title = " + title);
		ResponseBuilder builder = null;
		// if title provided, deal with it
		if (title != null) {
			System.out.println("GET: There is a title field!");
			Contact contact = dao.findByTitle(title);
			if(contact == null) { // no match contact title
				System.out.println("GET: No matched title found");
				builder = Response.status(Response.Status.NOT_FOUND);
			}
			else { // title match contact
				System.out.println("GET: Matched title found!");
				builder = Response.ok(contact, "application/xml");
			}
		}
		// no title provided, return all contacts
		else {
			System.out.println("GET: No title field");
			List<Contact> contacts = dao.findAll();
			builder = Response.ok(
					new GenericEntity<List<Contact>>(contacts){}, MediaType.APPLICATION_XML);
		}
		return builder.build();


	}
	
	/**
	 * Get the specified contact by ID
	 * @param id Contact ID
	 * @return Information of such contact
	 */
	@GET
	@Path("{id : \\d+}")
	@Produces("application/xml")
	public Response getContact(@PathParam("id") long id, @Context Request request) {
		Contact contact = dao.find(id);
		EntityTag tag = new EntityTag(Integer.toString(contact.hashCode()));
		ResponseBuilder builder = request.evaluatePreconditions(tag);
		if (builder != null) {
			return builder.build();
		}
		// Preconditions not met!
		builder = Response.ok(contact, "application/xml");
		builder.tag(tag);
		return builder.build();
	}
	
	/**
	 * Create a new contact.
	 */
	@POST
	@Consumes("application/xml")
	public Response createContact(Contact contact) {
// You have to handle 3 cases here:
// 1. contact has an id attribute > 0 and the id conflicts with an existing contact (return CONFLICT)
// 2. contact doesn't have id attribute or it is zero. 
// 2a. save returns true.. this is the case you have
// 2b. save returns false.. could be application failure or bad request.
		dao.save(contact);
		EntityTag tag = new EntityTag(Integer.toString(contact.hashCode()));
// You must use the uriInfo to discover the actual uri. Don't assume it.
		ResponseBuilder builder = Response.created(URI.create("/contacts/" + contact.getId()));
		builder.tag(tag);
		return builder.build();
	}

	// TODO
	// correctly update and return ETag whenever PUT is performed
	/**
	 * Replace the existing contact with a newer one.
	 */
	@PUT
	@Path("{id : \\d+}")
	@Consumes("application/xml")
	public Response updateContact(@PathParam("id") int id, Contact update, @Context Request request) {
		Contact current = dao.find(id);
		// validate update info
		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		EntityTag tag = new EntityTag(Integer.toString(current.hashCode()));
		ResponseBuilder builder = request.evaluatePreconditions(tag);
		if (builder != null) {
			// Preconditions not met!
			return builder.build();
		}
		// perform the update
		current.copyOf(update);
		dao.update(current);
// Should test wheter update returned true of false.
		tag = new EntityTag(Integer.toString(current.hashCode()));
		builder = Response.ok();
		builder.tag(tag);
		return builder.build();
		
	}
	
	// TODO
	// add PATCH annotation and its corresponding method
	// still don't know how to do it
	/*
	 * Make a partial update to the existing contact.
	 * The old information will remain the same.
	 */

	@PATCH
	@Path("{id : \\d+}")
	@Consumes("application/xml")
	public Response patchContact(@PathParam("id") int id, Contact update, @Context Request request) {
		System.out.println("Patch is called.");
		Contact current = dao.find(id);
		// validate patch info
		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		EntityTag tag = new EntityTag(Integer.toString(current.hashCode()));
		ResponseBuilder builder = request.evaluatePreconditions(tag);
		if (builder != null) {
			// Preconditions not met!
			return builder.build();
		}
		// perform the patch
		current.applyUpdate(update);
		dao.update(current);
		return Response.ok().build();
	}

	
	/**
	 * Delete the existing contact.
	 */
	@DELETE
	@Path("{id : \\d+}")
	public Response delete(@PathParam("id") int id, @Context Request request) {
		Contact current = dao.find(id);
		// check contact availability
		if(current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		EntityTag tag = new EntityTag(Integer.toString(current.hashCode()));
		ResponseBuilder builder = request.evaluatePreconditions(tag);
		if (builder != null) {
			// Preconditions not met!
			return builder.build();
		}
		dao.delete(id);
		return Response.ok().build();
	}
	
	

	
}
