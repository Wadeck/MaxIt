package maxit

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

import java.util.Map.Entry

import maxit.commons.core.IArtificialPlayer
import maxit.commons.logic.server.TournamentLogic
import maxit.ia.impl.BestStepPlayer
import maxit.ia.impl.MaxNextPlayer
import maxit.ia.impl.MinMaxPlayer
import maxit.ia.impl.NStepPlayer
import maxit.ia.impl.RandomPlayer

import org.apache.log4j.Logger

/**
 * Use to compare a set of IA
 * @author Wadeck
 */
@CompileStatic
public class MaxItTournament {
	private static Logger log = Logger.getLogger(this.class)
	private static final int numFight = 10
	private static final boolean randomSeeds = false
	
	private static final boolean withReturn = true
	private static final boolean withDetails = true
	
	// to show progression
	private static final long timeToAdd = 1000 * 10
	private long nextTime = System.currentTimeMillis() + timeToAdd // 10 seconds
	private int currentStep, totalStep
	
//	private Map<Closure, PlayerStats> stats
	private Map stats
	private List<Closure> playerFactories
	
	private List<Integer> evos1 = []
	private List<Integer> evos2 = []
	private def firstP
	
	MaxItTournament(List<Closure> players){
		this.playerFactories = players
		this.stats = [:]
		players.each{ Closure c ->
			stats << [(c): new PlayerStats(c)]
		}
		currentStep = 1
		totalStep = (int)(numFight * (withReturn ? 2 : 1) * (playerFactories.size() * (playerFactories.size()-1) / 2))
		log.info "Config for tournament: numFight=${numFight}, withReturn=${withReturn} randomSeeds=${randomSeeds} withDetails=${withDetails} totalStep=${totalStep}"
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
	
	def startRound(Closure pf1, Closure pf2){
		this.firstP = pf1
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
	
//	@CompileStatic(TypeCheckingMode.SKIP)
	def displayResult(){
		for (Entry e : stats.entrySet()) {
			Closure pc = (Closure)(e.key)
			PlayerStats ps = (PlayerStats)(e.value)
			log.info "Stats for pc: ${ pc() }"
			
			for(Entry e2 : ps.scores.entrySet()){
				def other = (Closure)e2.key
				def scores = (List)e2.value
				def numWin = scores.count { Integer item -> item > 0 }
				log.info "- vs ${ other() } = ${ numWin } / ${ scores.size() } ${ withDetails ? scores : ''}"
			}
		}
	}
	
	def oneGame(Closure pf1, Closure pf2, int seed){
		if(nextTime < System.currentTimeMillis()){
			nextTime += this.timeToAdd // 10 seconds
			log.info "total step: ${ currentStep } / ${ totalStep }"
		}
		currentStep++
		IArtificialPlayer p1 = (IArtificialPlayer)pf1()
		IArtificialPlayer p2 = (IArtificialPlayer)pf2()
		TournamentLogic logic = new TournamentLogic(p1, p2, seed)
		logic.start()
		
		((PlayerStats)stats[pf1]).addStatVs(pf2, logic.getScoreH(), logic.getScoreV())
		((PlayerStats)stats[pf2]).addStatVs(pf1, logic.getScoreV(), logic.getScoreH())
		
	
		List<Integer> firstScores, secondScores
		if(firstP == pf1){
			firstScores = logic.scoreHs
			secondScores = logic.scoreVs
		}else{
			firstScores = logic.scoreVs
			secondScores = logic.scoreHs
		}
		for (int i = 0; i < firstScores.size(); i++) {
			evos1[i] = (int)(evos1[i] ?: 0) + (int)(firstScores[i])
			evos2[i] = (int)(evos2[i] ?: 0) + (int)(secondScores[i])
		}
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis()
		def participants = [
			{ new RandomPlayer() }, 
			{ new MaxNextPlayer() }, 
			{ new MinMaxPlayer() }, 
			{ new NStepPlayer(3) },
			{ new NStepPlayer(4) },
			{ new NStepPlayer(5) },
			{ new BestStepPlayer(3, 5) },
			{ new BestStepPlayer(3, 4) },
			{ new BestStepPlayer(4, 5) },
			{ new BestStepPlayer(4, 4) },
			{ new BestStepPlayer(4, 3) },
			{ new BestStepPlayer(5, 4) },
			{ new BestStepPlayer(5, 3) },
			{ new BestStepPlayer(6, 4) },
			{ new BestStepPlayer(6, 3) },
			{ new BestStepPlayer(7, 3) },
			{ new BestStepPlayer(8, 3) },
			{ new BestStepPlayer(9, 2) },
		]
		new MaxItTournament(participants).start()
		
		long endTime = System.currentTimeMillis()
		
		println("Time taken: ${ (endTime - startTime)*0.001 }s")
	}
}

class PlayerStats{
	public Closure p
	Map<Closure, List<Integer>> scores
	def PlayerStats(Closure p){
		this.p = p
		this.scores = [:]
	}
	
	def addStatVs(Closure other, int thisScore, int otherScore){
		if(!(other in scores)){
			scores[other] = []
		}
		scores[other] << thisScore - otherScore
	}
}

/*
19:05:54,411 [INFO ] Config for tournament: numFight=5000, withReturn=true randomSeeds=true withDetails=false
19:06:04,350 [INFO ] Stats for pc: RandomPlayer
19:06:04,359 [INFO ] - vs MaxNextPlayer = 0 / 10000
19:06:04,364 [INFO ] - vs MinMaxPlayer = 0 / 10000
19:06:04,364 [INFO ] Stats for pc: MaxNextPlayer
19:06:04,367 [INFO ] - vs RandomPlayer = 10000 / 10000
19:06:04,369 [INFO ] - vs MinMaxPlayer = 1554 / 10000
19:06:04,369 [INFO ] Stats for pc: MinMaxPlayer
19:06:04,372 [INFO ] - vs RandomPlayer = 10000 / 10000
19:06:04,375 [INFO ] - vs MaxNextPlayer = 8177 / 10000
*/

/**
19:30:19,394 [INFO ] Config for tournament: numFight=10, withReturn=true randomSeeds=false withDetails=true totalStep=120
19:31:09,124 [INFO ] Stats for pc: NStepPlayer@2
19:31:09,130 [INFO ] - vs NStepPlayer@3 = 7 / 20 [8, 8, -8, 2, -4, -18, 18, 0, -3, -30, -10, -16, -18, -5, -6, 2, 4, -4, 4, -14]
19:31:09,130 [INFO ] - vs NStepPlayer@4 = 8 / 20 [6, 2, 14, 6, -28, -2, 22, -2, 30, 4, -12, -25, -16, -34, -2, -21, -8, -8, 24, -7]
19:31:09,131 [INFO ] - vs NStepPlayer@5 = 9 / 20 [14, 2, 24, -7, -30, 2, 44, -44, 12, -18, -2, -14, -2, -24, -13, 4, 4, -3, 18, -18]
19:31:09,131 [INFO ] Stats for pc: NStepPlayer@3
19:31:09,131 [INFO ] - vs NStepPlayer@2 = 12 / 20 [-8, -8, 8, -2, 4, 18, -18, 0, 3, 30, 10, 16, 18, 5, 6, -2, -4, 4, -4, 14]
19:31:09,131 [INFO ] - vs NStepPlayer@4 = 8 / 20 [10, -25, 34, 18, 6, 10, -10, -28, -13, 0, 21, -24, -22, -7, -8, 19, 0, 14, -6, -5]
19:31:09,131 [INFO ] - vs NStepPlayer@5 = 9 / 20 [-10, 4, -10, 4, -18, -4, 10, -11, -2, 4, 14, -24, -4, 12, 10, 4, 4, -22, -5, -18]
19:31:09,131 [INFO ] Stats for pc: NStepPlayer@4
19:31:09,131 [INFO ] - vs NStepPlayer@2 = 12 / 20 [-6, -2, -14, -6, 28, 2, -22, 2, -30, -4, 12, 25, 16, 34, 2, 21, 8, 8, -24, 7]
19:31:09,131 [INFO ] - vs NStepPlayer@3 = 10 / 20 [-10, 25, -34, -18, -6, -10, 10, 28, 13, 0, -21, 24, 22, 7, 8, -19, 0, -14, 6, 5]
19:31:09,132 [INFO ] - vs NStepPlayer@5 = 7 / 20 [0, 2, -14, 5, -8, -1, 26, -45, -12, -4, -3, -19, -8, -48, 0, 4, 10, 4, 14, -9]
19:31:09,132 [INFO ] Stats for pc: NStepPlayer@5
19:31:09,132 [INFO ] - vs NStepPlayer@2 = 11 / 20 [-14, -2, -24, 7, 30, -2, -44, 44, -12, 18, 2, 14, 2, 24, 13, -4, -4, 3, -18, 18]
19:31:09,132 [INFO ] - vs NStepPlayer@3 = 11 / 20 [10, -4, 10, -4, 18, 4, -10, 11, 2, -4, -14, 24, 4, -12, -10, -4, -4, 22, 5, 18]
19:31:09,132 [INFO ] - vs NStepPlayer@4 = 11 / 20 [0, -2, 14, -5, 8, 1, -26, 45, 12, 4, 3, 19, 8, 48, 0, -4, -10, -4, -14, 9]
*/

/*
20:16:39,374 [INFO ] Config for tournament: numFight=1, withReturn=true randomSeeds=false withDetails=true totalStep=30
20:21:15,657 [INFO ] Stats for pc: NStepPlayer@2
20:21:15,767 [INFO ] - vs NStepPlayer@3 = 2 / 2 [8, 8]
20:21:15,767 [INFO ] - vs NStepPlayer@4 = 2 / 2 [6, 2]
20:21:15,767 [INFO ] - vs NStepPlayer@5 = 2 / 2 [14, 2]
20:21:15,767 [INFO ] - vs NStepPlayer@6 = 0 / 2 [-20, -14]
20:21:15,768 [INFO ] - vs NStepPlayer@7 = 1 / 2 [-18, 6]
20:21:15,768 [INFO ] Stats for pc: NStepPlayer@3
20:21:15,768 [INFO ] - vs NStepPlayer@2 = 0 / 2 [-8, -8]
20:21:15,768 [INFO ] - vs NStepPlayer@4 = 1 / 2 [10, -25]
20:21:15,768 [INFO ] - vs NStepPlayer@5 = 1 / 2 [-10, 4]
20:21:15,768 [INFO ] - vs NStepPlayer@6 = 0 / 2 [-10, -3]
20:21:15,768 [INFO ] - vs NStepPlayer@7 = 0 / 2 [-20, -14]
20:21:15,770 [INFO ] Stats for pc: NStepPlayer@4
20:21:15,770 [INFO ] - vs NStepPlayer@2 = 0 / 2 [-6, -2]
20:21:15,770 [INFO ] - vs NStepPlayer@3 = 1 / 2 [-10, 25]
20:21:15,770 [INFO ] - vs NStepPlayer@5 = 1 / 2 [0, 2]
20:21:15,770 [INFO ] - vs NStepPlayer@6 = 1 / 2 [18, -8]
20:21:15,770 [INFO ] - vs NStepPlayer@7 = 2 / 2 [14, 10]
20:21:15,770 [INFO ] Stats for pc: NStepPlayer@5
20:21:15,771 [INFO ] - vs NStepPlayer@2 = 0 / 2 [-14, -2]
20:21:15,771 [INFO ] - vs NStepPlayer@3 = 1 / 2 [10, -4]
20:21:15,771 [INFO ] - vs NStepPlayer@4 = 0 / 2 [0, -2]
20:21:15,771 [INFO ] - vs NStepPlayer@6 = 0 / 2 [-6, -8]
20:21:15,771 [INFO ] - vs NStepPlayer@7 = 0 / 2 [-10, 0]
20:21:15,771 [INFO ] Stats for pc: NStepPlayer@6
20:21:15,771 [INFO ] - vs NStepPlayer@2 = 2 / 2 [20, 14]
20:21:15,772 [INFO ] - vs NStepPlayer@3 = 2 / 2 [10, 3]
20:21:15,772 [INFO ] - vs NStepPlayer@4 = 1 / 2 [-18, 8]
20:21:15,772 [INFO ] - vs NStepPlayer@5 = 2 / 2 [6, 8]
20:21:15,772 [INFO ] - vs NStepPlayer@7 = 1 / 2 [-18, 14]
20:21:15,772 [INFO ] Stats for pc: NStepPlayer@7
20:21:15,772 [INFO ] - vs NStepPlayer@2 = 1 / 2 [18, -6]
20:21:15,773 [INFO ] - vs NStepPlayer@3 = 2 / 2 [20, 14]
20:21:15,773 [INFO ] - vs NStepPlayer@4 = 0 / 2 [-14, -10]
20:21:15,773 [INFO ] - vs NStepPlayer@5 = 1 / 2 [10, 0]
20:21:15,773 [INFO ] - vs NStepPlayer@6 = 1 / 2 [18, -14]
*/