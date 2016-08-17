import os
import shutil
import subprocess

from oeqa.oetest import oeSDKExtTest
from oeqa.utils.httpserver import HTTPService

class SdkUpdateTest(oeSDKExtTest):

    @classmethod
    def setUpClass(self):
        self.publish_dir = os.path.join(self.tc.sdktestdir, 'esdk_publish')
        if os.path.exists(self.publish_dir):
            shutil.rmtree(self.publish_dir)
        os.mkdir(self.publish_dir)

        tcname_new = self.tc.d.expand(
            "${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}-new.sh")
        if not os.path.exists(tcname_new):
            tcname_new = self.tc.tcname

        cmd = 'oe-publish-sdk %s %s' % (tcname_new, self.publish_dir)
        subprocess.check_output(cmd, shell=True)

        self.http_service = HTTPService(self.publish_dir)
        self.http_service.start()

        self.http_url = "http://127.0.0.1:%d" % self.http_service.port

    def test_sdk_update_http(self):
        output = self._run("devtool sdk-update \"%s\"" % self.http_url)

    def test_sdk_update_local(self):
        output = self._run("devtool sdk-update \"%s\"" % self.publish_dir)

    @classmethod
    def tearDownClass(self):
        self.http_service.stop()
        shutil.rmtree(self.publish_dir)
