# Copyright (C) 2013 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)


# testimage.bbclass enables testing of qemu images using python unittests.
# Most of the tests are commands run on target image over ssh.
# To use it add testimage to global inherit and call your target image with -c testimage
# You can try it out like this:
# - first build a qemu core-image-sato
# - add INHERIT += "testimage" in local.conf
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
TEST_EXPORT_ONLY ?= "0"

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
DEFAULT_TEST_SUITES_pn-core-image-lsb-sdk = "${NETTESTSUITE} buildcvs buildiptables buildsudoku \
    connman ${DEVTESTSUITE} pam perl python parselogs ${RPMTESTSUITE}"
DEFAULT_TEST_SUITES_pn-meta-toolchain = "auto"

# aarch64 has no graphics
DEFAULT_TEST_SUITES_remove_aarch64 = "xorg"

#qemumips is too slow for buildsudoku
DEFAULT_TEST_SUITES_remove_qemumips = "buildsudoku"

TEST_SUITES ?= "${DEFAULT_TEST_SUITES}"

TEST_QEMUBOOT_TIMEOUT ?= "1000"
TEST_TARGET ?= "qemu"
TEST_TARGET_IP ?= ""
TEST_SERVER_IP ?= ""

TESTIMAGEDEPENDS = ""
TESTIMAGEDEPENDS_qemuall = "qemu-native:do_populate_sysroot qemu-helper-native:do_populate_sysroot"

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

def exportTests(d,tc):
    import json
    import shutil
    import pkgutil
    import re

    exportpath = d.getVar("TEST_EXPORT_DIR", True)

    savedata = {}
    savedata["d"] = {}
    savedata["target"] = {}
    savedata["host_dumper"] = {}
    for key in tc.__dict__:
        # special cases
        if key not in ['d', 'target', 'host_dumper', 'suite']:
            savedata[key] = getattr(tc, key)
    savedata["target"]["ip"] = tc.target.ip or d.getVar("TEST_TARGET_IP", True)
    savedata["target"]["server_ip"] = tc.target.server_ip or d.getVar("TEST_SERVER_IP", True)

    keys = [ key for key in d.keys() if not key.startswith("_") and not key.startswith("BB") \
            and not key.startswith("B_pn") and not key.startswith("do_") and not d.getVarFlag(key, "func", True)]
    for key in keys:
        try:
            savedata["d"][key] = d.getVar(key, True)
        except bb.data_smart.ExpansionError:
            # we don't care about those anyway
            pass

    savedata["host_dumper"]["parent_dir"] = tc.host_dumper.parent_dir
    savedata["host_dumper"]["cmds"] = tc.host_dumper.cmds

    json_file = os.path.join(exportpath, "testdata.json")
    with open(json_file, "w") as f:
            json.dump(savedata, f, skipkeys=True, indent=4, sort_keys=True)

    # Replace absolute path with relative in the file
    exclude_path = os.path.join(d.getVar("COREBASE", True),'meta','lib','oeqa')
    f1 = open(json_file,'r').read()
    f2 = open(json_file,'w')
    m = f1.replace(exclude_path,'oeqa')
    f2.write(m)
    f2.close()

    # now start copying files
    # we'll basically copy everything under meta/lib/oeqa, with these exceptions
    #  - oeqa/targetcontrol.py - not needed
    #  - oeqa/selftest - something else
    # That means:
    #   - all tests from oeqa/runtime defined in TEST_SUITES (including from other layers)
    #   - the contents of oeqa/utils and oeqa/runtime/files
    #   - oeqa/oetest.py and oeqa/runexport.py (this will get copied to exportpath not exportpath/oeqa)
    #   - __init__.py files
    bb.utils.mkdirhier(os.path.join(exportpath, "oeqa/runtime/files"))
    bb.utils.mkdirhier(os.path.join(exportpath, "oeqa/utils"))
    # copy test modules, this should cover tests in other layers too
    bbpath = d.getVar("BBPATH", True).split(':')
    for t in tc.testslist:
        isfolder = False
        if re.search("\w+\.\w+\.test_\S+", t):
            t = '.'.join(t.split('.')[:3])
        mod = pkgutil.get_loader(t)
        # More depth than usual?
        if (t.count('.') > 2):
            for p in bbpath:
                foldername = os.path.join(p, 'lib',  os.sep.join(t.split('.')).rsplit(os.sep, 1)[0])
                if os.path.isdir(foldername):
                    isfolder = True
                    target_folder = os.path.join(exportpath, "oeqa", "runtime", os.path.basename(foldername))
                    if not os.path.exists(target_folder):
                        shutil.copytree(foldername, target_folder)
        if not isfolder:
            shutil.copy2(mod.filename, os.path.join(exportpath, "oeqa/runtime"))
    # copy __init__.py files
    oeqadir = pkgutil.get_loader("oeqa").filename
    shutil.copy2(os.path.join(oeqadir, "__init__.py"), os.path.join(exportpath, "oeqa"))
    shutil.copy2(os.path.join(oeqadir, "runtime/__init__.py"), os.path.join(exportpath, "oeqa/runtime"))
    # copy oeqa/oetest.py and oeqa/runexported.py
    shutil.copy2(os.path.join(oeqadir, "oetest.py"), os.path.join(exportpath, "oeqa"))
    shutil.copy2(os.path.join(oeqadir, "runexported.py"), exportpath)
    # copy oeqa/utils/*.py
    for root, dirs, files in os.walk(os.path.join(oeqadir, "utils")):
        for f in files:
            if f.endswith(".py"):
                shutil.copy2(os.path.join(root, f), os.path.join(exportpath, "oeqa/utils"))
    # copy oeqa/runtime/files/*
    for root, dirs, files in os.walk(os.path.join(oeqadir, "runtime/files")):
        for f in files:
            shutil.copy2(os.path.join(root, f), os.path.join(exportpath, "oeqa/runtime/files"))

    bb.plain("Exported tests to: %s" % exportpath)

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
    export = oe.utils.conditional("TEST_EXPORT_ONLY", "1", True, False, d)
    bb.utils.mkdirhier(d.getVar("TEST_LOG_DIR", True))
    if export:
        bb.utils.remove(d.getVar("TEST_EXPORT_DIR", True), recurse=True)
        bb.utils.mkdirhier(d.getVar("TEST_EXPORT_DIR", True))

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

    if export:
        signal.signal(signal.SIGTERM, tc.origsigtermhandler)
        tc.origsigtermhandler = None
        exportTests(d,tc)
    else:
        target.deploy()
        try:
            target.start()
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
                raise bb.build.FuncFailed("%s - FAILED - check the task log and the ssh log" % pn )
        finally:
            signal.signal(signal.SIGTERM, tc.origsigtermhandler)
            target.stop()

testimage_main[vardepsexclude] =+ "BB_ORIGENV"

inherit testsdk
