
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import sajas.sim.repast3.Repast3Launcher;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;

public class Repast3ServiceLauncher extends Repast3Launcher {

	private static final boolean BATCH_MODE = true;

	private int N = 15;
	
	public static final boolean SEPARATE_CONTAINERS = false;
	private ContainerController mainContainer;
	private ContainerController agentContainer;
	
	private List<ClientAgent> clients;
	
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
	
	private void launchAgents() {
		Random random = new Random(System.currentTimeMillis());
		
		int N_CLIENTS = N;
		
		clients = new ArrayList<ClientAgent>();
		nodes = new ArrayList<DefaultDrawableNode>();

		try{
			//Crate Distributor
			DistributorAgent da = new DistributorAgent();
			agentContainer.acceptNewAgent("Distributor", da).start();
			//if this is the coords to draw the agent, we'll have to change theses parameters to
			//the client's actual coords
			DefaultDrawableNode nodeDistr =
					generateNode("Distributor", Color.WHITE,
							random.nextInt(WIDTH/2),random.nextInt(HEIGHT/2));
			nodes.add(nodeDistr);
			da.setNode(nodeDistr);


			//Create supplier
			SupplierAgent sa = new SupplierAgent();
			agentContainer.acceptNewAgent("Supplier", sa).start();
			//if this is the coords to draw the agent, we'll have to change theses parameters to
			//the client's actual coords
			DefaultDrawableNode nodeSupplier =
					generateNode("Supplier", Color.WHITE,
							random.nextInt(WIDTH/2),random.nextInt(HEIGHT/2));
			nodes.add(nodeSupplier);
			sa.setNode(nodeSupplier);


			//Crate Clients
			for(int i = 0; i < N_CLIENTS; i++)
			{
				ClientAgent ca = new ClientAgent();
				agentContainer.acceptNewAgent("Client" + i, ca).start();
				//if this is the coords to draw the agent, we'll have to change theses parameters to
				//the client's actual coords
				DefaultDrawableNode node =
						generateNode("Client" + i, Color.WHITE,
								random.nextInt(WIDTH/2),random.nextInt(HEIGHT/2));
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
        oval.setHeight(5);
        oval.setWidth(5);
        
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
	private int WIDTH = 200, HEIGHT = 200;
	private OpenSequenceGraph plot;

	//TODO
	private void buildAndScheduleDisplay() {
		// display surface
		if (dsurf != null) dsurf.dispose();
		dsurf = new DisplaySurface(this, "Service Consumer/Provider Display");
		registerDisplaySurface("Service Consumer/Provider Display", dsurf);
		Network2DDisplay display = new Network2DDisplay(nodes,WIDTH,HEIGHT);
		dsurf.addDisplayableProbeable(display, "Network Display");
		dsurf.addZoomable(display);
		addSimEventListener(dsurf);
		dsurf.display();
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
