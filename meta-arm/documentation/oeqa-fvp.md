# OEQA on Arm FVPs

OE-Core's [oeqa][OEQA] framework provides a method of performing runtime tests on machines using the `testimage` Yocto task. meta-arm has good support for writing test cases against [Arm FVPs][FVP], meaning the [runfvp][RUNFVP] boot configuration can be re-used.

Tests can be configured to run automatically post-build by setting the variable `TESTIMAGE_AUTO="1"`, e.g. in your Kas file or local.conf.

There are two main methods of testing, using different test "targets".

## OEFVPTarget

This runs test cases on a machine using SSH. It therefore requires that an SSH server is installed in the image.

In test cases, the primary interface with the target is, e.g:
```
(status, output) = self.target.run('uname -a')
```
which runs a single command on the target (using `ssh -c`) and returns the status code and the output. It is therefore useful for running tests in a Linux environment.

For examples of test cases, see meta/lib/oeqa/runtime/cases in OE-Core. The majority of test cases depend on `ssh.SSHTest.test_ssh`, which first validates that the SSH connection is functioning.

Example machine configuration:
```
TEST_TARGET = "OEFVPTarget"
TEST_SERVER_IP = "127.0.0.1"
TEST_TARGET_IP = "127.0.0.1:8022"
IMAGE_FEATURES:append = " ssh-server-dropbear"
FVP_CONFIG[bp.virtio_net.hostbridge.userNetPorts] ?= "8022=22"
```

## OEFVPSerialTarget

This runs tests against one or more serial consoles on the FVP. It is more flexible than OEFVPTarget, but test cases written for this test target do not support the test cases in OE-core. As it does not require an SSH server, it is suitable for machines with performance or memory limitations.

Internally, this test target launches a [Pexpect][PEXPECT] instance for each entry in FVP_CONSOLES which can be used with the provided alias. The whole Pexpect API is exposed on the target, where the alias is always passed as the first argument, e.g.:
```
self.target.expect('default', r'root@.*\:~#', timeout=30)
self.assertNotIn(b'ERROR:', self.target.before('tf-a'))
```

For an example of a full test case, see meta-arm/lib/oeqa/runtime/cases/linuxboot.py This test case can be used to minimally verify that a machine boots to a Linux shell. The default timeout is 10 minutes, but this can be configured with the variable TEST_FVP_LINUX_BOOT_TIMEOUT, which expects a value in seconds.

The SSH interface described above is also available on OEFVPSerialTarget to support writing a set of hybrid test suites that use a combination of serial and SSH access. Note however that this test target does not guarantee that Linux has booted to shell prior to running any tests, so the test cases in OE-core are not supported.

Example machine configuration:
```
TEST_TARGET="OEFVPSerialTarget"
TEST_SUITES="linuxboot"
FVP_CONSOLES[default] = "terminal_0"
FVP_CONSOLES[tf-a] = "s_terminal_0"
```

[OEQA]: https://docs.yoctoproject.org/test-manual/intro.html
[FVP]: https://developer.arm.com/tools-and-software/simulation-models/fixed-virtual-platforms
[RUNFVP]: runfvp.md
[PEXPECT]: https://pexpect.readthedocs.io/en/stable/overview.html
