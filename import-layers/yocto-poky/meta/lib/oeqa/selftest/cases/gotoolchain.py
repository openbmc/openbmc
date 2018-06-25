import glob
import os
import shutil
import tempfile
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_vars


class oeGoToolchainSelfTest(OESelftestTestCase):
    """
    Test cases for OE's Go toolchain
    """

    @staticmethod
    def get_sdk_environment(tmpdir_SDKQA):
        pattern = os.path.join(tmpdir_SDKQA, "environment-setup-*")
        # FIXME: this is a very naive implementation
        return glob.glob(pattern)[0]

    @staticmethod
    def get_sdk_toolchain():
        bb_vars = get_bb_vars(['SDK_DEPLOY', 'TOOLCHAIN_OUTPUTNAME'],
                              "meta-go-toolchain")
        sdk_deploy = bb_vars['SDK_DEPLOY']
        toolchain_name = bb_vars['TOOLCHAIN_OUTPUTNAME']
        return os.path.join(sdk_deploy, toolchain_name + ".sh")

    @classmethod
    def setUpClass(cls):
        super(oeGoToolchainSelfTest, cls).setUpClass()
        cls.tmpdir_SDKQA = tempfile.mkdtemp(prefix='SDKQA')
        cls.go_path = os.path.join(cls.tmpdir_SDKQA, "go")
        # Build the SDK and locate it in DEPLOYDIR
        bitbake("meta-go-toolchain")
        cls.sdk_path = oeGoToolchainSelfTest.get_sdk_toolchain()
        # Install the SDK into the tmpdir
        runCmd("sh %s -y -d \"%s\"" % (cls.sdk_path, cls.tmpdir_SDKQA))
        cls.env_SDK = oeGoToolchainSelfTest.get_sdk_environment(cls.tmpdir_SDKQA)

    @classmethod
    def tearDownClass(cls):
        shutil.rmtree(cls.tmpdir_SDKQA, ignore_errors=True)
        super(oeGoToolchainSelfTest, cls).tearDownClass()

    def run_sdk_go_command(self, gocmd):
        cmd = "cd %s; " % self.tmpdir_SDKQA
        cmd = cmd + ". %s; " % self.env_SDK
        cmd = cmd + "export GOPATH=%s; " % self.go_path
        cmd = cmd + "${CROSS_COMPILE}go %s" % gocmd
        return runCmd(cmd).status

    def test_go_dep_build(self):
        proj = "github.com/golang"
        name = "dep"
        ver = "v0.3.1"
        archive = ".tar.gz"
        url = "https://%s/%s/archive/%s%s" % (proj, name, ver, archive)

        runCmd("cd %s; wget %s" % (self.tmpdir_SDKQA, url))
        runCmd("cd %s; tar -xf %s" % (self.tmpdir_SDKQA, ver+archive))
        runCmd("mkdir -p %s/src/%s" % (self.go_path, proj))
        runCmd("mv %s/dep-0.3.1 %s/src/%s/%s"
               % (self.tmpdir_SDKQA, self.go_path, proj, name))
        retv = self.run_sdk_go_command('build  %s/%s/cmd/dep'
                                       % (proj, name))
        self.assertEqual(retv, 0,
                         msg="Running go build failed for %s" % name)
