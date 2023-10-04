# OEQA on Arm FVPs

OE-Core's [oeqa][OEQA] framework provides a method of performing runtime tests on machines using the `testimage` Yocto task. meta-arm has good support for writing test cases against [Arm FVPs][FVP], meaning the [runfvp][RUNFVP] boot configuration can be re-used.

Tests can be configured to run automatically post-build by setting the variable `TESTIMAGE_AUTO="1"`, e.g. in your Kas file or local.conf.

meta-arm provides the OEFVPTarget which must be set up in the machine configuration:
```
TEST_TARGET = "OEFVPTarget"
TEST_SERVER_IP = "127.0.0.1"
TEST_TARGET_IP = "127.0.0.1:2222"
IMAGE_FEATURES:append = " ssh-server-dropbear"
FVP_CONFIG[bp.virtio_net.hostbridge.userNetPorts] ?= "2222=22"
FVP_CONSOLES[default] = "terminal_0"
FVP_CONSOLES[tf-a] = "s_terminal_0"
```

The test target also generates a log file with the prefix 'fvp_log' in the image recipe's `${WORKDIR}/testimage` containing the FVP's stdout.

OEFVPTarget supports two different test interfaces - SSH and pexpect.

## SSH

As in OEQA in OE-core, tests cases can run commands on the machine using SSH. It therefore requires that an SSH server is installed in the image.

This uses the `run` method on the target, e.g:
```
(status, output) = self.target.run('uname -a')
```
which executes a single command on the target (using `ssh -c`) and returns the status code and the output. It is therefore useful for running tests in a Linux environment.

For examples of test cases, see meta/lib/oeqa/runtime/cases in OE-Core. The majority of test cases depend on `ssh.SSHTest.test_ssh`, which first validates that the SSH connection is functioning.

## pexpect

To support firmware and baremetal testing, OEFVPTarget also allows test cases to make assertions against one or more consoles using the pexpect library.

Internally, this test target launches a [Pexpect][PEXPECT] instance for each entry in FVP_CONSOLES which can be used with the provided alias. The whole Pexpect API is exposed on the target, where the alias is always passed as the first argument, e.g.:
```
self.target.expect('default', r'root@.*\:~#', timeout=30)
self.assertNotIn(b'ERROR:', self.target.before('tf-a'))
```

For an example of a full test case, see meta-arm/lib/oeqa/runtime/cases/linuxboot.py This test case can be used to minimally verify that a machine boots to a Linux shell. The default timeout is 10 minutes, but this can be configured with the variable TEST_FVP_LINUX_BOOT_TIMEOUT, which expects a value in seconds.

[OEQA]: https://docs.yoctoproject.org/test-manual/intro.html
[FVP]: https://developer.arm.com/tools-and-software/simulation-models/fixed-virtual-platforms
[RUNFVP]: runfvp.md
[PEXPECT]: https://pexpect.readthedocs.io/en/stable/overview.html
