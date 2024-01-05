#! /usr/bin/env python3
#
# BitBake Toaster functional tests implementation
#
# Copyright (C) 2017 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import re
from django.urls import reverse
import pytest
from tests.functional.functional_helpers import SeleniumFunctionalTestCase
from orm.models import Project
from selenium.webdriver.common.by import By

from tests.functional.utils import get_projectId_from_url


@pytest.mark.django_db
@pytest.mark.order("second_to_last")
class FuntionalTestBasic(SeleniumFunctionalTestCase):
    """Basic functional tests for Toaster"""
    project_id = None

    def setUp(self):
        super(FuntionalTestBasic, self).setUp()
        if not FuntionalTestBasic.project_id:
            self._create_slenium_project()
            current_url = self.driver.current_url
            FuntionalTestBasic.project_id = get_projectId_from_url(current_url)

#   testcase (1514)
    def _create_slenium_project(self):
        project_name = 'selenium-project'
        self.get(reverse('newproject'))
        self.wait_until_visible('#new-project-name', poll=3)
        self.driver.find_element(By.ID, "new-project-name").send_keys(project_name)
        self.driver.find_element(By.ID, 'projectversion').click()
        self.driver.find_element(By.ID, "create-project-button").click()
        element = self.wait_until_visible('#project-created-notification', poll=10)
        self.assertTrue(self.element_exists('#project-created-notification'),'Project creation notification not shown')
        self.assertTrue(project_name in element.text,
                        "New project name not in new project notification")
        self.assertTrue(Project.objects.filter(name=project_name).count(),
                        "New project not found in database")
        return Project.objects.last().id

 #  testcase (1515)
    def test_verify_left_bar_menu(self):
        self.get(reverse('all-projects'))
        self.wait_until_present('#projectstable', poll=10)
        self.find_element_by_link_text_in_table('projectstable', 'selenium-project').click()
        self.wait_until_present('#config-nav', poll=10)
        self.assertTrue(self.element_exists('#config-nav'),'Configuration Tab does not exist')
        project_URL=self.get_URL()
        self.driver.find_element(By.XPATH, '//a[@href="'+project_URL+'"]').click()
        self.wait_until_present('#config-nav', poll=10)

        try:
            self.driver.find_element(By.XPATH, "//*[@id='config-nav']/ul/li/a[@href="+'"'+project_URL+'customimages/"'+"]").click()
            self.wait_until_present('#config-nav', poll=10)
            self.assertTrue(re.search("Custom images",self.driver.find_element(By.XPATH, "//div[@class='col-md-10']").text),'Custom images information is not loading properly')
        except:
            self.fail(msg='No Custom images tab available')

        try:
            self.driver.find_element(By.XPATH, "//*[@id='config-nav']/ul/li/a[@href="+'"'+project_URL+'images/"'+"]").click()
            self.wait_until_present('#config-nav', poll=10)
            self.assertTrue(re.search("Compatible image recipes",self.driver.find_element(By.XPATH, "//div[@class='col-md-10']").text),'The Compatible image recipes information is not loading properly')
        except:
            self.fail(msg='No Compatible image tab available')

        try:
            self.driver.find_element(By.XPATH, "//*[@id='config-nav']/ul/li/a[@href="+'"'+project_URL+'softwarerecipes/"'+"]").click()
            self.wait_until_present('#config-nav', poll=10)
            self.assertTrue(re.search("Compatible software recipes",self.driver.find_element(By.XPATH, "//div[@class='col-md-10']").text),'The Compatible software recipe information is not loading properly')
        except:
            self.fail(msg='No Compatible software recipe tab available')

        try:
            self.driver.find_element(By.XPATH, "//*[@id='config-nav']/ul/li/a[@href="+'"'+project_URL+'machines/"'+"]").click()
            self.wait_until_present('#config-nav', poll=10)
            self.assertTrue(re.search("Compatible machines",self.driver.find_element(By.XPATH, "//div[@class='col-md-10']").text),'The Compatible machine information is not loading properly')
        except:
            self.fail(msg='No Compatible machines tab available')

        try:
            self.driver.find_element(By.XPATH, "//*[@id='config-nav']/ul/li/a[@href="+'"'+project_URL+'layers/"'+"]").click()
            self.wait_until_present('#config-nav', poll=10)
            self.assertTrue(re.search("Compatible layers",self.driver.find_element(By.XPATH, "//div[@class='col-md-10']").text),'The Compatible layer information is not loading properly')
        except:
            self.fail(msg='No Compatible layers tab available')

        try:
            self.driver.find_element(By.XPATH, "//*[@id='config-nav']/ul/li/a[@href="+'"'+project_URL+'configuration"'+"]").click()
            self.wait_until_present('#config-nav', poll=10)
            self.assertTrue(re.search("Bitbake variables",self.driver.find_element(By.XPATH, "//div[@class='col-md-10']").text),'The Bitbake variables information is not loading properly')
        except:
            self.fail(msg='No Bitbake variables tab available')

#   testcase (1516)
    def test_review_configuration_information(self):
        self.get(reverse('all-projects'))
        self.wait_until_present('#projectstable', poll=10)
        self.find_element_by_link_text_in_table('projectstable', 'selenium-project').click()
        project_URL=self.get_URL()
        self.wait_until_present('#config-nav', poll=10)
        try:
           self.assertTrue(self.element_exists('#machine-section'),'Machine section for the project configuration page does not exist')
           self.assertTrue(re.search("qemux86-64",self.driver.find_element(By.XPATH, "//span[@id='project-machine-name']").text),'The machine type is not assigned')
           self.driver.find_element(By.XPATH, "//span[@id='change-machine-toggle']").click()
           self.wait_until_visible('#select-machine-form', poll=10)
           self.wait_until_visible('#cancel-machine-change', poll=10)
           self.driver.find_element(By.XPATH, "//form[@id='select-machine-form']/a[@id='cancel-machine-change']").click()
        except:
           self.fail(msg='The machine information is wrong in the configuration page')

        try:
           self.driver.find_element(By.ID, 'no-most-built')
        except:
           self.fail(msg='No Most built information in project detail page')

        try:
           self.assertTrue(re.search("Yocto Project master",self.driver.find_element(By.XPATH, "//span[@id='project-release-title']").text),'The project release is not defined')
        except:
           self.fail(msg='No project release title information in project detail page')

        try:
           self.driver.find_element(By.XPATH, "//div[@id='layer-container']")
           self.assertTrue(re.search("3",self.driver.find_element(By.ID, "project-layers-count").text),'There should be 3 layers listed in the layer count')
           layer_list = self.driver.find_element(By.ID, "layers-in-project-list")
           layers = layer_list.find_elements(By.TAG_NAME, "li")
           for layer in layers:
               if re.match ("openembedded-core",layer.text):
                    print ("openembedded-core layer is a default layer in the project configuration")
               elif re.match ("meta-poky",layer.text):
                  print ("meta-poky layer is a default layer in the project configuration")
               elif re.match ("meta-yocto-bsp",layer.text):
                  print ("meta-yocto-bsp is a default layer in the project configuratoin")
               else:
                  self.fail(msg='default layers are missing from the project configuration')
        except:
           self.fail(msg='No Layer information in project detail page')

#   testcase (1517)
    def test_verify_machine_information(self):
        self.get(reverse('all-projects'))
        self.wait_until_present('#projectstable', poll=10)
        self.find_element_by_link_text_in_table('projectstable', 'selenium-project').click()
        self.wait_until_present('#config-nav', poll=10)

        try:
            self.assertTrue(self.element_exists('#machine-section'),'Machine section for the project configuration page does not exist')
            self.assertTrue(re.search("qemux86-64",self.driver.find_element(By.ID, "project-machine-name").text),'The machine type is not assigned')
            self.driver.find_element(By.ID, "change-machine-toggle").click()
            self.wait_until_visible('#select-machine-form', poll=10)
            self.wait_until_visible('#cancel-machine-change', poll=10)
            self.driver.find_element(By.ID, "cancel-machine-change").click()
        except:
            self.fail(msg='The machine information is wrong in the configuration page')

#   testcase (1518)
    def test_verify_most_built_recipes_information(self):
        self.get(reverse('all-projects'))
        self.wait_until_present('#projectstable', poll=10)
        self.find_element_by_link_text_in_table('projectstable', 'selenium-project').click()
        self.wait_until_present('#config-nav', poll=10)
        project_URL=self.get_URL()
        try:
            self.assertTrue(re.search("You haven't built any recipes yet",self.driver.find_element(By.ID, "no-most-built").text),'Default message of no builds is not present')
            self.driver.find_element(By.XPATH, "//div[@id='no-most-built']/p/a[@href="+'"'+project_URL+'images/"'+"]").click()
            self.wait_until_present('#config-nav', poll=10)
            self.assertTrue(re.search("Compatible image recipes",self.driver.find_element(By.XPATH, "//div[@class='col-md-10']").text),'The Choose a recipe to build link  is not working  properly')
        except:
            self.fail(msg='No Most built information in project detail page')

#   testcase (1519)
    def test_verify_project_release_information(self):
        self.get(reverse('all-projects'))
        self.wait_until_present('#projectstable', poll=10)
        self.find_element_by_link_text_in_table('projectstable', 'selenium-project').click()
        self.wait_until_present('#config-nav', poll=10)

        try:
            self.assertTrue(re.search("Yocto Project master",self.driver.find_element(By.ID, "project-release-title").text),'The project release is not defined')
        except:
            self.fail(msg='No project release title information in project detail page')

#   testcase (1520)
    def test_verify_layer_information(self):
        self.get(reverse('all-projects'))
        self.wait_until_present('#projectstable', poll=10)
        self.find_element_by_link_text_in_table('projectstable', 'selenium-project').click()
        self.wait_until_present('#config-nav', poll=10)
        project_URL=self.get_URL()
        try:
           self.driver.find_element(By.XPATH, "//div[@id='layer-container']")
           self.assertTrue(re.search("3",self.driver.find_element(By.ID, "project-layers-count").text),'There should be 3 layers listed in the layer count')
           layer_list = self.driver.find_element(By.ID, "layers-in-project-list")
           layers = layer_list.find_elements(By.TAG_NAME, "li")

           for layer in layers:
               if re.match ("openembedded-core",layer.text):
                    print ("openembedded-core layer is a default layer in the project configuration")
               elif re.match ("meta-poky",layer.text):
                  print ("meta-poky layer is a default layer in the project configuration")
               elif re.match ("meta-yocto-bsp",layer.text):
                  print ("meta-yocto-bsp is a default layer in the project configuratoin")
               else:
                  self.fail(msg='default layers are missing from the project configuration')

           self.driver.find_element(By.XPATH, "//input[@id='layer-add-input']")
           self.driver.find_element(By.XPATH, "//button[@id='add-layer-btn']")
           self.driver.find_element(By.XPATH, "//div[@id='layer-container']/form[@class='form-inline']/p/a[@id='view-compatible-layers']")
           self.driver.find_element(By.XPATH, "//div[@id='layer-container']/form[@class='form-inline']/p/a[@href="+'"'+project_URL+'importlayer"'+"]")
        except:
            self.fail(msg='No Layer information in project detail page')

#   testcase (1521)
    def test_verify_project_detail_links(self):
        self.get(reverse('all-projects'))
        self.wait_until_present('#projectstable', poll=10)
        self.find_element_by_link_text_in_table('projectstable', 'selenium-project').click()
        self.wait_until_present('#config-nav', poll=10)
        project_URL=self.get_URL()
        self.driver.find_element(By.XPATH, "//div[@id='project-topbar']/ul[@class='nav nav-tabs']/li[@id='topbar-configuration-tab']/a[@href="+'"'+project_URL+'"'+"]").click()
        self.wait_until_present('#config-nav', poll=10)
        self.assertTrue(re.search("Configuration",self.driver.find_element(By.XPATH, "//div[@id='project-topbar']/ul[@class='nav nav-tabs']/li[@id='topbar-configuration-tab']/a[@href="+'"'+project_URL+'"'+"]").text), 'Configuration tab in project topbar is misspelled')

        try:
            self.driver.find_element(By.XPATH, "//div[@id='project-topbar']/ul[@class='nav nav-tabs']/li/a[@href="+'"'+project_URL+'builds/"'+"]").click()
            self.wait_until_visible('#project-topbar', poll=10)
            self.assertTrue(re.search("Builds",self.driver.find_element(By.XPATH, "//div[@id='project-topbar']/ul[@class='nav nav-tabs']/li/a[@href="+'"'+project_URL+'builds/"'+"]").text), 'Builds tab in project topbar is misspelled')
            self.driver.find_element(By.XPATH, "//div[@id='empty-state-projectbuildstable']")
        except:
            self.fail(msg='Builds tab information is not present')

        try:
            self.driver.find_element(By.XPATH, "//div[@id='project-topbar']/ul[@class='nav nav-tabs']/li/a[@href="+'"'+project_URL+'importlayer"'+"]").click()
            self.wait_until_visible('#project-topbar', poll=10)
            self.assertTrue(re.search("Import layer",self.driver.find_element(By.XPATH, "//div[@id='project-topbar']/ul[@class='nav nav-tabs']/li/a[@href="+'"'+project_URL+'importlayer"'+"]").text), 'Import layer tab in project topbar is misspelled')
            self.driver.find_element(By.XPATH, "//fieldset[@id='repo-select']")
            self.driver.find_element(By.XPATH, "//fieldset[@id='git-repo']")
        except:
            self.fail(msg='Import layer tab not loading properly')

        try:
            self.driver.find_element(By.XPATH, "//div[@id='project-topbar']/ul[@class='nav nav-tabs']/li/a[@href="+'"'+project_URL+'newcustomimage/"'+"]").click()
            self.wait_until_visible('#project-topbar', poll=10)
            self.assertTrue(re.search("New custom image",self.driver.find_element(By.XPATH, "//div[@id='project-topbar']/ul[@class='nav nav-tabs']/li/a[@href="+'"'+project_URL+'newcustomimage/"'+"]").text), 'New custom image tab in project topbar is misspelled')
            self.assertTrue(re.search("Select the image recipe you want to customise",self.driver.find_element(By.XPATH, "//div[@class='col-md-12']/h2").text),'The new custom image tab is not loading correctly')
        except:
            self.fail(msg='New custom image tab not loading properly')



