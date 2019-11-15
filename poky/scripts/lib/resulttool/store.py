# resulttool - store test results
#
# Copyright (c) 2019, Intel Corporation.
# Copyright (c) 2019, Linux Foundation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import tempfile
import os
import subprocess
import json
import shutil
import scriptpath
scriptpath.add_bitbake_lib_path()
scriptpath.add_oe_lib_path()
import resulttool.resultutils as resultutils
import oeqa.utils.gitarchive as gitarchive


def store(args, logger):
    tempdir = tempfile.mkdtemp(prefix='testresults.')
    try:
        configvars = resultutils.extra_configvars.copy()
        if args.executed_by:
            configvars['EXECUTED_BY'] = args.executed_by
        if args.extra_test_env:
            configvars['EXTRA_TEST_ENV'] = args.extra_test_env
        results = {}
        logger.info('Reading files from %s' % args.source)
        if resultutils.is_url(args.source) or os.path.isfile(args.source):
            resultutils.append_resultsdata(results, args.source, configvars=configvars)
        else:
            for root, dirs,  files in os.walk(args.source):
                for name in files:
                    f = os.path.join(root, name)
                    if name == "testresults.json":
                        resultutils.append_resultsdata(results, f, configvars=configvars)
                    elif args.all:
                        dst = f.replace(args.source, tempdir + "/")
                        os.makedirs(os.path.dirname(dst), exist_ok=True)
                        shutil.copyfile(f, dst)

        revisions = {}

        if not results and not args.all:
            if args.allow_empty:
                logger.info("No results found to store")
                return 0
            logger.error("No results found to store")
            return 1

        # Find the branch/commit/commit_count and ensure they all match
        for suite in results:
            for result in results[suite]:
                config = results[suite][result]['configuration']['LAYERS']['meta']
                revision = (config['commit'], config['branch'], str(config['commit_count']))
                if revision not in revisions:
                    revisions[revision] = {}
                if suite not in revisions[revision]:
                    revisions[revision][suite] = {}
                revisions[revision][suite][result] = results[suite][result]

        logger.info("Found %d revisions to store" % len(revisions))

        for r in revisions:
            results = revisions[r]
            keywords = {'commit': r[0], 'branch': r[1], "commit_count": r[2]}
            subprocess.check_call(["find", tempdir, "!", "-path", "./.git/*", "-delete"])
            resultutils.save_resultsdata(results, tempdir, ptestlogs=True)

            logger.info('Storing test result into git repository %s' % args.git_dir)

            gitarchive.gitarchive(tempdir, args.git_dir, False, False,
                                  "Results of {branch}:{commit}", "branch: {branch}\ncommit: {commit}", "{branch}",
                                  False, "{branch}/{commit_count}-g{commit}/{tag_number}",
                                  'Test run #{tag_number} of {branch}:{commit}', '',
                                  [], [], False, keywords, logger)

    finally:
        subprocess.check_call(["rm", "-rf",  tempdir])

    return 0

def register_commands(subparsers):
    """Register subcommands from this plugin"""
    parser_build = subparsers.add_parser('store', help='store test results into a git repository',
                                         description='takes a results file or directory of results files and stores '
                                                     'them into the destination git repository, splitting out the results '
                                                     'files as configured',
                                         group='setup')
    parser_build.set_defaults(func=store)
    parser_build.add_argument('source',
                              help='source file/directory/URL that contain the test result files to be stored')
    parser_build.add_argument('git_dir',
                              help='the location of the git repository to store the results in')
    parser_build.add_argument('-a', '--all', action='store_true',
                              help='include all files, not just testresults.json files')
    parser_build.add_argument('-e', '--allow-empty', action='store_true',
                              help='don\'t error if no results to store are found')
    parser_build.add_argument('-x', '--executed-by', default='',
                              help='add executed-by configuration to each result file')
    parser_build.add_argument('-t', '--extra-test-env', default='',
                              help='add extra test environment data to each result file configuration')
