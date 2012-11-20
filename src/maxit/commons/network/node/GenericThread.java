package maxit.commons.network.node;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import maxit.commons.network.message.AbstractMessageReader;
import maxit.commons.network.message.type.IMessage;

import org.apache.log4j.Logger;

public class GenericThread<M extends IMessage> extends Thread {
	private static Logger log = Logger.getLogger(GenericThread.class);
	private final Object lock;

	protected Socket s;
	protected BufferedReader buffInput;
	protected BufferedWriter buffOutput;

	protected boolean end;
	private boolean started;
	private IThreadListener<M> threadListener;
	private IMessageListener<M> listener;
	private AbstractMessageReader<M> messageReader;

	public GenericThread(IThreadListener<M> threadListener,
			IMessageListener<M> listener, AbstractMessageReader<M> messageReader) {
		this.threadListener = threadListener;
		this.listener = listener;
		this.messageReader = messageReader;
		this.lock = new Object();
		this.setDaemon(true);

		this.end = false;
		this.started = false;

		this.reset();
	}

	public void reset() {
		this.s = null;
		this.buffInput = null;
		this.buffOutput = null;
	}

	/**
	 * Must be called if we don't pass the inputs/outputs
	 * 
	 * @param s
	 */
	public void startWithSocket(Socket s) {
		this.s = s;
		this.init();

		synchronized (lock) {
			this.lock.notify();
		}

		if (!started) {
			start();
		}
	}

	public void startWithSocket(Socket s, BufferedReader buffInput,
			BufferedWriter buffOutput) {
		this.s = s;

		this.buffInput = buffInput;
		this.buffOutput = buffOutput;

		this.init();

		synchronized (lock) {
			this.lock.notify();
		}

		if (!started) {
			start();
		}
	}

	private void init() {
		try {
			if (null == buffInput) {
				InputStream input = s.getInputStream();
				this.buffInput = new BufferedReader(
						new InputStreamReader(input));
			}

			if (null == buffOutput) {
				OutputStream output = s.getOutputStream();
				this.buffOutput = new BufferedWriter(new PrintWriter(output,
						true));
			}
		} catch (IOException e) {
			log.error("input / output", e);
		}
	}

	private void finalClose() {
		if (this.s != null) {
			if (!this.s.isClosed()) {
				try {
					this.s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sendMessage(IMessage message) {
		try {
			String data = message.toXml();
			buffOutput.write(data);
			buffOutput.newLine();
			buffOutput.flush();
			// TODO remove me
//			log.debug("Data sent: " + data);
		} catch (IOException e) {
			log.error("output message", e);
		}
	}

	@Override
	public void run() {
		super.run();

		started = true;

		while (!end) {
			if (s == null) {
				synchronized (lock) {
					try {
						this.lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (s == null) {
					continue;
				}
			}
			String xml;
			try {
				xml = buffInput.readLine();
			} catch (SocketException e) {
				log.error("socket error", e);
				end = true;
				continue;
			} catch (Exception e) {
				log.error("build", e);
				continue;
			}

			if (xml.startsWith("<?xml")) {
				continue;
			}

			M message;
			try {
//				log.debug("received: " + xml);
				message = messageReader.readMessage(xml);
				if (message != null) {
					listener.onMessage(message);
				}
			} catch (Exception e) {
				log.error("readCommand", e);
			}
		}

		finalClose();

		log.info("Thread ended");
		threadListener.removeMe(this);
	}

	public Socket getSocket() {
		return s;
	}

	public BufferedWriter getOutputBuffer() {
		return buffOutput;
	}

	public BufferedReader getInputBuffer() {
		return buffInput;
	}

	public boolean isStillConnected() {
		return s.isConnected();
	}
}
