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
        project_name = self.find('#project-name').text.strip()
        self.assertEqual(project_name, self.CLI_BUILDS_PROJECT_NAME)
