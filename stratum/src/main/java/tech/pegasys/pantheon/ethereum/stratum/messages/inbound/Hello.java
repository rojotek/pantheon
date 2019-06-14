package tech.pegasys.pantheon.ethereum.stratum.messages.inbound;

public class Hello {
  private final String agent;
  private final String host;
  private final String port;
  private final String proto;

  public Hello(final String agent, final String host, final String port, final String proto) {
    this.agent = agent;
    this.host = host;
    this.port = port;
    this.proto = proto;
  }
}
