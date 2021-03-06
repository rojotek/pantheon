/*
 * Copyright 2018 ConsenSys AG.
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
package tech.pegasys.pantheon.ethereum.jsonrpc.websocket.subscription.pending;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import tech.pegasys.pantheon.ethereum.chain.Blockchain;
import tech.pegasys.pantheon.ethereum.core.Block;
import tech.pegasys.pantheon.ethereum.core.Hash;
import tech.pegasys.pantheon.ethereum.core.Transaction;
import tech.pegasys.pantheon.ethereum.jsonrpc.websocket.subscription.Subscription;
import tech.pegasys.pantheon.ethereum.jsonrpc.websocket.subscription.SubscriptionManager;
import tech.pegasys.pantheon.ethereum.jsonrpc.websocket.subscription.request.SubscriptionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PendingTransactionSubscriptionServiceTest {

  private static final Hash TX_ONE =
      Hash.fromHexString("0x15876958423545c3c7b0fcf9be8ffb543305ee1b43db87ed380dcf0cd16589f7");

  @Mock private SubscriptionManager subscriptionManager;
  @Mock private Blockchain blockchain;
  @Mock private Block block;

  private PendingTransactionSubscriptionService service;

  @Before
  public void setUp() {
    service = new PendingTransactionSubscriptionService(subscriptionManager);
  }

  @Test
  public void onTransactionAddedMustSendMessage() {
    final long[] subscriptionIds = new long[] {5, 56, 989};
    setUpSubscriptions(subscriptionIds);
    final Transaction pending = transaction(TX_ONE);

    service.onTransactionAdded(pending);

    verifyZeroInteractions(block);
    verifyZeroInteractions(blockchain);
    verifySubscriptionMangerInteractions(messages(TX_ONE, subscriptionIds));
  }

  private void verifySubscriptionMangerInteractions(final Map<Long, Hash> expected) {
    verify(subscriptionManager)
        .subscriptionsOfType(SubscriptionType.NEW_PENDING_TRANSACTIONS, Subscription.class);

    for (final Map.Entry<Long, Hash> message : expected.entrySet()) {
      verify(subscriptionManager)
          .sendMessage(
              eq(message.getKey()), refEq(new PendingTransactionResult(message.getValue())));
    }

    verifyNoMoreInteractions(subscriptionManager);
  }

  private Map<Long, Hash> messages(final Hash result, final long... subscriptionIds) {
    final Map<Long, Hash> messages = new HashMap<>();

    for (final long subscriptionId : subscriptionIds) {
      messages.put(subscriptionId, result);
    }

    return messages;
  }

  private Transaction transaction(final Hash hash) {
    final Transaction tx = mock(Transaction.class);
    when(tx.hash()).thenReturn(hash);
    return tx;
  }

  private void setUpSubscriptions(final long... subscriptionsIds) {
    when(subscriptionManager.subscriptionsOfType(any(), any()))
        .thenReturn(
            Arrays.stream(subscriptionsIds)
                .mapToObj(id -> new Subscription(id, SubscriptionType.NEW_PENDING_TRANSACTIONS))
                .collect(Collectors.toList()));
  }
}
