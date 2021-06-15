# resulttool - merge multiple testresults.json files into a file or directory
#
# Copyright (c) 2019, Intel Corporation.
# Copyright (c) 2019, Linux Foundation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import json
import resulttool.resultutils as resultutils

def merge(args, logger):
    configvars = {}
    if not args.not_add_testseries:
        configvars = resultutils.extra_configvars.copy()
    if args.executed_by:
        configvars['EXECUTED_BY'] = args.executed_by
    if resultutils.is_url(args.target_results) or os.path.isdir(args.target_results):
        results = resultutils.load_resultsdata(args.target_results, configmap=resultutils.store_map, configvars=configvars)
        resultutils.append_resultsdata(results, args.base_results, configmap=resultutils.store_map, configvars=configvars)
        resultutils.save_resultsdata(results, args.target_results)
    else:
        results = resultutils.load_resultsdata(args.base_results, configmap=resultutils.flatten_map, configvars=configvars)
        if os.path.exists(args.target_results):
            resultutils.append_resultsdata(results, args.target_results, configmap=resultutils.flatten_map, configvars=configvars)
        resultutils.save_resultsdata(results, os.path.dirname(args.target_results), fn=os.path.basename(args.target_results))

    logger.info('Merged results to %s' % os.path.dirname(args.target_results))

    return 0

def register_commands(subparsers):
    """Register subcommands from this plugin"""
    parser_build = subparsers.add_parser('merge', help='merge test result files/directories/URLs',
                                         description='merge the results from multiple files/directories/URLs into the target file or directory',
                                         group='setup')
    parser_build.set_defaults(func=merge)
    parser_build.add_argument('base_results',
                              help='the results file/directory/URL to import')
    parser_build.add_argument('target_results',
                              help='the target file or directory to merge the base_results with')
    parser_build.add_argument('-t', '--not-add-testseries', action='store_true',
                              help='do not add testseries configuration to results')
    parser_build.add_argument('-x', '--executed-by', default='',
                              help='add executed-by configuration to each result file')
