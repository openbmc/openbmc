# Copyright (C) 2013 - 2016 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# testsdk.bbclass enables testing for SDK and Extensible SDK
#
# To run SDK tests, run the commands:
# $ bitbake <image-name> -c populate_sdk
# $ bitbake <image-name> -c testsdk
#
# To run eSDK tests, run the commands:
# $ bitbake <image-name> -c populate_sdk_ext
# $ bitbake <image-name> -c testsdkext
#
# where "<image-name>" is an image like core-image-sato.

def testsdk_main(d):
    import os
    import subprocess
    import json
    import logging

    from bb.utils import export_proxies
    from oeqa.sdk.context import OESDKTestContext, OESDKTestContextExecutor
    from oeqa.utils import make_logger_bitbake_compatible

    bb.event.enable_threadlock()

    pn = d.getVar("PN")
    logger = make_logger_bitbake_compatible(logging.getLogger("BitBake"))

    # sdk use network for download projects for build
    export_proxies(d)

    tcname = d.expand("${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.sh")
    if not os.path.exists(tcname):
        bb.fatal("The toolchain %s is not built. Build it before running the tests: 'bitbake <image> -c populate_sdk' ." % tcname)

    tdname = d.expand("${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.testdata.json")
    test_data = json.load(open(tdname, "r"))

    target_pkg_manifest = OESDKTestContextExecutor._load_manifest(
        d.expand("${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.target.manifest"))
    host_pkg_manifest = OESDKTestContextExecutor._load_manifest(
        d.expand("${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.host.manifest"))

    sdk_dir = d.expand("${WORKDIR}/testimage-sdk/")
    bb.utils.remove(sdk_dir, True)
    bb.utils.mkdirhier(sdk_dir)
    try:
        subprocess.check_output("cd %s; %s <<EOF\n./\nY\nEOF" % (sdk_dir, tcname), shell=True)
    except subprocess.CalledProcessError as e:
        bb.fatal("Couldn't install the SDK:\n%s" % e.output.decode("utf-8"))

    fail = False
    sdk_envs = OESDKTestContextExecutor._get_sdk_environs(sdk_dir)
    for s in sdk_envs:
        sdk_env = sdk_envs[s]
        bb.plain("SDK testing environment: %s" % s)
        tc = OESDKTestContext(td=test_data, logger=logger, sdk_dir=sdk_dir,
            sdk_env=sdk_env, target_pkg_manifest=target_pkg_manifest,
            host_pkg_manifest=host_pkg_manifest)

        try:
            tc.loadTests(OESDKTestContextExecutor.default_cases)
        except Exception as e:
            import traceback
            bb.fatal("Loading tests failed:\n%s" % traceback.format_exc())

        result = tc.runTests()

        component = "%s %s" % (pn, OESDKTestContextExecutor.name)
        context_msg = "%s:%s" % (os.path.basename(tcname), os.path.basename(sdk_env))

        result.logDetails()
        result.logSummary(component, context_msg)

        if not result.wasSuccessful():
            fail = True

    if fail:
        bb.fatal("%s - FAILED - check the task log and the commands log" % pn)
  
testsdk_main[vardepsexclude] =+ "BB_ORIGENV"

python do_testsdk() {
    testsdk_main(d)
}
addtask testsdk
do_testsdk[nostamp] = "1"

def testsdkext_main(d):
    import os
    import json
    import subprocess
    import logging

    from bb.utils import export_proxies
    from oeqa.utils import avoid_paths_in_environ, make_logger_bitbake_compatible, subprocesstweak
    from oeqa.sdkext.context import OESDKExtTestContext, OESDKExtTestContextExecutor

    bb.event.enable_threadlock()

    pn = d.getVar("PN")
    logger = make_logger_bitbake_compatible(logging.getLogger("BitBake"))

    # extensible sdk use network
    export_proxies(d)

    subprocesstweak.errors_have_output()

    # extensible sdk can be contaminated if native programs are
    # in PATH, i.e. use perl-native instead of eSDK one.
    paths_to_avoid = [d.getVar('STAGING_DIR'),
                      d.getVar('BASE_WORKDIR')]
    os.environ['PATH'] = avoid_paths_in_environ(paths_to_avoid)

    tcname = d.expand("${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.sh")
    if not os.path.exists(tcname):
        bb.fatal("The toolchain ext %s is not built. Build it before running the" \
                 " tests: 'bitbake <image> -c populate_sdk_ext' ." % tcname)

    tdname = d.expand("${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.testdata.json")
    test_data = json.load(open(tdname, "r"))

    target_pkg_manifest = OESDKExtTestContextExecutor._load_manifest(
        d.expand("${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.target.manifest"))
    host_pkg_manifest = OESDKExtTestContextExecutor._load_manifest(
        d.expand("${SDK_DEPLOY}/${TOOLCHAINEXT_OUTPUTNAME}.host.manifest"))

    sdk_dir = d.expand("${WORKDIR}/testsdkext/")
    bb.utils.remove(sdk_dir, True)
    bb.utils.mkdirhier(sdk_dir)
    try:
        subprocess.check_output("%s -y -d %s" % (tcname, sdk_dir), shell=True)
    except subprocess.CalledProcessError as e:
        msg = "Couldn't install the extensible SDK:\n%s" % e.output.decode("utf-8")
        logfn = os.path.join(sdk_dir, 'preparing_build_system.log')
        if os.path.exists(logfn):
            msg += '\n\nContents of preparing_build_system.log:\n'
            with open(logfn, 'r') as f:
                for line in f:
                    msg += line
        bb.fatal(msg)

    fail = False
    sdk_envs = OESDKExtTestContextExecutor._get_sdk_environs(sdk_dir)
    for s in sdk_envs:
        bb.plain("Extensible SDK testing environment: %s" % s)

        sdk_env = sdk_envs[s]

        # Use our own SSTATE_DIR and DL_DIR so that updates to the eSDK come from our sstate cache
        # and we don't spend hours downloading kernels for the kernel module test
        # Abuse auto.conf since local.conf would be overwritten by the SDK
        with open(os.path.join(sdk_dir, 'conf', 'auto.conf'), 'a+') as f:
            f.write('SSTATE_MIRRORS += " \\n file://.* file://%s/PATH"\n' % test_data.get('SSTATE_DIR'))
            f.write('SOURCE_MIRROR_URL = "file://%s"\n' % test_data.get('DL_DIR'))
            f.write('INHERIT += "own-mirrors"')

        # We need to do this in case we have a minimal SDK
        subprocess.check_output(". %s > /dev/null; devtool sdk-install meta-extsdk-toolchain" % sdk_env, cwd=sdk_dir, shell=True)

        tc = OESDKExtTestContext(td=test_data, logger=logger, sdk_dir=sdk_dir,
            sdk_env=sdk_env, target_pkg_manifest=target_pkg_manifest,
            host_pkg_manifest=host_pkg_manifest)

        try:
            tc.loadTests(OESDKExtTestContextExecutor.default_cases)
        except Exception as e:
            import traceback
            bb.fatal("Loading tests failed:\n%s" % traceback.format_exc())

        result = tc.runTests()

        component = "%s %s" % (pn, OESDKExtTestContextExecutor.name)
        context_msg = "%s:%s" % (os.path.basename(tcname), os.path.basename(sdk_env))

        result.logDetails()
        result.logSummary(component, context_msg)

        if not result.wasSuccessful():
            fail = True

    if fail:
        bb.fatal("%s - FAILED - check the task log and the commands log" % pn)

testsdkext_main[vardepsexclude] =+ "BB_ORIGENV"

python do_testsdkext() {
    testsdkext_main(d)
}
addtask testsdkext
do_testsdkext[nostamp] = "1"

