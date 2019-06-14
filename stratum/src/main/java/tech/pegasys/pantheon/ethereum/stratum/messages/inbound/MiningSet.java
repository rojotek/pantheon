package tech.pegasys.pantheon.ethereum.stratum.messages.inbound;

public class MiningSet {
  private final String epoch;
  private final String target;
  private final String algo;
  private final String extranonce;

  public MiningSet(final String epoch, final String target, final String algo,
      final String extranonce) {
    this.epoch = epoch;
    this.target = target;
    this.algo = algo;
    this.extranonce = extranonce;
  }
}
