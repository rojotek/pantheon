/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.permissioning;

import static java.nio.charset.StandardCharsets.UTF_8;

import tech.pegasys.pantheon.crypto.Hash;
import tech.pegasys.pantheon.ethereum.core.Address;
import tech.pegasys.pantheon.ethereum.permissioning.node.NodePermissioningProvider;
import tech.pegasys.pantheon.ethereum.transaction.CallParameter;
import tech.pegasys.pantheon.ethereum.transaction.TransactionSimulator;
import tech.pegasys.pantheon.ethereum.transaction.TransactionSimulatorResult;
import tech.pegasys.pantheon.util.bytes.BytesValue;
import tech.pegasys.pantheon.util.bytes.BytesValues;
import tech.pegasys.pantheon.util.enode.EnodeURL;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Optional;

/**
 * Controller that can read from a smart contract that exposes the permissioning calls
 * connectionAllowedIpv4(bytes32,bytes32,bytes4,uint16,bytes32,bytes32,bytes4,uint16)
 * connectionAllowedIpv6(bytes32,bytes32,bytes16,uint16,bytes32,bytes32,bytes16,uint16)
 */
public class SmartContractPermissioningController implements NodePermissioningProvider {
  private final Address contractAddress;
  private final TransactionSimulator transactionSimulator;

  // full function signature for ipv4 call
  private static final String IPV4_FUNCTION_SIGNATURE =
      "connectionAllowedIpv4(bytes32,bytes32,bytes4,uint16,bytes32,bytes32,bytes4,uint16)";
  // hashed function signature for ipv4 call
  private static final BytesValue IPV4_FUNCTION_SIGNATURE_HASH =
      hashSignature(IPV4_FUNCTION_SIGNATURE);
  // full function signature for ipv6 call
  private static final String IPV6_FUNCTION_SIGNATURE =
      "connectionAllowedIpv6(bytes32,bytes32,bytes16,uint16,bytes32,bytes32,bytes16,uint16)";
  // hashed function signature for ipv6 call
  private static final BytesValue IPV6_FUNCTION_SIGNATURE_HASH =
      hashSignature(IPV6_FUNCTION_SIGNATURE);

  // The first 4 bytes of the hash of the full textual signature of the function is used in
  // contract calls to determine the function being called
  private static BytesValue hashSignature(final String signature) {
    return Hash.keccak256(BytesValue.of(signature.getBytes(UTF_8))).slice(0, 4);
  }

  // True from a contract is 1 filled to 32 bytes
  private static final BytesValue TRUE_RESPONSE;

  static {
    final byte[] trueValue = new byte[32];
    trueValue[31] = (byte) (0xFF & 1L);
    TRUE_RESPONSE = BytesValue.wrap(trueValue);
  }

  /**
   * Creates a permissioning controller attached to a blockchain
   *
   * @param contractAddress The address at which the permissioning smart contract resides
   * @param transactionSimulator A transaction simulator with attached blockchain and world state
   */
  public SmartContractPermissioningController(
      final Address contractAddress, final TransactionSimulator transactionSimulator) {
    this.contractAddress = contractAddress;
    this.transactionSimulator = transactionSimulator;
  }

  /**
   * Check whether a given connection from the source to destination enode should be permitted
   *
   * @param sourceEnode The enode url of the node initiating the connection
   * @param destinationEnode The enode url of the node receiving the connection
   * @return boolean of whether or not to permit the connection to occur
   */
  @Override
  public boolean isPermitted(final EnodeURL sourceEnode, final EnodeURL destinationEnode) {
    final BytesValue payload = createPayload(sourceEnode, destinationEnode);
    final CallParameter callParams =
        new CallParameter(null, contractAddress, -1, null, null, payload);

    final Optional<TransactionSimulatorResult> result =
        transactionSimulator.processAtHead(callParams);

    return result.map(r -> checkTransactionResult(r.getOutput())).orElse(false);
  }

  // Checks the returned bytes from the permissioning contract call to see if it's a value we
  // understand
  private Boolean checkTransactionResult(final BytesValue result) {
    // booleans are padded to 32 bytes
    if (result.size() != 32) {
      throw new IllegalArgumentException("Unexpected result size");
    }

    // 0 is false
    if (result.isZero()) {
      return false;
      // 1 filled to 32 bytes is true
    } else if (result.compareTo(TRUE_RESPONSE) == 0) {
      return true;
      // Anything else is wrong
    } else {
      throw new IllegalStateException("Unexpected result form");
    }
  }

  // Assemble the bytevalue payload to call the contract
  private BytesValue createPayload(final EnodeURL sourceEnode, final EnodeURL destinationEnode) {
    final BytesValue signature;
    // Grab the right function signature based on the enodes provided
    if (sourceEnode.getInetAddress() instanceof Inet4Address
        && destinationEnode.getInetAddress() instanceof Inet4Address) {
      signature = IPV4_FUNCTION_SIGNATURE_HASH;
    } else if (sourceEnode.getInetAddress() instanceof Inet6Address
        && destinationEnode.getInetAddress() instanceof Inet6Address) {
      signature = IPV6_FUNCTION_SIGNATURE_HASH;
    } else {
      // If we got mixed mode enodes then it's wrong
      throw new IllegalArgumentException(
          "No payload possible for checking an ipv4 to ipv6 connection");
    }
    return BytesValues.concatenate(
        signature, encodeEnodeUrl(sourceEnode), encodeEnodeUrl(destinationEnode));
  }

  private BytesValue encodeEnodeUrl(final EnodeURL enode) {
    return BytesValues.concatenate(
        encodeEnodeId(enode.getNodeId()),
        encodeIp(enode.getInetAddress()),
        encodePort(enode.getListeningPort()));
  }

  // As a function parameter an ip needs to be the appropriate number of bytes, big endian, and
  // filled to 32 bytes
  private BytesValue encodeIp(final InetAddress addr) {
    // InetAddress deals with giving us the right number of bytes
    final byte[] address = addr.getAddress();
    final byte[] res = new byte[32];
    System.arraycopy(address, 0, res, 0, address.length);
    return BytesValue.wrap(res);
  }

  // The port, a uint16, needs to be 2 bytes, little endian, and filled to 32 bytes
  private BytesValue encodePort(final Integer port) {
    final byte[] res = new byte[32];
    res[31] = (byte) ((port) & 0xFF);
    res[30] = (byte) ((port >> 8) & 0xFF);
    return BytesValue.wrap(res);
  }

  // The enode high and low need to be 32 bytes each. They then get concatenated as they are
  // adjacent parameters
  private BytesValue encodeEnodeId(final String id) {
    return BytesValue.fromHexString(id);
  }
}
