package mapRunner.ontology;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.PrimitiveSchema;
import mapRunner.map.Point;
import mapRunner.map.RunnerLocation;
import mapRunner.map.navigation.Navigation;
import mapRunner.map.navigation.NavigationCommand;
import mapRunner.map.navigation.NavigationToTarget;
import mapRunner.map.navigation.Target;

public class MapRunnerOntology extends Ontology {
	private static final long serialVersionUID = 5644667211496682059L;

	public static final String NAME = "map-runner-ontology";

	public static final String TARGET = "target";
	public static final String TARGET_DESTINATION = "destination";
	public static final String TARGET_LOCATION = "location";

	public static final String NAVIGATION = "path";
	public static final String NAVIGATION_COMMANDS = "commands";

	public static final String POINT = "point";
	public static final String POINT_NAME = "name";

	public static final String NAVIGATION_COMMAND = "navigation-commmand";
	public static final String NAVIGATION_COMMAND_TYPE = "type";
	public static final String NAVIGATION_COMMAND_QUANTITY = "quantity";
	public static final String NAVIGATION_COMMAND_POINT = "point";

	public static final String PATH_TO_TARGET = "path-to-target";
	public static final String PATH_TO_TARGET_TARGET = "target";
	public static final String PATH_TO_TARGET_NAVIGATION = "navigation";

	public static final String RUNNER_LOCATION = "runner-location";
	public static final String RUNNER_LOCATION_POINT = "point";
	public static final String RUNNER_LOCATION_RUNNER = "runner";

	private MapRunnerOntology() {
		super(NAME, BasicOntology.getInstance());

		try {
			add(new ConceptSchema(TARGET), Target.class);
			add(new ConceptSchema(NAVIGATION), Navigation.class);
			add(new ConceptSchema(POINT), Point.class);
			add(new ConceptSchema(NAVIGATION_COMMAND), NavigationCommand.class);
			add(new PredicateSchema(PATH_TO_TARGET), NavigationToTarget.class);
			add(new PredicateSchema(RUNNER_LOCATION), RunnerLocation.class);

			ConceptSchema cs;
			cs = (ConceptSchema) getSchema(TARGET);
			cs.add(TARGET_DESTINATION, (ConceptSchema) getSchema(POINT));
			cs.add(TARGET_LOCATION, (ConceptSchema) getSchema(POINT));

			cs = (ConceptSchema) getSchema(NAVIGATION);
			cs.add(NAVIGATION_COMMANDS, (ConceptSchema) getSchema(NAVIGATION_COMMAND), 0, ObjectSchema.UNLIMITED);

			cs = (ConceptSchema) getSchema(POINT);
			cs.add(POINT_NAME, (PrimitiveSchema) getSchema(BasicOntology.STRING));

			cs = (ConceptSchema) getSchema(NAVIGATION_COMMAND);
			cs.add(NAVIGATION_COMMAND_TYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
			cs.add(NAVIGATION_COMMAND_QUANTITY, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
			cs.add(NAVIGATION_COMMAND_POINT, (ConceptSchema) getSchema(POINT));

			PredicateSchema ps;
			ps = (PredicateSchema) getSchema(PATH_TO_TARGET);
			ps.add(PATH_TO_TARGET_TARGET, (ConceptSchema) getSchema(TARGET));
			ps.add(PATH_TO_TARGET_NAVIGATION, (ConceptSchema) getSchema(NAVIGATION));

			ps = (PredicateSchema) getSchema(RUNNER_LOCATION);
			ps.add(RUNNER_LOCATION_POINT, (ConceptSchema) getSchema(POINT));
			ps.add(RUNNER_LOCATION_RUNNER, (PrimitiveSchema) getSchema(BasicOntology.STRING));

		} catch (OntologyException e) {
			e.printStackTrace();
		}
	}

	private static Ontology theInstance = new MapRunnerOntology();

	public static Ontology getInstance() {
		return theInstance;
	}
}
