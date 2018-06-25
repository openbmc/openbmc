import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage

class GObjectIntrospectionTest(OERuntimeTestCase):

    @OETestDepends(["ssh.SSHTest.test_ssh"])
    @OEHasPackage(["python3-pygobject"])
    def test_python(self):
        script = """from gi.repository import GObject; print(GObject.markup_escape_text("<testing&testing>"))"""
        status, output = self.target.run("python3 -c '%s'" % script)
        self.assertEqual(status, 0, msg="Python failed (%s)" % (output))
        self.assertEqual(output, "&lt;testing&amp;testing&gt;", msg="Unexpected output (%s)" % output)
