#! /usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2015 Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

"""Test cases for Toaster GUI and ReST."""

from django.test import TestCase
from django.test.client import RequestFactory
from django.core.urlresolvers import reverse
from django.utils import timezone

from orm.models import Project, Release, BitbakeVersion, Package, LogMessage
from orm.models import ReleaseLayerSourcePriority, LayerSource, Layer, Build
from orm.models import Layer_Version, Recipe, Machine, ProjectLayer, Target
from orm.models import CustomImageRecipe, ProjectVariable
from orm.models import Branch

import toastermain

from toastergui.tables import SoftwareRecipesTable
import json
from bs4 import BeautifulSoup
import re

PROJECT_NAME = "test project"
CLI_BUILDS_PROJECT_NAME = 'Command line builds'

# by default, tests are run in build mode; to run in analysis mode,
# set this to False in individual test cases
toastermain.settings.BUILD_MODE = True

class ViewTests(TestCase):
    """Tests to verify view APIs."""

    def setUp(self):
        bbv = BitbakeVersion.objects.create(name="test bbv", giturl="/tmp/",
                                            branch="master", dirpath="")
        release = Release.objects.create(name="test release",
                                         branch_name="master",
                                         bitbake_version=bbv)
        self.project = Project.objects.create_project(name=PROJECT_NAME,
                                                      release=release)
        now = timezone.now()

        build = Build.objects.create(project=self.project,
                                     started_on=now,
                                     completed_on=now)

        layersrc = LayerSource.objects.create(sourcetype=LayerSource.TYPE_IMPORTED)
        self.priority = ReleaseLayerSourcePriority.objects.create(release=release,
                                                                  layer_source=layersrc)
        layer = Layer.objects.create(name="base-layer", layer_source=layersrc,
                                     vcs_url="/tmp/")

        branch = Branch.objects.create(name="master", layer_source=layersrc)

        lver = Layer_Version.objects.create(layer=layer, project=self.project,
                                            layer_source=layersrc, commit="master",
                                            up_branch=branch)

        self.recipe1 = Recipe.objects.create(layer_source=layersrc,
                                             name="base-recipe",
                                             version="1.2",
                                             summary="one recipe",
                                             description="recipe",
                                             layer_version=lver)

        Machine.objects.create(layer_version=lver, name="wisk",
                               description="wisking machine")

        ProjectLayer.objects.create(project=self.project, layercommit=lver)


        self.customr = CustomImageRecipe.objects.create(\
                           name="custom recipe", project=self.project,
                           base_recipe=self.recipe1)

        self.package = Package.objects.create(name='pkg1', recipe=self.recipe1,
                                              build=build)


        # recipe with project for testing AvailableRecipe table
        self.recipe2 = Recipe.objects.create(layer_source=layersrc,
                                             name="fancy-recipe",
                                             version="1.4",
                                             summary="a fancy recipe",
                                             description="fancy recipe",
                                             layer_version=lver,
                                             file_path='/home/foo')

        self.assertTrue(lver in self.project.compatible_layerversions())

    def test_get_base_call_returns_html(self):
        """Basic test for all-projects view"""
        response = self.client.get(reverse('all-projects'), follow=True)
        self.assertEqual(response.status_code, 200)
        self.assertTrue(response['Content-Type'].startswith('text/html'))
        self.assertTemplateUsed(response, "projects.html")
        self.assertTrue(PROJECT_NAME in response.content)

    def test_get_json_call_returns_json(self):
        """Test for all projects output in json format"""
        url = reverse('all-projects')
        response = self.client.get(url, {"format": "json"}, follow=True)
        self.assertEqual(response.status_code, 200)
        self.assertTrue(response['Content-Type'].startswith('application/json'))

        data = json.loads(response.content)

        self.assertTrue("error" in data)
        self.assertEqual(data["error"], "ok")
        self.assertTrue("rows" in data)

        self.assertTrue(PROJECT_NAME in [x["name"] for x in data["rows"]])
        self.assertTrue("id" in data["rows"][0])

        self.assertEqual(sorted(data["rows"][0]),
                         ['bitbake_version_id', 'created', 'id',
                          'is_default', 'layersTypeAheadUrl', 'name',
                          'num_builds', 'projectBuildsUrl', 'projectPageUrl',
                          'recipesTypeAheadUrl', 'release_id',
                          'short_description', 'updated', 'user_id'])

    def test_typeaheads(self):
        """Test typeahead ReST API"""
        layers_url = reverse('xhr_layerstypeahead', args=(self.project.id,))
        prj_url = reverse('xhr_projectstypeahead')

        urls = [layers_url,
                prj_url,
                reverse('xhr_recipestypeahead', args=(self.project.id,)),
                reverse('xhr_machinestypeahead', args=(self.project.id,)),
               ]

        def basic_reponse_check(response, url):
            """Check data structure of http response."""
            self.assertEqual(response.status_code, 200)
            self.assertTrue(response['Content-Type'].startswith('application/json'))

            data = json.loads(response.content)

            self.assertTrue("error" in data)
            self.assertEqual(data["error"], "ok")
            self.assertTrue("results" in data)

            # We got a result so now check the fields
            if len(data['results']) > 0:
                result = data['results'][0]

                self.assertTrue(len(result['name']) > 0)
                self.assertTrue("detail" in result)
                self.assertTrue(result['id'] > 0)

                # Special check for the layers typeahead's extra fields
                if url == layers_url:
                    self.assertTrue(len(result['layerdetailurl']) > 0)
                    self.assertTrue(len(result['vcs_url']) > 0)
                    self.assertTrue(len(result['vcs_reference']) > 0)
                # Special check for project typeahead extra fields
                elif url == prj_url:
                    self.assertTrue(len(result['projectPageUrl']) > 0)

                return True

            return False

        import string

        for url in urls:
            results = False

            for typeing in list(string.ascii_letters):
                response = self.client.get(url, {'search': typeing})
                results = basic_reponse_check(response, url)
                if results:
                    break

            # After "typeing" the alpabet we should have result true
            # from each of the urls
            self.assertTrue(results)

    def test_xhr_import_layer(self):
        """Test xhr_importlayer API"""
        #Test for importing an already existing layer
        args = {'vcs_url' : "git://git.example.com/test",
                'name' : "base-layer",
                'git_ref': "c12b9596afd236116b25ce26dbe0d793de9dc7ce",
                'project_id': 1, 'dir_path' : "/path/in/repository"}
        response = self.client.post(reverse('xhr_importlayer'), args)
        data = json.loads(response.content)
        self.assertEqual(response.status_code, 200)
        self.assertNotEqual(data["error"], "ok")

        #Test to verify import of a layer successful
        args['name'] = "meta-oe"
        response = self.client.post(reverse('xhr_importlayer'), args)
        data = json.loads(response.content)
        self.assertTrue(data["error"], "ok")

        #Test for html tag in the data
        args['<'] = "testing html tag"
        response = self.client.post(reverse('xhr_importlayer'), args)
        data = json.loads(response.content)
        self.assertNotEqual(data["error"], "ok")

        #Empty data passed
        args = {}
        response = self.client.post(reverse('xhr_importlayer'), args)
        data = json.loads(response.content)
        self.assertNotEqual(data["error"], "ok")

    def test_custom_ok(self):
        """Test successful return from ReST API xhr_customrecipe"""
        url = reverse('xhr_customrecipe')
        params = {'name': 'custom', 'project': self.project.id,
                  'base': self.recipe1.id}
        response = self.client.post(url, params)
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.content)
        self.assertEqual(data['error'], 'ok')
        self.assertTrue('url' in data)
        # get recipe from the database
        recipe = CustomImageRecipe.objects.get(project=self.project,
                                               name=params['name'])
        args = (self.project.id, recipe.id,)
        self.assertEqual(reverse('customrecipe', args=args), data['url'])

    def test_custom_incomplete_params(self):
        """Test not passing all required parameters to xhr_customrecipe"""
        url = reverse('xhr_customrecipe')
        for params in [{}, {'name': 'custom'},
                       {'name': 'custom', 'project': self.project.id}]:
            response = self.client.post(url, params)
            self.assertEqual(response.status_code, 200)
            data = json.loads(response.content)
            self.assertNotEqual(data["error"], "ok")

    def test_xhr_custom_wrong_project(self):
        """Test passing wrong project id to xhr_customrecipe"""
        url = reverse('xhr_customrecipe')
        params = {'name': 'custom', 'project': 0, "base": self.recipe1.id}
        response = self.client.post(url, params)
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.content)
        self.assertNotEqual(data["error"], "ok")

    def test_xhr_custom_wrong_base(self):
        """Test passing wrong base recipe id to xhr_customrecipe"""
        url = reverse('xhr_customrecipe')
        params = {'name': 'custom', 'project': self.project.id, "base": 0}
        response = self.client.post(url, params)
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.content)
        self.assertNotEqual(data["error"], "ok")

    def test_xhr_custom_details(self):
        """Test getting custom recipe details"""
        name = "custom recipe"
        url = reverse('xhr_customrecipe_id', args=(self.customr.id,))
        response = self.client.get(url)
        self.assertEqual(response.status_code, 200)
        expected = {"error": "ok",
                    "info": {'id': self.customr.id,
                             'name': name,
                             'base_recipe_id': self.recipe1.id,
                             'project_id': self.project.id,
                            }
                   }
        self.assertEqual(json.loads(response.content), expected)

    def test_xhr_custom_del(self):
        """Test deleting custom recipe"""
        name = "to be deleted"
        recipe = CustomImageRecipe.objects.create(\
                     name=name, project=self.project,
                     base_recipe=self.recipe1)
        url = reverse('xhr_customrecipe_id', args=(recipe.id,))
        response = self.client.delete(url)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content), {"error": "ok"})
        # try to delete not-existent recipe
        url = reverse('xhr_customrecipe_id', args=(recipe.id,))
        response = self.client.delete(url)
        self.assertEqual(response.status_code, 200)
        self.assertNotEqual(json.loads(response.content)["error"], "ok")

    def test_xhr_custom_packages(self):
        """Test adding and deleting package to a custom recipe"""
        url = reverse('xhr_customrecipe_packages',
                      args=(self.customr.id, self.package.id))
        # add self.package1 to recipe
        response = self.client.put(url)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content), {"error": "ok"})
        self.assertEqual(self.customr.packages.all()[0].id, self.package.id)
        # delete it
        response = self.client.delete(url)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content), {"error": "ok"})
        self.assertFalse(self.customr.packages.all())
        # delete it again to test error condition
        response = self.client.delete(url)
        self.assertEqual(response.status_code, 200)
        self.assertNotEqual(json.loads(response.content)["error"], "ok")

    def test_xhr_custom_packages_err(self):
        """Test error conditions of xhr_customrecipe_packages"""
        # test calls with wrong recipe id and wrong package id
        for args in [(0, self.package.id), (self.customr.id, 0)]:
            url = reverse('xhr_customrecipe_packages', args=args)
            # test put and delete methods
            for method in (self.client.put, self.client.delete):
                response = method(url)
                self.assertEqual(response.status_code, 200)
                self.assertNotEqual(json.loads(response.content),
                                    {"error": "ok"})

    def test_software_recipes_table(self):
        """Test structure returned for Software RecipesTable"""
        table = SoftwareRecipesTable()
        request = RequestFactory().get('/foo/', {'format': 'json'})
        response = table.get(request, pid=self.project.id)
        data = json.loads(response.content)

        rows = data['rows']
        row1 = next(x for x in rows if x['name'] == self.recipe1.name)
        row2 = next(x for x in rows if x['name'] == self.recipe2.name)

        self.assertEqual(response.status_code, 200, 'should be 200 OK status')
        self.assertEqual(len(rows), 2, 'should be 2 recipes')

        # check other columns have been populated correctly
        self.assertEqual(row1['name'], self.recipe1.name)
        self.assertEqual(row1['version'], self.recipe1.version)
        self.assertEqual(row1['get_description_or_summary'],
                         self.recipe1.description)
        self.assertEqual(row1['layer_version__layer__name'],
                         self.recipe1.layer_version.layer.name)
        self.assertEqual(row2['name'], self.recipe2.name)
        self.assertEqual(row2['version'], self.recipe2.version)
        self.assertEqual(row2['get_description_or_summary'],
                         self.recipe2.description)
        self.assertEqual(row2['layer_version__layer__name'],
                         self.recipe2.layer_version.layer.name)

class LandingPageTests(TestCase):
    """ Tests for redirects on the landing page """
    # disable bogus pylint message error:
    # "Instance of 'WSGIRequest' has no 'url' member (no-member)"
    # (see https://github.com/landscapeio/pylint-django/issues/42)
    # pylint: disable=E1103

    LANDING_PAGE_TITLE = 'This is Toaster'

    def setUp(self):
        """ Add default project manually """
        self.project = Project.objects.create_project('foo', None)
        self.project.is_default = True
        self.project.save()

    def test_only_default_project(self):
        """
        No projects except default
        => get the landing page
        """
        response = self.client.get(reverse('landing'))
        self.assertTrue(self.LANDING_PAGE_TITLE in response.content)

    def test_default_project_has_build(self):
        """
        Default project has a build, no other projects
        => get the builds page
        """
        now = timezone.now()
        build = Build.objects.create(project=self.project,
                                     started_on=now,
                                     completed_on=now)
        build.save()

        response = self.client.get(reverse('landing'))
        self.assertEqual(response.status_code, 302,
                         'response should be a redirect')
        self.assertTrue('/builds' in response.url,
                        'should redirect to builds')

    def test_user_project_exists(self):
        """
        User has added a project (without builds)
        => get the projects page
        """
        user_project = Project.objects.create_project('foo', None)
        user_project.save()

        response = self.client.get(reverse('landing'))
        self.assertEqual(response.status_code, 302,
                         'response should be a redirect')
        self.assertTrue('/projects' in response.url,
                        'should redirect to projects')

    def test_user_project_has_build(self):
        """
        User has added a project (with builds)
        => get the builds page
        """
        user_project = Project.objects.create_project('foo', None)
        user_project.save()

        now = timezone.now()
        build = Build.objects.create(project=user_project,
                                     started_on=now,
                                     completed_on=now)
        build.save()

        response = self.client.get(reverse('landing'))
        self.assertEqual(response.status_code, 302,
                         'response should be a redirect')
        self.assertTrue('/builds' in response.url,
                        'should redirect to builds')

class AllProjectsPageTests(TestCase):
    """ Tests for projects page /projects/ """

    MACHINE_NAME = 'delorean'

    def setUp(self):
        """ Add default project manually """
        project = Project.objects.create_project(CLI_BUILDS_PROJECT_NAME, None)
        self.default_project = project
        self.default_project.is_default = True
        self.default_project.save()

        # this project is only set for some of the tests
        self.project = None

        self.release = None

    def _add_build_to_default_project(self):
        """ Add a build to the default project (not used in all tests) """
        now = timezone.now()
        build = Build.objects.create(project=self.default_project,
                                     started_on=now,
                                     completed_on=now)
        build.save()

    def _add_non_default_project(self):
        """ Add another project """
        bbv = BitbakeVersion.objects.create(name="test bbv", giturl="/tmp/",
                                            branch="master", dirpath="")
        self.release = Release.objects.create(name="test release",
                                              branch_name="master",
                                              bitbake_version=bbv)
        self.project = Project.objects.create_project(PROJECT_NAME, self.release)
        self.project.is_default = False
        self.project.save()

        # fake the MACHINE variable
        project_var = ProjectVariable.objects.create(project=self.project,
                                                     name='MACHINE',
                                                     value=self.MACHINE_NAME)
        project_var.save()

    def test_default_project_hidden(self):
        """ The default project should be hidden if it has no builds """
        params = {"count": 10, "orderby": "updated:-", "page": 1}
        response = self.client.get(reverse('all-projects'), params)

        self.assertTrue(not('tr class="data"' in response.content),
                        'should be no project rows in the page')
        self.assertTrue(not(CLI_BUILDS_PROJECT_NAME in response.content),
                        'default project "cli builds" should not be in page')

    def test_default_project_has_build(self):
        """ The default project should be shown if it has builds """
        self._add_build_to_default_project()

        params = {"count": 10, "orderby": "updated:-", "page": 1}
        response = self.client.get(reverse('all-projects'), params)

        self.assertTrue('tr class="data"' in response.content,
                        'should be a project row in the page')
        self.assertTrue(CLI_BUILDS_PROJECT_NAME in response.content,
                        'default project "cli builds" should be in page')

    def test_default_project_release(self):
        """
        The release for the default project should display as
        'Not applicable'
        """
        # need a build, otherwise project doesn't display at all
        self._add_build_to_default_project()

        # another project to test, which should show release
        self._add_non_default_project()

        response = self.client.get(reverse('all-projects'), follow=True)
        soup = BeautifulSoup(response.content)

        # check the release cell for the default project
        attrs = {'data-project': str(self.default_project.id)}
        rows = soup.find_all('tr', attrs=attrs)
        self.assertEqual(len(rows), 1, 'should be one row for default project')
        cells = rows[0].find_all('td', attrs={'data-project-field': 'release'})
        self.assertEqual(len(cells), 1, 'should be one release cell')
        text = cells[0].select('span.muted')[0].text
        self.assertEqual(text, 'Not applicable',
                         'release should be not applicable for default project')

        # check the link in the release cell for the other project
        attrs = {'data-project': str(self.project.id)}
        rows = soup.find_all('tr', attrs=attrs)
        cells = rows[0].find_all('td', attrs={'data-project-field': 'release'})
        text = cells[0].select('a')[0].text
        self.assertEqual(text, self.release.name,
                         'release name should be shown for non-default project')

    def test_default_project_machine(self):
        """
        The machine for the default project should display as
        'Not applicable'
        """
        # need a build, otherwise project doesn't display at all
        self._add_build_to_default_project()

        # another project to test, which should show machine
        self._add_non_default_project()

        response = self.client.get(reverse('all-projects'), follow=True)
        soup = BeautifulSoup(response.content)

        # check the machine cell for the default project
        attrs = {'data-project': str(self.default_project.id)}
        rows = soup.find_all('tr', attrs=attrs)
        self.assertEqual(len(rows), 1, 'should be one row for default project')
        cells = rows[0].find_all('td', attrs={'data-project-field': 'machine'})
        self.assertEqual(len(cells), 1, 'should be one machine cell')
        text = cells[0].select('span.muted')[0].text
        self.assertEqual(text, 'Not applicable',
                         'machine should be not applicable for default project')

        # check the link in the machine cell for the other project
        attrs = {'data-project': str(self.project.id)}
        rows = soup.find_all('tr', attrs=attrs)
        cells = rows[0].find_all('td', attrs={'data-project-field': 'machine'})
        text = cells[0].select('a')[0].text
        self.assertEqual(text, self.MACHINE_NAME,
                         'machine name should be shown for non-default project')

    def test_project_page_links(self):
        """
        Test that links for the default project point to the builds
        page /projects/X/builds for that project, and that links for
        other projects point to their configuration pages /projects/X/
        """

        # need a build, otherwise project doesn't display at all
        self._add_build_to_default_project()

        # another project to test, which should show machine
        self._add_non_default_project()

        response = self.client.get(reverse('all-projects'), follow=True)
        soup = BeautifulSoup(response.content)

        # link for default project
        row = soup.find('tr', attrs={'data-project': self.default_project.id})
        cell = row.find('td', attrs={'data-project-field': 'name'})
        expected_url = reverse('projectbuilds', args=(self.default_project.id,))
        self.assertEqual(cell.find('a')['href'], expected_url,
                         'link on default project name should point to builds')

        # link for other project
        row = soup.find('tr', attrs={'data-project': self.project.id})
        cell = row.find('td', attrs={'data-project-field': 'name'})
        expected_url = reverse('project', args=(self.project.id,))
        self.assertEqual(cell.find('a')['href'], expected_url,
                         'link on project name should point to configuration')

class ProjectBuildsPageTests(TestCase):
    """ Test data at /project/X/builds is displayed correctly """

    def setUp(self):
        bbv = BitbakeVersion.objects.create(name="bbv1", giturl="/tmp/",
                                            branch="master", dirpath="")
        release = Release.objects.create(name="release1",
                                         bitbake_version=bbv)
        self.project1 = Project.objects.create_project(name=PROJECT_NAME,
                                                       release=release)
        self.project1.save()

        self.project2 = Project.objects.create_project(name=PROJECT_NAME,
                                                       release=release)
        self.project2.save()

        self.default_project = Project.objects.create_project(
            name=CLI_BUILDS_PROJECT_NAME,
            release=release
        )
        self.default_project.is_default = True
        self.default_project.save()

        # parameters for builds to associate with the projects
        now = timezone.now()

        self.project1_build_success = {
            "project": self.project1,
            "started_on": now,
            "completed_on": now,
            "outcome": Build.SUCCEEDED
        }

        self.project1_build_in_progress = {
            "project": self.project1,
            "started_on": now,
            "completed_on": now,
            "outcome": Build.IN_PROGRESS
        }

        self.project2_build_success = {
            "project": self.project2,
            "started_on": now,
            "completed_on": now,
            "outcome": Build.SUCCEEDED
        }

        self.project2_build_in_progress = {
            "project": self.project2,
            "started_on": now,
            "completed_on": now,
            "outcome": Build.IN_PROGRESS
        }

    def _get_rows_for_project(self, project_id):
        """ Helper to retrieve HTML rows for a project """
        url = reverse("projectbuilds", args=(project_id,))
        response = self.client.get(url, follow=True)
        soup = BeautifulSoup(response.content)
        return soup.select('tr[class="data"]')

    def test_show_builds_for_project(self):
        """ Builds for a project should be displayed """
        Build.objects.create(**self.project1_build_success)
        Build.objects.create(**self.project1_build_success)
        build_rows = self._get_rows_for_project(self.project1.id)
        self.assertEqual(len(build_rows), 2)

    def test_show_builds_project_only(self):
        """ Builds for other projects should be excluded """
        Build.objects.create(**self.project1_build_success)
        Build.objects.create(**self.project1_build_success)
        Build.objects.create(**self.project1_build_success)

        # shouldn't see these two
        Build.objects.create(**self.project2_build_success)
        Build.objects.create(**self.project2_build_in_progress)

        build_rows = self._get_rows_for_project(self.project1.id)
        self.assertEqual(len(build_rows), 3)

    def test_builds_exclude_in_progress(self):
        """ "in progress" builds should not be shown """
        Build.objects.create(**self.project1_build_success)
        Build.objects.create(**self.project1_build_success)

        # shouldn't see this one
        Build.objects.create(**self.project1_build_in_progress)

        # shouldn't see these two either, as they belong to a different project
        Build.objects.create(**self.project2_build_success)
        Build.objects.create(**self.project2_build_in_progress)

        build_rows = self._get_rows_for_project(self.project1.id)
        self.assertEqual(len(build_rows), 2)

    def test_tasks_in_projectbuilds(self):
        """ Task should be shown as suffix on build name """
        build = Build.objects.create(**self.project1_build_success)
        Target.objects.create(build=build, target='bash', task='clean')
        url = reverse("projectbuilds", args=(self.project1.id,))
        response = self.client.get(url, follow=True)
        result = re.findall('^ +bash:clean$', response.content, re.MULTILINE)
        self.assertEqual(len(result), 2)

    def test_cli_builds_hides_tabs(self):
        """
        Display for command line builds should hide tabs;
        note that the latest builds section is already tested in
        AllBuildsPageTests, as the template is the same
        """
        url = reverse("projectbuilds", args=(self.default_project.id,))
        response = self.client.get(url, follow=True)
        soup = BeautifulSoup(response.content)
        tabs = soup.select('#project-topbar')
        self.assertEqual(len(tabs), 0,
                         'should be no top bar shown for command line builds')

    def test_non_cli_builds_has_tabs(self):
        """
        Non-command-line builds projects should show the tabs
        """
        url = reverse("projectbuilds", args=(self.project1.id,))
        response = self.client.get(url, follow=True)
        soup = BeautifulSoup(response.content)
        tabs = soup.select('#project-topbar')
        self.assertEqual(len(tabs), 1,
                         'should be a top bar shown for non-command-line builds')

class AllBuildsPageTests(TestCase):
    """ Tests for all builds page /builds/ """

    def setUp(self):
        bbv = BitbakeVersion.objects.create(name="bbv1", giturl="/tmp/",
                                            branch="master", dirpath="")
        release = Release.objects.create(name="release1",
                                         bitbake_version=bbv)
        self.project1 = Project.objects.create_project(name=PROJECT_NAME,
                                                       release=release)
        self.default_project = Project.objects.create_project(
            name=CLI_BUILDS_PROJECT_NAME,
            release=release
        )
        self.default_project.is_default = True
        self.default_project.save()

        # parameters for builds to associate with the projects
        now = timezone.now()

        self.project1_build_success = {
            "project": self.project1,
            "started_on": now,
            "completed_on": now,
            "outcome": Build.SUCCEEDED
        }

        self.default_project_build_success = {
            "project": self.default_project,
            "started_on": now,
            "completed_on": now,
            "outcome": Build.SUCCEEDED
        }

    def test_show_tasks_in_allbuilds(self):
        """ Task should be shown as suffix on build name """
        build = Build.objects.create(**self.project1_build_success)
        Target.objects.create(build=build, target='bash', task='clean')
        url = reverse('all-builds')
        response = self.client.get(url, follow=True)
        result = re.findall('bash:clean', response.content, re.MULTILINE)
        self.assertEqual(len(result), 3)

    def test_no_run_again_for_cli_build(self):
        """ "Run again" button should not be shown for command-line builds """
        build = Build.objects.create(**self.default_project_build_success)
        url = reverse('all-builds')
        response = self.client.get(url, follow=True)
        soup = BeautifulSoup(response.content)

        attrs = {'data-latest-build-result': build.id}
        result = soup.find('div', attrs=attrs)

        # shouldn't see a run again button for command-line builds
        run_again_button = result.select('button')
        self.assertEqual(len(run_again_button), 0)

        # should see a help icon for command-line builds
        help_icon = result.select('i.get-help-green')
        self.assertEqual(len(help_icon), 1)

    def test_tooltips_on_project_name(self):
        """
        A tooltip should be present next to the command line
        builds project name in the all builds page, but not for
        other projects
        """
        build1 = Build.objects.create(**self.project1_build_success)
        default_build = Build.objects.create(**self.default_project_build_success)

        url = reverse('all-builds')
        response = self.client.get(url, follow=True)
        soup = BeautifulSoup(response.content)

        # no help icon on non-default project name
        result = soup.find('tr', attrs={'data-table-build-result': build1.id})
        name = result.select('td.project-name')[0]
        icons = name.select('i.get-help')
        self.assertEqual(len(icons), 0,
                         'should not be a help icon for non-cli builds name')

        # help icon on default project name
        result = soup.find('tr', attrs={'data-table-build-result': default_build.id})
        name = result.select('td.project-name')[0]
        icons = name.select('i.get-help')
        self.assertEqual(len(icons), 1,
                         'should be a help icon for cli builds name')

class ProjectPageTests(TestCase):
    """ Test project data at /project/X/ is displayed correctly """
    CLI_BUILDS_PROJECT_NAME = 'Command line builds'

    def test_command_line_builds_in_progress(self):
        """
        In progress builds should not cause an error to be thrown
        when navigating to "command line builds" project page;
        see https://bugzilla.yoctoproject.org/show_bug.cgi?id=8277
        """

        # add the "command line builds" default project; this mirrors what
        # we do in migration 0026_set_default_project.py
        default_project = Project.objects.create_project(self.CLI_BUILDS_PROJECT_NAME, None)
        default_project.is_default = True
        default_project.save()

        # add an "in progress" build for the default project
        now = timezone.now()
        build = Build.objects.create(project=default_project,
                                     started_on=now,
                                     completed_on=now,
                                     outcome=Build.IN_PROGRESS)

        # navigate to the project page for the default project
        url = reverse("project", args=(default_project.id,))
        response = self.client.get(url, follow=True)

        self.assertEqual(response.status_code, 200)

class BuildDashboardTests(TestCase):
    """ Tests for the build dashboard /build/X """

    def setUp(self):
        bbv = BitbakeVersion.objects.create(name="bbv1", giturl="/tmp/",
                                            branch="master", dirpath="")
        release = Release.objects.create(name="release1",
                                         bitbake_version=bbv)
        project = Project.objects.create_project(name=PROJECT_NAME,
                                                 release=release)

        now = timezone.now()

        self.build1 = Build.objects.create(project=project,
                                           started_on=now,
                                           completed_on=now)

        # exception
        msg1 = 'an exception was thrown'
        self.exception_message = LogMessage.objects.create(
            build=self.build1,
            level=LogMessage.EXCEPTION,
            message=msg1
        )

        # critical
        msg2 = 'a critical error occurred'
        self.critical_message = LogMessage.objects.create(
            build=self.build1,
            level=LogMessage.CRITICAL,
            message=msg2
        )

    def _get_build_dashboard_errors(self):
        """
        Get a list of HTML fragments representing the errors on the
        build dashboard
        """
        url = reverse('builddashboard', args=(self.build1.id,))
        response = self.client.get(url)
        soup = BeautifulSoup(response.content)
        return soup.select('#errors div.alert-error')

    def _check_for_log_message(self, log_message):
        """
        Check whether the LogMessage instance <log_message> is
        represented as an HTML error in the build dashboard page
        """
        errors = self._get_build_dashboard_errors()
        self.assertEqual(len(errors), 2)

        expected_text = log_message.message
        expected_id = str(log_message.id)

        found = False
        for error in errors:
            error_text = error.find('pre').text
            text_matches = (error_text == expected_text)

            error_id = error['data-error']
            id_matches = (error_id == expected_id)

            if text_matches and id_matches:
                found = True
                break

        template_vars = (expected_text, error_text,
                         expected_id, error_id)
        assertion_error_msg = 'exception not found as error: ' \
            'expected text "%s" and got "%s"; ' \
            'expected ID %s and got %s' % template_vars
        self.assertTrue(found, assertion_error_msg)

    def test_exceptions_show_as_errors(self):
        """
        LogMessages with level EXCEPTION should display in the errors
        section of the page
        """
        self._check_for_log_message(self.exception_message)

    def test_criticals_show_as_errors(self):
        """
        LogMessages with level CRITICAL should display in the errors
        section of the page
        """
        self._check_for_log_message(self.critical_message)
