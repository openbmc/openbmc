# resulttool - Show logs
#
# Copyright (c) 2019 Garmin International
#
# SPDX-License-Identifier: GPL-2.0-only
#
import os
import resulttool.resultutils as resultutils

def show_ptest(result, ptest, logger):
    if 'ptestresult.sections' in result:
        if ptest in result['ptestresult.sections'] and 'log' in result['ptestresult.sections'][ptest]:
            print(result['ptestresult.sections'][ptest]['log'])
            return 0

    print("ptest '%s' not found" % ptest)
    return 1

def log(args, logger):
    results = resultutils.load_resultsdata(args.source)

    ptest_count = sum(1 for _, _, _, r in resultutils.test_run_results(results) if 'ptestresult.sections' in r)
    if ptest_count > 1 and not args.prepend_run:
        print("%i ptest sections found. '--prepend-run' is required" % ptest_count)
        return 1

    for _, run_name, _, r in resultutils.test_run_results(results):
        if args.dump_ptest:
            if 'ptestresult.sections' in r:
                for name, ptest in r['ptestresult.sections'].items():
                    if 'log' in ptest:
                        dest_dir = args.dump_ptest
                        if args.prepend_run:
                            dest_dir = os.path.join(dest_dir, run_name)

                        os.makedirs(dest_dir, exist_ok=True)

                        dest = os.path.join(dest_dir, '%s.log' % name)
                        print(dest)
                        with open(dest, 'w') as f:
                            f.write(ptest['log'])

        if args.raw:
            if 'ptestresult.rawlogs' in r:
                print(r['ptestresult.rawlogs']['log'])
            else:
                print('Raw logs not found')
                return 1

        for ptest in args.ptest:
            if not show_ptest(r, ptest, logger):
                return 1

def register_commands(subparsers):
    """Register subcommands from this plugin"""
    parser = subparsers.add_parser('log', help='show logs',
                                         description='show the logs from test results',
                                         group='analysis')
    parser.set_defaults(func=log)
    parser.add_argument('source',
            help='the results file/directory/URL to import')
    parser.add_argument('--ptest', action='append', default=[],
            help='show logs for a ptest')
    parser.add_argument('--dump-ptest', metavar='DIR',
            help='Dump all ptest log files to the specified directory.')
    parser.add_argument('--prepend-run', action='store_true',
            help='''Dump ptest results to a subdirectory named after the test run when using --dump-ptest.
                    Required if more than one test run is present in the result file''')
    parser.add_argument('--raw', action='store_true',
            help='show raw logs')

