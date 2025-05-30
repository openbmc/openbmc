#
# Copyright 2018 by Garmin Ltd. or its subsidiaries
#
# SPDX-License-Identifier: MIT
#

from oeqa.sdk.context import OESDKTestContext, OESDKTestContextExecutor

class TestSDKBase(object):
    @staticmethod
    def get_sdk_configuration(d, test_type):
        import platform
        import oe.lsb
        from oeqa.utils.metadata import get_layers
        configuration = {'TEST_TYPE': test_type,
                        'MACHINE': d.getVar("MACHINE"),
                        'SDKMACHINE': d.getVar("SDKMACHINE"),
                        'IMAGE_BASENAME': d.getVar("IMAGE_BASENAME"),
                        'IMAGE_PKGTYPE': d.getVar("IMAGE_PKGTYPE"),
                        'STARTTIME': d.getVar("DATETIME"),
                        'HOST_DISTRO': oe.lsb.distro_identifier().replace(' ', '-'),
                        'LAYERS': get_layers(d.getVar("BBLAYERS"))}
        return configuration

    @staticmethod
    def get_sdk_result_id(configuration):
        return '%s_%s_%s_%s_%s' % (configuration['TEST_TYPE'], configuration['IMAGE_BASENAME'], configuration['SDKMACHINE'], configuration['MACHINE'], configuration['STARTTIME'])

class TestSDK(TestSDKBase):
    context_executor_class = OESDKTestContextExecutor
    context_class = OESDKTestContext
    test_type = 'sdk'

    def get_tcname(self, d):
        """
        Get the name of the SDK file
        """
        return d.expand("${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.sh")

    def extract_sdk(self, tcname, sdk_dir, d):
        """
        Extract the SDK to the specified location
        """
        import subprocess

        try:
            subprocess.check_output("cd %s; %s <<EOF\n./\nY\nEOF" % (sdk_dir, tcname), shell=True)
        except subprocess.CalledProcessError as e:
            bb.fatal("Couldn't install the SDK:\n%s" % e.output.decode("utf-8"))

    def setup_context(self, d):
        """
        Return a dictionary of additional arguments that should be passed to
        the context_class on construction
        """
        return dict()

    def run(self, d):

        import os
        import subprocess
        import json
        import logging

        from bb.utils import export_proxies
        from oeqa.utils import make_logger_bitbake_compatible
        from oeqa.utils import get_json_result_dir

        pn = d.getVar("PN")
        logger = make_logger_bitbake_compatible(logging.getLogger("BitBake"))

        # sdk use network for download projects for build
        export_proxies(d)

        # We need the original PATH for testing the eSDK, not with our manipulations
        os.environ['PATH'] = d.getVar("BB_ORIGENV", False).getVar("PATH")

        tcname = self.get_tcname(d)

        if not os.path.exists(tcname):
            bb.fatal("The toolchain %s is not built. Build it before running the tests: 'bitbake <image> -c populate_sdk' ." % tcname)

        tdname = d.expand("${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.testdata.json")
        test_data = json.load(open(tdname, "r"))

        target_pkg_manifest = self.context_executor_class._load_manifest(
            d.expand("${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.target.manifest"))
        host_pkg_manifest = self.context_executor_class._load_manifest(
            d.expand("${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.host.manifest"))

        processes = d.getVar("TESTIMAGE_NUMBER_THREADS") or d.getVar("BB_NUMBER_THREADS")
        if processes:
            try:
                import testtools, subunit
            except ImportError:
                bb.warn("Failed to import testtools or subunit, the testcases will run serially")
                processes = None

        sdk_dir = d.expand("${WORKDIR}/testimage-sdk/")
        bb.utils.remove(sdk_dir, True)
        bb.utils.mkdirhier(sdk_dir)

        context_args = self.setup_context(d)

        self.extract_sdk(tcname, sdk_dir, d)

        fail = False
        sdk_envs = self.context_executor_class._get_sdk_environs(sdk_dir)
        for s in sdk_envs:
            sdk_env = sdk_envs[s]
            bb.plain("SDK testing environment: %s" % s)
            tc = self.context_class(td=test_data, logger=logger, sdk_dir=sdk_dir,
                sdk_env=sdk_env, target_pkg_manifest=target_pkg_manifest,
                host_pkg_manifest=host_pkg_manifest, **context_args)

            try:
                modules = (d.getVar("TESTSDK_SUITES") or "").split()
                tc.loadTests(self.context_executor_class.default_cases, modules)
            except Exception as e:
                import traceback
                bb.fatal("Loading tests failed:\n%s" % traceback.format_exc())

            if processes:
                result = tc.runTests(processes=int(processes))
            else:
                result = tc.runTests()

            component = "%s %s" % (pn, self.context_executor_class.name)
            context_msg = "%s:%s" % (os.path.basename(tcname), os.path.basename(sdk_env))
            configuration = self.get_sdk_configuration(d, self.test_type)
            result.logDetails(get_json_result_dir(d),
                            configuration,
                            self.get_sdk_result_id(configuration))
            result.logSummary(component, context_msg)

            if not result.wasSuccessful():
                fail = True

        if fail:
            bb.fatal("%s - FAILED - check the task log and the commands log" % pn)


