import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class HelperClass {
    public static AID getAIDbyType(Agent agent, String type)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd  = new ServiceDescription();
        //name of agent to search for (registered in setup)
        sd.setType(type);
        dfd.addServices(sd);
        DFAgentDescription[] result = null;
        try{
            result = DFService.search(agent, dfd);
        }
        catch(FIPAException fipaException){
            System.out.println("Got an exception");
            return null;
        }

        if(result.length == 0)
        {
            System.out.format("Couldn't find any %s agent\n", type);
            return null;
        }
        return result[0].getName();
    }

    public static void registerAgent(Agent agent, String type)
    {
        //Register agent in directory facilitator
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( agent.getAID() );
        ServiceDescription sd  = new ServiceDescription();
        sd.setType( type );
        sd.setName(agent.getLocalName() );
        dfd.addServices(sd);

        try {
            DFService.register(agent, dfd );
        }
        catch (FIPAException fe) { fe.printStackTrace(); }
    }

    public static void main(String[] args) {
        ArrayList<Pair<Item, Integer>> items = getItemsAndStock("Products.txt");
        System.out.println("It's done!");
    }
    public static ArrayList<Item> getItems(String filename)
    {
        ArrayList<Item> items = new ArrayList<>();
        try {
            File myObj = new File(System.getProperty("user.dir") + "/src/" + filename);
            Scanner myReader = new Scanner(myObj);
            String data = null;
            while (myReader.hasNextLine()) {
                //name
                data = myReader.nextLine();
                Item item = new Item();
                item.setName(data);
                //stock
                data = myReader.nextLine();
                //weight
                data = myReader.nextLine();
                item.setWeight(Integer.parseInt(data));
                items.add(item);
            }
            myReader.close();
            return items;
        } catch (FileNotFoundException e) {
            System.out.format("Couldn't open %s :( \n", filename);
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Pair<Item, Integer>> getItemsAndStock(String filename)
    {
        ArrayList<Pair<Item, Integer>> items = new ArrayList<>();

        try {
            File myObj = new File(System.getProperty("user.dir") + "/src/" + filename);
            Scanner myReader = new Scanner(myObj);
            String data = null;
            String stock = "";
            while (myReader.hasNextLine()) {
                Item item = new Item();
                //name
                data = myReader.nextLine();
                item.setName(data);
                //stock
                stock = myReader.nextLine();

                //weight
                data = myReader.nextLine();
                item.setWeight(Integer.parseInt(data));
                items.add(new Pair<Item, Integer>(item, Integer.parseInt(stock)));
            }
            myReader.close();
            return items;
        } catch (FileNotFoundException e) {
            System.out.format("Couldn't open %s :( \n", filename);
            e.printStackTrace();
            return null;
        }
    }

}
