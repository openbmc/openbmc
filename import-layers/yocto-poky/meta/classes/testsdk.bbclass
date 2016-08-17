# Copyright (C) 2013 - 2016 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# testsdk.bbclass enables testing for SDK and Extensible SDK
#
# For run SDK tests you need to do,
# - bitbake core-image-sato -c populate_sdk
# - bitbake core-image-sato -c testsdk
#
# For run eSDK tests you need to do,
# - bitbake core-image-sato -c populate_sdk_ext
# - bitbake core-image-sato -c testsdkext

TEST_LOG_DIR ?= "${WORKDIR}/testimage"
TESTSDKLOCK = "${TMPDIR}/testsdk.lock"

def run_test_context(CTestContext, d, testdir, tcname, pn, *args):
    import glob
    import time

    targets = glob.glob(d.expand(testdir + "/tc/environment-setup-*"))
    for sdkenv in targets:
        bb.plain("Testing %s" % sdkenv)
        tc = CTestContext(d, testdir, sdkenv, tcname, args)

        # this is a dummy load of tests
        # we are doing that to find compile errors in the tests themselves
        # before booting the image
        try:
            tc.loadTests()
        except Exception as e:
            import traceback
            bb.fatal("Loading tests failed:\n%s" % traceback.format_exc())

        starttime = time.time()
        result = tc.runTests()
        stoptime = time.time()
        if result.wasSuccessful():
            bb.plain("%s SDK(%s):%s - Ran %d test%s in %.3fs" % (pn, os.path.basename(tcname), os.path.basename(sdkenv),result.testsRun, result.testsRun != 1 and "s" or "", stoptime - starttime))
            msg = "%s - OK - All required tests passed" % pn
            skipped = len(result.skipped)
            if skipped:
                msg += " (skipped=%d)" % skipped
            bb.plain(msg)
        else:
            raise bb.build.FuncFailed("%s - FAILED - check the task log and the commands log" % pn )

def testsdk_main(d):
    import os
    import oeqa.sdk
    import subprocess
    from oeqa.oetest import SDKTestContext

    pn = d.getVar("PN", True)
    bb.utils.mkdirhier(d.getVar("TEST_LOG_DIR", True))

    tcname = d.expand("${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.sh")
    if not os.path.exists(tcname):
        bb.fatal("The toolchain is not built. Build it before running the tests: 'bitbake <image> -c populate_sdk' .")

    sdktestdir = d.expand("${WORKDIR}/testimage-sdk/")
    bb.utils.remove(sdktestdir, True)
    bb.utils.mkdirhier(sdktestdir)
    try:
        subprocess.check_output("cd %s; %s <<EOF\n./tc\nY\nEOF" % (sdktestdir, tcname), shell=True)
    except subprocess.CalledProcessError as e:
        bb.fatal("Couldn't install the SDK:\n%s" % e.output)

    try:
        run_test_context(SDKTestContext, d, sdktestdir, tcname, pn)
    finally:
        bb.utils.remove(sdktestdir, True)

testsdk_main[vardepsexclude] =+ "BB_ORIGENV"

python do_testsdk() {
    testsdk_main(d)
}
addtask testsdk
do_testsdk[nostamp] = "1"
do_testsdk[lockfiles] += "${TESTSDKLOCK}"

TEST_LOG_SDKEXT_DIR ?= "${WORKDIR}/testsdkext"
TESTSDKEXTLOCK = "${TMPDIR}/testsdkext.lock"

def testsdkext_main(d):
    import os
    import oeqa.sdkext
    import subprocess
    from bb.utils import export_proxies
    from oeqa.oetest import SDKTestContext, SDKExtTestContext
    from oeqa.utils import avoid_paths_in_environ


    # extensible sdk use network
    export_proxies(d)

    # extensible sdk can be contaminated if native programs are
    # in PATH, i.e. use perl-native instead of eSDK one.
    paths_to_avoid = [d.getVar('STAGING_DIR', True),
                      d.getVar('BASE_WORKDIR', True)]
    os.environ['PATH'] = avoid_paths_in_environ(paths_to_avoid)

    pn = d.getVar("PN", True)
    bb.utils.mkdirhier(d.getVar("TEST_LOG_SDKEXT_DIR", True))

    tcname = d.expand("${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.sh")
    if not os.path.exists(tcname):
        bb.fatal("The toolchain ext is not built. Build it before running the" \
                 " tests: 'bitbake <image> -c populate_sdk_ext' .")

    testdir = d.expand("${WORKDIR}/testsdkext/")
    bb.utils.remove(testdir, True)
    bb.utils.mkdirhier(testdir)
    try:
        subprocess.check_output("%s -y -d %s/tc" % (tcname, testdir), shell=True)
    except subprocess.CalledProcessError as e:
        bb.fatal("Couldn't install the SDK EXT:\n%s" % e.output)

    try:
        bb.plain("Running SDK Compatibility tests ...")
        run_test_context(SDKExtTestContext, d, testdir, tcname, pn, True)
    finally:
        pass

    try:
        bb.plain("Running Extensible SDK tests ...")
        run_test_context(SDKExtTestContext, d, testdir, tcname, pn)
    finally:
        pass

    bb.utils.remove(testdir, True)

testsdkext_main[vardepsexclude] =+ "BB_ORIGENV"

python do_testsdkext() {
    testsdkext_main(d)
}
addtask testsdkext
do_testsdkext[nostamp] = "1"
do_testsdkext[lockfiles] += "${TESTSDKEXTLOCK}"
