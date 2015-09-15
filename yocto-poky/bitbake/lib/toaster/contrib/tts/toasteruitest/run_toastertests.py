#!/usr/bin/python

# Copyright

# DESCRIPTION
# This is script for running all selected toaster cases on
# selected web browsers manifested in toaster_test.cfg.

# 1. How to start toaster in yocto:
# $ source poky/oe-init-build-env
# $ source toaster start
# $ bitbake core-image-minimal

# 2. How to install selenium on Ubuntu:
# $ sudo apt-get install scrot python-pip
# $ sudo pip install selenium

# 3. How to install selenium addon in firefox:
# Download the lastest firefox addon from http://release.seleniumhq.org/selenium-ide/
# Then install it. You can also install firebug and firepath addon

# 4. How to start writing a new case:
# All you need to do is to implement the function test_xxx() and  pile it on.

# 5. How to test with Chrome browser
# Download/install chrome on host
# Download chromedriver from https://code.google.com/p/chromedriver/downloads/list  according to your host type
# put chromedriver in PATH, (e.g. /usr/bin/, bear in mind to chmod)
# For windows host, you may put chromedriver.exe in the same directory as chrome.exe


import unittest, time, re, sys, getopt, os, logging, platform
import ConfigParser
import subprocess


class toaster_run_all():
    def __init__(self):
        # in case this script is called from other directory
        os.chdir(os.path.abspath(sys.path[0]))
        self.starttime = time.strptime(time.ctime())
        self.parser = ConfigParser.SafeConfigParser()
        found = self.parser.read('toaster_test.cfg')
        self.host_os = platform.system().lower()
        self.run_all_cases()
        self.collect_log()

    def get_test_cases(self):
        # we have config groups for different os type in toaster_test.cfg
        cases_to_run = eval(self.parser.get('toaster_test_' + self.host_os, 'test_cases'))
        return cases_to_run


    def run_all_cases(self):
        cases_temp = self.get_test_cases()
        for case in cases_temp:
            single_case_cmd = "python -m unittest toaster_automation_test.toaster_cases.test_" + str(case)
            print single_case_cmd
            subprocess.call(single_case_cmd, shell=True)

    def collect_log(self):
        """
        the log files are temporarily stored in ./log/tmp/..
        After all cases are done, they should be transfered to ./log/$TIMESTAMP/
        """
        def comple(number):
            if number < 10:
                return str(0) + str(number)
            else:
                return str(number)
        now = self.starttime
        now_str = comple(now.tm_year) + comple(now.tm_mon) + comple(now.tm_mday) + \
                  comple(now.tm_hour) + comple(now.tm_min) + comple(now.tm_sec)
        log_dir = os.path.abspath(sys.path[0]) + os.sep + 'log' + os.sep + now_str
        log_tmp_dir = os.path.abspath(sys.path[0]) + os.sep + 'log' + os.sep + 'tmp'
        try:
            os.renames(log_tmp_dir, log_dir)
        except OSError :
            logging.error(" Cannot create log dir(timestamp)  under log, please check your privilege")


if __name__ == "__main__":
    toaster_run_all()




