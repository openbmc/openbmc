# resulttool - regression analysis
#
# Copyright (c) 2019, Intel Corporation.
# Copyright (c) 2019, Linux Foundation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import resulttool.resultutils as resultutils

from oeqa.utils.git import GitRepo
import oeqa.utils.gitarchive as gitarchive

METADATA_MATCH_TABLE = {
    "oeselftest": "OESELFTEST_METADATA"
}

OESELFTEST_METADATA_GUESS_TABLE={
    "trigger-build-posttrigger": {
        "run_all_tests": False,
        "run_tests":["buildoptions.SourceMirroring.test_yocto_source_mirror"],
        "skips": None,
        "machine": None,
        "select_tags":None,
        "exclude_tags": None
    },
    "reproducible": {
        "run_all_tests": False,
        "run_tests":["reproducible"],
        "skips": None,
        "machine": None,
        "select_tags":None,
        "exclude_tags": None
    },
    "arch-qemu-quick": {
        "run_all_tests": True,
        "run_tests":None,
        "skips": None,
        "machine": None,
        "select_tags":["machine"],
        "exclude_tags": None
    },
    "arch-qemu-full-x86-or-x86_64": {
        "run_all_tests": True,
        "run_tests":None,
        "skips": None,
        "machine": None,
        "select_tags":["machine", "toolchain-system"],
        "exclude_tags": None
    },
    "arch-qemu-full-others": {
        "run_all_tests": True,
        "run_tests":None,
        "skips": None,
        "machine": None,
        "select_tags":["machine", "toolchain-user"],
        "exclude_tags": None
    },
    "selftest": {
        "run_all_tests": True,
        "run_tests":None,
        "skips": ["distrodata.Distrodata.test_checkpkg", "buildoptions.SourceMirroring.test_yocto_source_mirror", "reproducible"],
        "machine": None,
        "select_tags":None,
        "exclude_tags": ["machine", "toolchain-system", "toolchain-user"]
    },
    "bringup": {
        "run_all_tests": True,
        "run_tests":None,
        "skips": ["distrodata.Distrodata.test_checkpkg", "buildoptions.SourceMirroring.test_yocto_source_mirror"],
        "machine": None,
        "select_tags":None,
        "exclude_tags": ["machine", "toolchain-system", "toolchain-user"]
    }
}

STATUS_STRINGS = {
    "None": "No matching test result"
}

REGRESSIONS_DISPLAY_LIMIT=50

MISSING_TESTS_BANNER =   "-------------------------- Missing tests --------------------------"
ADDITIONAL_DATA_BANNER = "--------------------- Matches and improvements --------------------"

def test_has_at_least_one_matching_tag(test, tag_list):
    return "oetags" in test and any(oetag in tag_list for oetag in test["oetags"])

def all_tests_have_at_least_one_matching_tag(results, tag_list):
    return all(test_has_at_least_one_matching_tag(test_result, tag_list) or test_name.startswith("ptestresult") for (test_name, test_result) in results.items())

def any_test_have_any_matching_tag(results, tag_list):
    return any(test_has_at_least_one_matching_tag(test, tag_list) for test in results.values())

def have_skipped_test(result, test_prefix):
    return all( result[test]['status'] == "SKIPPED" for test in result if test.startswith(test_prefix))

def have_all_tests_skipped(result, test_prefixes_list):
    return all(have_skipped_test(result, test_prefix) for test_prefix in test_prefixes_list)

def guess_oeselftest_metadata(results):
    """
    When an oeselftest test result is lacking OESELFTEST_METADATA, we can try to guess it based on results content.
    Check results for specific values (absence/presence of oetags, number and name of executed tests...),
    and if it matches one of known configuration from autobuilder configuration, apply guessed OSELFTEST_METADATA
    to it to allow proper test filtering.
    This guessing process is tightly coupled to config.json in autobuilder. It should trigger less and less,
    as new tests will have OESELFTEST_METADATA properly appended at test reporting time
    """

    if len(results) == 1 and "buildoptions.SourceMirroring.test_yocto_source_mirror" in results:
        return OESELFTEST_METADATA_GUESS_TABLE['trigger-build-posttrigger']
    elif all(result.startswith("reproducible") for result in results):
        return OESELFTEST_METADATA_GUESS_TABLE['reproducible']
    elif all_tests_have_at_least_one_matching_tag(results, ["machine"]):
        return OESELFTEST_METADATA_GUESS_TABLE['arch-qemu-quick']
    elif all_tests_have_at_least_one_matching_tag(results, ["machine", "toolchain-system"]):
        return OESELFTEST_METADATA_GUESS_TABLE['arch-qemu-full-x86-or-x86_64']
    elif all_tests_have_at_least_one_matching_tag(results, ["machine", "toolchain-user"]):
        return OESELFTEST_METADATA_GUESS_TABLE['arch-qemu-full-others']
    elif not any_test_have_any_matching_tag(results, ["machine", "toolchain-user", "toolchain-system"]):
        if have_all_tests_skipped(results, ["distrodata.Distrodata.test_checkpkg", "buildoptions.SourceMirroring.test_yocto_source_mirror", "reproducible"]):
            return OESELFTEST_METADATA_GUESS_TABLE['selftest']
        elif have_all_tests_skipped(results, ["distrodata.Distrodata.test_checkpkg", "buildoptions.SourceMirroring.test_yocto_source_mirror"]):
            return OESELFTEST_METADATA_GUESS_TABLE['bringup']

    return None


def metadata_matches(base_configuration, target_configuration):
    """
    For passed base and target, check test type. If test type matches one of
    properties described in METADATA_MATCH_TABLE, compare metadata if it is
    present in base. Return true if metadata matches, or if base lacks some
    data (either TEST_TYPE or the corresponding metadata)
    """
    test_type = base_configuration.get('TEST_TYPE')
    if test_type not in METADATA_MATCH_TABLE:
        return True

    metadata_key = METADATA_MATCH_TABLE.get(test_type)
    if target_configuration.get(metadata_key) != base_configuration.get(metadata_key):
        return False

    return True


def machine_matches(base_configuration, target_configuration):
    return base_configuration.get('MACHINE') == target_configuration.get('MACHINE')


def can_be_compared(logger, base, target):
    """
    Some tests are not relevant to be compared, for example some oeselftest
    run with different tests sets or parameters. Return true if tests can be
    compared
    """
    ret = True
    base_configuration = base['configuration']
    target_configuration = target['configuration']

    # Older test results lack proper OESELFTEST_METADATA: if not present, try to guess it based on tests results.
    if base_configuration.get('TEST_TYPE') == 'oeselftest' and 'OESELFTEST_METADATA' not in base_configuration:
        guess = guess_oeselftest_metadata(base['result'])
        if guess is None:
            logger.error(f"ERROR: did not manage to guess oeselftest metadata for {base_configuration['STARTTIME']}")
        else:
            logger.debug(f"Enriching {base_configuration['STARTTIME']} with {guess}")
            base_configuration['OESELFTEST_METADATA'] = guess
    if target_configuration.get('TEST_TYPE') == 'oeselftest' and 'OESELFTEST_METADATA' not in target_configuration:
        guess = guess_oeselftest_metadata(target['result'])
        if guess is None:
            logger.error(f"ERROR: did not manage to guess oeselftest metadata for {target_configuration['STARTTIME']}")
        else:
            logger.debug(f"Enriching {target_configuration['STARTTIME']} with {guess}")
            target_configuration['OESELFTEST_METADATA'] = guess

    # Test runs with LTP results in should only be compared with other runs with LTP tests in them
    if base_configuration.get('TEST_TYPE') == 'runtime' and any(result.startswith("ltpresult") for result in base['result']):
        ret = target_configuration.get('TEST_TYPE') == 'runtime' and any(result.startswith("ltpresult") for result in target['result'])

    return ret and metadata_matches(base_configuration, target_configuration) \
        and machine_matches(base_configuration, target_configuration)

def get_status_str(raw_status):
    raw_status_lower = raw_status.lower() if raw_status else "None"
    return STATUS_STRINGS.get(raw_status_lower, raw_status)

def get_additional_info_line(new_pass_count, new_tests):
    result=[]
    if new_tests:
        result.append(f'+{new_tests} test(s) present')
    if new_pass_count:
        result.append(f'+{new_pass_count} test(s) now passing')

    if not result:
        return ""

    return '    -> ' + ', '.join(result) + '\n'

def compare_result(logger, base_name, target_name, base_result, target_result, display_limit=None):
    base_result = base_result.get('result')
    target_result = target_result.get('result')
    result = {}
    new_tests = 0
    regressions = {}
    resultstring = ""
    new_tests = 0
    new_pass_count = 0

    display_limit = int(display_limit) if display_limit else REGRESSIONS_DISPLAY_LIMIT

    if base_result and target_result:
        for k in base_result:
            base_testcase = base_result[k]
            base_status = base_testcase.get('status')
            if base_status:
                target_testcase = target_result.get(k, {})
                target_status = target_testcase.get('status')
                if base_status != target_status:
                    result[k] = {'base': base_status, 'target': target_status}
            else:
                logger.error('Failed to retrieved base test case status: %s' % k)

        # Also count new tests that were not present in base results: it
        # could be newly added tests, but it could also highlights some tests
        # renames or fixed faulty ptests
        for k in target_result:
            if k not in base_result:
                new_tests += 1
    if result:
        new_pass_count = sum(test['target'] is not None and test['target'].startswith("PASS") for test in result.values())
        # Print a regression report only if at least one test has a regression status (FAIL, SKIPPED, absent...)
        if new_pass_count < len(result):
            resultstring = "Regression:  %s\n             %s\n" % (base_name, target_name)
            for k in sorted(result):
                if not result[k]['target'] or not result[k]['target'].startswith("PASS"):
                    # Differentiate each ptest kind when listing regressions
                    key_parts = k.split('.')
                    key = '.'.join(key_parts[:2]) if k.startswith('ptest') else key_parts[0]
                    # Append new regression to corresponding test family
                    regressions[key] = regressions.setdefault(key, []) + ['        %s: %s -> %s\n' % (k, get_status_str(result[k]['base']), get_status_str(result[k]['target']))]
            resultstring += f"    Total: {sum([len(regressions[r]) for r in regressions])} new regression(s):\n"
            for k in regressions:
                resultstring += f"    {len(regressions[k])} regression(s) for {k}\n"
                count_to_print=min([display_limit, len(regressions[k])]) if display_limit > 0 else len(regressions[k])
                resultstring += ''.join(regressions[k][:count_to_print])
                if count_to_print < len(regressions[k]):
                    resultstring+='        [...]\n'
            if new_pass_count > 0:
                resultstring += f'    Additionally, {new_pass_count} previously failing test(s) is/are now passing\n'
            if new_tests > 0:
                resultstring += f'    Additionally, {new_tests} new test(s) is/are present\n'
        else:
            resultstring = "%s\n%s\n" % (base_name, target_name)
            result = None
    else:
        resultstring = "%s\n%s\n" % (base_name, target_name)

    if not result:
        additional_info = get_additional_info_line(new_pass_count, new_tests)
        if additional_info:
            resultstring += additional_info

    return result, resultstring

def get_results(logger, source):
    return resultutils.load_resultsdata(source, configmap=resultutils.regression_map)

def regression(args, logger):
    base_results = get_results(logger, args.base_result)
    target_results = get_results(logger, args.target_result)

    regression_common(args, logger, base_results, target_results)

# Some test case naming is poor and contains random strings, particularly lttng/babeltrace.
# Truncating the test names works since they contain file and line number identifiers
# which allows us to match them without the random components.
def fixup_ptest_names(results, logger):
    for r in results:
        for i in results[r]:
            tests = list(results[r][i]['result'].keys())
            for test in tests:
                new = None
                if test.startswith(("ptestresult.lttng-tools.", "ptestresult.babeltrace.", "ptestresult.babeltrace2")) and "_-_" in test:
                    new = test.split("_-_")[0]
                elif test.startswith(("ptestresult.curl.")) and "__" in test:
                    new = test.split("__")[0]
                elif test.startswith(("ptestresult.dbus.")) and "__" in test:
                    new = test.split("__")[0]
                elif test.startswith("ptestresult.binutils") and "build-st-" in test:
                    new = test.split(" ")[0]
                elif test.startswith("ptestresult.gcc") and "/tmp/runtest." in test:
                    new = ".".join(test.split(".")[:2])
                if new:
                    results[r][i]['result'][new] = results[r][i]['result'][test]
                    del results[r][i]['result'][test]

def regression_common(args, logger, base_results, target_results):
    if args.base_result_id:
        base_results = resultutils.filter_resultsdata(base_results, args.base_result_id)
    if args.target_result_id:
        target_results = resultutils.filter_resultsdata(target_results, args.target_result_id)

    fixup_ptest_names(base_results, logger)
    fixup_ptest_names(target_results, logger)

    matches = []
    regressions = []
    notfound = []

    for a in base_results:
        if a in target_results:
            base = list(base_results[a].keys())
            target = list(target_results[a].keys())
            # We may have multiple base/targets which are for different configurations. Start by
            # removing any pairs which match
            for c in base.copy():
                for b in target.copy():
                    if not can_be_compared(logger, base_results[a][c], target_results[a][b]):
                        continue
                    res, resstr = compare_result(logger, c, b, base_results[a][c], target_results[a][b], args.limit)
                    if not res:
                        matches.append(resstr)
                        base.remove(c)
                        target.remove(b)
                        break
            # Should only now see regressions, we may not be able to match multiple pairs directly
            for c in base:
                for b in target:
                    if not can_be_compared(logger, base_results[a][c], target_results[a][b]):
                        continue
                    res, resstr = compare_result(logger, c, b, base_results[a][c], target_results[a][b], args.limit)
                    if res:
                        regressions.append(resstr)
        else:
            notfound.append("%s not found in target" % a)
    print("\n".join(sorted(regressions)))
    print("\n" + MISSING_TESTS_BANNER + "\n")
    print("\n".join(sorted(notfound)))
    print("\n" + ADDITIONAL_DATA_BANNER + "\n")
    print("\n".join(sorted(matches)))
    return 0

def regression_git(args, logger):
    base_results = {}
    target_results = {}

    tag_name = "{branch}/{commit_number}-g{commit}/{tag_number}"
    repo = GitRepo(args.repo)

    revs = gitarchive.get_test_revs(logger, repo, tag_name, branch=args.branch)

    if args.branch2:
        revs2 = gitarchive.get_test_revs(logger, repo, tag_name, branch=args.branch2)
        if not len(revs2):
            logger.error("No revisions found to compare against")
            return 1
        if not len(revs):
            logger.error("No revision to report on found")
            return 1
    else:
        if len(revs) < 2:
            logger.error("Only %d tester revisions found, unable to generate report" % len(revs))
            return 1

    # Pick revisions
    if args.commit:
        if args.commit_number:
            logger.warning("Ignoring --commit-number as --commit was specified")
        index1 = gitarchive.rev_find(revs, 'commit', args.commit)
    elif args.commit_number:
        index1 = gitarchive.rev_find(revs, 'commit_number', args.commit_number)
    else:
        index1 = len(revs) - 1

    if args.branch2:
        revs2.append(revs[index1])
        index1 = len(revs2) - 1
        revs = revs2

    if args.commit2:
        if args.commit_number2:
            logger.warning("Ignoring --commit-number2 as --commit2 was specified")
        index2 = gitarchive.rev_find(revs, 'commit', args.commit2)
    elif args.commit_number2:
        index2 = gitarchive.rev_find(revs, 'commit_number', args.commit_number2)
    else:
        if index1 > 0:
            index2 = index1 - 1
            # Find the closest matching commit number for comparision
            # In future we could check the commit is a common ancestor and
            # continue back if not but this good enough for now
            while index2 > 0 and revs[index2].commit_number > revs[index1].commit_number:
                index2 = index2 - 1
        else:
            logger.error("Unable to determine the other commit, use "
                      "--commit2 or --commit-number2 to specify it")
            return 1

    logger.info("Comparing:\n%s\nto\n%s\n" % (revs[index1], revs[index2]))

    base_results = resultutils.git_get_result(repo, revs[index1][2])
    target_results = resultutils.git_get_result(repo, revs[index2][2])

    regression_common(args, logger, base_results, target_results)

    return 0

def register_commands(subparsers):
    """Register subcommands from this plugin"""

    parser_build = subparsers.add_parser('regression', help='regression file/directory analysis',
                                         description='regression analysis comparing the base set of results to the target results',
                                         group='analysis')
    parser_build.set_defaults(func=regression)
    parser_build.add_argument('base_result',
                              help='base result file/directory/URL for the comparison')
    parser_build.add_argument('target_result',
                              help='target result file/directory/URL to compare with')
    parser_build.add_argument('-b', '--base-result-id', default='',
                              help='(optional) filter the base results to this result ID')
    parser_build.add_argument('-t', '--target-result-id', default='',
                              help='(optional) filter the target results to this result ID')

    parser_build = subparsers.add_parser('regression-git', help='regression git analysis',
                                         description='regression analysis comparing base result set to target '
                                                     'result set',
                                         group='analysis')
    parser_build.set_defaults(func=regression_git)
    parser_build.add_argument('repo',
                              help='the git repository containing the data')
    parser_build.add_argument('-b', '--base-result-id', default='',
                              help='(optional) default select regression based on configurations unless base result '
                                   'id was provided')
    parser_build.add_argument('-t', '--target-result-id', default='',
                              help='(optional) default select regression based on configurations unless target result '
                                   'id was provided')

    parser_build.add_argument('--branch', '-B', default='master', help="Branch to find commit in")
    parser_build.add_argument('--branch2', help="Branch to find comparision revisions in")
    parser_build.add_argument('--commit', help="Revision to search for")
    parser_build.add_argument('--commit-number', help="Revision number to search for, redundant if --commit is specified")
    parser_build.add_argument('--commit2', help="Revision to compare with")
    parser_build.add_argument('--commit-number2', help="Revision number to compare with, redundant if --commit2 is specified")
    parser_build.add_argument('-l', '--limit', default=REGRESSIONS_DISPLAY_LIMIT, help="Maximum number of changes to display per test. Can be set to 0 to print all changes")

