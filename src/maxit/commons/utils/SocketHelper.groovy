package maxit.commons.utils

import groovy.transform.CompileStatic;

import java.net.Socket

@CompileStatic
class SocketHelper {
	static Socket tryConnect(String ip, int port, int numTry = 1){
		Socket s
		while (!s && numTry >= 0) {
			numTry--
			try {
				s = new Socket(ip, port)
			} catch (Exception e) {
				s = null
				Thread.sleep(1000)
			}
		}
		return s
	}
}
