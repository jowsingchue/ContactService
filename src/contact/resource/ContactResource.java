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
	public Contact getContact(@PathParam("id") int id) {
		return dao.find(id);
	}
	

	@POST
	@Consumes("application/xml")
	public Response createContact( Contact contact, @Context UriInfo uriInfo ) {
		dao.save(contact);
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
		current.applyUpdate(update);
		dao.update(current);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("{id : \\d+}")
	public Response delete(@PathParam("id") int id) {
		Boolean status = dao.delete(id);
		if(status) {
			return Response.ok().build();
		}
		else {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
	
}
