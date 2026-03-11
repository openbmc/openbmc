#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import shutil
import subprocess
import tempfile
import time
import os
from datetime import datetime
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.runtime.decorator.package import OEHasPackage

### Status of qemu images.
#   - runqemu qemuppc64 comes up blank. (skip)
#   - qemuarmv5 comes up with multiple heads but sending "head" to screendump.
#     seems to create a png with a bad header? (skip for now, but come back to fix)
#   - qemuriscv32 and qemuloongarch64 doesn't work with testimage apparently? (skip)
#   - qemumips64 is missing mouse icon.
#   - qemumips takes forever to render and is missing mouse icon.
#   - qemuarm and qemuppc are odd as they don't resize so we need to just set width.
#   - All images have home and screen flipper icons not always rendered fully at first.
#     the sleep seems to help this out some, depending on machine load.
###

class LoginTest(OERuntimeTestCase):
    @OEHasPackage(['matchbox-desktop', 'dbus-wait'])
    def test_screenshot(self):
        if self.td.get('MACHINE') in ("qemuppc64", "qemuarmv5", "qemuriscv32", "qemuriscv64", "qemuloongarch64"):
            self.skipTest("{0} is not currently supported.".format(self.td.get('MACHINE')))

        pn = self.td.get('PN')

        ourenv = os.environ.copy()
        origpath = self.td.get("ORIGPATH")
        if origpath:
            ourenv['PATH'] = ourenv['PATH'] + ":" + origpath

        for cmd in ["identify.im7", "convert.im7", "compare.im7"]:
            try:
                subprocess.check_output(["which", cmd], env=ourenv)
            except subprocess.CalledProcessError:
                self.skipTest("%s (from imagemagick) not available" % cmd)


        # Store images so we can debug them if needed
        saved_screenshots_dir = self.td.get('T') + "/saved-screenshots/"

        ###
        # This is a really horrible way of doing this but I've not found the
        # right event to determine "The system is loaded and screen is rendered"
        #
        # Using dbus-wait for matchbox is the wrong answer because while it
        # ensures the system is up, it doesn't mean the screen is rendered.
        #
        # Checking the qmp socket doesn't work afaik either.
        #
        # One way to do this is to do compares of known good screendumps until
        # we either get expected or close to expected or we time out. Part of the
        # issue here with that is that there is a very fine difference in the
        # diff between a screendump where the icons haven't loaded yet and
        # one where they won't load. I'll look at that next, but, for now, this.
        #
        # Which is ugly and I hate it but it 'works' for various definitions of
        # 'works'.
        ###
        # RP: if the signal is sent before we run this, it will never be seen and we'd timeout
        #status, output = self.target.run('dbus-wait org.matchbox_project.desktop Loaded')
        #if status != 0 or "Timeout" in output:
        #    self.fail('dbus-wait failed (%s, %s). This could mean that the image never loaded the matchbox desktop.' % (status, output))

        # Start taking screenshots every 2 seconds until diff=0 or timeout is 60 seconds
        timeout = time.time() + 60
        diff = True
        with tempfile.NamedTemporaryFile(prefix="oeqa-screenshot-login", suffix=".png") as t:
            while diff != 0 and time.time() < timeout:
                time.sleep(2)
                ret = self.target.runner.run_monitor("screendump", args={"filename": t.name, "format":"png"})

                # Find out size of image so we can determine where to blank out clock.
                # qemuarm and qemuppc are odd as it doesn't resize the window and returns
                # incorrect widths
                if self.td.get('MACHINE') == "qemuarm" or self.td.get('MACHINE') == "qemuppc":
                    width = "640"
                else:
                    cmd = "identify.im7 -ping -format '%w' {0}".format(t.name)
                    width = subprocess.check_output(cmd, shell=True, env=ourenv).decode()

                rblank = int(float(width))
                lblank = rblank-80

                # Use the meta-oe version of convert, along with it's suffix. This blanks out the clock.
                cmd = "convert.im7 {0} -fill white -draw 'rectangle {1},4 {2},28' {3}".format(t.name, str(rblank), str(lblank), t.name)
                convert_out=subprocess.check_output(cmd, shell=True, env=ourenv).decode()

                bb.utils.mkdirhier(saved_screenshots_dir)
                savedfile = "{0}/saved-{1}-{2}-{3}.png".format(saved_screenshots_dir, \
                                                                            datetime.timestamp(datetime.now()), \
                                                                            pn, \
                                                                            self.td.get('MACHINE'))
                shutil.copy2(t.name, savedfile)

                refimage = self.td.get('COREBASE') + "/meta/files/screenshot-tests/" + pn + "-" + self.td.get('MACHINE') +".png"
                if not os.path.exists(refimage):
                    self.skipTest("No reference image for comparision (%s)" % refimage)

                cmd = "compare.im7 -metric MSE {0} {1} /dev/null".format(t.name, refimage)
                compare_out = subprocess.run(cmd, shell=True, capture_output=True, text=True, env=ourenv)
                diff=float(compare_out.stderr.replace("(", "").replace(")","").split()[1])
            if diff > 0:
                # Keep a copy of the failed screenshot so we can see what happened.
                self.fail("Screenshot diff is {0}. Failed image stored in {1}".format(str(diff), savedfile))
            else:
                self.assertEqual(0, diff, "Screenshot diff is {0}.".format(str(diff)))
