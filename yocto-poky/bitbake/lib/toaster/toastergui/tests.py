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
from django.core.urlresolvers import reverse
from django.utils import timezone
from orm.models import Project, Release, BitbakeVersion, ProjectTarget
from orm.models import ReleaseLayerSourcePriority, LayerSource, Layer, Build
from orm.models import Layer_Version, Recipe, Machine, ProjectLayer
import json
from bs4 import BeautifulSoup

PROJECT_NAME = "test project"

class ViewTests(TestCase):
    """Tests to verify view APIs."""

    def setUp(self):
        bbv = BitbakeVersion.objects.create(name="test bbv", giturl="/tmp/",
                                            branch="master", dirpath="")
        release = Release.objects.create(name="test release",
                                         bitbake_version=bbv)
        self.project = Project.objects.create_project(name=PROJECT_NAME,
                                                      release=release)
        layersrc = LayerSource.objects.create(sourcetype=LayerSource.TYPE_IMPORTED)
        self.priority = ReleaseLayerSourcePriority.objects.create(release=release,
                                                                  layer_source=layersrc)
        layer = Layer.objects.create(name="base-layer", layer_source=layersrc,
                                     vcs_url="/tmp/")

        lver = Layer_Version.objects.create(layer=layer, project=self.project,
                                            layer_source=layersrc, commit="master")

        Recipe.objects.create(layer_source=layersrc, name="base-recipe",
                              version="1.2", summary="one recipe",
                              description="recipe", layer_version=lver)

        Machine.objects.create(layer_version=lver, name="wisk",
                               description="wisking machine")

        ProjectLayer.objects.create(project=self.project, layercommit=lver)

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

class ProjectsPageTests(TestCase):
    """ Tests for projects page """

    PROJECT_NAME = 'cli builds'

    def setUp(self):
        """ Add default project manually """
        project = Project.objects.create_project(self.PROJECT_NAME, None)
        self.default_project = project
        self.default_project.is_default = True
        self.default_project.save()

    def test_default_project_hidden(self):
        """ The default project should be hidden if it has no builds """
        params = {"count": 10, "orderby": "updated:-", "page": 1}
        response = self.client.get(reverse('all-projects'), params)

        self.assertTrue(not('tr class="data"' in response.content),
                        'should be no project rows in the page')
        self.assertTrue(not(self.PROJECT_NAME in response.content),
                        'default project "cli builds" should not be in page')

    def test_default_project_has_build(self):
        """ The default project should be shown if it has builds """
        now = timezone.now()
        build = Build.objects.create(project=self.default_project,
                                     started_on=now,
                                     completed_on=now)
        build.save()

        params = {"count": 10, "orderby": "updated:-", "page": 1}
        response = self.client.get(reverse('all-projects'), params)

        self.assertTrue('tr class="data"' in response.content,
                        'should be a project row in the page')
        self.assertTrue(self.PROJECT_NAME in response.content,
                        'default project "cli builds" should be in page')

class ProjectBuildsDisplayTest(TestCase):
    """ Test data at /project/X/builds is displayed correctly """

    def setUp(self):
        bbv = BitbakeVersion.objects.create(name="bbv1", giturl="/tmp/",
                                            branch="master", dirpath="")
        release = Release.objects.create(name="release1",
                                         bitbake_version=bbv)
        self.project1 = Project.objects.create_project(name=PROJECT_NAME,
                                                       release=release)
        self.project2 = Project.objects.create_project(name=PROJECT_NAME,
                                                       release=release)

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
        url = reverse("projectbuilds", args=(project_id,))
        response = self.client.get(url, follow=True)
        soup = BeautifulSoup(response.content)
        return soup.select('tr[class="data"]')

    def test_show_builds_for_project(self):
        """ Builds for a project should be displayed """
        build1a = Build.objects.create(**self.project1_build_success)
        build1b = Build.objects.create(**self.project1_build_success)
        build_rows = self._get_rows_for_project(self.project1.id)
        self.assertEqual(len(build_rows), 2)

    def test_show_builds_for_project_only(self):
        """ Builds for other projects should be excluded """
        build1a = Build.objects.create(**self.project1_build_success)
        build1b = Build.objects.create(**self.project1_build_success)
        build1c = Build.objects.create(**self.project1_build_success)

        # shouldn't see these two
        build2a = Build.objects.create(**self.project2_build_success)
        build2b = Build.objects.create(**self.project2_build_in_progress)

        build_rows = self._get_rows_for_project(self.project1.id)
        self.assertEqual(len(build_rows), 3)

    def test_show_builds_exclude_in_progress(self):
        """ "in progress" builds should not be shown """
        build1a = Build.objects.create(**self.project1_build_success)
        build1b = Build.objects.create(**self.project1_build_success)

        # shouldn't see this one
        build1c = Build.objects.create(**self.project1_build_in_progress)

        # shouldn't see these two either, as they belong to a different project
        build2a = Build.objects.create(**self.project2_build_success)
        build2b = Build.objects.create(**self.project2_build_in_progress)

        build_rows = self._get_rows_for_project(self.project1.id)
        self.assertEqual(len(build_rows), 2)