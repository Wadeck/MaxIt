/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maxit

import groovy.transform.CompileStatic
import maxit.server.Server

/**
 *
 * @author Wadeck
 */
@CompileStatic
public class MaxItServer {
	public static void main(String[] args) {
		new Server(Integer.parseInt(args[0])).start()
	}
}
