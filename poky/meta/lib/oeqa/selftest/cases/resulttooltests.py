#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import sys
basepath = os.path.abspath(os.path.dirname(__file__) + '/../../../../../')
lib_path = basepath + '/scripts/lib'
sys.path = sys.path + [lib_path]
from resulttool.report import ResultsTextReport
from resulttool import regression as regression
from resulttool import resultutils as resultutils
from oeqa.selftest.case import OESelftestTestCase

class ResultToolTests(OESelftestTestCase):
    base_results_data = {'base_result1': {'configuration': {"TEST_TYPE": "runtime",
                                                            "TESTSERIES": "series1",
                                                            "IMAGE_BASENAME": "image",
                                                            "IMAGE_PKGTYPE": "ipk",
                                                            "DISTRO": "mydistro",
                                                            "MACHINE": "qemux86"},
                                          'result': {}},
                         'base_result2': {'configuration': {"TEST_TYPE": "runtime",
                                                            "TESTSERIES": "series1",
                                                            "IMAGE_BASENAME": "image",
                                                            "IMAGE_PKGTYPE": "ipk",
                                                            "DISTRO": "mydistro",
                                                            "MACHINE": "qemux86-64"},
                                          'result': {}}}
    target_results_data = {'target_result1': {'configuration': {"TEST_TYPE": "runtime",
                                                                "TESTSERIES": "series1",
                                                                "IMAGE_BASENAME": "image",
                                                                "IMAGE_PKGTYPE": "ipk",
                                                                "DISTRO": "mydistro",
                                                                "MACHINE": "qemux86"},
                                          'result': {}},
                           'target_result2': {'configuration': {"TEST_TYPE": "runtime",
                                                                "TESTSERIES": "series1",
                                                                "IMAGE_BASENAME": "image",
                                                                "IMAGE_PKGTYPE": "ipk",
                                                                "DISTRO": "mydistro",
                                                                "MACHINE": "qemux86"},
                                          'result': {}},
                           'target_result3': {'configuration': {"TEST_TYPE": "runtime",
                                                                "TESTSERIES": "series1",
                                                                "IMAGE_BASENAME": "image",
                                                                "IMAGE_PKGTYPE": "ipk",
                                                                "DISTRO": "mydistro",
                                                                "MACHINE": "qemux86-64"},
                                          'result': {}}}

    def test_report_can_aggregate_test_result(self):
        result_data = {'result': {'test1': {'status': 'PASSED'},
                                  'test2': {'status': 'PASSED'},
                                  'test3': {'status': 'FAILED'},
                                  'test4': {'status': 'ERROR'},
                                  'test5': {'status': 'SKIPPED'}}}
        report = ResultsTextReport()
        result_report = report.get_aggregated_test_result(None, result_data, 'DummyMachine')
        self.assertTrue(result_report['passed'] == 2, msg="Passed count not correct:%s" % result_report['passed'])
        self.assertTrue(result_report['failed'] == 2, msg="Failed count not correct:%s" % result_report['failed'])
        self.assertTrue(result_report['skipped'] == 1, msg="Skipped count not correct:%s" % result_report['skipped'])

    def test_regression_can_get_regression_base_target_pair(self):

        results = {}
        resultutils.append_resultsdata(results, ResultToolTests.base_results_data)
        resultutils.append_resultsdata(results, ResultToolTests.target_results_data)
        self.assertTrue('target_result1' in results['runtime/mydistro/qemux86/image'], msg="Pair not correct:%s" % results)
        self.assertTrue('target_result3' in results['runtime/mydistro/qemux86-64/image'], msg="Pair not correct:%s" % results)

    def test_regression_can_get_regression_result(self):
        base_result_data = {'result': {'test1': {'status': 'PASSED'},
                                       'test2': {'status': 'PASSED'},
                                       'test3': {'status': 'FAILED'},
                                       'test4': {'status': 'ERROR'},
                                       'test5': {'status': 'SKIPPED'}}}
        target_result_data = {'result': {'test1': {'status': 'PASSED'},
                                         'test2': {'status': 'FAILED'},
                                         'test3': {'status': 'PASSED'},
                                         'test4': {'status': 'ERROR'},
                                         'test5': {'status': 'SKIPPED'}}}
        result, text = regression.compare_result(self.logger, "BaseTestRunName", "TargetTestRunName", base_result_data, target_result_data)
        self.assertTrue(result['test2']['base'] == 'PASSED',
                        msg="regression not correct:%s" % result['test2']['base'])
        self.assertTrue(result['test2']['target'] == 'FAILED',
                        msg="regression not correct:%s" % result['test2']['target'])
        self.assertTrue(result['test3']['base'] == 'FAILED',
                        msg="regression not correct:%s" % result['test3']['base'])
        self.assertTrue(result['test3']['target'] == 'PASSED',
                        msg="regression not correct:%s" % result['test3']['target'])

    def test_merge_can_merged_results(self):
        results = {}
        resultutils.append_resultsdata(results, ResultToolTests.base_results_data, configmap=resultutils.flatten_map)
        resultutils.append_resultsdata(results, ResultToolTests.target_results_data, configmap=resultutils.flatten_map)
        self.assertEqual(len(results[''].keys()), 5, msg="Flattened results not correct %s" % str(results))

    def test_results_without_metadata_can_be_compared(self):
        base_configuration = {"configuration": {
            "TEST_TYPE": "oeselftest",
            "TESTSERIES": "series1",
            "IMAGE_BASENAME": "image",
            "IMAGE_PKGTYPE": "ipk",
            "DISTRO": "mydistro",
            "MACHINE": "qemux86",
            "STARTTIME": 1672527600
        }, "result": {}}
        target_configuration = {"configuration": {
                                "TEST_TYPE": "oeselftest",
                                "TESTSERIES": "series1",
                                "IMAGE_BASENAME": "image",
                                "IMAGE_PKGTYPE": "ipk",
                                "DISTRO": "mydistro",
                                "MACHINE": "qemux86",
                                "STARTTIME": 1672527600
                                }, "result": {}}
        self.assertTrue(regression.can_be_compared(self.logger, base_configuration, target_configuration),
                        msg="incorrect metadata filtering, tests without metadata should be compared")

    def test_target_result_with_missing_metadata_can_not_be_compared(self):
        base_configuration = {"configuration": {
            "TEST_TYPE": "oeselftest",
            "TESTSERIES": "series1",
            "IMAGE_BASENAME": "image",
            "IMAGE_PKGTYPE": "ipk",
            "DISTRO": "mydistro",
            "MACHINE": "qemux86",
            "OESELFTEST_METADATA": {
                "run_all_tests": True,
                "run_tests": None,
                "skips": None,
                "machine": None,
                "select_tags": ["toolchain-user", "toolchain-system"],
                "exclude_tags": None
            }}, "result": {}}
        target_configuration = {"configuration": {"TEST_TYPE": "oeselftest",
                                "TESTSERIES": "series1",
                                                  "IMAGE_BASENAME": "image",
                                                  "IMAGE_PKGTYPE": "ipk",
                                                  "DISTRO": "mydistro",
                                                  "MACHINE": "qemux86",
                                                  "STARTTIME": 1672527600
                                                  }, "result": {}}
        self.assertFalse(regression.can_be_compared(self.logger, base_configuration, target_configuration),
                         msg="incorrect metadata filtering, tests should not be compared")

    def test_results_with_matching_metadata_can_be_compared(self):
        base_configuration = {"configuration": {
            "TEST_TYPE": "oeselftest",
            "TESTSERIES": "series1",
            "IMAGE_BASENAME": "image",
            "IMAGE_PKGTYPE": "ipk",
                             "DISTRO": "mydistro",
                             "MACHINE": "qemux86",
            "STARTTIME": 1672527600,
                             "OESELFTEST_METADATA": {"run_all_tests": True,
                                                     "run_tests": None,
                                                     "skips": None,
                                                     "machine": None,
                                                     "select_tags": ["toolchain-user", "toolchain-system"],
                                                     "exclude_tags": None}
        }, "result": {}}
        target_configuration = {"configuration": {
            "TEST_TYPE": "oeselftest",
            "TESTSERIES": "series1",
            "IMAGE_BASENAME": "image",
            "IMAGE_PKGTYPE": "ipk",
            "DISTRO": "mydistro",
            "MACHINE": "qemux86",
                                "STARTTIME": 1672527600,
                                "OESELFTEST_METADATA": {"run_all_tests": True,
                                                        "run_tests": None,
                                                        "skips": None,
                                                        "machine": None,
                                                        "select_tags": ["toolchain-user", "toolchain-system"],
                                                        "exclude_tags": None}
                                }, "result": {}}
        self.assertTrue(regression.can_be_compared(self.logger, base_configuration, target_configuration),
                        msg="incorrect metadata filtering, tests with matching metadata should be compared")

    def test_results_with_mismatching_metadata_can_not_be_compared(self):
        base_configuration = {"configuration": {
            "TEST_TYPE": "oeselftest",
            "TESTSERIES": "series1",
            "IMAGE_BASENAME": "image",
            "IMAGE_PKGTYPE": "ipk",
            "DISTRO": "mydistro",
            "MACHINE": "qemux86",
            "STARTTIME": 1672527600,
            "OESELFTEST_METADATA": {"run_all_tests": True,
                                    "run_tests": None,
                                    "skips": None,
                                    "machine": None,
                                    "select_tags": ["toolchain-user", "toolchain-system"],
                                    "exclude_tags": None}
        }, "result": {}}
        target_configuration = {"configuration": {
            "TEST_TYPE": "oeselftest",
            "TESTSERIES": "series1",
            "IMAGE_BASENAME": "image",
            "IMAGE_PKGTYPE": "ipk",
            "DISTRO": "mydistro",
            "MACHINE": "qemux86",
                                "STARTTIME": 1672527600,
                                "OESELFTEST_METADATA": {"run_all_tests": True,
                                                        "run_tests": None,
                                                        "skips": None,
                                                        "machine": None,
                                                        "select_tags": ["machine"],
                                                        "exclude_tags": None}
                                }, "result": {}}
        self.assertFalse(regression.can_be_compared(self.logger, base_configuration, target_configuration),
                         msg="incorrect metadata filtering, tests with mismatching metadata should not be compared")

    def test_metadata_matching_is_only_checked_for_relevant_test_type(self):
        base_configuration = {"configuration": {"TEST_TYPE": "runtime",
                              "TESTSERIES": "series1",
                                                "IMAGE_BASENAME": "image",
                                                "IMAGE_PKGTYPE": "ipk",
                                                "DISTRO": "mydistro",
                                                "MACHINE": "qemux86",
                                                "STARTTIME": 1672527600,
                                                "OESELFTEST_METADATA": {"run_all_tests": True,
                                                                        "run_tests": None,
                                                                        "skips": None,
                                                                        "machine": None,
                                                                        "select_tags": ["toolchain-user", "toolchain-system"],
                                                                        "exclude_tags": None}}, "result": {}}
        target_configuration = {"configuration": {"TEST_TYPE": "runtime",
                                "TESTSERIES": "series1",
                                                  "IMAGE_BASENAME": "image",
                                                  "IMAGE_PKGTYPE": "ipk",
                                                  "DISTRO": "mydistro",
                                                  "MACHINE": "qemux86",
                                                  "STARTTIME": 1672527600,
                                                  "OESELFTEST_METADATA": {"run_all_tests": True,
                                                                          "run_tests": None,
                                                                          "skips": None,
                                                                          "machine": None,
                                                                          "select_tags": ["machine"],
                                                                          "exclude_tags": None}}, "result": {}}
        self.assertTrue(regression.can_be_compared(self.logger, base_configuration, target_configuration),
                        msg="incorrect metadata filtering, %s tests should be compared" % base_configuration['configuration']['TEST_TYPE'])

    def test_machine_matches(self):
        base_configuration = {"configuration": {
            "TEST_TYPE": "runtime",
            "MACHINE": "qemux86"}, "result": {}}
        target_configuration = {"configuration": {
            "TEST_TYPE": "runtime",
            "MACHINE": "qemux86"
        }, "result": {}}
        self.assertTrue(regression.can_be_compared(self.logger, base_configuration, target_configuration),
                        msg="incorrect machine filtering, identical machine tests should be compared")

    def test_machine_mismatches(self):
        base_configuration = {"configuration": {
            "TEST_TYPE": "runtime",
            "MACHINE": "qemux86"
        }, "result": {}}
        target_configuration = {"configuration": {
            "TEST_TYPE": "runtime",
            "MACHINE": "qemux86_64"
        }, "result": {}}
        self.assertFalse(regression.can_be_compared(self.logger, base_configuration, target_configuration),
                         msg="incorrect machine filtering, mismatching machine tests should not be compared")

    def test_can_not_compare_non_ltp_tests(self):
        base_configuration = {"configuration": {
            "TEST_TYPE": "runtime",
            "MACHINE": "qemux86"
        }, "result": {
            "ltpresult_foo": {
                "status": "PASSED"
            }}}
        target_configuration = {"configuration": {
            "TEST_TYPE": "runtime",
            "MACHINE": "qemux86_64"
        }, "result": {
            "bar": {
                "status": "PASSED"
            }}}
        self.assertFalse(regression.can_be_compared(self.logger, base_configuration, target_configuration),
                         msg="incorrect ltpresult filtering, mismatching ltpresult content should not be compared")

    def test_can_compare_ltp_tests(self):
        base_configuration = {"configuration": {
            "TEST_TYPE": "runtime",
            "MACHINE": "qemux86"
        }, "result": {
            "ltpresult_foo": {
                "status": "PASSED"
            }}}
        target_configuration = {"configuration": {
            "TEST_TYPE": "runtime",
            "MACHINE": "qemux86"
        }, "result": {
            "ltpresult_foo": {
                "status": "PASSED"
            }}}
        self.assertTrue(regression.can_be_compared(self.logger, base_configuration, target_configuration),
                        msg="incorrect ltpresult filtering, matching ltpresult content should be compared")

    def test_can_match_non_static_ptest_names(self):
        base_configuration = {"a": {
            "conf_X": {
                "configuration": {
                    "TEST_TYPE": "runtime",
                    "MACHINE": "qemux86"
                }, "result": {
                    "ptestresult.lttng-tools.foo_-_bar_-_moo": {
                        "status": "PASSED"
                    },
                    "ptestresult.babeltrace.bar_-_moo_-_foo": {
                        "status": "PASSED"
                    },
                    "ptestresult.babeltrace2.moo_-_foo_-_bar": {
                        "status": "PASSED"
                    },
                    "ptestresult.curl.test_0000__foo_out_of_bar": {
                        "status": "PASSED"
                    },
                    "ptestresult.dbus.test_0000__foo_out_of_bar,_remaining:_00:02,_took_0.032s,_duration:_03:32_": {
                        "status": "PASSED"
                    },
                    "ptestresult.binutils-ld.in testcase /foo/build-st-bar/moo/ctf.exp": {
                        "status": "PASSED"
                    },
                    "ptestresult.gcc-libstdc++-v3.Couldn't create remote directory /tmp/runtest.30975 on target": {
                        "status": "PASSED"
                    },
                    "ptestresult.gcc-libgomp.Couldn't create remote directory /tmp/runtest.3657621 on": {
                        "status": "PASSED"
                    }
                }}}}
        target_configuration = {"a": {
            "conf_Y": {
                "configuration": {
                    "TEST_TYPE": "runtime",
                    "MACHINE": "qemux86"
                }, "result": {
                    "ptestresult.lttng-tools.foo_-_yyy_-_zzz": {
                        "status": "PASSED"
                    },
                    "ptestresult.babeltrace.bar_-_zzz_-_xxx": {
                        "status": "PASSED"
                    },
                    "ptestresult.babeltrace2.moo_-_xxx_-_yyy": {
                        "status": "PASSED"
                    },
                    "ptestresult.curl.test_0000__xxx_out_of_yyy": {
                        "status": "PASSED"
                    },
                    "ptestresult.dbus.test_0000__yyy_out_of_zzz,_remaining:_00:03,_took_0.034s,_duration:_03:30_": {
                        "status": "PASSED"
                    },
                    "ptestresult.binutils-ld.in testcase /xxx/build-st-yyy/zzz/ctf.exp": {
                        "status": "PASSED"
                    },
                    "ptestresult.gcc-libstdc++-v3.Couldn't create remote directory /tmp/runtest.45678 on target": {
                        "status": "PASSED"
                    },
                    "ptestresult.gcc-libgomp.Couldn't create remote directory /tmp/runtest.3657621 on": {
                        "status": "PASSED"
                    }
                }}}}
        regression.fixup_ptest_names(base_configuration, self.logger)
        regression.fixup_ptest_names(target_configuration, self.logger)
        result, resultstring = regression.compare_result(
            self.logger, "A", "B", base_configuration["a"]["conf_X"], target_configuration["a"]["conf_Y"])
        self.assertDictEqual(
            result, {}, msg=f"ptests should be compared: {resultstring}")
