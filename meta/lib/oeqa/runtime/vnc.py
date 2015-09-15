from oeqa.oetest import oeRuntimeTest, skipModuleUnless
from oeqa.utils.decorators import *
import re

def setUpModule():
    skipModuleUnless(oeRuntimeTest.hasPackage('x11vnc'), "No x11vnc package in image")

class VNCTest(oeRuntimeTest):

    @testcase(213)
    @skipUnlessPassed('test_ssh')
    def test_vnc(self):
        (status, output) = self.target.run('x11vnc -display :0 -bg -o x11vnc.log')
        self.assertEqual(status, 0, msg="x11vnc server failed to start: %s" % output)
        port = re.search('PORT=[0-9]*', output)
        self.assertTrue(port, msg="Listening port not specified in command output: %s" %output)

        vncport = port.group(0).split('=')[1]
        (status, output) = self.target.run('netstat -ntl | grep ":%s"' % vncport)
        self.assertEqual(status, 0, msg="x11vnc server not running on port %s\n\n%s" % (vncport, self.target.run('netstat -ntl; cat x11vnc.log')[1]))
