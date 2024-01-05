#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# BitBake Toaster UI tests implementation
#
# Copyright (C) 2023 Savoir-faire Linux
#
# SPDX-License-Identifier: GPL-2.0-only


from time import sleep
from selenium.common.exceptions import NoSuchElementException, StaleElementReferenceException, TimeoutException
from selenium.webdriver.common.by import By

from orm.models import Build


def wait_until_build(test_instance, state):
    timeout = 60
    start_time = 0
    build_state = ''
    while True:
        try:
            if start_time > timeout:
                raise TimeoutException(
                    f'Build did not reach {state} state within {timeout} seconds'
                )
            last_build_state = test_instance.driver.find_element(
                By.XPATH,
                '//*[@id="latest-builds"]/div[1]//div[@class="build-state"]',
            )
            build_state = last_build_state.get_attribute(
                'data-build-state')
            state_text = state.lower().split()
            if any(x in str(build_state).lower() for x in state_text):
                return str(build_state).lower()
            if 'failed' in str(build_state).lower():
                break
        except NoSuchElementException:
            continue
        except TimeoutException:
            break
        start_time += 1
        sleep(1) # take a breath and try again

def wait_until_build_cancelled(test_instance):
    """ Cancel build take a while sometime, the method is to wait driver action
        until build being cancelled
    """
    timeout = 30
    start_time = 0
    build = None
    while True:
        try:
            if start_time > timeout:
                raise TimeoutException(
                    f'Build did not reach cancelled state within {timeout} seconds'
                )
            last_build_state = test_instance.driver.find_element(
                By.XPATH,
                '//*[@id="latest-builds"]/div[1]//div[@class="build-state"]',
            )
            build_state = last_build_state.get_attribute(
                'data-build-state')
            if 'failed' in str(build_state).lower():
                break
            if 'cancelling' in str(build_state).lower():
                # Change build state to cancelled
                if not build:  # get build object only once
                    build = Build.objects.last()
                    build.outcome = Build.CANCELLED
                    build.save()
            if 'cancelled' in str(build_state).lower():
                break
        except NoSuchElementException:
            continue
        except StaleElementReferenceException:
            continue
        except TimeoutException:
            break
        start_time += 1
        sleep(1) # take a breath and try again

def get_projectId_from_url(url):
    # url = 'http://domainename.com/toastergui/project/1656/whatever
    # or url = 'http://domainename.com/toastergui/project/1/
    # or url = 'http://domainename.com/toastergui/project/186
    assert '/toastergui/project/' in url, "URL is not valid"
    url_to_list = url.split('/toastergui/project/')
    return  int(url_to_list[1].split('/')[0])  # project_id
