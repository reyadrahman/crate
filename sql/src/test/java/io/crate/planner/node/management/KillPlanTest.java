/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.planner.node.management;

import io.crate.executor.transport.kill.KillAllRequest;
import io.crate.executor.transport.kill.TransportKillAllNodeAction;
import io.crate.executor.transport.kill.TransportKillJobsNodeAction;
import io.crate.testing.TestingRowConsumer;
import org.elasticsearch.action.ActionListener;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class KillPlanTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testKillTaskCallsBroadcastOnTransportKillAllNodeAction() throws Exception {
        TransportKillAllNodeAction killAllNodeAction = mock(TransportKillAllNodeAction.class);
        KillPlan killPlan = new KillPlan();

        killPlan.execute(killAllNodeAction, mock(TransportKillJobsNodeAction.class), new TestingRowConsumer());;
        verify(killAllNodeAction, times(1)).broadcast(any(KillAllRequest.class), any(ActionListener.class));
        verify(killAllNodeAction, times(0)).nodeOperation(any(KillAllRequest.class));
    }

}
