# resulttool - regression analysis
#
# Copyright (c) 2019, Intel Corporation.
# Copyright (c) 2019, Linux Foundation
#
# This program is free software; you can redistribute it and/or modify it
# under the terms and conditions of the GNU General Public License,
# version 2, as published by the Free Software Foundation.
#
# This program is distributed in the hope it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
# more details.
#
import resulttool.resultutils as resultutils
import json

from oeqa.utils.git import GitRepo
import oeqa.utils.gitarchive as gitarchive

def compare_result(logger, base_name, target_name, base_result, target_result):
    base_result = base_result.get('result')
    target_result = target_result.get('result')
    result = {}
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
    if result:
        resultstring = "Regression: %s\n            %s\n" % (base_name, target_name)
        for k in sorted(result):
            resultstring += '    %s: %s -> %s\n' % (k, result[k]['base'], result[k]['target'])
    else:
        resultstring = "Match: %s\n       %s" % (base_name, target_name)
    return result, resultstring

def get_results(logger, source):
    return resultutils.load_resultsdata(source, configmap=resultutils.regression_map)

def regression(args, logger):
    base_results = get_results(logger, args.base_result)
    target_results = get_results(logger, args.target_result)

    regression_common(args, logger, base_results, target_results)

def regression_common(args, logger, base_results, target_results):
    if args.base_result_id:
        base_results = resultutils.filter_resultsdata(base_results, args.base_result_id)
    if args.target_result_id:
        target_results = resultutils.filter_resultsdata(target_results, args.target_result_id)

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
                    res, resstr = compare_result(logger, c, b, base_results[a][c], target_results[a][b])
                    if not res:
                        matches.append(resstr)
                        base.remove(c)
                        target.remove(b)
                        break
            # Should only now see regressions, we may not be able to match multiple pairs directly
            for c in base:
                for b in target:
                    res, resstr = compare_result(logger, c, b, base_results[a][c], target_results[a][b])
                    if res:
                        regressions.append(resstr)
        else:
            notfound.append("%s not found in target" % a)
    print("\n".join(sorted(matches)))
    print("\n".join(sorted(regressions)))
    print("\n".join(sorted(notfound)))

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
                              help='base result file/directory for the comparison')
    parser_build.add_argument('target_result',
                              help='target result file/directory to compare with')
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

