/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.command.canvas;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementAddChildNodeCanvasCommandTest extends AbstractCanvasCommandTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkExecute() {
        addChildNode();

        verify(canvasHandler).register(eq(SHAPE_SET_ID),
                                       eq(candidate));
        verify(canvasHandler).addChild(eq(parent),
                                       eq(candidate),
                                       eq(0));
    }

    private CaseManagementAddChildNodeCanvasCommand addChildNode() {
        final CaseManagementAddChildNodeCanvasCommand command = new CaseManagementAddChildNodeCanvasCommand(parent,
                                                                                                            candidate,
                                                                                                            SHAPE_SET_ID);

        command.execute(canvasHandler);
        return command;
    }

    @Test
    public void checkUndo() {
        //Setup the relationship to undo
        final CaseManagementAddChildNodeCanvasCommand command = addChildNode();

        //Perform test
        command.undo(canvasHandler);

        verify(canvasHandler).removeChild(eq(parent),
                                          eq(candidate));
        verify(canvasHandler).deregister(eq(candidate));
    }
}
