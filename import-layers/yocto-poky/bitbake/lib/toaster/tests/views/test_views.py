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
from django.db.models import Q

from orm.models import Project, Package
from orm.models import Layer_Version, Recipe
from orm.models import CustomImageRecipe
from orm.models import CustomImagePackage

import inspect
import toastergui

from toastergui.tables import SoftwareRecipesTable
import json
from bs4 import BeautifulSoup
import string

PROJECT_NAME = "test project"
PROJECT_NAME2 = "test project 2"
CLI_BUILDS_PROJECT_NAME = 'Command line builds'


class ViewTests(TestCase):
    """Tests to verify view APIs."""

    fixtures = ['toastergui-unittest-data']

    def setUp(self):

        self.project = Project.objects.first()
        self.recipe1 = Recipe.objects.get(pk=2)
        self.customr = CustomImageRecipe.objects.first()
        self.cust_package = CustomImagePackage.objects.first()
        self.package = Package.objects.first()
        self.lver = Layer_Version.objects.first()

    def test_get_base_call_returns_html(self):
        """Basic test for all-projects view"""
        response = self.client.get(reverse('all-projects'), follow=True)
        self.assertEqual(response.status_code, 200)
        self.assertTrue(response['Content-Type'].startswith('text/html'))
        self.assertTemplateUsed(response, "projects-toastertable.html")

    def test_get_json_call_returns_json(self):
        """Test for all projects output in json format"""
        url = reverse('all-projects')
        response = self.client.get(url, {"format": "json"}, follow=True)
        self.assertEqual(response.status_code, 200)
        self.assertTrue(response['Content-Type'].startswith(
            'application/json'))

        data = json.loads(response.content.decode('utf-8'))

        self.assertTrue("error" in data)
        self.assertEqual(data["error"], "ok")
        self.assertTrue("rows" in data)

        name_found = False
        for row in data["rows"]:
            name_found = row['name'].find(self.project.name)

        self.assertTrue(name_found,
                        "project name not found in projects table")

    def test_typeaheads(self):
        """Test typeahead ReST API"""
        layers_url = reverse('xhr_layerstypeahead', args=(self.project.id,))
        prj_url = reverse('xhr_projectstypeahead')

        urls = [layers_url,
                prj_url,
                reverse('xhr_recipestypeahead', args=(self.project.id,)),
                reverse('xhr_machinestypeahead', args=(self.project.id,))]

        def basic_reponse_check(response, url):
            """Check data structure of http response."""
            self.assertEqual(response.status_code, 200)
            self.assertTrue(response['Content-Type'].startswith(
                'application/json'))

            data = json.loads(response.content.decode('utf-8'))

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

    def test_xhr_add_layer(self):
        """Test xhr_add API"""
        # Test for importing an already existing layer
        api_url = reverse('xhr_layer', args=(self.project.id,))

        layer_data = {'vcs_url': "git://git.example.com/test",
                      'name': "base-layer",
                      'git_ref': "c12b9596afd236116b25ce26dbe0d793de9dc7ce",
                      'project_id': self.project.id,
                      'local_source_dir': "",
                      'add_to_project': True,
                      'dir_path': "/path/in/repository"}

        layer_data_json = json.dumps(layer_data)

        response = self.client.put(api_url, layer_data_json)
        data = json.loads(response.content.decode('utf-8'))
        self.assertEqual(response.status_code, 200)
        self.assertEqual(data["error"], "ok")

        self.assertTrue(
            layer_data['name'] in
            self.project.get_all_compatible_layer_versions().values_list(
                'layer__name',
                flat=True),
            "Could not find imported layer in project's all layers list"
        )

        # Empty data passed
        response = self.client.put(api_url, "{}")
        data = json.loads(response.content.decode('utf-8'))
        self.assertNotEqual(data["error"], "ok")

    def test_custom_ok(self):
        """Test successful return from ReST API xhr_customrecipe"""
        url = reverse('xhr_customrecipe')
        params = {'name': 'custom', 'project': self.project.id,
                  'base': self.recipe1.id}
        response = self.client.post(url, params)
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.content.decode('utf-8'))
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
            data = json.loads(response.content.decode('utf-8'))
            self.assertNotEqual(data["error"], "ok")

    def test_xhr_custom_wrong_project(self):
        """Test passing wrong project id to xhr_customrecipe"""
        url = reverse('xhr_customrecipe')
        params = {'name': 'custom', 'project': 0, "base": self.recipe1.id}
        response = self.client.post(url, params)
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.content.decode('utf-8'))
        self.assertNotEqual(data["error"], "ok")

    def test_xhr_custom_wrong_base(self):
        """Test passing wrong base recipe id to xhr_customrecipe"""
        url = reverse('xhr_customrecipe')
        params = {'name': 'custom', 'project': self.project.id, "base": 0}
        response = self.client.post(url, params)
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.content.decode('utf-8'))
        self.assertNotEqual(data["error"], "ok")

    def test_xhr_custom_details(self):
        """Test getting custom recipe details"""
        url = reverse('xhr_customrecipe_id', args=(self.customr.id,))
        response = self.client.get(url)
        self.assertEqual(response.status_code, 200)
        expected = {"error": "ok",
                    "info": {'id': self.customr.id,
                             'name': self.customr.name,
                             'base_recipe_id': self.recipe1.id,
                             'project_id': self.project.id}}
        self.assertEqual(json.loads(response.content.decode('utf-8')),
                         expected)

    def test_xhr_custom_del(self):
        """Test deleting custom recipe"""
        name = "to be deleted"
        recipe = CustomImageRecipe.objects.create(
                     name=name, project=self.project,
                     base_recipe=self.recipe1,
                     file_path="/tmp/testing",
                     layer_version=self.customr.layer_version)
        url = reverse('xhr_customrecipe_id', args=(recipe.id,))
        response = self.client.delete(url)
        self.assertEqual(response.status_code, 200)

        gotoUrl = reverse('projectcustomimages', args=(self.project.pk,))

        self.assertEqual(json.loads(response.content.decode('utf-8')),
                         {"error": "ok",
                          "gotoUrl": gotoUrl})

        # try to delete not-existent recipe
        url = reverse('xhr_customrecipe_id', args=(recipe.id,))
        response = self.client.delete(url)
        self.assertEqual(response.status_code, 200)
        self.assertNotEqual(json.loads(
            response.content.decode('utf-8'))["error"], "ok")

    def test_xhr_custom_packages(self):
        """Test adding and deleting package to a custom recipe"""
        # add self.package to recipe
        response = self.client.put(reverse('xhr_customrecipe_packages',
                                           args=(self.customr.id,
                                                 self.cust_package.id)))

        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content.decode('utf-8')),
                         {"error": "ok"})
        self.assertEqual(self.customr.appends_set.first().name,
                         self.cust_package.name)
        # delete it
        to_delete = self.customr.appends_set.first().pk
        del_url = reverse('xhr_customrecipe_packages',
                          args=(self.customr.id, to_delete))

        response = self.client.delete(del_url)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content.decode('utf-8')),
                         {"error": "ok"})
        all_packages = self.customr.get_all_packages().values_list('pk',
                                                                   flat=True)

        self.assertFalse(to_delete in all_packages)
        # delete invalid package to test error condition
        del_url = reverse('xhr_customrecipe_packages',
                          args=(self.customr.id,
                                99999))

        response = self.client.delete(del_url)
        self.assertEqual(response.status_code, 200)
        self.assertNotEqual(json.loads(
            response.content.decode('utf-8'))["error"], "ok")

    def test_xhr_custom_packages_err(self):
        """Test error conditions of xhr_customrecipe_packages"""
        # test calls with wrong recipe id and wrong package id
        for args in [(0, self.package.id), (self.customr.id, 0)]:
            url = reverse('xhr_customrecipe_packages', args=args)
            # test put and delete methods
            for method in (self.client.put, self.client.delete):
                response = method(url)
                self.assertEqual(response.status_code, 200)
                self.assertNotEqual(json.loads(
                    response.content.decode('utf-8')),
                    {"error": "ok"})

    def test_download_custom_recipe(self):
        """Download the recipe file generated for the custom image"""

        # Create a dummy recipe file for the custom image generation to read
        open("/tmp/a_recipe.bb", 'a').close()
        response = self.client.get(reverse('customrecipedownload',
                                           args=(self.project.id,
                                                 self.customr.id)))

        self.assertEqual(response.status_code, 200)

    def test_software_recipes_table(self):
        """Test structure returned for Software RecipesTable"""
        table = SoftwareRecipesTable()
        request = RequestFactory().get('/foo/', {'format': 'json'})
        response = table.get(request, pid=self.project.id)
        data = json.loads(response.content.decode('utf-8'))

        recipes = Recipe.objects.filter(Q(is_image=False))
        self.assertTrue(len(recipes) > 1,
                        "Need more than one software recipe to test "
                        "SoftwareRecipesTable")

        recipe1 = recipes[0]
        recipe2 = recipes[1]

        rows = data['rows']
        row1 = next(x for x in rows if x['name'] == recipe1.name)
        row2 = next(x for x in rows if x['name'] == recipe2.name)

        self.assertEqual(response.status_code, 200, 'should be 200 OK status')

        # check other columns have been populated correctly
        self.assertTrue(recipe1.name in row1['name'])
        self.assertTrue(recipe1.version in row1['version'])
        self.assertTrue(recipe1.description in
                        row1['get_description_or_summary'])

        self.assertTrue(recipe1.layer_version.layer.name in
                        row1['layer_version__layer__name'])

        self.assertTrue(recipe2.name in row2['name'])
        self.assertTrue(recipe2.version in row2['version'])
        self.assertTrue(recipe2.description in
                        row2['get_description_or_summary'])

        self.assertTrue(recipe2.layer_version.layer.name in
                        row2['layer_version__layer__name'])

    def test_toaster_tables(self):
        """Test all ToasterTables instances"""

        def get_data(table, options={}):
            """Send a request and parse the json response"""
            options['format'] = "json"
            options['nocache'] = "true"
            request = RequestFactory().get('/', options)

            # This is the image recipe needed for a package list for
            # PackagesTable do this here to throw a non exist exception
            image_recipe = Recipe.objects.get(pk=4)

            # Add any kwargs that are needed by any of the possible tables
            args = {'pid': self.project.id,
                    'layerid': self.lver.pk,
                    'recipeid': self.recipe1.pk,
                    'recipe_id': image_recipe.pk,
                    'custrecipeid': self.customr.pk,
                    'build_id': 1,
                    'target_id': 1}

            response = table.get(request, **args)
            return json.loads(response.content.decode('utf-8'))

        def get_text_from_td(td):
            """If we have html in the td then extract the text portion"""
            # just so we don't waste time parsing non html
            if "<" not in td:
                ret = td
            else:
                ret = BeautifulSoup(td, "html.parser").text

            if len(ret):
                return "0"
            else:
                return ret

        # Get a list of classes in tables module
        tables = inspect.getmembers(toastergui.tables, inspect.isclass)
        tables.extend(inspect.getmembers(toastergui.buildtables,
                                         inspect.isclass))

        for name, table_cls in tables:
            # Filter out the non ToasterTables from the tables module
            if not issubclass(table_cls, toastergui.widgets.ToasterTable) or \
                table_cls == toastergui.widgets.ToasterTable or \
               'Mixin' in name:
                continue

            # Get the table data without any options, this also does the
            # initialisation of the table i.e. setup_columns,
            # setup_filters and setup_queryset that we can use later
            table = table_cls()
            all_data = get_data(table)

            self.assertTrue(len(all_data['rows']) > 1,
                            "Cannot test on a %s table with < 1 row" % name)

            if table.default_orderby:
                row_one = get_text_from_td(
                    all_data['rows'][0][table.default_orderby.strip("-")])
                row_two = get_text_from_td(
                    all_data['rows'][1][table.default_orderby.strip("-")])

                if '-' in table.default_orderby:
                    self.assertTrue(row_one >= row_two,
                                    "Default ordering not working on %s"
                                    " '%s' should be >= '%s'" %
                                    (name, row_one, row_two))
                else:
                    self.assertTrue(row_one <= row_two,
                                    "Default ordering not working on %s"
                                    " '%s' should be <= '%s'" %
                                    (name, row_one, row_two))

            # Test the column ordering and filtering functionality
            for column in table.columns:
                if column['orderable']:
                    # If a column is orderable test it in both order
                    # directions ordering on the columns field_name
                    ascending = get_data(table_cls(),
                                         {"orderby": column['field_name']})

                    row_one = get_text_from_td(
                        ascending['rows'][0][column['field_name']])
                    row_two = get_text_from_td(
                        ascending['rows'][1][column['field_name']])

                    self.assertTrue(row_one <= row_two,
                                    "Ascending sort applied but row 0: \"%s\""
                                    " is less than row 1: \"%s\" "
                                    "%s %s " %
                                    (row_one, row_two,
                                     column['field_name'], name))

                    descending = get_data(table_cls(),
                                          {"orderby":
                                           '-'+column['field_name']})

                    row_one = get_text_from_td(
                        descending['rows'][0][column['field_name']])
                    row_two = get_text_from_td(
                        descending['rows'][1][column['field_name']])

                    self.assertTrue(row_one >= row_two,
                                    "Descending sort applied but row 0: %s"
                                    "is greater than row 1: %s"
                                    "field %s table %s" %
                                    (row_one,
                                     row_two,
                                     column['field_name'], name))

                    # If the two start rows are the same we haven't actually
                    # changed the order
                    self.assertNotEqual(ascending['rows'][0],
                                        descending['rows'][0],
                                        "An orderby %s has not changed the "
                                        "order of the data in table %s" %
                                        (column['field_name'], name))

                if column['filter_name']:
                    # If a filter is available for the column get the filter
                    # info. This contains what filter actions are defined.
                    filter_info = get_data(table_cls(),
                                           {"cmd": "filterinfo",
                                            "name": column['filter_name']})
                    self.assertTrue(len(filter_info['filter_actions']) > 0,
                                    "Filter %s was defined but no actions "
                                    "added to it" % column['filter_name'])

                    for filter_action in filter_info['filter_actions']:
                        # filter string to pass as the option
                        # This is the name of the filter:action
                        # e.g. project_filter:not_in_project
                        filter_string = "%s:%s" % (
                            column['filter_name'],
                            filter_action['action_name'])
                        # Now get the data with the filter applied
                        filtered_data = get_data(table_cls(),
                                                 {"filter": filter_string})

                        # date range filter actions can't specify the
                        # number of results they return, so their count is 0
                        if filter_action['count'] is not None:
                            self.assertEqual(
                                len(filtered_data['rows']),
                                int(filter_action['count']),
                                "We added a table filter for %s but "
                                "the number of rows returned was not "
                                "what the filter info said there "
                                "would be" % name)

            # Test search functionality on the table
            something_found = False
            for search in list(string.ascii_letters):
                search_data = get_data(table_cls(), {'search': search})

                if len(search_data['rows']) > 0:
                    something_found = True
                    break

            self.assertTrue(something_found,
                            "We went through the whole alphabet and nothing"
                            " was found for the search of table %s" % name)

            # Test the limit functionality on the table
            limited_data = get_data(table_cls(), {'limit': "1"})
            self.assertEqual(len(limited_data['rows']),
                             1,
                             "Limit 1 set on table %s but not 1 row returned"
                             % name)

            # Test the pagination functionality on the table
            page_one_data = get_data(table_cls(), {'limit': "1",
                                                   "page": "1"})['rows'][0]

            page_two_data = get_data(table_cls(), {'limit': "1",
                                                   "page": "2"})['rows'][0]

            self.assertNotEqual(page_one_data,
                                page_two_data,
                                "Changed page on table %s but first row is"
                                " the same as the previous page" % name)
