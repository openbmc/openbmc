#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

class TestSDK(object):
    def run(self, d):
        import json
        import logging
        from oeqa.sdk.context import OESDKTestContext, OESDKTestContextExecutor
        from oeqa.utils import make_logger_bitbake_compatible

        pn = d.getVar("PN")

        logger = make_logger_bitbake_compatible(logging.getLogger("BitBake"))

        sdk_dir = d.expand("${WORKDIR}/testsdk/")
        bb.utils.remove(sdk_dir, True)
        bb.utils.mkdirhier(sdk_dir)

        sdk_envs = OESDKTestContextExecutor._get_sdk_environs(d.getVar("DEPLOY_DIR_IMAGE"))
        tdname = d.expand("${DEPLOY_DIR_IMAGE}/${PN}.testdata.json")
        test_data = json.load(open(tdname, "r"))

        host_pkg_manifest = {"cmake-native":"", "gcc-cross":"", "gettext-native":"", "meson-native":"", "perl-native":"", "python3-core-native":"", }
        target_pkg_manifest = {"gtk+3":""}

        for s in sdk_envs:
            bb.plain("meta-ide-support based SDK testing environment: %s" % s)

            sdk_env = sdk_envs[s]

            tc = OESDKTestContext(td=test_data, logger=logger, sdk_dir=sdk_dir,
                sdk_env=sdk_env, target_pkg_manifest=target_pkg_manifest,
                host_pkg_manifest=host_pkg_manifest)

            tc.loadTests(OESDKTestContextExecutor.default_cases)

            results = tc.runTests()
            if results:
                results.logSummary(pn)

            if (not results) or (not results.wasSuccessful()):
                bb.fatal('%s - FAILED' % (pn,), forcelog=True)
