package tech.pegasys.pantheon.ethereum.stratum.messages.outbound;

public class Reconnect {
  private final String host;
  private final String port;
  private final String resume;

  public Reconnect(final String host, final String port, final String resume) {
    this.host = host;
    this.port = port;
    this.resume = resume;
  }
}
