#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Copyright (C) 2013-2016 Intel Corporation
#

from django.urls import reverse
from django.utils import timezone
from tests.browser.selenium_helpers import SeleniumTestCase
from selenium.webdriver.common.by import By

from orm.models import Layer, Layer_Version, Project, Build


class TestLandingPage(SeleniumTestCase):
    """ Tests for redirects on the landing page """

    PROJECT_NAME = 'test project'
    LANDING_PAGE_TITLE = 'This is Toaster'
    CLI_BUILDS_PROJECT_NAME = 'command line builds'

    def setUp(self):
        """ Add default project manually """
        self.project = Project.objects.create_project(
            self.CLI_BUILDS_PROJECT_NAME,
            None
        )
        self.project.is_default = True
        self.project.save()

    def test_icon_info_visible_and_clickable(self):
        """ Test that the information icon is visible and clickable """
        self.get(reverse('landing'))
        info_sign = self.find('#toaster-version-info-sign')

        # check that the info sign is visible
        self.assertTrue(info_sign.is_displayed())

        # check that the info sign is clickable
        # and info modal is appearing when clicking on the info sign
        info_sign.click()  # click on the info sign make attribute 'aria-describedby' visible
        info_model_id = info_sign.get_attribute('aria-describedby')
        info_modal = self.find(f'#{info_model_id}')
        self.assertTrue(info_modal.is_displayed())
        self.assertTrue("Toaster version information" in info_modal.text)

    def test_documentation_link_displayed(self):
        """ Test that the documentation link is displayed """
        self.get(reverse('landing'))
        documentation_link = self.find('#navbar-docs > a')

        # check that the documentation link is visible
        self.assertTrue(documentation_link.is_displayed())

        # check browser open new tab toaster manual when clicking on the documentation link
        self.assertEqual(documentation_link.get_attribute('target'), '_blank')
        self.assertEqual(
            documentation_link.get_attribute('href'),
            'http://docs.yoctoproject.org/toaster-manual/index.html#toaster-user-manual')
        self.assertTrue("Documentation" in documentation_link.text)

    def test_openembedded_jumbotron_link_visible_and_clickable(self):
        """ Test OpenEmbedded link jumbotron is visible and clickable: """
        self.get(reverse('landing'))
        jumbotron = self.find('.jumbotron')

        # check OpenEmbedded
        openembedded = jumbotron.find_element(By.LINK_TEXT, 'OpenEmbedded')
        self.assertTrue(openembedded.is_displayed())
        openembedded.click()
        self.assertTrue("openembedded.org" in self.driver.current_url)

    def test_bitbake_jumbotron_link_visible_and_clickable(self):
        """ Test BitBake link jumbotron is visible and clickable: """
        self.get(reverse('landing'))
        jumbotron = self.find('.jumbotron')

        # check BitBake
        bitbake = jumbotron.find_element(By.LINK_TEXT, 'BitBake')
        self.assertTrue(bitbake.is_displayed())
        bitbake.click()
        self.assertTrue(
            "docs.yoctoproject.org/bitbake.html" in self.driver.current_url)

    def test_yoctoproject_jumbotron_link_visible_and_clickable(self):
        """ Test Yocto Project link jumbotron is visible and clickable: """
        self.get(reverse('landing'))
        jumbotron = self.find('.jumbotron')

        # check Yocto Project
        yoctoproject = jumbotron.find_element(By.LINK_TEXT, 'Yocto Project')
        self.assertTrue(yoctoproject.is_displayed())
        yoctoproject.click()
        self.assertTrue("yoctoproject.org" in self.driver.current_url)

    def test_link_setup_using_toaster_visible_and_clickable(self):
        """ Test big magenta button setting up and using toaster link in jumbotron
            if visible and clickable
        """
        self.get(reverse('landing'))
        jumbotron = self.find('.jumbotron')

        # check Big magenta button
        big_magenta_button = jumbotron.find_element(By.LINK_TEXT,
                                                    'Toaster is ready to capture your command line builds'
                                                    )
        self.assertTrue(big_magenta_button.is_displayed())
        big_magenta_button.click()
        self.assertTrue(
            "docs.yoctoproject.org/toaster-manual/setup-and-use.html#setting-up-and-using-toaster" in self.driver.current_url)

    def test_link_create_new_project_in_jumbotron_visible_and_clickable(self):
        """ Test big blue button create new project jumbotron if visible and clickable """
        # Create a layer and a layer version to make visible the big blue button
        layer = Layer.objects.create(name='bar')
        Layer_Version.objects.create(layer=layer)

        self.get(reverse('landing'))
        jumbotron = self.find('.jumbotron')

        # check Big Blue button
        big_blue_button = jumbotron.find_element(By.LINK_TEXT,
                                                 'Create your first Toaster project to run manage builds'
                                                 )
        self.assertTrue(big_blue_button.is_displayed())
        big_blue_button.click()
        self.assertTrue("toastergui/newproject/" in self.driver.current_url)

    def test_toaster_manual_link_visible_and_clickable(self):
        """ Test Read the Toaster manual link jumbotron is visible and clickable: """
        self.get(reverse('landing'))
        jumbotron = self.find('.jumbotron')

        # check Read the Toaster manual
        toaster_manual = jumbotron.find_element(
            By.LINK_TEXT, 'Read the Toaster manual')
        self.assertTrue(toaster_manual.is_displayed())
        toaster_manual.click()
        self.assertTrue(
            "https://docs.yoctoproject.org/toaster-manual/index.html#toaster-user-manual" in self.driver.current_url)

    def test_contrib_to_toaster_link_visible_and_clickable(self):
        """ Test Contribute to Toaster link jumbotron is visible and clickable: """
        self.get(reverse('landing'))
        jumbotron = self.find('.jumbotron')

        # check Contribute to Toaster
        contribute_to_toaster = jumbotron.find_element(
            By.LINK_TEXT, 'Contribute to Toaster')
        self.assertTrue(contribute_to_toaster.is_displayed())
        contribute_to_toaster.click()
        self.assertTrue(
            "wiki.yoctoproject.org/wiki/contribute_to_toaster" in str(self.driver.current_url).lower())

    def test_only_default_project(self):
        """
        No projects except default
        => should see the landing page
        """
        self.get(reverse('landing'))
        self.assertTrue(self.LANDING_PAGE_TITLE in self.get_page_source())

    def test_default_project_has_build(self):
        """
        Default project has a build, no other projects
        => should see the builds page
        """
        now = timezone.now()
        build = Build.objects.create(project=self.project,
                                     started_on=now,
                                     completed_on=now)
        build.save()

        self.get(reverse('landing'))

        elements = self.find_all('#allbuildstable')
        self.assertEqual(len(elements), 1, 'should redirect to builds')
        content = self.get_page_source()
        self.assertFalse(self.PROJECT_NAME in content,
                         'should not show builds for project %s' % self.PROJECT_NAME)
        self.assertTrue(self.CLI_BUILDS_PROJECT_NAME in content,
                        'should show builds for cli project')

    def test_user_project_exists(self):
        """
        User has added a project (without builds)
        => should see the projects page
        """
        user_project = Project.objects.create_project('foo', None)
        user_project.save()

        self.get(reverse('landing'))

        elements = self.find_all('#projectstable')
        self.assertEqual(len(elements), 1, 'should redirect to projects')

    def test_user_project_has_build(self):
        """
        User has added a project (with builds), command line builds doesn't
        => should see the builds page
        """
        user_project = Project.objects.create_project(self.PROJECT_NAME, None)
        user_project.save()

        now = timezone.now()
        build = Build.objects.create(project=user_project,
                                     started_on=now,
                                     completed_on=now)
        build.save()

        self.get(reverse('landing'))

        self.wait_until_visible("#latest-builds", poll=3)
        elements = self.find_all('#allbuildstable')
        self.assertEqual(len(elements), 1, 'should redirect to builds')
        content = self.get_page_source()
        self.assertTrue(self.PROJECT_NAME in content,
                        'should show builds for project %s' % self.PROJECT_NAME)
