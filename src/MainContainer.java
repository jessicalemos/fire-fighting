import jade.core.Runtime;
import Apoio.Mapa;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class MainContainer {
	Runtime rt;
	ContainerController container;
	
	public ContainerController initContainerInPlatform(String host, String port, String containerName) {
		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, port);
		// create a non-main agent container
		ContainerController container = rt.createAgentContainer(profile);
		return container;
	}
	
	public void initMainContainerInPlatform(String host, String port, String containerName) {

		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile prof = new ProfileImpl();
		prof.setParameter(Profile.CONTAINER_NAME, containerName);
		prof.setParameter(Profile.MAIN_HOST, host);
		prof.setParameter(Profile.MAIN_PORT, port);
		prof.setParameter(Profile.MAIN, "true");
		prof.setParameter(Profile.GUI, "true");

		// create a main agent container
		this.container = rt.createMainContainer(prof);
		rt.setCloseVM(true);

	}
	
	public void startAgentInPlatform(String name, String classpath) {
		try {
			AgentController ac = container.createNewAgent(name, classpath, new Object[] {new Mapa()});
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		MainContainer a = new MainContainer();
		a.initMainContainerInPlatform("localhost", "9999", "MainContainer");
		a.startAgentInPlatform("AgenteInterface", "agents.AgenteInterface");
		a.startAgentInPlatform("AgenteCentral", "agents.AgenteCentral");
		a.startAgentInPlatform("AgenteSecundario", "agents.AgenteSecundario");
		//a.startAgentInPlatform("AgenteParticipativo", "agents.Drone");
		for(int i = 0; i<10; i++)
			a.startAgentInPlatform("AgenteParticipativo " + i, "agents.Drone");
		for(int i = 10; i<12; i++)
			a.startAgentInPlatform("AgenteParticipativo " + i, "agents.Aeronave");
		for(int i = 12; i<17; i++)
			a.startAgentInPlatform("AgenteParticipativo " + i, "agents.Camiao");
	}
}
