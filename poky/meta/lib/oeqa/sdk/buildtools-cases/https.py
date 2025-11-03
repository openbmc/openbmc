#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.sdk.case import OESDKTestCase
from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class HTTPTests(OESDKTestCase):
    """
    Verify that HTTPS certificates are working correctly, as this depends on
    environment variables being set correctly.
    """

    def test_wget(self):
        self._run('env -i wget --debug --output-document /dev/null https://www.yoctoproject.org/connectivity.html')

    def test_python(self):
        # urlopen() returns a file-like object on success and throws an exception otherwise
        self._run('python3 -c \'import urllib.request; urllib.request.urlopen("https://www.yoctoproject.org/connectivity.html")\'')
