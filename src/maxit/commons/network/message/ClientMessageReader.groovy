package maxit.commons.network.message

import groovy.transform.CompileStatic

import maxit.commons.network.message.type.IClientMessage
import org.apache.log4j.Logger

@CompileStatic
class ClientMessageReader extends AbstractMessageReader<IClientMessage>{
	private static Logger log = Logger.getLogger(this.class)

	protected String secondaryPackageName
	ClientMessageReader(){
		super('maxit.commons.network.message.clientGame')
		this.secondaryPackageName = 'maxit.commons.network.message.clientMeta' + '.'
	}

	@Override
	protected Class<?> loadClass(String type) {
		Class<?> clazz = super.loadClass(type)
		if(!clazz){
			try{
				clazz = classLoader.loadClass(secondaryPackageName + type)
			}
			catch(ClassNotFoundException e){
				log.error 'No class found for class with type: ' + type
				clazz = null
			}
		}
		return clazz
	}
}
