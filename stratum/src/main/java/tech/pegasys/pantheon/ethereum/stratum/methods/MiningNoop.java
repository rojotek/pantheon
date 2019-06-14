package tech.pegasys.pantheon.ethereum.stratum.methods;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequest;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.methods.JsonRpcMethod;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;

public class MiningNoop implements JsonRpcMethod {

  @Override
  public String getName() {
    return "mining.noop";
  }

  @Override
  public JsonRpcResponse response(final JsonRpcRequest request) {
    return null;
  }
}
