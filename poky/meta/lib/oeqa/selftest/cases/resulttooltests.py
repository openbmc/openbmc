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

    def test_regrresion_can_get_regression_result(self):
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

