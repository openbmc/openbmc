#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

from datetime import datetime

from django.core.urlresolvers import reverse
from django.utils import timezone
from tests.browser.selenium_helpers import SeleniumTestCase
from orm.models import BitbakeVersion, Release, Project, Build

class TestToasterTableUI(SeleniumTestCase):
    """
    Tests for the UI elements of ToasterTable (sorting etc.);
    note that the tests cover generic functionality of ToasterTable which
    manifests as UI elements in the browser, and can only be tested via
    Selenium.
    """

    def setUp(self):
        pass

    def _get_orderby_heading(self, table):
        """
        Get the current order by finding the column heading in <table> with
        the sorted class on it.

        table: WebElement for a ToasterTable
        """
        selector = 'thead a.sorted'
        heading = table.find_element_by_css_selector(selector)
        return heading.get_attribute('innerHTML').strip()

    def _get_datetime_from_cell(self, row, selector):
        """
        Return the value in the cell selected by <selector> on <row> as a
        datetime.

        row: <tr> WebElement for a row in the ToasterTable
        selector: CSS selector to use to find the cell containing the date time
        string
        """
        cell = row.find_element_by_css_selector(selector)
        cell_text = cell.get_attribute('innerHTML').strip()
        return datetime.strptime(cell_text, '%d/%m/%y %H:%M')

    def test_revert_orderby(self):
        """
        Test that sort order for a table reverts to the default sort order
        if the current sort column is hidden.
        """
        now = timezone.now()
        later = now + timezone.timedelta(hours=1)
        even_later = later + timezone.timedelta(hours=1)

        bbv = BitbakeVersion.objects.create(name='test bbv', giturl='/tmp/',
                                            branch='master', dirpath='')
        release = Release.objects.create(name='test release',
                                         branch_name='master',
                                         bitbake_version=bbv)

        project = Project.objects.create_project('project', release)

        # set up two builds which will order differently when sorted by
        # started_on or completed_on

        # started first, finished last
        build1 = Build.objects.create(project=project,
                                      started_on=now,
                                      completed_on=even_later,
                                      outcome=Build.SUCCEEDED)

        # started second, finished first
        build2 = Build.objects.create(project=project,
                                      started_on=later,
                                      completed_on=later,
                                      outcome=Build.SUCCEEDED)

        url = reverse('all-builds')
        self.get(url)
        table = self.wait_until_visible('#allbuildstable')

        # check ordering (default is by -completed_on); so build1 should be
        # first as it finished last
        active_heading = self._get_orderby_heading(table)
        self.assertEqual(active_heading, 'Completed on',
            'table should be sorted by "Completed on" by default')

        row_selector = '#allbuildstable tbody tr'
        cell_selector = 'td.completed_on'

        rows = self.find_all(row_selector)
        row1_completed_on = self._get_datetime_from_cell(rows[0], cell_selector)
        row2_completed_on = self._get_datetime_from_cell(rows[1], cell_selector)
        self.assertTrue(row1_completed_on > row2_completed_on,
            'table should be sorted by -completed_on')

        # turn on started_on column
        self.click('#edit-columns-button')
        self.click('#checkbox-started_on')

        # sort by started_on column
        links = table.find_elements_by_css_selector('th.started_on a')
        for link in links:
            if link.get_attribute('innerHTML').strip() == 'Started on':
                link.click()
                break

        # wait for table data to reload in response to new sort
        self.wait_until_visible('#allbuildstable')

        # check ordering; build1 should be first
        active_heading = self._get_orderby_heading(table)
        self.assertEqual(active_heading, 'Started on',
            'table should be sorted by "Started on"')

        cell_selector = 'td.started_on'

        rows = self.find_all(row_selector)
        row1_started_on = self._get_datetime_from_cell(rows[0], cell_selector)
        row2_started_on = self._get_datetime_from_cell(rows[1], cell_selector)
        self.assertTrue(row1_started_on < row2_started_on,
            'table should be sorted by started_on')

        # turn off started_on column
        self.click('#edit-columns-button')
        self.click('#checkbox-started_on')

        # wait for table data to reload in response to new sort
        self.wait_until_visible('#allbuildstable')

        # check ordering (should revert to completed_on); build2 should be first
        active_heading = self._get_orderby_heading(table)
        self.assertEqual(active_heading, 'Completed on',
            'table should be sorted by "Completed on" after hiding sort column')

        cell_selector = 'td.completed_on'

        rows = self.find_all(row_selector)
        row1_completed_on = self._get_datetime_from_cell(rows[0], cell_selector)
        row2_completed_on = self._get_datetime_from_cell(rows[1], cell_selector)
        self.assertTrue(row1_completed_on > row2_completed_on,
            'table should be sorted by -completed_on')
