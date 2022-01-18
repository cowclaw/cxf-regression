import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

class CxfRegressionTest {

	private static final JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider();
	private static final String serviceUrl = "local://sampleService";

	private Server server;

	@BeforeEach
	void setupServer() {
		JAXRSServerFactoryBean jaxrsServerFactoryBean = new JAXRSServerFactoryBean();
		jaxrsServerFactoryBean.setServiceBean(new SampleServiceImpl());
		jaxrsServerFactoryBean.setProviders(List.of(jacksonJsonProvider));
		jaxrsServerFactoryBean.setAddress(serviceUrl);
		server = jaxrsServerFactoryBean.create();
	}

	@AfterEach
	void destroyServer() {
		server.destroy();
	}

	@Test
	void verifyResponseWithBufferedEntity_stillHasEntity_afterReading_whenReadFails() {
		SampleService client = createClient();
		Response response = client.sampleOperation();

		assertTrue(response.hasEntity());
		assertTrue(response.bufferEntity());

		assertThrows(ResponseProcessingException.class, () -> response.readEntity(OtherClass.class),
			"Cannot deserialize value of type `int` from String \"Hello World!\"");

		assertTrue(response.hasEntity()); //fails unexpectedly starting with cxf version 3.4.1
	}

	@Test
	void verifyResponseWithBufferedEntity_stillHasEntity_afterReading_whenReadSuccessful() {
		SampleService client = createClient();
		Response response = client.sampleOperation();

		assertTrue(response.hasEntity());
		assertTrue(response.bufferEntity());

		SomeClass entity = response.readEntity(SomeClass.class);
		assertNotNull(entity);
		assertEquals("Hello World!", entity.getFoo());

		assertTrue(response.hasEntity());
	}

	private SampleService createClient() {
		JAXRSClientFactoryBean jaxrsClientFactoryBean = new JAXRSClientFactoryBean();
		jaxrsClientFactoryBean.setServiceClass(SampleService.class);
		jaxrsClientFactoryBean.setProviders(List.of(jacksonJsonProvider));
		jaxrsClientFactoryBean.setAddress(serviceUrl);
		return jaxrsClientFactoryBean.create(SampleService.class);
	}

}
