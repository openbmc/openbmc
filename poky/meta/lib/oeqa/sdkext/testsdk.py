#
# Copyright 2018 by Garmin Ltd. or its subsidiaries
#
# SPDX-License-Identifier: MIT
#

from oeqa.sdk.testsdk import TestSDKBase

class TestSDKExt(TestSDKBase):
    def run(self, d):
        import os
        import json
        import subprocess
        import logging

        from bb.utils import export_proxies
        from oeqa.utils import avoid_paths_in_environ, make_logger_bitbake_compatible, subprocesstweak
        from oeqa.sdkext.context import OESDKExtTestContext, OESDKExtTestContextExecutor
        from oeqa.utils import get_json_result_dir

        pn = d.getVar("PN")
        logger = make_logger_bitbake_compatible(logging.getLogger("BitBake"))

        # extensible sdk use network
        export_proxies(d)

        subprocesstweak.errors_have_output()

        # We need the original PATH for testing the eSDK, not with our manipulations
        os.environ['PATH'] = d.getVar("BB_ORIGENV", False).getVar("PATH")

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
                f.write('SSTATE_MIRRORS += "file://.* file://%s/PATH"\n' % test_data.get('SSTATE_DIR'))
                f.write('SOURCE_MIRROR_URL = "file://%s"\n' % test_data.get('DL_DIR'))
                f.write('INHERIT += "own-mirrors"\n')
                f.write('PREMIRRORS:prepend = "git://git.yoctoproject.org/.* git://%s/git2/git.yoctoproject.org.BASENAME "\n' % test_data.get('DL_DIR'))

            # We need to do this in case we have a minimal SDK
            subprocess.check_output(". %s > /dev/null; devtool sdk-install meta-extsdk-toolchain" % \
                    sdk_env, cwd=sdk_dir, shell=True, stderr=subprocess.STDOUT)

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
            configuration = self.get_sdk_configuration(d, 'sdkext')
            result.logDetails(get_json_result_dir(d),
                            configuration,
                            self.get_sdk_result_id(configuration))
            result.logSummary(component, context_msg)

            if not result.wasSuccessful():
                fail = True

            # Clean the workspace/sources to avoid `devtool add' failure because of non-empty source directory
            bb.utils.remove(sdk_dir+'workspace/sources', True)

        if fail:
            bb.fatal("%s - FAILED - check the task log and the commands log" % pn)

