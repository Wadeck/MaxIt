package maxit.commons.network.message



import groovy.transform.CompileStatic;
import maxit.commons.network.message.type.IMessage

import org.apache.log4j.Logger

abstract class AbstractMessageReader<M extends IMessage> {
	private static Logger log = Logger.getLogger(this.class)

	protected ClassLoader classLoader
	private String packageName

	/**
	 * @param packageName "maxit.message.server" without dot at the end
	 */
	AbstractMessageReader(String packageName){
		this.classLoader = ClassLoader.getSystemClassLoader()
		this.packageName = packageName + "."
	}

	public M readMessage(String xmlString)
	throws Exception {
		XmlSlurper slurper = new XmlSlurper()
		def message = slurper.parseText(xmlString)

		String type = message.type[0].text()
		Class<?> clazz = loadClass(type)
		if(!clazz){
			return null
		}
		//
		M result = (M) clazz.newInstance()

		def contentMap = [:]
		message.content.children().collect{
			contentMap << [(it.name()): it.text()]
		}
		
		result.build(contentMap)

		return result
	}

	protected Class<?> loadClass(String type){
		Class<?> clazz
		try{
			clazz = classLoader.loadClass(packageName + type)
		}
		catch(ClassNotFoundException e){
//			log.error 'No class found for class with type: ' + type
			clazz = null
		}
		return clazz
	}
}
