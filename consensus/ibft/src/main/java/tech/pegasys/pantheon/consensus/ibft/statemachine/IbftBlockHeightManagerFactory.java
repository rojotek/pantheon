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
package tech.pegasys.pantheon.consensus.ibft.statemachine;

import tech.pegasys.pantheon.consensus.ibft.IbftContext;
import tech.pegasys.pantheon.consensus.ibft.validation.MessageValidatorFactory;
import tech.pegasys.pantheon.ethereum.ProtocolContext;
import tech.pegasys.pantheon.ethereum.core.BlockHeader;

import java.time.Clock;

public class IbftBlockHeightManagerFactory {

  private final IbftRoundFactory roundFactory;
  private final IbftFinalState finalState;
  private final ProtocolContext<IbftContext> protocolContext;
  private final MessageValidatorFactory messageValidatorFactory;

  public IbftBlockHeightManagerFactory(
      final IbftFinalState finalState,
      final IbftRoundFactory roundFactory,
      final MessageValidatorFactory messageValidatorFactory,
      final ProtocolContext<IbftContext> protocolContext) {
    this.roundFactory = roundFactory;
    this.finalState = finalState;
    this.protocolContext = protocolContext;
    this.messageValidatorFactory = messageValidatorFactory;
  }

  public IbftBlockHeightManager create(final BlockHeader parentHeader) {
    long nextChainHeight = parentHeader.getNumber() + 1;
    return new IbftBlockHeightManager(
        parentHeader,
        finalState,
        new RoundChangeManager(
            nextChainHeight,
            finalState.getValidators(),
            (roundIdentifier) ->
                messageValidatorFactory.createMessageValidator(roundIdentifier, parentHeader)),
        roundFactory,
        Clock.systemUTC(),
        messageValidatorFactory);
  }
}
