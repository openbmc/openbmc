# resulttool - merge multiple testresults.json files into a file or directory
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
import os
import json
import resulttool.resultutils as resultutils

def merge(args, logger):
    if os.path.isdir(args.target_results):
        results = resultutils.load_resultsdata(args.target_results, configmap=resultutils.store_map)
        resultutils.append_resultsdata(results, args.base_results, configmap=resultutils.store_map)
        resultutils.save_resultsdata(results, args.target_results)
    else:
        results = resultutils.load_resultsdata(args.base_results, configmap=resultutils.flatten_map)
        if os.path.exists(args.target_results):
            resultutils.append_resultsdata(results, args.target_results, configmap=resultutils.flatten_map)
        resultutils.save_resultsdata(results, os.path.dirname(args.target_results), fn=os.path.basename(args.target_results))

    return 0

def register_commands(subparsers):
    """Register subcommands from this plugin"""
    parser_build = subparsers.add_parser('merge', help='merge test result files/directories',
                                         description='merge the results from multiple files/directories into the target file or directory',
                                         group='setup')
    parser_build.set_defaults(func=merge)
    parser_build.add_argument('base_results',
                              help='the results file/directory to import')
    parser_build.add_argument('target_results',
                              help='the target file or directory to merge the base_results with')

