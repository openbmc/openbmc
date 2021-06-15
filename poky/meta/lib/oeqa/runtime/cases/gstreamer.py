#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.runtime.decorator.package import OEHasPackage

class GstreamerCliTest(OERuntimeTestCase):

    @OEHasPackage(['gstreamer1.0'])
    def test_gst_inspect_can_list_all_plugins(self):
        status, output = self.target.run('gst-inspect-1.0')
        self.assertEqual(status, 0, 'gst-inspect-1.0 does not appear to be running.')

    @OEHasPackage(['gstreamer1.0'])
    def test_gst_launch_can_create_video_pipeline(self):
        status, output = self.target.run('gst-launch-1.0 -v fakesrc silent=false num-buffers=3 ! fakesink silent=false')
        self.assertEqual(status, 0, 'gst-launch-1.0 does not appear to be running.')
