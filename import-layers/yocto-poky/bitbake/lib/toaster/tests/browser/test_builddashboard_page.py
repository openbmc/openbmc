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

from tests.browser.selenium_helpers import SeleniumTestCase

from orm.models import Project, Release, BitbakeVersion, Build, LogMessage
from orm.models import Layer, Layer_Version, Recipe, CustomImageRecipe, Variable

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
                                           completed_on=now,
                                           outcome=Build.SUCCEEDED)

        self.build2 = Build.objects.create(project=project,
                                           started_on=now,
                                           completed_on=now,
                                           outcome=Build.SUCCEEDED)

        self.build3 = Build.objects.create(project=project,
                                           started_on=now,
                                           completed_on=now,
                                           outcome=Build.FAILED)

        # add Variable objects to the successful builds, as this is the criterion
        # used to determine whether the left-hand panel should be displayed
        Variable.objects.create(build=self.build1,
                                variable_name='Foo',
                                variable_value='Bar')
        Variable.objects.create(build=self.build2,
                                variable_name='Foo',
                                variable_value='Bar')

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

        # error on the failed build
        msg3 = 'an error occurred'
        self.error_message = LogMessage.objects.create(
            build=self.build3,
            level=LogMessage.ERROR,
            message=msg3
        )

        # warning on the failed build
        msg4 = 'DANGER WILL ROBINSON'
        self.warning_message = LogMessage.objects.create(
            build=self.build3,
            level=LogMessage.WARNING,
            message=msg4
        )

        # recipes related to the build, for testing the edit custom image/new
        # custom image buttons
        layer = Layer.objects.create(name='alayer')
        layer_version = Layer_Version.objects.create(
            layer=layer, build=self.build1
        )

        # non-image recipes related to a build, for testing the new custom
        # image button
        layer_version2 = Layer_Version.objects.create(layer=layer,
            build=self.build3)

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
        return self.find_all('#errors div.alert-danger')

    def _check_for_log_message(self, message_elements, log_message):
        """
        Check that the LogMessage <log_message> has a representation in
        the HTML elements <message_elements>.

        message_elements: WebElements representing the log messages shown
        in the build dashboard; each should have a <pre> element inside
        it with a data-log-message-id attribute

        log_message: orm.models.LogMessage instance
        """
        expected_text = log_message.message
        expected_pk = str(log_message.pk)

        found = False
        for element in message_elements:
            log_message_text = element.find_element_by_tag_name('pre').text.strip()
            text_matches = (log_message_text == expected_text)

            log_message_pk = element.get_attribute('data-log-message-id')
            id_matches = (log_message_pk == expected_pk)

            if text_matches and id_matches:
                found = True
                break

        template_vars = (expected_text, expected_pk)
        assertion_failed_msg = 'message not found: ' \
            'expected text "%s" and ID %s' % template_vars
        self.assertTrue(found, assertion_failed_msg)

    def _check_for_error_message(self, build, log_message):
        """
        Check whether the LogMessage instance <log_message> is
        represented as an HTML error in the dashboard page for the Build object
        build
        """
        errors = self._get_build_dashboard_errors(build)
        self._check_for_log_message(errors, log_message)

    def _check_labels_in_modal(self, modal, expected):
        """
        Check that the text values of the <label> elements inside
        the WebElement modal match the list of text values in expected
        """
        # labels containing the radio buttons we're testing for
        labels = modal.find_elements_by_css_selector(".radio")

        labels_text = [lab.text for lab in labels]
        self.assertEqual(len(labels_text), len(expected))

        for expected_text in expected:
            self.assertTrue(expected_text in labels_text,
                            "Could not find %s in %s" % (expected_text,
                                                         labels_text))

    def test_exceptions_show_as_errors(self):
        """
        LogMessages with level EXCEPTION should display in the errors
        section of the page
        """
        self._check_for_error_message(self.build1, self.exception_message)

    def test_criticals_show_as_errors(self):
        """
        LogMessages with level CRITICAL should display in the errors
        section of the page
        """
        self._check_for_error_message(self.build1, self.critical_message)

    def test_edit_custom_image_button(self):
        """
        A build which built two custom images should present a modal which lets
        the user choose one of them to edit
        """
        self._get_build_dashboard(self.build1)

        # click the "edit custom image" button, which populates the modal
        selector = '[data-role="edit-custom-image-trigger"]'
        self.click(selector)

        modal = self.driver.find_element_by_id('edit-custom-image-modal')
        self.wait_until_visible("#edit-custom-image-modal")

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
        selector = '[data-role="new-custom-image-trigger"]'
        self.click(selector)

        modal = self.driver.find_element_by_id('new-custom-image-modal')
        self.wait_until_visible("#new-custom-image-modal")

        # recipes we expect to see in the new custom image modal
        expected_recipes = [
            self.image_recipe1.name,
            self.image_recipe2.name,
            self.custom_image_recipe1.name,
            self.custom_image_recipe2.name
        ]

        self._check_labels_in_modal(modal, expected_recipes)

    def test_new_custom_image_button_no_image(self):
        """
        Check that a build which builds non-image recipes doesn't show
        the new custom image button on the dashboard.
        """
        self._get_build_dashboard(self.build3)
        selector = '[data-role="new-custom-image-trigger"]'
        self.assertFalse(self.element_exists(selector),
            'new custom image button should not show for builds which ' \
            'don\'t have any image recipes')

    def test_left_panel(self):
        """"
        Builds which succeed should have a left panel and a build summary
        """
        self._get_build_dashboard(self.build1)

        left_panel = self.find_all('#nav')
        self.assertEqual(len(left_panel), 1)

        build_summary = self.find_all('[data-role="build-summary-heading"]')
        self.assertEqual(len(build_summary), 1)

    def test_failed_no_left_panel(self):
        """
        Builds which fail should have no left panel and no build summary
        """
        self._get_build_dashboard(self.build3)

        left_panel = self.find_all('#nav')
        self.assertEqual(len(left_panel), 0)

        build_summary = self.find_all('[data-role="build-summary-heading"]')
        self.assertEqual(len(build_summary), 0)

    def test_failed_shows_errors_and_warnings(self):
        """
        Failed builds should still show error and warning messages
        """
        self._get_build_dashboard(self.build3)

        errors = self.find_all('#errors div.alert-danger')
        self._check_for_log_message(errors, self.error_message)

        # expand the warnings area
        self.click('#warning-toggle')
        self.wait_until_visible('#warnings div.alert-warning')

        warnings = self.find_all('#warnings div.alert-warning')
        self._check_for_log_message(warnings, self.warning_message)
