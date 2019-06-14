package tech.pegasys.pantheon.ethereum.stratum.notifications;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import tech.pegasys.pantheon.ethereum.stratum.StratumNotification;

public class MiningNotify implements StratumNotification {

  @Override
  public String getName() {
    return "mining.notify";
  }

  @Override
  public JsonRpcResponse response() {
    return null;
  }
}
