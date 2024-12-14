# resulttool - report test results in JUnit XML format
#
# Copyright (c) 2024, Siemens AG.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import re
import xml.etree.ElementTree as ET
import resulttool.resultutils as resultutils

def junit(args, logger):
    testresults = resultutils.load_resultsdata(args.json_file, configmap=resultutils.store_map)

    total_time = 0
    skipped = 0
    failures = 0
    errors = 0

    for tests in testresults.values():
        results = tests[next(reversed(tests))].get("result", {})

    for result_id, result in results.items():
        # filter out ptestresult.rawlogs and ptestresult.sections
        if re.search(r'\.test_', result_id):
            total_time += result.get("duration", 0)

            if result['status'] == "FAILED":
                failures += 1
            elif result['status'] == "ERROR":
                errors += 1
            elif result['status'] == "SKIPPED":
                skipped += 1

    testsuites_node = ET.Element("testsuites")
    testsuites_node.set("time", "%s" % total_time)
    testsuite_node = ET.SubElement(testsuites_node, "testsuite")
    testsuite_node.set("name", "Testimage")
    testsuite_node.set("time", "%s" % total_time)
    testsuite_node.set("tests", "%s" % len(results))
    testsuite_node.set("failures", "%s" % failures)
    testsuite_node.set("errors", "%s" % errors)
    testsuite_node.set("skipped", "%s" % skipped)

    for result_id, result in results.items():
        if re.search(r'\.test_', result_id):
            testcase_node = ET.SubElement(testsuite_node, "testcase", {
                "name": result_id,
                "classname": "Testimage",
                "time": str(result['duration'])
            })
            if result['status'] == "SKIPPED":
                ET.SubElement(testcase_node, "skipped", message=result['log'])
            elif result['status'] == "FAILED":
                ET.SubElement(testcase_node, "failure", message=result['log'])
            elif result['status'] == "ERROR":
                ET.SubElement(testcase_node, "error", message=result['log'])

    tree = ET.ElementTree(testsuites_node)

    if args.junit_xml_path is None:
        args.junit_xml_path = os.environ['BUILDDIR'] + '/tmp/log/oeqa/junit.xml'
    tree.write(args.junit_xml_path, encoding='UTF-8', xml_declaration=True)

    logger.info('Saved JUnit XML report as %s' % args.junit_xml_path)

def register_commands(subparsers):
    """Register subcommands from this plugin"""
    parser_build = subparsers.add_parser('junit', help='create test report in JUnit XML format',
                                         description='generate unit test report in JUnit XML format based on the latest test results in the testresults.json.',
                                         group='analysis')
    parser_build.set_defaults(func=junit)
    parser_build.add_argument('json_file',
                              help='json file should point to the testresults.json')
    parser_build.add_argument('-j', '--junit_xml_path',
                              help='junit xml path allows setting the path of the generated test report. The default location is <build_dir>/tmp/log/oeqa/junit.xml')
