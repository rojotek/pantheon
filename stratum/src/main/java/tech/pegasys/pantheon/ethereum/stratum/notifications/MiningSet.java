package tech.pegasys.pantheon.ethereum.stratum.notifications;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import tech.pegasys.pantheon.ethereum.stratum.StratumNotification;

public class MiningSet implements StratumNotification {

  @Override
  public String getName() {
    return "mining.set";
  }

  @Override
  public JsonRpcResponse response() {
    return null;
  }
}
