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

from resulttool.junit import junit_tree, PtestSummary
import xml.etree.ElementTree as ET
import logging
import json


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

    @property
    def _get_junit_testresults_1(self):
        base_testresults = {
            "a": {
                "runtime_a-image": {
                    "configuration": {"TEST_TYPE": "runtime", "MACHINE": "qemux86"},
                    "result": {
                        # Image test skipped
                        "ptest.PtestRunnerTest.test_ptestrunner_expectfail": {
                            "duration": 0,
                            "log": "Cannot run ptests with @expectedFailure as ptests are required to pass",
                            "status": "SKIPPED",
                        },
                        # Image test passed
                        "ptest.PtestRunnerTest.test_ptestrunner_expectsuccess": {
                            "duration": 7,
                            "status": "PASSED",
                        },
                        # Passed and skipped tests: passed
                        "ptestresult.package-passed.test_passed": {"status": "PASSED"},
                        "ptestresult.package-passed.test_skipped": {
                            "status": "SKIPPED"
                        },
                        # All tests are skipped: skipped
                        "ptestresult.package-skipped.test_skipped": {
                            "status": "SKIPPED"
                        },
                        # One or more errors: error
                        "ptestresult.package-error.test_error": {"status": "ERROR"},
                        "ptestresult.package-error.test_failed": {"status": "FAILED"},
                        "ptestresult.package-error.test_skipped": {"status": "SKIPPED"},
                        "ptestresult.package-error.test_passed": {"status": "PASSED"},
                        # No error and one or more failed: failed
                        "ptestresult.package-failed.test_failed": {"status": "FAILED"},
                        "ptestresult.package-failed.test_passed": {"status": "PASSED"},
                        "ptestresult.sections": {
                            "package-passed": {
                                "duration": "2",
                                "log": "PASS: package-passed.test_passed\nPASS: package-passed.test_skipped\n",
                            },
                            "package-skipped": {
                                "duration": "1",
                                "log": "SKIPPED: package-skipped.test_skipped\n",
                            },
                            "package-error": {
                                "duration": "4",
                                "log": "ERROR: ERROR: package-error.test_error\nFAILED: package-error.test_failed\nSKIPPED: package-error.test_skipped\nPASSED: package-error.test_passed\n",
                            },
                            "package-failed": {
                                "duration": "2",
                                "log": "FAILED: package-failed.test_failed\nPASS: package-failed.test_passed\n",
                            },
                            # Test with exitcode without ptestresult
                            "package-error-noresult": {
                                "duration": "6",
                                "exitcode": "123",
                                "log": "ERROR: -bash: testerror: command not found\nERROR: Exit status is 123\n",
                            },
                        },
                    },
                }
            }
        }
        return base_testresults

    def _dump_junit_tree(self, testresults, tree, files_name="junit"):
        if self.logger.level <= logging.DEBUG:
            junit_json_path = files_name + ".json"
            with open(junit_json_path, "w") as f:
                json.dump(testresults, f, indent=4)
            self.logger.debug(
                "Saved testresults json %s" % os.path.abspath(junit_json_path)
            )
            junit_xml_path = files_name + ".xml"
            tree.write(junit_xml_path, encoding="UTF-8", xml_declaration=True)
            self.logger.debug(
                "Saved JUnit XML report as %s" % os.path.abspath(junit_xml_path)
            )

    def _check_junit_testresults_1(self, testsuites_node):
        self.assertEqual(testsuites_node.attrib["errors"], "2")
        self.assertEqual(testsuites_node.attrib["failures"], "1")
        self.assertEqual(testsuites_node.attrib["skipped"], "2")
        self.assertEqual(testsuites_node.attrib["tests"], "7")
        self.assertEqual(testsuites_node.attrib["time"], "22")

        testsuites = testsuites_node.findall("testsuite")
        self.assertEqual(testsuites[0].attrib["name"], "runtime_a-image")
        inner_testsuites = testsuites[0].findall("testsuite")
        self.assertEqual(inner_testsuites[0].attrib["name"], "Image Tests")
        self.assertEqual(inner_testsuites[1].attrib["name"], "Package Tests")

        ptests_suite = testsuites_node.find(".//testsuite[@name='Package Tests']")
        testcases = ptests_suite.findall("testcase")
        self.assertEqual(testcases[0].attrib["name"], "package-passed")
        self.assertEqual(testcases[1].attrib["name"], "package-skipped")
        self.assertEqual(testcases[2].attrib["name"], "package-error")
        self.assertEqual(testcases[3].attrib["name"], "package-failed")
        self.assertEqual(testcases[4].attrib["name"], "package-error-noresult")
        self.assertEqual(testcases[0].attrib["time"], "2")
        self.assertEqual(testcases[1].attrib["time"], "1")
        self.assertEqual(testcases[2].attrib["time"], "4")
        self.assertEqual(testcases[3].attrib["time"], "2")
        self.assertEqual(testcases[4].attrib["time"], "6")

    def test_junit_log_inline(self):
        testresults = self._get_junit_testresults_1
        tree, test_logfiles = junit_tree(testresults)
        self._dump_junit_tree(testresults, tree)
        testsuites_node = tree.getroot()

        # Verify the common part
        self._check_junit_testresults_1(testsuites_node)

        # Verify the inlined log files
        ptests_suite = testsuites_node.find(".//testsuite[@name='Package Tests']")
        testcases = ptests_suite.findall("testcase")
        ptestresult_sections = testresults["a"]["runtime_a-image"]["result"][
            "ptestresult.sections"
        ]
        # The inline system-out now includes a PtestSummary section followed by the raw section log.
        # Build expected summaries and verify both parts are present.
        pkg_error_summary = PtestSummary()
        pkg_error_summary.add_status("test_error", "ERROR")
        pkg_error_summary.add_status("test_failed", "FAILED")
        pkg_error_summary.add_status("test_skipped", "SKIPPED")
        pkg_failed_summary = PtestSummary()
        pkg_failed_summary.add_status("test_failed", "FAILED")

        pkg_error_out = testcases[2].find("system-out").text
        self.assertIn(pkg_error_summary.log_summary, pkg_error_out)
        self.assertIn(ptestresult_sections["package-error"]["log"], pkg_error_out)

        pkg_failed_out = testcases[3].find("system-out").text
        self.assertIn(pkg_failed_summary.log_summary, pkg_failed_out)
        self.assertIn(ptestresult_sections["package-failed"]["log"], pkg_failed_out)

        # Check the ptest log messages are inline
        self.assertDictEqual(test_logfiles, {})

    def test_junit_log_attached(self):
        testresults_1 = self._get_junit_testresults_1
        test_logdir = "test-logs"
        tree, test_logfiles = junit_tree(testresults_1, test_logdir)
        self._dump_junit_tree(testresults_1, tree, "junit_attached")
        testsuites_node = tree.getroot()

        # Verify the common part
        self._check_junit_testresults_1(testsuites_node)

        # Verify the attached log files
        ptests_suite = testsuites_node.find(".//testsuite[@name='Package Tests']")
        testcases = ptests_suite.findall("testcase")
        # Passed and skipped testcases do not include system-out attachments
        self.assertIsNone(testcases[0].find("system-out"))
        self.assertIsNone(testcases[1].find("system-out"))
        self.assertIn(
            "[[ATTACHMENT|test-logs/package-error.log]]",
            testcases[2].find("system-out").text,
        )
        self.assertIn(
            "[[ATTACHMENT|test-logs/package-failed.log]]",
            testcases[3].find("system-out").text,
        )
        self.assertIn(
            "[[ATTACHMENT|test-logs/package-error-noresult.log]]",
            testcases[4].find("system-out").text,
        )

        self.maxDiff = None
        self.assertDictEqual(
            test_logfiles,
            {
                "test-logs/package-passed.log": "PASS: package-passed.test_passed\nPASS: package-passed.test_skipped\n",
                "test-logs/package-skipped.log": "SKIPPED: package-skipped.test_skipped\n",
                "test-logs/package-error.log": "ERROR: ERROR: package-error.test_error\nFAILED: package-error.test_failed\nSKIPPED: package-error.test_skipped\nPASSED: package-error.test_passed\n",
                "test-logs/package-failed.log": "FAILED: package-failed.test_failed\nPASS: package-failed.test_passed\n",
                "test-logs/package-error-noresult.log": "ERROR: -bash: testerror: command not found\nERROR: Exit status is 123\n",
            },
        )
