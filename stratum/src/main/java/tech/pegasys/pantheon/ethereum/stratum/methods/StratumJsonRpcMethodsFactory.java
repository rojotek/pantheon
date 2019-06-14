package tech.pegasys.pantheon.ethereum.stratum.methods;

import java.util.HashMap;
import java.util.Map;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.methods.JsonRpcMethod;

public class StratumJsonRpcMethodsFactory {
  public Map<String, JsonRpcMethod> methods(){
    Map<String, JsonRpcMethod> result = new HashMap<>();
    add(result, new MiningAuthorize());
    add(result, new MiningBye());
    add(result, new MiningHashrate());
    add(result, new MiningHello());
    add(result, new MiningNoop());
    add(result, new MiningSubmit());
    add(result, new MiningSubscribe());
    return result;
  }

  private void add(final Map<String, JsonRpcMethod> result, final JsonRpcMethod method) {
    result.put(method.getName(), method);
  }

}
