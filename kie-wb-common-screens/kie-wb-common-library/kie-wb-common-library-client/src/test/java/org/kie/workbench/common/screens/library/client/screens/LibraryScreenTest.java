/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.library.client.screens;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.ExamplesUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LibraryScreenTest {

    @Mock
    private LibraryScreen.View view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private Event<ProjectDetailEvent> projectDetailEvent;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private ExamplesUtils examplesUtils;

    private LibraryScreen libraryScreen;

    private ExampleProject exampleProject1;
    private ExampleProject exampleProject2;

    private Project project1;
    private Project project2;
    private Project project3;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);

        exampleProject1 = mock(ExampleProject.class);
        exampleProject2 = mock(ExampleProject.class);

        final Set<ExampleProject> exampleProjects = new HashSet<>();
        exampleProjects.add(exampleProject1);
        exampleProjects.add(exampleProject2);

        doAnswer(invocationOnMock -> {
            final ParameterizedCommand<Set<ExampleProject>> callback = (ParameterizedCommand<Set<ExampleProject>>) invocationOnMock.getArguments()[0];
            callback.execute(exampleProjects);
            return null;
        }).when(examplesUtils).getExampleProjects(any(ParameterizedCommand.class));

        libraryScreen = spy(new LibraryScreen(view,
                                              placeManager,
                                              libraryPlaces,
                                              projectDetailEvent,
                                              libraryServiceCaller,
                                              examplesUtils));

        project1 = mock(Project.class);
        doReturn("project1Name").when(project1).getProjectName();
        project2 = mock(Project.class);
        doReturn("project2Name").when(project2).getProjectName();
        project3 = mock(Project.class);
        doReturn("project3Name").when(project3).getProjectName();

        final Set<Project> projects = new HashSet<>();
        projects.add(project1);
        projects.add(project2);
        projects.add(project3);

        final LibraryInfo libraryInfo = new LibraryInfo("master",
                                                        projects);
        doReturn(libraryInfo).when(libraryService).getLibraryInfo(any(Repository.class),
                                                                  anyString());

        libraryScreen.setup();
    }


    @Test
    public void setupTest() {
        verify(view).clearFilterText();
        verify(view).clearProjects();
        verify(placeManager).closePlace(LibraryPlaces.EMPTY_LIBRARY_SCREEN);

        verify(view,
               times(3)).addProject(anyString(),
                                    any(Command.class),
                                    any(Command.class));

        verify(view,
               times(1)).addProject(eq("project1Name"),
                                    any(Command.class),
                                    any(Command.class));
        verify(view,
               times(1)).addProject(eq("project2Name"),
                                    any(Command.class),
                                    any(Command.class));
        verify(view,
               times(1)).addProject(eq("project3Name"),
                                    any(Command.class),
                                    any(Command.class));
    }

    @Test
    public void newProjectTest() {
        libraryScreen.newProject();

        verify(libraryPlaces).goToNewProject();
    }

    @Test
    public void importProjectTest() {
        final ExampleProject exampleProject = mock(ExampleProject.class);

        libraryScreen.importProject(exampleProject);

        verify(examplesUtils).importProject(exampleProject);
    }

    @Test
    public void updateImportProjectsTest() {
        libraryScreen.updateImportProjects();

        verify(view).clearImportProjectsContainer();

        verify(view,
               times(2)).addProjectToImport(any(ExampleProject.class));
        verify(view).addProjectToImport(exampleProject1);
        verify(view).addProjectToImport(exampleProject2);
    }

    @Test
    public void selectCommandTest() {
        libraryScreen.selectCommand(project1).execute();

        verify(libraryPlaces).goToProject(any(ProjectInfo.class));
    }

    @Test
    public void detailsCommandTest() {
        libraryScreen.detailsCommand(project1).execute();

        verify(projectDetailEvent).fire(any(ProjectDetailEvent.class));
    }

    @Test
    public void filterProjectsTest() {
        assertEquals(3,
                     libraryScreen.libraryInfo.getProjects().size());
        assertEquals(1,
                     libraryScreen.filterProjects("project1").size());
    }
}