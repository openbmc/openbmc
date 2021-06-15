#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import subprocess

from oeqa.sdkext.case import OESDKExtTestCase
from oeqa.utils.httpserver import HTTPService

from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class DevtoolTest(OESDKExtTestCase):
    @classmethod
    def setUpClass(cls):
        myapp_src = os.path.join(cls.tc.esdk_files_dir, "myapp")
        cls.myapp_dst = os.path.join(cls.tc.sdk_dir, "myapp")
        shutil.copytree(myapp_src, cls.myapp_dst)
        subprocess.check_output(['git', 'init', '.'], cwd=cls.myapp_dst)
        subprocess.check_output(['git', 'add', '.'], cwd=cls.myapp_dst)
        subprocess.check_output(['git', 'commit', '-m', "'test commit'"], cwd=cls.myapp_dst)

        myapp_cmake_src = os.path.join(cls.tc.esdk_files_dir, "myapp_cmake")
        cls.myapp_cmake_dst = os.path.join(cls.tc.sdk_dir, "myapp_cmake")
        shutil.copytree(myapp_cmake_src, cls.myapp_cmake_dst)
        subprocess.check_output(['git', 'init', '.'], cwd=cls.myapp_cmake_dst)
        subprocess.check_output(['git', 'add', '.'], cwd=cls.myapp_cmake_dst)
        subprocess.check_output(['git', 'commit', '-m', "'test commit'"], cwd=cls.myapp_cmake_dst)

    @classmethod
    def tearDownClass(cls):
        shutil.rmtree(cls.myapp_dst)
        shutil.rmtree(cls.myapp_cmake_dst)

    def _test_devtool_build(self, directory):
        self._run('devtool add myapp %s' % directory)
        try:
            self._run('devtool build myapp')
        finally:
            self._run('devtool reset myapp')

    def _test_devtool_build_package(self, directory):
        self._run('devtool add myapp %s' % directory)
        try:
            self._run('devtool package myapp')
        finally:
            self._run('devtool reset myapp')

    def test_devtool_location(self):
        output = self._run('which devtool')
        self.assertEqual(output.startswith(self.tc.sdk_dir), True, \
            msg="Seems that devtool isn't the eSDK one: %s" % output)

    def test_devtool_add_reset(self):
        self._run('devtool add myapp %s' % self.myapp_dst)
        self._run('devtool reset myapp')

    def test_devtool_build_make(self):
        self._test_devtool_build(self.myapp_dst)

    def test_devtool_build_esdk_package(self):
        self._test_devtool_build_package(self.myapp_dst)

    def test_devtool_build_cmake(self):
        self._test_devtool_build(self.myapp_cmake_dst)

    def test_extend_autotools_recipe_creation(self):
        req = 'https://github.com/rdfa/librdfa'
        recipe = "librdfa"
        self._run('devtool sdk-install libxml2')
        self._run('devtool add %s %s' % (recipe, req) )
        try:
            self._run('devtool build %s' % recipe)
        finally:
            self._run('devtool reset %s' % recipe)

    def test_devtool_kernelmodule(self):
        docfile = 'https://git.yoctoproject.org/git/kernel-module-hello-world'
        recipe = 'kernel-module-hello-world'
        self._run('devtool add %s %s' % (recipe, docfile) )
        try:
            self._run('devtool build %s' % recipe)
        finally:
            self._run('devtool reset %s' % recipe)

    def test_recipes_for_nodejs(self):
        package_nodejs = "npm://registry.npmjs.org;name=winston;version=2.2.0"
        self._run('devtool add %s ' % package_nodejs)
        try:
            self._run('devtool build %s ' % package_nodejs)
        finally:
            self._run('devtool reset %s '% package_nodejs)

class SdkUpdateTest(OESDKExtTestCase):
    @classmethod
    def setUpClass(self):
        self.publish_dir = os.path.join(self.tc.sdk_dir, 'esdk_publish')
        if os.path.exists(self.publish_dir):
            shutil.rmtree(self.publish_dir)
        os.mkdir(self.publish_dir)

        base_tcname = "%s/%s" % (self.td.get("SDK_DEPLOY", ''),
            self.td.get("TOOLCHAINEXT_OUTPUTNAME", ''))
        tcname_new = "%s-new.sh" % base_tcname
        if not os.path.exists(tcname_new):
            tcname_new = "%s.sh" % base_tcname

        cmd = 'oe-publish-sdk %s %s' % (tcname_new, self.publish_dir)
        subprocess.check_output(cmd, shell=True)

        self.http_service = HTTPService(self.publish_dir)
        self.http_service.start()

        self.http_url = "http://127.0.0.1:%d" % self.http_service.port

    def test_sdk_update_http(self):
        output = self._run("devtool sdk-update \"%s\"" % self.http_url)

    @classmethod
    def tearDownClass(self):
        self.http_service.stop()
        shutil.rmtree(self.publish_dir)
