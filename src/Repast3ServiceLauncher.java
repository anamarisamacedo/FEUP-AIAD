import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableEdge;
import uchicago.src.sim.network.DefaultDrawableNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Repast3ServiceLauncher extends Repast3Launcher {

	private static final boolean BATCH_MODE = true;

	private int N = 15;

	private DistributorAgent dAgent;
	
	public static final boolean SEPARATE_CONTAINERS = false;
	private ContainerController mainContainer;
	private ContainerController agentContainer;

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


	@Override
	public String[] getInitParam() {
		return new String[] {};
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

	//TODO: Add distributor and supplier's correct coords
	//TODO: Add edge to link distributor and the agent he's moving towards.
	//TODO: Fix the labels on each agent
	//TODO: Ask professor for ways to make the simulation look better
	private void launchAgents() {
		Random random = new Random(System.currentTimeMillis());
		
		int N_CLIENTS = N;
		nodes = new ArrayList<DefaultDrawableNode>();

		try{
			//Crate Distributor
			dAgent = new DistributorAgent();
			agentContainer.acceptNewAgent("Distributor", dAgent).start();
			DefaultDrawableNode nodeDistr =
					generateNode("Distributor", Color.RED,
							random.nextInt(WIDTH/2),random.nextInt(HEIGHT/2));
			nodes.add(nodeDistr);
			dAgent.setNode(nodeDistr);

			//Create supplier
			SupplierAgent sa = new SupplierAgent();
			agentContainer.acceptNewAgent("Supplier", sa).start();
			DefaultDrawableNode nodeSupplier =
					generateNode("Supplier", Color.BLUE,
							random.nextInt(WIDTH/2),random.nextInt(HEIGHT/2));
			nodes.add(nodeSupplier);
			sa.setNode(nodeSupplier);

			DefaultDrawableEdge sampleEdge = new DefaultDrawableEdge(nodeDistr, nodeSupplier);
			nodeDistr.addOutEdge(sampleEdge);
			nodeSupplier.addInEdge(sampleEdge);

			//Create pickupLocations
			List<Location> pickupLocations = sa.getPickupLocations();
			for(int i = 0; i < pickupLocations.size(); i++)
			{
				DefaultDrawableNode node =
						generateNode("Pickup Location" + i, Color.GREEN,
								pickupLocations.get(i).getLat(), pickupLocations.get(i).getLon());
				nodes.add(node);
			}

			//Crate Clients
			for(int i = 0; i < N_CLIENTS; i++)
			{
				ClientAgent ca = new ClientAgent();
				agentContainer.acceptNewAgent("Client" + i, ca).start();
				DefaultDrawableNode node =
						generateNode("Client" + i, Color.WHITE,
								ca.getLocation().getLat(), ca.getLocation().getLon());
				nodes.add(node);
				ca.setNode(node);
			}

		}catch (StaleProxyException e) {
			e.printStackTrace();
		}
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

		getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
		getSchedule().scheduleActionAtInterval(100, this, "step", Schedule.INTERVAL_UPDATER);
	}

	public void step()
	{
		dAgent.nextPos();
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
