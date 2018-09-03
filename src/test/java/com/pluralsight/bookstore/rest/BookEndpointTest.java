package com.pluralsight.bookstore.rest;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pluralsight.bookstore.model.Book;
import com.pluralsight.bookstore.model.Language;
import com.pluralsight.bookstore.repository.BookRepository;
import com.pluralsight.bookstore.util.IsbnGenerator;
import com.pluralsight.bookstore.util.NumberGenerator;
import com.pluralsight.bookstore.util.TextUtil;

@RunWith(Arquillian.class)
@RunAsClient
public class BookEndpointTest {
	
	@Deployment(testable = false)
	public static Archive<?> createDeploymentPackage() {
		return ShrinkWrap.create(WebArchive.class)
				.addClass(BookRepository.class)
				.addClass(Book.class)
				.addClass(Language.class)
				.addClass(TextUtil.class)
				.addClass(NumberGenerator.class)
				.addClass(IsbnGenerator.class)
				.addClass(BookEndPoint.class)
				.addClass(JAXRSConfiguration.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
	            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml");
	}
	
	private Response response;
	
	@Test
	@InSequence(2)
	public void createBook(@ArquillianResteasyResource("api/books")WebTarget webTarget) throws Exception {
		
		// Test counting books
		response = webTarget.path("count").request().get();
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		
		// Test find all
		response = webTarget.request().get();
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		
		// Create a book
		Book book = new Book("isbn", "a title", 12F, 123, Language.ENGLISH, new Date(), "http://blah.com", "description");
		response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(book, MediaType.APPLICATION_JSON));
		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		
	}
	
	@Test
	@InSequence(3)
	public void checkCreatedBook(@ArquillianResteasyResource("api/books")WebTarget webTarget) throws Exception {
		// Check added book
		response = webTarget.path("count").request().get();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(Long.valueOf(1), response.readEntity(Long.class));
	}
	
}
