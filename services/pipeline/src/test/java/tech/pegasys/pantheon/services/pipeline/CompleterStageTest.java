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
package tech.pegasys.pantheon.services.pipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static tech.pegasys.pantheon.metrics.noop.NoOpMetricsSystem.NO_OP_COUNTER;

import tech.pegasys.pantheon.metrics.Counter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CompleterStageTest {

  private final Pipe<String> pipe = new Pipe<>(10, NO_OP_COUNTER);
  private final List<String> output = new ArrayList<>();
  private final Counter outputCounter = mock(Counter.class);
  private final CompleterStage<String> stage =
      new CompleterStage<>(pipe, output::add, outputCounter);

  @Test
  public void shouldAddItemsToOutputUntilPipeHasNoMore() {
    pipe.put("a");
    pipe.put("b");
    pipe.put("c");
    pipe.close();

    stage.run();

    assertThat(output).containsExactly("a", "b", "c");
    verify(outputCounter, times(3)).inc();
  }
}
