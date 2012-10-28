package maxit.commons.network

import groovy.xml.MarkupBuilder

abstract class AbstractMessage {
	abstract Map<String, String> getContent()

	String getType(){
		return this.class.simpleName
	}

	public final String toXml() {
		String _type = getType()
		Map<String, String> mapContent = getContent()

		def writer = new StringWriter()
//		def xml = new MarkupBuilder(writer)
		def xml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false))
		xml.message() {
			type(_type)
			content(){
				mapContent.each{
					"${ it.key }"(it.value)
				}
			}
		}
		writer.toString()
	}
}
