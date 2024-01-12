#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#
from bldcontrol.models import BuildEnvironment

from django.urls import reverse
from tests.browser.selenium_helpers import SeleniumTestCase

from orm.models import BitbakeVersion, Release, Project, ProjectLayer, Layer
from orm.models import Layer_Version, Recipe, CustomImageRecipe


class TestNewCustomImagePage(SeleniumTestCase):
    CUSTOM_IMAGE_NAME = 'roopa-doopa'

    def setUp(self):
        BuildEnvironment.objects.get_or_create(
            betype=BuildEnvironment.TYPE_LOCAL,
        )
        release = Release.objects.create(
            name='baz',
            bitbake_version=BitbakeVersion.objects.create(name='v1')
        )

        # project to add new custom images to
        self.project = Project.objects.create(name='foo', release=release)

        # layer associated with the project
        layer = Layer.objects.create(name='bar')
        layer_version = Layer_Version.objects.create(
            layer=layer,
            project=self.project
        )

        # properly add the layer to the project
        ProjectLayer.objects.create(
            project=self.project,
            layercommit=layer_version,
            optional=False
        )

        # add a fake image recipe to the layer that can be customised
        builldir = os.environ.get('BUILDDIR', './')
        self.recipe = Recipe.objects.create(
            name='core-image-minimal',
            layer_version=layer_version,
            file_path=f'{builldir}/core-image-minimal.bb',
            is_image=True
        )
        # create a tmp file for the recipe
        with open(self.recipe.file_path, 'w') as f:
            f.write('foo')

        # another project with a custom image already in it
        project2 = Project.objects.create(name='whoop', release=release)
        layer_version2 = Layer_Version.objects.create(
            layer=layer,
            project=project2
        )
        ProjectLayer.objects.create(
            project=project2,
            layercommit=layer_version2,
            optional=False
        )
        recipe2 = Recipe.objects.create(
            name='core-image-minimal',
            layer_version=layer_version2,
            is_image=True
        )
        CustomImageRecipe.objects.create(
            name=self.CUSTOM_IMAGE_NAME,
            base_recipe=recipe2,
            layer_version=layer_version2,
            file_path='/1/2',
            project=project2
        )

    def _create_custom_image(self, new_custom_image_name):
        """
        1. Go to the 'new custom image' page
        2. Click the button for the fake core-image-minimal
        3. Wait for the dialog box for setting the name of the new custom
           image
        4. Insert new_custom_image_name into that dialog's text box
        """
        url = reverse('newcustomimage', args=(self.project.id,))
        self.get(url)
        self.wait_until_visible('#global-nav', poll=3)

        self.click('button[data-recipe="%s"]' % self.recipe.id)

        selector = '#new-custom-image-modal input[type="text"]'
        self.enter_text(selector, new_custom_image_name)

        self.click('#create-new-custom-image-btn')

    def _check_for_custom_image(self, image_name):
        """
        Fetch the list of custom images for the project and check the
        image with name image_name is listed there
        """
        url = reverse('projectcustomimages', args=(self.project.id,))
        self.get(url)

        self.wait_until_visible('#customimagestable')

        element = self.find('#customimagestable td[class="name"] a')
        msg = 'should be a custom image link with text %s' % image_name
        self.assertEqual(element.text.strip(), image_name, msg)

    def test_new_image(self):
        """
        Should be able to create a new custom image
        """
        custom_image_name = 'boo-image'
        self._create_custom_image(custom_image_name)
        self.wait_until_visible('#image-created-notification')
        self._check_for_custom_image(custom_image_name)

    def test_new_duplicates_other_project_image(self):
        """
        Should be able to create a new custom image if its name is the same
        as a custom image in another project
        """
        self._create_custom_image(self.CUSTOM_IMAGE_NAME)
        self.wait_until_visible('#image-created-notification')
        self._check_for_custom_image(self.CUSTOM_IMAGE_NAME)

    def test_new_duplicates_non_image_recipe(self):
        """
        Should not be able to create a new custom image whose name is the
        same as an existing non-image recipe
        """
        self._create_custom_image(self.recipe.name)
        element = self.wait_until_visible('#invalid-name-help')
        self.assertRegex(element.text.strip(),
                                 'image with this name already exists')

    def test_new_duplicates_project_image(self):
        """
        Should not be able to create a new custom image whose name is the same
        as a custom image in this project
        """
        # create the image
        custom_image_name = 'doh-image'
        self._create_custom_image(custom_image_name)
        self.wait_until_visible('#image-created-notification')
        self._check_for_custom_image(custom_image_name)

        # try to create an image with the same name
        self._create_custom_image(custom_image_name)
        element = self.wait_until_visible('#invalid-name-help')
        expected = 'An image with this name already exists in this project'
        self.assertRegex(element.text.strip(), expected)
