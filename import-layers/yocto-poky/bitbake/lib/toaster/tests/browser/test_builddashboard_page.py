#! /usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
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

from django.core.urlresolvers import reverse
from django.utils import timezone

from selenium_helpers import SeleniumTestCase

from orm.models import Project, Release, BitbakeVersion, Build, LogMessage
from orm.models import Layer, Layer_Version, Recipe, CustomImageRecipe

class TestBuildDashboardPage(SeleniumTestCase):
    """ Tests for the build dashboard /build/X """

    def setUp(self):
        bbv = BitbakeVersion.objects.create(name='bbv1', giturl='/tmp/',
                                            branch='master', dirpath="")
        release = Release.objects.create(name='release1',
                                         bitbake_version=bbv)
        project = Project.objects.create_project(name='test project',
                                                 release=release)

        now = timezone.now()

        self.build1 = Build.objects.create(project=project,
                                           started_on=now,
                                           completed_on=now)

        self.build2 = Build.objects.create(project=project,
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

        # recipes related to the build, for testing the edit custom image/new
        # custom image buttons
        layer = Layer.objects.create(name='alayer')
        layer_version = Layer_Version.objects.create(
            layer=layer, build=self.build1
        )

        # image recipes
        self.image_recipe1 = Recipe.objects.create(
            name='recipeA',
            layer_version=layer_version,
            file_path='/foo/recipeA.bb',
            is_image=True
        )
        self.image_recipe2 = Recipe.objects.create(
            name='recipeB',
            layer_version=layer_version,
            file_path='/foo/recipeB.bb',
            is_image=True
        )

        # custom image recipes for this project
        self.custom_image_recipe1 = CustomImageRecipe.objects.create(
            name='customRecipeY',
            project=project,
            layer_version=layer_version,
            file_path='/foo/customRecipeY.bb',
            base_recipe=self.image_recipe1,
            is_image=True
        )
        self.custom_image_recipe2 = CustomImageRecipe.objects.create(
            name='customRecipeZ',
            project=project,
            layer_version=layer_version,
            file_path='/foo/customRecipeZ.bb',
            base_recipe=self.image_recipe2,
            is_image=True
        )

        # custom image recipe for a different project (to test filtering
        # of image recipes and custom image recipes is correct: this shouldn't
        # show up in either query against self.build1)
        self.custom_image_recipe3 = CustomImageRecipe.objects.create(
            name='customRecipeOmega',
            project=Project.objects.create(name='baz', release=release),
            layer_version=Layer_Version.objects.create(
                layer=layer, build=self.build2
            ),
            file_path='/foo/customRecipeOmega.bb',
            base_recipe=self.image_recipe2,
            is_image=True
        )

        # another non-image recipe (to test filtering of image recipes and
        # custom image recipes is correct: this shouldn't show up in either
        # for any build)
        self.non_image_recipe = Recipe.objects.create(
            name='nonImageRecipe',
            layer_version=layer_version,
            file_path='/foo/nonImageRecipe.bb',
            is_image=False
        )

    def _get_build_dashboard(self, build):
        """
        Navigate to the build dashboard for build
        """
        url = reverse('builddashboard', args=(build.id,))
        self.get(url)

    def _get_build_dashboard_errors(self, build):
        """
        Get a list of HTML fragments representing the errors on the
        dashboard for the Build object build
        """
        self._get_build_dashboard(build)
        return self.find_all('#errors div.alert-error')

    def _check_for_log_message(self, build, log_message):
        """
        Check whether the LogMessage instance <log_message> is
        represented as an HTML error in the dashboard page for the Build object
        build
        """
        errors = self._get_build_dashboard_errors(build)
        self.assertEqual(len(errors), 2)

        expected_text = log_message.message
        expected_id = str(log_message.id)

        found = False
        for error in errors:
            error_text = error.find_element_by_tag_name('pre').text
            text_matches = (error_text == expected_text)

            error_id = error.get_attribute('data-error')
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

    def _check_labels_in_modal(self, modal, expected):
        """
        Check that the text values of the <label> elements inside
        the WebElement modal match the list of text values in expected
        """
        # labels containing the radio buttons we're testing for
        labels = modal.find_elements_by_tag_name('label')

        # because the label content has the structure
        #   label text
        #   <input...>
        # we have to regex on its innerHTML, as we can't just retrieve the
        # "label text" on its own via the Selenium API
        labels_text = sorted(map(
            lambda label: label.get_attribute('innerHTML'), labels
        ))

        expected = sorted(expected)

        self.assertEqual(len(labels_text), len(expected))

        for idx, label_text in enumerate(labels_text):
            self.assertRegexpMatches(label_text, expected[idx])

    def test_exceptions_show_as_errors(self):
        """
        LogMessages with level EXCEPTION should display in the errors
        section of the page
        """
        self._check_for_log_message(self.build1, self.exception_message)

    def test_criticals_show_as_errors(self):
        """
        LogMessages with level CRITICAL should display in the errors
        section of the page
        """
        self._check_for_log_message(self.build1, self.critical_message)

    def test_edit_custom_image_button(self):
        """
        A build which built two custom images should present a modal which lets
        the user choose one of them to edit
        """
        self._get_build_dashboard(self.build1)
        modal = self.driver.find_element_by_id('edit-custom-image-modal')

        # recipes we expect to see in the edit custom image modal
        expected_recipes = [
            self.custom_image_recipe1.name,
            self.custom_image_recipe2.name
        ]

        self._check_labels_in_modal(modal, expected_recipes)

    def test_new_custom_image_button(self):
        """
        Check that a build with multiple images and custom images presents
        all of them as options for creating a new custom image from
        """
        self._get_build_dashboard(self.build1)

        # click the "new custom image" button, which populates the modal
        selector = '[data-role="new-custom-image-trigger"] button'
        self.click(selector)

        modal = self.driver.find_element_by_id('new-custom-image-modal')

        # recipes we expect to see in the new custom image modal
        expected_recipes = [
            self.image_recipe1.name,
            self.image_recipe2.name,
            self.custom_image_recipe1.name,
            self.custom_image_recipe2.name
        ]

        self._check_labels_in_modal(modal, expected_recipes)
