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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.mem.MemDaoFactory;


/**
 * ContactResource provide functionalities for saving and retrieving contacts via HTTP request.
 * The contacts data are not stored in this class, use ContactDao instead.
 * 
 * @author wutichai
 *
 */
@Path("/contacts")
@Singleton
public class ContactResource {
	

	ContactDao dao;
	
	public ContactResource() {
		dao = MemDaoFactory.getInstance().getContactDao();
		System.out.println("Created a new ContactResource");
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<Contact> getContacts() {
		return dao.findAll();
	}
	
	@GET
	@Path("{id : \\d+}")
	@Produces("application/xml")
//Better to use long, in case you have more than 2 Billion contacts.
	public Contact getContact(@PathParam("id") int id) {
//Too simple!
// If contact doesn't exist, should return NOT_FOUND
		return dao.find(id);
	}
	

	@POST
	@Consumes("application/xml")
	public Response createContact( Contact contact, @Context UriInfo uriInfo ) {
// You have to handle 3 cases here:
// 1. contact has an id attribute > 0 and the id conflicts with an existing contact (return CONFLICT)
// 2. contact doesn't have id attribute or it is zero. 
// 2a. save returns true.. this is the case you have
// 2b. save returns false.. could be application failure or bad request.
		dao.save(contact);
// You must use the uriInfo to discover the actual uri. Don't assume it.
		return Response.created(URI.create("/contacts/" + contact.getId())).build();
	}

	@PUT
	@Path("{id : \\d+}")
	@Consumes("application/xml")
	public Response updateContact(@PathParam("id") int id, Contact update, @Context UriInfo uriInfo) {
		Contact current = dao.find(id);

		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
// Don't do this. Just give the update to DAO and let it handle it.
		current.applyUpdate(update);
		dao.update(current);
// Should test wheter update returned true of false.
		return Response.ok().build();
	}
	
	@DELETE
	@Path("{id : \\d+}")
	public Response delete(@PathParam("id") int id) {
		boolean status = dao.delete(id);
		if(status) {
			return Response.ok().build();
		}
		else {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
	
}
