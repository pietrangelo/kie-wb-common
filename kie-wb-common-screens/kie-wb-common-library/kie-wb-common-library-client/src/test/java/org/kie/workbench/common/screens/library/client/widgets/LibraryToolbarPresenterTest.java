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
package org.kie.workbench.common.screens.library.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryPreferences;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.OrganizationalUnitRepositoryInfo;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LibraryToolbarPresenterTest {

    @Mock
    private LibraryToolbarPresenter.View view;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private LibraryPreferences libraryPreferences;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private LibraryPlaces libraryPlaces;

    private LibraryToolbarPresenter presenter;

    private OrganizationalUnit selectedOrganizationalUnit;

    private Repository selectedRepository;

    private Command callback;

    private ArrayList<String> selectedRepositoryBranches;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);
        presenter = new LibraryToolbarPresenter(view,
                                                libraryServiceCaller,
                                                libraryPreferences,
                                                placeManager,
                                                libraryPlaces);

        selectedOrganizationalUnit = mock(OrganizationalUnit.class);
        doReturn("organizationalUnit1").when(selectedOrganizationalUnit).getIdentifier();
        OrganizationalUnit organizationalUnit2 = mock(OrganizationalUnit.class);
        doReturn("organizationalUnit2").when(organizationalUnit2).getIdentifier();
        List<OrganizationalUnit> organizationalUnits = new ArrayList<>();
        organizationalUnits.add(selectedOrganizationalUnit);
        organizationalUnits.add(organizationalUnit2);

        selectedRepository = mock(Repository.class);
        selectedRepositoryBranches = new ArrayList<>();
        selectedRepositoryBranches.add("master");
        when(selectedRepository.getDefaultBranch()).thenReturn("master");
        when(selectedRepository.getBranches()).thenReturn(selectedRepositoryBranches);
        doReturn("repository1").when(selectedRepository).getAlias();
        Repository repository2 = mock(Repository.class);
        doReturn("repository2").when(repository2).getAlias();
        doReturn("defaultBranch").when(repository2).getDefaultBranch();
        List<Repository> repositories = new ArrayList<>();
        repositories.add(selectedRepository);
        repositories.add(repository2);

        final OrganizationalUnitRepositoryInfo organizationalUnitRepositoryInfo
                = new OrganizationalUnitRepositoryInfo(organizationalUnits,
                                                       selectedOrganizationalUnit,
                                                       repositories,
                                                       selectedRepository);

        doReturn(organizationalUnitRepositoryInfo)
                .when(libraryService).getDefaultOrganizationalUnitRepositoryInfo();
        doReturn(organizationalUnitRepositoryInfo)
                .when(libraryService).getOrganizationalUnitRepositoryInfo(organizationalUnit2);

        callback = mock(Command.class);
    }

    @Test
    public void initTest() {
        presenter.init(callback);

        assertEquals(selectedOrganizationalUnit,
                     presenter.getSelectedOrganizationalUnit());
        assertEquals(selectedRepository,
                     presenter.getSelectedRepository());
        assertEquals("master",
                     presenter.getSelectedBranch());

        verify(view).init(presenter);

        verify(view).clearOrganizationalUnits();
        verify(view).addOrganizationUnit("organizationalUnit1");
        verify(view).addOrganizationUnit("organizationalUnit2");
        verify(view).setSelectedOrganizationalUnit("organizationalUnit1");

        verify(view).clearRepositories();
        verify(view).addRepository("repository1");
        verify(view).addRepository("repository2");
        verify(view).setSelectedRepository("repository1");

        verify(callback).execute();
    }

    @Test
    public void showBranchSelectorOnInit() throws Exception {
        presenter.init(callback);

        verify(view).setBranchSelectorVisibility(false);
    }

    @Test
    public void hideBranchSelectorOnInit() throws Exception {
        selectedRepositoryBranches.add("dev");

        presenter.init(callback);

        verify(view).setBranchSelectorVisibility(true);
    }

    @Test
    public void updateSelectedOrganizationalUnitFailedTest() {
        presenter.init(callback);
        Mockito.reset(view);

        doReturn(false).when(placeManager).closeAllPlacesOrNothing();
        doReturn("organizationalUnit2").when(view).getSelectedOrganizationalUnit();
        doReturn("repository2").when(view).getSelectedRepository();

        presenter.onUpdateSelectedOrganizationalUnit();

        assertEquals("organizationalUnit1",
                     presenter.getSelectedOrganizationalUnit().getIdentifier());
        assertEquals("repository1",
                     presenter.getSelectedRepository().getAlias());
        assertEquals("master",
                     presenter.getSelectedBranch());
    }

    @Test
    public void updateSelectedOrganizationalUnitSucceededTest() {
        presenter.init(callback);
        Mockito.reset(view);

        doReturn(true).when(placeManager).closeAllPlacesOrNothing();
        doReturn("organizationalUnit2").when(view).getSelectedOrganizationalUnit();
        doReturn("repository2").when(view).getSelectedRepository();

        presenter.onUpdateSelectedOrganizationalUnit();

        assertEquals("organizationalUnit2",
                     presenter.getSelectedOrganizationalUnit().getIdentifier());
        assertEquals("repository2",
                     presenter.getSelectedRepository().getAlias());

        verify(libraryPlaces).goToLibrary();

        verify(view).clearRepositories();
        verify(view).addRepository("repository1");
        verify(view).addRepository("repository2");
        verify(view).setSelectedRepository("repository1");
    }

    @Test
    public void updateSelectedRepositoryFailedTest() {
        presenter.init(callback);
        Mockito.reset(view);

        doReturn(false).when(placeManager).closeAllPlacesOrNothing();
        doReturn("organizationalUnit1").when(view).getSelectedOrganizationalUnit();
        doReturn("repository2").when(view).getSelectedRepository();

        presenter.onUpdateSelectedRepository();

        assertEquals("organizationalUnit1",
                     presenter.getSelectedOrganizationalUnit().getIdentifier());
        assertEquals("repository1",
                     presenter.getSelectedRepository().getAlias());
    }

    @Test
    public void updateSelectedRepositorySucceededTest() {
        presenter.init(callback);
        Mockito.reset(view);

        doReturn(true).when(placeManager).closeAllPlacesOrNothing();
        doReturn("organizationalUnit1").when(view).getSelectedOrganizationalUnit();
        doReturn("repository2").when(view).getSelectedRepository();

        presenter.onUpdateSelectedRepository();

        assertEquals("organizationalUnit1",
                     presenter.getSelectedOrganizationalUnit().getIdentifier());
        assertEquals("repository2",
                     presenter.getSelectedRepository().getAlias());
        assertEquals("defaultBranch",
                     presenter.getSelectedBranch());

        verify(libraryPlaces).goToLibrary();

        verify(view,
               never()).clearRepositories();
        verify(view,
               never()).addRepository(anyString());
        verify(view,
               never()).setSelectedRepository(anyString());
        verify(view).setSelectedBranch("defaultBranch");
    }

    @Test
    public void updateSelectedBranch() throws Exception {
        selectedRepositoryBranches.add("dev");

        presenter.init(callback);
        Mockito.reset(view);

        doReturn(true).when(placeManager).closeAllPlacesOrNothing();
        doReturn("organizationalUnit1").when(view).getSelectedOrganizationalUnit();
        doReturn("repository1").when(view).getSelectedRepository();
        doReturn("dev").when(view).getSelectedBranch();

        presenter.onUpdateSelectedBranch();

        verify(libraryPlaces).goToLibrary();

        verify(view,
               never()).clearRepositories();
        verify(view,
               never()).addRepository(anyString());
        verify(view,
               never()).setSelectedRepository(anyString());
        verify(view,
               never()).setSelectedBranch("dev");
    }
}