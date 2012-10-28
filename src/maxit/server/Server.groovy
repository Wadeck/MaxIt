package maxit.server;

import groovy.transform.CompileStatic
import maxit.commons.logic.server.ServerLogic

import org.apache.log4j.Logger

@CompileStatic
class Server {
	private static Logger log = Logger.getLogger(Server.class);
	private static int INITIAL_NUM_ROOM = 3;
	private static int INITIAL_NUM_HELLO = 2;

	private ServerSocket ss;
	private int port;

	private List<Room> runningRooms;
	private List<Room> waitingRooms;
	public ServerLogic logic;

	public List<HelloThread> helloThreads;
	
	private int genId = 100
	private boolean withLogging = true

	public Server(int port) {
		this.port = port;
		this.runningRooms = []
		this.waitingRooms = []
		for (int i = 0; i < INITIAL_NUM_ROOM; i++) {
			waitingRooms << new Room(this)
		}
		this.helloThreads = new ArrayList<HelloThread>();
		for (int i = 0; i < INITIAL_NUM_HELLO; i++) {
			helloThreads << new HelloThread(this)
		}
	}

	public void start() {
		try{
			def dirName = "./records/"
			File dir = new File(dirName)
			dir.mkdirs()
		}
		catch(Exception e){
			log.error 'Error during creation of log directory', e
			withLogging = false
		}
		
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			log.error("new ServerSocket", e);
			System.exit(-1);
		}

		Socket s;
		log.debug("Server running, waiting for clients...");
		while (true) {
			try {
				s = ss.accept();

				log.debug("new client");
				nextHelloThread(s);

			} catch (IOException e) {
				log.error("accept", e);
			}
		}
	}

	private synchronized void nextHelloThread(Socket s) throws IOException {
		HelloThread thread = null;
		for (int i = 0; i < helloThreads.size() && thread == null; i++) {
			thread = helloThreads[i]
			if (thread.isAlreadyOccupied()) {
				thread = null;
			}
		}
		if (thread == null) {
			thread = new HelloThread(this);
			helloThreads << thread
		}
		thread.startWithSocket(s);
	}

	public synchronized void addNewPlayer(String playerName, Socket s, 
			BufferedReader buffInput, BufferedWriter buffOutput) {
		Room room = null;
		for (int i = 0; i < waitingRooms.size() && room == null; i++) {
			room = waitingRooms[i]
			if (room.isFull()) {
				room = null;
			}
		}

		if (room == null) {
			room = new Room(this)
			waitingRooms << room
		}
		log.debug("Room trouvée: " + room);
		
		ServerThread serverThread = new ServerThread(room, playerName, genId++)
		serverThread.startWithSocket(s, buffInput, buffOutput)
		
		room.addPlayer(serverThread);

		log.debug("NotifyStart\nWaiting rooms: " + waitingRooms
				+ "\nRunning rooms: " + runningRooms);
	}

	public void removeMe(HelloThread thread) {
		this.helloThreads.remove(thread);
	}

	public void notifyStart(Room room) {
		if (waitingRooms.remove(room)) {
			runningRooms << room;
		}

		log.debug("NotifyStart\nWaiting rooms: " + waitingRooms
				+ "\nRunning rooms: " + runningRooms);
	}

	public void notifyEnd(Room room) {
		if (runningRooms.remove(room)) {
			try{
				if(withLogging){
					int seed = room.logic.getSeed()
					def moves = room.logic.getListOfMoves()
					def dirName = "./records/"
					def fileName = "games @ ${ room.getP1().getPlayerName() } vs ${ room.getP2().getPlayerName() }.txt"
					
					File file = new File(dirName + fileName)
					
					file << seed + "# seed"
					file << moves.join("\n")
					
					room.reset();
					if (waitingRooms.size() < INITIAL_NUM_ROOM) {
						waitingRooms << room
					} else {
						log.debug("Avoid to keep more room than initial amount");
					}
				}
			}
			catch(Exception e){
				log.error "Error occurred during the log writing at the end", e
			}
		}

		// writing records
		log.debug("NotifyEnd\nWaiting rooms: " + waitingRooms
				+ "\nRunning rooms: " + runningRooms);
	}

}
