package tech.pegasys.pantheon.ethereum.stratum.notifications;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import tech.pegasys.pantheon.ethereum.stratum.StratumNotification;

public class MiningHashrate implements StratumNotification {

  @Override
  public String getName() {
    return "mining.hashrate";
  }

  @Override
  public JsonRpcResponse response() {
    return null;
  }
}
