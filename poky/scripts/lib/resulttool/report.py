# test result tool - report text based test results
#
# Copyright (c) 2019, Intel Corporation.
# Copyright (c) 2019, Linux Foundation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import glob
import json
import resulttool.resultutils as resultutils
from oeqa.utils.git import GitRepo
import oeqa.utils.gitarchive as gitarchive


class ResultsTextReport(object):
    def __init__(self):
        self.ptests = {}
        self.ltptests = {}
        self.ltpposixtests = {}
        self.result_types = {'passed': ['PASSED', 'passed'],
                             'failed': ['FAILED', 'failed', 'ERROR', 'error', 'UNKNOWN'],
                             'skipped': ['SKIPPED', 'skipped']}


    def handle_ptest_result(self, k, status, result):
        if k == 'ptestresult.sections':
            # Ensure tests without any test results still show up on the report
            for suite in result['ptestresult.sections']:
                if suite not in self.ptests:
                    self.ptests[suite] = {'passed': 0, 'failed': 0, 'skipped': 0, 'duration' : '-', 'failed_testcases': []}
                if 'duration' in result['ptestresult.sections'][suite]:
                    self.ptests[suite]['duration'] = result['ptestresult.sections'][suite]['duration']
                if 'timeout' in result['ptestresult.sections'][suite]:
                    self.ptests[suite]['duration'] += " T"
            return
        try:
            _, suite, test = k.split(".", 2)
        except ValueError:
            return
        # Handle 'glib-2.0'
        if 'ptestresult.sections' in result and suite not in result['ptestresult.sections']:
            try:
                _, suite, suite1, test = k.split(".", 3)
                if suite + "." + suite1 in result['ptestresult.sections']:
                    suite = suite + "." + suite1
            except ValueError:
                pass
        if suite not in self.ptests:
            self.ptests[suite] = {'passed': 0, 'failed': 0, 'skipped': 0, 'duration' : '-', 'failed_testcases': []}
        for tk in self.result_types:
            if status in self.result_types[tk]:
                self.ptests[suite][tk] += 1

    def handle_ltptest_result(self, k, status, result):
        if k == 'ltpresult.sections':
            # Ensure tests without any test results still show up on the report
            for suite in result['ltpresult.sections']:
                if suite not in self.ltptests:
                    self.ltptests[suite] = {'passed': 0, 'failed': 0, 'skipped': 0, 'duration' : '-', 'failed_testcases': []}
                if 'duration' in result['ltpresult.sections'][suite]:
                    self.ltptests[suite]['duration'] = result['ltpresult.sections'][suite]['duration']
                if 'timeout' in result['ltpresult.sections'][suite]:
                    self.ltptests[suite]['duration'] += " T"
            return
        try:
            _, suite, test = k.split(".", 2)
        except ValueError:
            return
        # Handle 'glib-2.0'
        if 'ltpresult.sections' in result and suite not in result['ltpresult.sections']:
            try:
                _, suite, suite1, test = k.split(".", 3)
                print("split2: %s %s %s" % (suite, suite1, test))
                if suite + "." + suite1 in result['ltpresult.sections']:
                    suite = suite + "." + suite1
            except ValueError:
                pass
        if suite not in self.ltptests:
            self.ltptests[suite] = {'passed': 0, 'failed': 0, 'skipped': 0, 'duration' : '-', 'failed_testcases': []}
        for tk in self.result_types:
            if status in self.result_types[tk]:
                self.ltptests[suite][tk] += 1

    def handle_ltpposixtest_result(self, k, status, result):
        if k == 'ltpposixresult.sections':
            # Ensure tests without any test results still show up on the report
            for suite in result['ltpposixresult.sections']:
                if suite not in self.ltpposixtests:
                    self.ltpposixtests[suite] = {'passed': 0, 'failed': 0, 'skipped': 0, 'duration' : '-', 'failed_testcases': []}
                if 'duration' in result['ltpposixresult.sections'][suite]:
                    self.ltpposixtests[suite]['duration'] = result['ltpposixresult.sections'][suite]['duration']
            return
        try:
            _, suite, test = k.split(".", 2)
        except ValueError:
            return
        # Handle 'glib-2.0'
        if 'ltpposixresult.sections' in result and suite not in result['ltpposixresult.sections']:
            try:
                _, suite, suite1, test = k.split(".", 3)
                if suite + "." + suite1 in result['ltpposixresult.sections']:
                    suite = suite + "." + suite1
            except ValueError:
                pass
        if suite not in self.ltpposixtests:
            self.ltpposixtests[suite] = {'passed': 0, 'failed': 0, 'skipped': 0, 'duration' : '-', 'failed_testcases': []}
        for tk in self.result_types:
            if status in self.result_types[tk]:
                self.ltpposixtests[suite][tk] += 1

    def get_aggregated_test_result(self, logger, testresult):
        test_count_report = {'passed': 0, 'failed': 0, 'skipped': 0, 'failed_testcases': []}
    def get_aggregated_test_result(self, logger, testresult):
        test_count_report = {'passed': 0, 'failed': 0, 'skipped': 0, 'failed_testcases': []}
    def get_aggregated_test_result(self, logger, testresult):
        test_count_report = {'passed': 0, 'failed': 0, 'skipped': 0, 'failed_testcases': []}
    def get_aggregated_test_result(self, logger, testresult):
        test_count_report = {'passed': 0, 'failed': 0, 'skipped': 0, 'failed_testcases': []}
        result = testresult.get('result', [])
        for k in result:
            test_status = result[k].get('status', [])
            for tk in self.result_types:
                if test_status in self.result_types[tk]:
                    test_count_report[tk] += 1
            if test_status in self.result_types['failed']:
                test_count_report['failed_testcases'].append(k)
            if k.startswith("ptestresult."):
                self.handle_ptest_result(k, test_status, result)
            if k.startswith("ltpresult."):
                self.handle_ltptest_result(k, test_status, result)
            if k.startswith("ltpposixresult."):
                self.handle_ltpposixtest_result(k, test_status, result)
        return test_count_report

    def print_test_report(self, template_file_name, test_count_reports):
        from jinja2 import Environment, FileSystemLoader
        script_path = os.path.dirname(os.path.realpath(__file__))
        file_loader = FileSystemLoader(script_path + '/template')
        env = Environment(loader=file_loader, trim_blocks=True)
        template = env.get_template(template_file_name)
        havefailed = False
        haveptest = bool(self.ptests)
        haveltp = bool(self.ltptests)
        haveltpposix = bool(self.ltpposixtests)
        reportvalues = []
        cols = ['passed', 'failed', 'skipped']
        maxlen = {'passed' : 0, 'failed' : 0, 'skipped' : 0, 'result_id': 0, 'testseries' : 0, 'ptest' : 0 ,'ltptest': 0, 'ltpposixtest': 0}
        for line in test_count_reports:
            total_tested = line['passed'] + line['failed'] + line['skipped']
            vals = {}
            vals['result_id'] = line['result_id']
            vals['testseries'] = line['testseries']
            vals['sort'] = line['testseries'] + "_" + line['result_id']
            vals['failed_testcases'] = line['failed_testcases']
            for k in cols:
                vals[k] = "%d (%s%%)" % (line[k], format(line[k] / total_tested * 100, '.0f'))
            for k in maxlen:
                if k in vals and len(vals[k]) > maxlen[k]:
                    maxlen[k] = len(vals[k])
            reportvalues.append(vals)
            if line['failed_testcases']:
                havefailed = True
        for ptest in self.ptests:
            if len(ptest) > maxlen['ptest']:
                maxlen['ptest'] = len(ptest)
        for ltptest in self.ltptests:
            if len(ltptest) > maxlen['ltptest']:
                maxlen['ltptest'] = len(ltptest)
        for ltpposixtest in self.ltpposixtests:
            if len(ltpposixtest) > maxlen['ltpposixtest']:
                maxlen['ltpposixtest'] = len(ltpposixtest)
        output = template.render(reportvalues=reportvalues,
                                 havefailed=havefailed,
                                 haveptest=haveptest,
                                 ptests=self.ptests,
                                 haveltp=haveltp,
                                 haveltpposix=haveltpposix,
                                 ltptests=self.ltptests,
                                 ltpposixtests=self.ltpposixtests,
                                 maxlen=maxlen)
        print(output)

    def view_test_report(self, logger, source_dir, branch, commit, tag):
        test_count_reports = []
        if commit:
            if tag:
                logger.warning("Ignoring --tag as --commit was specified")
            tag_name = "{branch}/{commit_number}-g{commit}/{tag_number}"
            repo = GitRepo(source_dir)
            revs = gitarchive.get_test_revs(logger, repo, tag_name, branch=branch)
            rev_index = gitarchive.rev_find(revs, 'commit', commit)
            testresults = resultutils.git_get_result(repo, revs[rev_index][2])
        elif tag:
            repo = GitRepo(source_dir)
            testresults = resultutils.git_get_result(repo, [tag])
        else:
            testresults = resultutils.load_resultsdata(source_dir)
        for testsuite in testresults:
            for resultid in testresults[testsuite]:
                result = testresults[testsuite][resultid]
                test_count_report = self.get_aggregated_test_result(logger, result)
                test_count_report['testseries'] = result['configuration']['TESTSERIES']
                test_count_report['result_id'] = resultid
                test_count_reports.append(test_count_report)
        self.print_test_report('test_report_full_text.txt', test_count_reports)

def report(args, logger):
    report = ResultsTextReport()
    report.view_test_report(logger, args.source_dir, args.branch, args.commit, args.tag)
    return 0

def register_commands(subparsers):
    """Register subcommands from this plugin"""
    parser_build = subparsers.add_parser('report', help='summarise test results',
                                         description='print a text-based summary of the test results',
                                         group='analysis')
    parser_build.set_defaults(func=report)
    parser_build.add_argument('source_dir',
                              help='source file/directory/URL that contain the test result files to summarise')
    parser_build.add_argument('--branch', '-B', default='master', help="Branch to find commit in")
    parser_build.add_argument('--commit', help="Revision to report")
    parser_build.add_argument('-t', '--tag', default='',
                              help='source_dir is a git repository, report on the tag specified from that repository')
