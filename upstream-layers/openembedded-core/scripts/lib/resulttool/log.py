# resulttool - Show logs
#
# Copyright (c) 2019 Garmin International
#
# SPDX-License-Identifier: GPL-2.0-only
#
import os
import resulttool.resultutils as resultutils

def show_ptest(result, ptest, logger):
    logdata = resultutils.ptestresult_get_log(result, ptest)
    if logdata is not None:
        print(logdata)
        return 0

    print("ptest '%s' log not found" % ptest)
    return 1

def show_reproducible(result, reproducible, logger):
    try:
        print(result['reproducible'][reproducible]['diffoscope.text'])
        return 0

    except KeyError:
        print("reproducible '%s' not found" % reproducible)
        return 1

def log(args, logger):
    results = resultutils.load_resultsdata(args.source)

    for _, run_name, _, r in resultutils.test_run_results(results):
        if args.list_ptest:
            print('\n'.join(sorted(r['ptestresult.sections'].keys())))

        if args.dump_ptest:
            for sectname in ['ptestresult.sections', 'ltpposixresult.sections', 'ltpresult.sections']:
             if sectname in r:
              for name, ptest in r[sectname].items():
                logdata = resultutils.generic_get_log(sectname, r, name)
                if logdata is not None:
                    dest_dir = args.dump_ptest
                    if args.prepend_run:
                        dest_dir = os.path.join(dest_dir, run_name)
                    if not sectname.startswith("ptest"):
                        dest_dir = os.path.join(dest_dir, sectname.split(".")[0])

                    os.makedirs(dest_dir, exist_ok=True)
                    dest = os.path.join(dest_dir, '%s.log' % name)
                    if os.path.exists(dest):
                        print("Overlapping ptest logs found, skipping %s. The '--prepend-run' option would avoid this" % name)
                        continue
                    print(dest)
                    with open(dest, 'w') as f:
                        f.write(logdata)

        if args.raw_ptest:
            found = False
            for sectname in ['ptestresult.rawlogs', 'ltpposixresult.rawlogs', 'ltpresult.rawlogs']:
                rawlog = resultutils.generic_get_rawlogs(sectname, r)
                if rawlog is not None:
                    print(rawlog)
                    found = True
            if not found:
                print('Raw ptest logs not found')
                return 1

        if args.raw_reproducible:
            if 'reproducible.rawlogs' in r:
                print(r['reproducible.rawlogs']['log'])
            else:
                print('Raw reproducible logs not found')
                return 1

        for ptest in args.ptest:
            if not show_ptest(r, ptest, logger):
                return 1

        for reproducible in args.reproducible:
            if not show_reproducible(r, reproducible, logger):
                return 1

def register_commands(subparsers):
    """Register subcommands from this plugin"""
    parser = subparsers.add_parser('log', help='show logs',
                                         description='show the logs from test results',
                                         group='analysis')
    parser.set_defaults(func=log)
    parser.add_argument('source',
            help='the results file/directory/URL to import')
    parser.add_argument('--list-ptest', action='store_true',
            help='list the ptest test names')
    parser.add_argument('--ptest', action='append', default=[],
            help='show logs for a ptest')
    parser.add_argument('--dump-ptest', metavar='DIR',
            help='Dump all ptest log files to the specified directory.')
    parser.add_argument('--reproducible', action='append', default=[],
            help='show logs for a reproducible test')
    parser.add_argument('--prepend-run', action='store_true',
            help='''Dump ptest results to a subdirectory named after the test run when using --dump-ptest.
                    Required if more than one test run is present in the result file''')
    parser.add_argument('--raw', action='store_true',
            help='show raw (ptest) logs. Deprecated. Alias for "--raw-ptest"', dest='raw_ptest')
    parser.add_argument('--raw-ptest', action='store_true',
            help='show raw ptest log')
    parser.add_argument('--raw-reproducible', action='store_true',
            help='show raw reproducible build logs')

