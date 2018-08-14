# This test should cover https://bugzilla.yoctoproject.org/tr_show_case.cgi?case_id=284
# testcase. Image under test must have meta-skeleton layer in bblayers and
# IMAGE_INSTALL_append = " service" in local.conf
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfDataVar
from oeqa.runtime.decorator.package import OEHasPackage

class SkeletonBasicTest(OERuntimeTestCase):

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['service'])
    @skipIfDataVar('VIRTUAL-RUNTIME_init_manager', 'systemd',
                   'Not appropiate for systemd image')
    def test_skeleton_availability(self):
        status, output = self.target.run('ls /etc/init.d/skeleton')
        msg = 'skeleton init script not found. Output:\n%s' % output
        self.assertEqual(status, 0, msg=msg)

        status, output =  self.target.run('ls /usr/sbin/skeleton-test')
        msg = 'skeleton-test not found. Output:\n%s' % output
        self.assertEqual(status, 0, msg=msg)

    @OETestID(284)
    @OETestDepends(['skeletoninit.SkeletonBasicTest.test_skeleton_availability'])
    def test_skeleton_script(self):
        output1 = self.target.run("/etc/init.d/skeleton start")[1]
        cmd = '%s | grep [s]keleton-test' % self.tc.target_cmds['ps']
        status, output2 = self.target.run(cmd)
        msg = ('Skeleton script could not be started:'
               '\n%s\n%s' % (output1, output2))
        self.assertEqual(status, 0, msg=msg)
