# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import shutil
import subprocess

from oeqa.sdkext.case import OESDKExtTestCase
from oeqa.utils.httpserver import HTTPService

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
