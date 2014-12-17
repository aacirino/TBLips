package ${basePackage}.override;

import org.treasureboat.webcore.override.TBWInitializerOfRest;

public class RestInitializer extends TBWInitializerOfRest {

	@Override
	public void restInitializer() {
		super.restInitializer();

	/*
	 * Uncomment this to create the restRequestHandler for Example : Person
	 * 
	 * ERXRouteRequestHandler restRequestHandler = new ERXRouteRequestHandler();
	 * restRequestHandler.addDefaultRoutes(Person.ENTITY_NAME);
	 * ERXRouteRequestHandler.register(restRequestHandler);
	 */

	// TODO Paul :  Could we loop through the EO's that are decorated to be Restful...??
	// TODO Ken : will be implemented ...

  }
}
