import javax.ws.rs.core.Response;

public class SampleServiceImpl implements SampleService {
	@Override
	public Response sampleOperation() {
		SomeClass entity = new SomeClass("Hello World!");
		return Response.ok(entity).build();
	}
}