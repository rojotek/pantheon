package tech.pegasys.pantheon.ethereum.stratum.notifications;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import tech.pegasys.pantheon.ethereum.stratum.StratumNotification;

public class MiningBye implements StratumNotification {

  @Override
  public String getName() {
    return "mining.bye";
  }

  @Override
  public JsonRpcResponse response() {
    return null;
  }
}
