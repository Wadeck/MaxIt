package maxit.commons.network.node;

import maxit.commons.network.message.type.IMessage;

public interface IThreadListener<M extends IMessage> {
	public void removeMe(GenericThread<M> thread);
}
