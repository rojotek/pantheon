package tech.pegasys.pantheon.ethereum.stratum.messages.outbound;

public class HelloResult {

  public static final String STRATUM_V2 = "EthereumStratum/2.0.0";
  private final String proto;
  private final String encoding;
  private final Long resume;
  private final Long timeout;
  private final Long maxerrors;
  private final String node;

  public HelloResult(final String proto, final String encoding, final Long resume,
      final Long timeout,
      final Long maxerrors, final String node) {
    this.proto = proto;
    this.encoding = encoding;
    this.resume = resume;
    this.timeout = timeout;
    this.maxerrors = maxerrors;
    this.node = node;
  }

  public String getProto() {
    return proto;
  }

  public String getEncoding() {
    return encoding;
  }

  public Long getResume() {
    return resume;
  }

  public Long getTimeout() {
    return timeout;
  }

  public Long getMaxerrors() {
    return maxerrors;
  }

  public String getNode() {
    return node;
  }
}
