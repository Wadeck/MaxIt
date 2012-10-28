package maxit

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import maxit.commons.core.IArtificialPlayer
import maxit.commons.logic.server.TournamentLogic
import maxit.ia.impl.MaxNextPlayer
import maxit.ia.impl.MinMaxPlayer
import maxit.ia.impl.RandomPlayer

import org.apache.log4j.Logger

/**
 * 
 * @author Wadeck
 */
//@CompileStatic
public class MaxItTournament {
	private static Logger log = Logger.getLogger(this.class)
	private static numFight = 500
	private static withReturn = true
	private static randomSeeds = false
	
	private Map<Class, PlayerStats> stats
	private List<Class<IArtificialPlayer>> playerFactories
	MaxItTournament(List<Class<IArtificialPlayer>> players){
		this.playerFactories = players
		this.stats = players.collectEntries{ Class c ->
			[(c): new PlayerStats(c)]
		}
		log.info "Config for tournament: numFight=${numFight}, withReturn=${withReturn} randomSeeds=${randomSeeds}"
	}
	
	def start(){
		def numPlayer = playerFactories.size()
		// player factories
		def pf1, pf2
		for (int i = 0; i < numPlayer; i++) {
			for (int j = i + 1; j < numPlayer; j++) {
				pf1 = playerFactories[i]
				pf2 = playerFactories[j]
				
				startRound(pf1, pf2)
			}
		}
		
		displayResult()
	}
	
	def startRound(Class<IArtificialPlayer> pf1, Class<IArtificialPlayer> pf2){
		int seed
		for (int n = 0; n < numFight; n++) {
			if(randomSeeds){
				seed = new Random().nextInt()
			}else{
				seed = n
			}
			if(withReturn){
				oneGame(pf1, pf2, seed)
				oneGame(pf2, pf1, seed)
			}else{
				if(n % 2){
					oneGame(pf1, pf2, seed)
				}else{
					oneGame(pf2, pf1, seed)
				}
			}
		}
	}
	
	@CompileStatic(TypeCheckingMode.SKIP)
	def displayResult(){
		stats.entrySet().each{ e ->
//		stats.each { e ->
			Class pc = e.key
			PlayerStats ps = e.value
			log.info "Stats for pc: ${ pc }"
			ps.scores.each{ Class other, List scores ->
				log.info "- vs ${ other } = ${ scores.count{ it > 0 } } / ${ scores.size() }"
			}
		}
	}
	
	def oneGame(Class<IArtificialPlayer> pf1, Class<IArtificialPlayer> pf2, int seed){
		IArtificialPlayer p1 = pf1.newInstance()
		IArtificialPlayer p2 = pf2.newInstance()
		TournamentLogic logic = new TournamentLogic(p1, p2, seed)
		logic.start()
		
		stats[pf1].addStatVs(pf2, logic.getScoreH(), logic.getScoreV())
		stats[pf2].addStatVs(pf1, logic.getScoreV(), logic.getScoreH())
	}
//	def oneGame(IArtificialPlayer p1, IArtificialPlayer p2, int seed){
//		TournamentLogic logic = new TournamentLogic(p1, p2, seed)
//		logic.start()
//		log.info "Game[seed=${ seed }, p1:${ p1 }=${ logic.getScoreH() }, p2:${ p2 }=${ logic.getScoreV() }, diff=${ Math.abs(logic.getScoreH() - logic.getScoreV()) }]"
//	}
	
	public static void main(String[] args) {
		def participants = [
			MaxNextPlayer, 
			MinMaxPlayer, 
			RandomPlayer, 
//			NStepPlayer,
		]
		new MaxItTournament(participants).start()
	}
}

class PlayerStats{
	private Class p
	Map<Class, List<Integer>> scores
	def PlayerStats(Class p){
		this.p = p
		this.scores = [:]
	}
	
	def addStatVs(Class other, int thisScore, int otherScore){
		if(!(other in scores)){
			scores[other] = []
		}
		scores[other] << thisScore - otherScore
	}
}

/**
14:05:11,930 [INFO ] Stats for MaxNextPlayer:
14:05:11,993 [INFO ] - vs class maxit.ia.impl.MinMaxPlayer = 162106 / 200000
14:05:12,040 [INFO ] - vs class maxit.ia.impl.RandomPlayer = 200000 / 200000
14:05:12,040 [INFO ] Stats for MinMaxPlayer:
14:05:12,087 [INFO ] - vs class maxit.ia.impl.MaxNextPlayer = 33361 / 200000
14:05:12,134 [INFO ] - vs class maxit.ia.impl.RandomPlayer = 199865 / 200000
14:05:12,134 [INFO ] Stats for RandomPlayer:
14:05:12,180 [INFO ] - vs class maxit.ia.impl.MaxNextPlayer = 0 / 200000
14:05:12,227 [INFO ] - vs class maxit.ia.impl.MinMaxPlayer = 123 / 200000
*/