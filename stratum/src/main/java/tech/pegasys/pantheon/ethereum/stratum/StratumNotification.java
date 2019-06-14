package tech.pegasys.pantheon.ethereum.stratum;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;

public interface StratumNotification {
  String getName();
  JsonRpcResponse response();

}
