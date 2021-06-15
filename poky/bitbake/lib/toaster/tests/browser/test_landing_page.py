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

from orm.models import Project, Build

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

        elements = self.find_all('#allbuildstable')
        self.assertEqual(len(elements), 1, 'should redirect to builds')
        content = self.get_page_source()
        self.assertTrue(self.PROJECT_NAME in content,
                        'should show builds for project %s' % self.PROJECT_NAME)
        self.assertFalse(self.CLI_BUILDS_PROJECT_NAME in content,
                         'should not show builds for cli project')
