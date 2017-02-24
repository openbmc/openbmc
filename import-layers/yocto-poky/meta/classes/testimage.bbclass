# Copyright (C) 2013 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)


# testimage.bbclass enables testing of qemu images using python unittests.
# Most of the tests are commands run on target image over ssh.
# To use it add testimage to global inherit and call your target image with -c testimage
# You can try it out like this:
# - first build a qemu core-image-sato
# - add IMAGE_CLASSES += "testimage" in local.conf
# - then bitbake core-image-sato -c testimage. That will run a standard suite of tests.

# You can set (or append to) TEST_SUITES in local.conf to select the tests
# which you want to run for your target.
# The test names are the module names in meta/lib/oeqa/runtime.
# Each name in TEST_SUITES represents a required test for the image. (no skipping allowed)
# Appending "auto" means that it will try to run all tests that are suitable for the image (each test decides that on it's own).
# Note that order in TEST_SUITES is relevant: tests are run in an order such that
# tests mentioned in @skipUnlessPassed run before the tests that depend on them,
# but without such dependencies, tests run in the order in which they are listed
# in TEST_SUITES.
#
# A layer can add its own tests in lib/oeqa/runtime, provided it extends BBPATH as normal in its layer.conf.

# TEST_LOG_DIR contains a command ssh log and may contain infromation about what command is running, output and return codes and for qemu a boot log till login.
# Booting is handled by this class, and it's not a test in itself.
# TEST_QEMUBOOT_TIMEOUT can be used to set the maximum time in seconds the launch code will wait for the login prompt.

TEST_LOG_DIR ?= "${WORKDIR}/testimage"

TEST_EXPORT_DIR ?= "${TMPDIR}/testimage/${PN}"
TEST_INSTALL_TMP_DIR ?= "${WORKDIR}/testimage/install_tmp"
TEST_NEEDED_PACKAGES_DIR ?= "${WORKDIR}/testimage/packages"
TEST_EXTRACTED_DIR ?= "${TEST_NEEDED_PACKAGES_DIR}/extracted"
TEST_PACKAGED_DIR ?= "${TEST_NEEDED_PACKAGES_DIR}/packaged"

RPMTESTSUITE = "${@bb.utils.contains('IMAGE_PKGTYPE', 'rpm', 'smart rpm', '', d)}"
MINTESTSUITE = "ping"
NETTESTSUITE = "${MINTESTSUITE} ssh df date scp syslog"
DEVTESTSUITE = "gcc kernelmodule ldd"

DEFAULT_TEST_SUITES = "${MINTESTSUITE} auto"
DEFAULT_TEST_SUITES_pn-core-image-minimal = "${MINTESTSUITE}"
DEFAULT_TEST_SUITES_pn-core-image-minimal-dev = "${MINTESTSUITE}"
DEFAULT_TEST_SUITES_pn-core-image-full-cmdline = "${NETTESTSUITE} perl python logrotate"
DEFAULT_TEST_SUITES_pn-core-image-x11 = "${MINTESTSUITE}"
DEFAULT_TEST_SUITES_pn-core-image-lsb = "${NETTESTSUITE} pam parselogs ${RPMTESTSUITE}"
DEFAULT_TEST_SUITES_pn-core-image-sato = "${NETTESTSUITE} connman xorg parselogs ${RPMTESTSUITE} \
    ${@bb.utils.contains('IMAGE_PKGTYPE', 'rpm', 'python', '', d)}"
DEFAULT_TEST_SUITES_pn-core-image-sato-sdk = "${NETTESTSUITE} connman xorg perl python \
    ${DEVTESTSUITE} parselogs ${RPMTESTSUITE}"
DEFAULT_TEST_SUITES_pn-core-image-lsb-dev = "${NETTESTSUITE} pam perl python parselogs ${RPMTESTSUITE}"
DEFAULT_TEST_SUITES_pn-core-image-lsb-sdk = "${NETTESTSUITE} buildcvs buildiptables buildgalculator \
    connman ${DEVTESTSUITE} pam perl python parselogs ${RPMTESTSUITE}"
DEFAULT_TEST_SUITES_pn-meta-toolchain = "auto"

# aarch64 has no graphics
DEFAULT_TEST_SUITES_remove_aarch64 = "xorg"

# qemumips is quite slow and has reached the timeout limit several times on the YP build cluster,
# mitigate this by removing build tests for qemumips machines.
MIPSREMOVE ??= "buildcvs buildiptables buildgalculator"
DEFAULT_TEST_SUITES_remove_qemumips = "${MIPSREMOVE}"
DEFAULT_TEST_SUITES_remove_qemumips64 = "${MIPSREMOVE}"

TEST_SUITES ?= "${DEFAULT_TEST_SUITES}"

TEST_QEMUBOOT_TIMEOUT ?= "1000"
TEST_TARGET ?= "qemu"

TESTIMAGEDEPENDS = ""
TESTIMAGEDEPENDS_qemuall = "qemu-native:do_populate_sysroot qemu-helper-native:do_populate_sysroot"
TESTIMAGEDEPENDS += "${@bb.utils.contains('IMAGE_PKGTYPE', 'rpm', 'cpio-native:do_populate_sysroot', '', d)}"
TESTIMAGEDEPENDS_qemuall += "${@bb.utils.contains('IMAGE_PKGTYPE', 'rpm', 'cpio-native:do_populate_sysroot', '', d)}"
TESTIMAGEDEPENDS_qemuall += "${@bb.utils.contains('IMAGE_PKGTYPE', 'rpm', 'createrepo-native:do_populate_sysroot', '', d)}"
TESTIMAGEDEPENDS += "${@bb.utils.contains('IMAGE_PKGTYPE', 'rpm', 'python-smartpm-native:do_populate_sysroot', '', d)}"
TESTIMAGEDEPENDS += "${@bb.utils.contains('IMAGE_PKGTYPE', 'ipk', 'opkg-utils-native:do_populate_sysroot', '', d)}"
TESTIMAGEDEPENDS += "${@bb.utils.contains('IMAGE_PKGTYPE', 'deb', 'apt-native:do_populate_sysroot', '', d)}"


TESTIMAGELOCK = "${TMPDIR}/testimage.lock"
TESTIMAGELOCK_qemuall = ""

TESTIMAGE_DUMP_DIR ?= "/tmp/oe-saved-tests/"

testimage_dump_target () {
    top -bn1
    ps
    free
    df
    # The next command will export the default gateway IP
    export DEFAULT_GATEWAY=$(ip route | awk '/default/ { print $3}')
    ping -c3 $DEFAULT_GATEWAY
    dmesg
    netstat -an
    ip address
    # Next command will dump logs from /var/log/
    find /var/log/ -type f 2>/dev/null -exec echo "====================" \; -exec echo {} \; -exec echo "====================" \; -exec cat {} \; -exec echo "" \;
}

testimage_dump_host () {
    top -bn1
    iostat -x -z -N -d -p ALL 20 2
    ps -ef
    free
    df
    memstat
    dmesg
    ip -s link
    netstat -an
}

python do_testimage() {
    testimage_main(d)
}

addtask testimage
do_testimage[nostamp] = "1"
do_testimage[depends] += "${TESTIMAGEDEPENDS}"
do_testimage[lockfiles] += "${TESTIMAGELOCK}"

def testimage_main(d):
    import unittest
    import os
    import oeqa.runtime
    import time
    import signal
    from oeqa.oetest import ImageTestContext
    from oeqa.targetcontrol import get_target_controller
    from oeqa.utils.dump import get_host_dumper

    pn = d.getVar("PN", True)
    bb.utils.mkdirhier(d.getVar("TEST_LOG_DIR", True))
    test_create_extract_dirs(d)

    # we need the host dumper in test context
    host_dumper = get_host_dumper(d)

    # the robot dance
    target = get_target_controller(d)

    # test context
    tc = ImageTestContext(d, target, host_dumper)

    # this is a dummy load of tests
    # we are doing that to find compile errors in the tests themselves
    # before booting the image
    try:
        tc.loadTests()
    except Exception as e:
        import traceback
        bb.fatal("Loading tests failed:\n%s" % traceback.format_exc())

    tc.extract_packages()
    target.deploy()
    try:
        bootparams = None
        if d.getVar('VIRTUAL-RUNTIME_init_manager', '') == 'systemd':
            bootparams = 'systemd.log_level=debug systemd.log_target=console'
        target.start(extra_bootparams=bootparams)
        starttime = time.time()
        result = tc.runTests()
        stoptime = time.time()
        if result.wasSuccessful():
            bb.plain("%s - Ran %d test%s in %.3fs" % (pn, result.testsRun, result.testsRun != 1 and "s" or "", stoptime - starttime))
            msg = "%s - OK - All required tests passed" % pn
            skipped = len(result.skipped)
            if skipped:
                msg += " (skipped=%d)" % skipped
            bb.plain(msg)
        else:
            bb.fatal("%s - FAILED - check the task log and the ssh log" % pn)
    finally:
        signal.signal(signal.SIGTERM, tc.origsigtermhandler)
        target.stop()

def test_create_extract_dirs(d):
    install_path = d.getVar("TEST_INSTALL_TMP_DIR", True)
    package_path = d.getVar("TEST_PACKAGED_DIR", True)
    extracted_path = d.getVar("TEST_EXTRACTED_DIR", True)
    bb.utils.mkdirhier(d.getVar("TEST_LOG_DIR", True))
    bb.utils.remove(package_path, recurse=True)
    bb.utils.mkdirhier(install_path)
    bb.utils.mkdirhier(package_path)
    bb.utils.mkdirhier(extracted_path)


testimage_main[vardepsexclude] =+ "BB_ORIGENV"

inherit testsdk
