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
from tests.browser.selenium_helpers import SeleniumTestCase

from orm.models import Layer, Layer_Version, Project, LayerSource, Release
from orm.models import BitbakeVersion

from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By


class TestLayerDetailsPage(SeleniumTestCase):
    """ Test layerdetails page works correctly """

    def __init__(self, *args, **kwargs):
        super(TestLayerDetailsPage, self).__init__(*args, **kwargs)

        self.initial_values = None
        self.url = None
        self.imported_layer_version = None

    def setUp(self):
        release = Release.objects.create(
            name='baz',
            bitbake_version=BitbakeVersion.objects.create(name='v1')
        )

        # project to add new custom images to
        self.project = Project.objects.create(name='foo', release=release)

        name = "meta-imported"
        vcs_url = "git://example.com/meta-imported"
        subdir = "/layer"
        gitrev = "d33d"
        summary = "A imported layer"
        description = "This was imported"

        imported_layer = Layer.objects.create(name=name,
                                              vcs_url=vcs_url,
                                              summary=summary,
                                              description=description)

        self.imported_layer_version = Layer_Version.objects.create(
            layer=imported_layer,
            layer_source=LayerSource.TYPE_IMPORTED,
            branch=gitrev,
            commit=gitrev,
            dirpath=subdir,
            project=self.project)

        self.initial_values = [name, vcs_url, subdir, gitrev, summary,
                               description]
        self.url = reverse('layerdetails',
                           args=(self.project.pk,
                                 self.imported_layer_version.pk))

    def test_edit_layerdetails(self):
        """ Edit all the editable fields for the layer refresh the page and
        check that the new values exist"""

        self.get(self.url)

        self.click("#add-remove-layer-btn")
        self.click("#edit-layer-source")
        self.click("#repo")

        self.wait_until_visible("#layer-git-repo-url")

        # Open every edit box
        for btn in self.find_all("dd .glyphicon-edit"):
            btn.click()

        # Wait for the inputs to become visible after animation
        self.wait_until_visible("#layer-git input[type=text]")
        self.wait_until_visible("dd textarea")
        self.wait_until_visible("dd .change-btn")

        # Edit each value
        for inputs in self.find_all("#layer-git input[type=text]") + \
                self.find_all("dd textarea"):
            # ignore the tt inputs (twitter typeahead input)
            if "tt-" in inputs.get_attribute("class"):
                continue

            value = inputs.get_attribute("value")

            self.assertTrue(value in self.initial_values,
                            "Expecting any of \"%s\"but got \"%s\"" %
                            (self.initial_values, value))

            inputs.send_keys("-edited")

        # Save the new values
        for save_btn in self.find_all(".change-btn"):
            save_btn.click()

        self.click("#save-changes-for-switch")
        self.wait_until_visible("#edit-layer-source")

        # Refresh the page to see if the new values are returned
        self.get(self.url)

        new_values = ["%s-edited" % old_val
                      for old_val in self.initial_values]

        for inputs in self.find_all('#layer-git input[type="text"]') + \
                self.find_all('dd textarea'):
            # ignore the tt inputs (twitter typeahead input)
            if "tt-" in inputs.get_attribute("class"):
                continue

            value = inputs.get_attribute("value")

            self.assertTrue(value in new_values,
                            "Expecting any of \"%s\" but got \"%s\"" %
                            (new_values, value))

        # Now convert it to a local layer
        self.click("#edit-layer-source")
        self.click("#dir")
        dir_input = self.wait_until_visible("#layer-dir-path-in-details")

        new_dir = "/home/test/my-meta-dir"
        dir_input.send_keys(new_dir)

        self.click("#save-changes-for-switch")
        self.wait_until_visible("#edit-layer-source")

        # Refresh the page to see if the new values are returned
        self.get(self.url)
        dir_input = self.find("#layer-dir-path-in-details")
        self.assertTrue(new_dir in dir_input.get_attribute("value"),
                        "Expected %s in the dir value for layer directory" %
                        new_dir)

    def test_delete_layer(self):
        """ Delete the layer """

        self.get(self.url)

        # Wait for the tables to load to avoid a race condition where the
        # toaster tables have made an async request. If the layer is deleted
        # before the request finishes it will cause an exception and fail this
        # test.
        wait = WebDriverWait(self.driver, 30)

        wait.until(EC.text_to_be_present_in_element(
            (By.CLASS_NAME,
             "table-count-recipestable"), "0"))

        wait.until(EC.text_to_be_present_in_element(
            (By.CLASS_NAME,
             "table-count-machinestable"), "0"))

        self.click('a[data-target="#delete-layer-modal"]')
        self.wait_until_visible("#delete-layer-modal")
        self.click("#layer-delete-confirmed")

        notification = self.wait_until_visible("#change-notification-msg")
        expected_text = "You have deleted 1 layer from your project: %s" % \
            self.imported_layer_version.layer.name

        self.assertTrue(expected_text in notification.text,
                        "Expected notification text \"%s\" not found instead"
                        "it was \"%s\"" %
                        (expected_text, notification.text))

    def test_addrm_to_project(self):
        self.get(self.url)

        # Add the layer
        self.click("#add-remove-layer-btn")

        notification = self.wait_until_visible("#change-notification-msg")

        expected_text = "You have added 1 layer to your project: %s" % \
            self.imported_layer_version.layer.name

        self.assertTrue(expected_text in notification.text,
                        "Expected notification text %s not found was "
                        " \"%s\" instead" %
                        (expected_text, notification.text))

        # Remove the layer
        self.click("#add-remove-layer-btn")

        notification = self.wait_until_visible("#change-notification-msg")

        expected_text = "You have removed 1 layer from your project: %s" % \
            self.imported_layer_version.layer.name

        self.assertTrue(expected_text in notification.text,
                        "Expected notification text %s not found was "
                        " \"%s\" instead" %
                        (expected_text, notification.text))
