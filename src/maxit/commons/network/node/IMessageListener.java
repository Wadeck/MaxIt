package maxit.commons.network.node;

public interface IMessageListener<T> {
	public void onMessage(T message);
}
