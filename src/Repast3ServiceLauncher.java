import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.reflector.ListPropertyDescriptor;
import uchicago.src.reflector.RangePropertyDescriptor;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableEdge;
import uchicago.src.sim.network.DefaultDrawableNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Repast3ServiceLauncher extends Repast3Launcher {

	private static final boolean BATCH_MODE = true;

	private int N_CLIENTS = 10;
	private int SUPPLIER_LAT_1 = 500;
	private int SUPPLIER_LON_1 = 500;
	private int SUPPLIER_LAT_2 = 500;
	private int SUPPLIER_LON_2 = 500;
	private int SUPPLIER_LAT_3 = 500;
	private int SUPPLIER_LON_3 = 500;
	private DistributorMethod ALLOCATION = DistributorMethod.regular;

	private Repast3ServiceLauncher repastLauncher = this;

	private DistributorAgent dAgent;
	
	public static final boolean SEPARATE_CONTAINERS = false;
	private ContainerController mainContainer;
	private ContainerController agentContainer;
	private List<ClientAgent> clients;
	private List<Location> pickupLocations;
	private boolean fleetReady;

	private static List<DefaultDrawableNode> nodes;
	
	private boolean runInBatchMode;
	
	public Repast3ServiceLauncher(boolean runInBatchMode) {
		super();
		this.runInBatchMode = runInBatchMode;
	}

	public static DefaultDrawableNode getNode(String label) {
		for(DefaultDrawableNode node : nodes) {
			if(node.getNodeLabel().equals(label)) {
				return node;
			}
		}
		return null;
	}
	
	public int getN_CLIENTS() {
		return N_CLIENTS;
	}

	public void setN_CLIENTS(int N_CLIENTS) {
		this.N_CLIENTS = N_CLIENTS;
	}
	
	public int getSUPPLIER_LAT_1() {
		return SUPPLIER_LAT_1;
	}

	public void setSUPPLIER_LAT_1(int SUPPLIER_LAT_1) {
		this.SUPPLIER_LAT_1 = SUPPLIER_LAT_1;
	}
	
	public int getSUPPLIER_LON_1() {
		return SUPPLIER_LON_1;
	}

	public void setSUPPLIER_LON_1(int SUPPLIER_LON_1) {
		this.SUPPLIER_LON_1 = SUPPLIER_LON_1;
	}
	
	public int getSUPPLIER_LAT_2() {
		return SUPPLIER_LAT_2;
	}

	public void setSUPPLIER_LAT_2(int SUPPLIER_LAT_2) {
		this.SUPPLIER_LAT_2 = SUPPLIER_LAT_2;
	}
	
	public int getSUPPLIER_LON_2() {
		return SUPPLIER_LON_2;
	}

	public void setSUPPLIER_LON_2(int SUPPLIER_LON_2) {
		this.SUPPLIER_LON_2 = SUPPLIER_LON_2;
	}
	
	public int getSUPPLIER_LAT_3() {
		return SUPPLIER_LAT_3;
	}

	public void setSUPPLIER_LAT_3(int SUPPLIER_LAT_3) {
		this.SUPPLIER_LAT_3 = SUPPLIER_LAT_3;
	}
	
	public int getSUPPLIER_LON_3() {
		return SUPPLIER_LON_3;
	}

	public void setSUPPLIER_LON_3(int SUPPLIER_LON_3) {
		this.SUPPLIER_LON_3 = SUPPLIER_LON_3;
	}
	
	public DistributorMethod getALLOCATION() {
		return ALLOCATION;
	}

	public void setALLOCATION(DistributorMethod ALLOCATION) {
		this.ALLOCATION = ALLOCATION;
	}
	

	@Override
	public String[] getInitParam() {
		return new String[] {"N_CLIENTS", "SUPPLIER_LAT_1", "SUPPLIER_LON_1", "SUPPLIER_LAT_2", "SUPPLIER_LON_2", "SUPPLIER_LAT_3", "SUPPLIER_LON_3", "ALLOCATION" };
	}

	@Override
	public String getName() {
		return "Service Supplier/Distributor/Client -- SAJaS Repast3 Jade";
	}

	@Override
	protected void launchJADE() {
		
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		
		if(SEPARATE_CONTAINERS) {
			Profile p2 = new ProfileImpl();
			agentContainer = rt.createAgentContainer(p2);
		} else {
			agentContainer = mainContainer;
		}
		
		launchAgents();
	}


	//TODO: Fix the labels on each agent
	//TODO: Ask professor for ways to make the simulation look better
	private int clientCount = 0;
	private void launchAgents() {
		Random random = new Random(System.currentTimeMillis());
		
		clients = new ArrayList<ClientAgent>();
		nodes = new ArrayList<DefaultDrawableNode>();
		pickupLocations = new ArrayList<>();
		
		try{
			//Create Distributor
			dAgent = new DistributorAgent(ALLOCATION);
			dAgent.setLocation(new Location(300, 300));
			agentContainer.acceptNewAgent("Distributor", dAgent).start();
			DefaultDrawableNode nodeDistr =
					generateNode("Distributor", Color.RED,
							dAgent.getLocation().getLat(), dAgent.getLocation().getLon());
			nodes.add(nodeDistr);
			dAgent.setNode(nodeDistr);
			
			//Create pickupLocations
			Location l1 = new Location(SUPPLIER_LAT_1, SUPPLIER_LON_1);
			Location l2 = new Location(SUPPLIER_LAT_2, SUPPLIER_LON_2);
			Location l3 = new Location(SUPPLIER_LAT_3, SUPPLIER_LON_3);
			pickupLocations.add(l1);
			pickupLocations.add(l2);
			pickupLocations.add(l3);

			//Create supplier
			SupplierAgent sAgent = new SupplierAgent(pickupLocations);
			agentContainer.acceptNewAgent("Supplier", sAgent).start();
			DefaultDrawableNode nodeSupplier =
					generateNode("Supplier", Color.BLUE,
							random.nextInt(WIDTH/2),random.nextInt(HEIGHT/2));
			nodes.add(nodeSupplier);
			sAgent.setNode(nodeSupplier);


			//Create pickupLocations
			for(int i = 0; i < pickupLocations.size(); i++)
			{
				DefaultDrawableNode node =
						generateNode("Pickup Location" + i, Color.GREEN,
								pickupLocations.get(i).getLat(), pickupLocations.get(i).getLon());
				nodes.add(node);
			}
			//Create clients
			for(int i = 0; i < N_CLIENTS; i++)
			{
				generateClients();
			}
			//Create vehicles nodes
			for(int i = 0; i < dAgent.getFleet().size(); i++)
			{
				DefaultDrawableNode node = generateNode("Vehicle" + i, Color.GRAY, dAgent.getLocation().getLat(), dAgent.getLocation().getLon());
				nodes.add(node);
				dAgent.getFleet().get(i).setNode(node);

				//Sample edge
				DefaultDrawableEdge edge = new DefaultDrawableEdge(node, nodeDistr);
				node.addOutEdge(edge);
				nodeDistr.addInEdge(edge);
			}

		}catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

	public void generateClients() {
		//Create Clients
		clientCount++;
		ClientAgent ca = new ClientAgent();
		try {
			agentContainer.acceptNewAgent("Client" + clientCount, ca).start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clients.add(ca);
		DefaultDrawableNode node =
				generateNode("Client" + clientCount , Color.WHITE,
						ca.getLocation().getLat(), ca.getLocation().getLon());
		nodes.add(node);
		ca.setNode(node);
	}

	private DefaultDrawableNode generateNode(String label, Color color, int x, int y) {
        OvalNetworkItem oval = new OvalNetworkItem(x,y);
        oval.allowResizing(false);
        oval.setHeight(30);
        oval.setWidth(30);
		DefaultDrawableNode node = new DefaultDrawableNode(label, oval);
		node.setColor(color);
		return node;
	}
	
	@Override
	public void setup() {
		super.setup();

		descriptors.put("N_CLIENTS", new RangePropertyDescriptor("N_CLIENTS", 5, 15, 2));
		descriptors.put("SUPPLIER_LAT_1", new RangePropertyDescriptor("SUPPLIER_LAT_1", 0, 1000, 200));
		descriptors.put("SUPPLIER_LON_1", new RangePropertyDescriptor("SUPPLIER_LON_1", 0, 1000, 200));
		descriptors.put("SUPPLIER_LAT_2", new RangePropertyDescriptor("SUPPLIER_LAT_2", 0, 1000, 200));
		descriptors.put("SUPPLIER_LON_2", new RangePropertyDescriptor("SUPPLIER_LON_2", 0, 1000, 200));
		descriptors.put("SUPPLIER_LAT_3", new RangePropertyDescriptor("SUPPLIER_LAT_3", 0, 1000, 200));
		descriptors.put("SUPPLIER_LON_3", new RangePropertyDescriptor("SUPPLIER_LON_3", 0, 1000, 200));
		Vector v = new Vector();
		v.add(DistributorMethod.regular);
		v.add(DistributorMethod.even);
		v.add(DistributorMethod.random);
		descriptors.put("ALLOCATION", new ListPropertyDescriptor("ALLOCATION", v));

		
	}


	@Override
	public void begin() {
		super.begin();
		if(!runInBatchMode) {
			buildAndScheduleDisplay();
		}
	}

	private DisplaySurface dsurf;
	private int WIDTH = 1200, HEIGHT = 1200;
	private OpenSequenceGraph plot;

	private void buildAndScheduleDisplay() {
		// display surface
		if (dsurf != null) dsurf.dispose();
		dsurf = new DisplaySurface(this, "Service Client/Distributor/Supplier Display");
		registerDisplaySurface("Service Client/Distributor/Supplier Display", dsurf);
		Network2DDisplay display = new Network2DDisplay(nodes,WIDTH,HEIGHT);
		dsurf.addDisplayableProbeable(display, "Network Display");
		dsurf.addZoomable(display);
		addSimEventListener(dsurf);
		dsurf.display();

		// graph
		if (plot != null) plot.dispose();
		plot = new OpenSequenceGraph("Service performance", this);
		plot.setAxisTitles("time", "% successful service executions");

		plot.addSequence("Distributor", new Sequence() {
			public double getSValue() {
			/*	// iterate through consumers
				double v = 0.0;
				for(int i = 0; i < consumers.size(); i++) {
					v += consumers.get(i).getMovingAverage(10);
				}*/
				return 1;
			}
		});
		
		plot.display();
		getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
		getSchedule().scheduleActionAtInterval(10, this, "step", Schedule.INTERVAL_UPDATER);
	}

	public void step()
	{
		//dAgent.nextPos();
		dAgent.moveVehicles();
	}


	public static DefaultDrawableNode getNodeAt(Location location)
	{
		for(DefaultDrawableNode node : nodes)
		{
			if(node.getX() == location.getLat() && node.getY() == location.getLon())
			{
				return node;
			}
		}
		return null;
	}


	/**
	 * Launching Repast3
	 * @param args
	 */
	public static void main(String[] args) {
		boolean runMode = !BATCH_MODE;   // BATCH_MODE or !BATCH_MODE

		SimInit init = new SimInit();
		init.setNumRuns(1);   // works only in batch mode
		init.loadModel(new Repast3ServiceLauncher(runMode), null, runMode);
	}

}

