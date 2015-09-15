#!/usr/bin/env python

import os
import sys
from optparse import OptionParser


def parse_args():
    parser = OptionParser()
    parser.add_option('-s', '--settings', help='Define settings.')
    parser.add_option('-t', '--unittest', help='Define which test to run. Default all.')
    options, args = parser.parse_args()

    if not options.settings:
        parser.print_help()
        sys.exit(1)

    if not options.unittest:
        options.unittest = ['aggregation']

    return options


def get_runner(settings_module):
    '''
    Asks Django for the TestRunner defined in settings or the default one.
    '''
    os.environ['DJANGO_SETTINGS_MODULE'] = settings_module

    import django
    from django.test.utils import get_runner
    from django.conf import settings

    if hasattr(django, 'setup'):
        django.setup()

    return get_runner(settings)


def runtests():
    options = parse_args()
    TestRunner = get_runner(options.settings)
    runner = TestRunner(verbosity=1, interactive=True, failfast=False)
    sys.exit(runner.run_tests([]))


if __name__ == '__main__':
    runtests()
