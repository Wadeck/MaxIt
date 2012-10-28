package maxit.commons.network.message.type;

import java.util.Map;

public interface IMessage {
	/**
	 * To build the incoming message from the xml content
	 * 
	 * @param map
	 */
	void build(Map<String, String> map);
	
	String toXml();
}