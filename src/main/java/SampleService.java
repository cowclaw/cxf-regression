import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

interface SampleService {
	@GET
	@Produces("application/json;charset=utf-8")
	Response sampleOperation();
}