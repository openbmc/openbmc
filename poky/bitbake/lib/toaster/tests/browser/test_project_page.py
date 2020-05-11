#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.urls import reverse
from django.utils import timezone
from tests.browser.selenium_helpers import SeleniumTestCase

from orm.models import Build, Project

class TestProjectPage(SeleniumTestCase):
    """ Test project data at /project/X/ is displayed correctly """

    CLI_BUILDS_PROJECT_NAME = 'Command line builds'

    def test_cli_builds_in_progress(self):
        """
        In progress builds should not cause an error to be thrown
        when navigating to "command line builds" project page;
        see https://bugzilla.yoctoproject.org/show_bug.cgi?id=8277
        """

        # add the "command line builds" default project; this mirrors what
        # we do with get_or_create_default_project()
        default_project = Project.objects.create_project(self.CLI_BUILDS_PROJECT_NAME, None)
        default_project.is_default = True
        default_project.save()

        # add an "in progress" build for the default project
        now = timezone.now()
        Build.objects.create(project=default_project,
                             started_on=now,
                             completed_on=now,
                             outcome=Build.IN_PROGRESS)

        # navigate to the project page for the default project
        url = reverse("project", args=(default_project.id,))
        self.get(url)

        # check that we get a project page with the correct heading
        project_name = self.find('.project-name').text.strip()
        self.assertEqual(project_name, self.CLI_BUILDS_PROJECT_NAME)
