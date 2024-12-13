# test case management tool - manual execution from testopia test cases
#
# Copyright (c) 2018, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import argparse
import json
import os
import sys
import datetime
import re
import copy
from oeqa.core.runner import OETestResultJSONHelper


def load_json_file(f):
    with open(f, "r") as filedata:
        return json.load(filedata)

def write_json_file(f, json_data):
    os.makedirs(os.path.dirname(f), exist_ok=True)
    with open(f, 'w') as filedata:
        filedata.write(json.dumps(json_data, sort_keys=True, indent=1))

class ManualTestRunner(object):

    def _get_test_module(self, case_file):
        return os.path.basename(case_file).split('.')[0]

    def _get_input(self, config):
        while True:
            output = input('{} = '.format(config))
            if re.match('^[a-z0-9-.]+$', output):
                break
            print('Only lowercase alphanumeric, hyphen and dot are allowed. Please try again')
        return output

    def _get_available_config_options(self, config_options, test_module, target_config):
        avail_config_options = None
        if test_module in config_options:
            avail_config_options = config_options[test_module].get(target_config)
        return avail_config_options

    def _choose_config_option(self, options):
        while True:
            output = input('{} = '.format('Option index number'))
            if output in options:
                break
            print('Only integer index inputs from above available configuration options are allowed. Please try again.')
        return options[output]

    def _get_config(self, config_options, test_module):
        from oeqa.utils.metadata import get_layers
        from oeqa.utils.commands import get_bb_var
        from resulttool.resultutils import store_map

        layers = get_layers(get_bb_var('BBLAYERS'))
        configurations = {}
        configurations['LAYERS'] = layers
        configurations['STARTTIME'] = datetime.datetime.now().strftime('%Y%m%d%H%M%S')
        configurations['TEST_TYPE'] = 'manual'
        configurations['TEST_MODULE'] = test_module

        extra_config = set(store_map['manual']) - set(configurations)
        for config in sorted(extra_config):
            avail_config_options = self._get_available_config_options(config_options, test_module, config)
            if avail_config_options:
                print('---------------------------------------------')
                print('These are available configuration #%s options:' % config)
                print('---------------------------------------------')
                for option, _ in sorted(avail_config_options.items(), key=lambda x: int(x[0])):
                    print('%s: %s' % (option, avail_config_options[option]))
                print('Please select configuration option, enter the integer index number.')
                value_conf = self._choose_config_option(avail_config_options)
                print('---------------------------------------------\n')
            else:
                print('---------------------------------------------')
                print('This is configuration #%s. Please provide configuration value(use "None" if not applicable).' % config)
                print('---------------------------------------------')
                value_conf = self._get_input('Configuration Value')
                print('---------------------------------------------\n')
            configurations[config] = value_conf
        return configurations

    def _execute_test_steps(self, case):
        test_result = {}
        print('------------------------------------------------------------------------')
        print('Executing test case: %s' % case['test']['@alias'])
        print('------------------------------------------------------------------------')
        print('You have total %s test steps to be executed.' % len(case['test']['execution']))
        print('------------------------------------------------------------------------\n')
        for step, _ in sorted(case['test']['execution'].items(), key=lambda x: int(x[0])):
            print('Step %s: %s' % (step, case['test']['execution'][step]['action']))
            expected_output = case['test']['execution'][step]['expected_results']
            if expected_output:
                print('Expected output: %s' % expected_output)
        while True:
            done = input('\nPlease provide test results: (P)assed/(F)ailed/(B)locked/(S)kipped? \n').lower()
            result_types = {'p':'PASSED',
                            'f':'FAILED',
                            'b':'BLOCKED',
                            's':'SKIPPED'}
            if done in result_types:
                for r in result_types:
                    if done == r:
                        res = result_types[r]
                        if res == 'FAILED':
                            log_input = input('\nPlease enter the error and the description of the log: (Ex:log:211 Error Bitbake)\n')
                            test_result.update({case['test']['@alias']: {'status': '%s' % res, 'log': '%s' % log_input}})
                        else:
                            test_result.update({case['test']['@alias']: {'status': '%s' % res}})
                break
            print('Invalid input!')
        return test_result

    def _get_write_dir(self):
        return os.environ['BUILDDIR'] + '/tmp/log/manual/'

    def run_test(self, case_file, config_options_file, testcase_config_file):
        test_module = self._get_test_module(case_file)
        cases = load_json_file(case_file)
        config_options = {}
        if config_options_file:
            config_options = load_json_file(config_options_file)
        configurations = self._get_config(config_options, test_module)
        result_id = 'manual_%s_%s' % (test_module, configurations['STARTTIME'])
        test_results = {}
        if testcase_config_file:
            test_case_config = load_json_file(testcase_config_file)
            test_case_to_execute = test_case_config['testcases']
            for case in copy.deepcopy(cases) :
                if case['test']['@alias'] not in test_case_to_execute:
                    cases.remove(case)

        print('\nTotal number of test cases in this test suite: %s\n' % len(cases))
        for c in cases:
            test_result = self._execute_test_steps(c)
            test_results.update(test_result)
        return configurations, result_id, self._get_write_dir(), test_results

    def _get_true_false_input(self, input_message):
        yes_list = ['Y', 'YES']
        no_list = ['N', 'NO']
        while True:
            more_config_option = input(input_message).upper()
            if more_config_option in yes_list or more_config_option in no_list:
                break
            print('Invalid input!')
        if more_config_option in no_list:
            return False
        return True

    def make_config_option_file(self, logger, case_file, config_options_file):
        config_options = {}
        if config_options_file:
            config_options = load_json_file(config_options_file)
        new_test_module = self._get_test_module(case_file)
        print('Creating configuration options file for test module: %s' % new_test_module)
        new_config_options = {}

        while True:
            config_name = input('\nPlease provide test configuration to create:\n').upper()
            new_config_options[config_name] = {}
            while True:
                config_value = self._get_input('Configuration possible option value')
                config_option_index = len(new_config_options[config_name]) + 1
                new_config_options[config_name][config_option_index] = config_value
                more_config_option = self._get_true_false_input('\nIs there more configuration option input: (Y)es/(N)o\n')
                if not more_config_option:
                    break
            more_config = self._get_true_false_input('\nIs there more configuration to create: (Y)es/(N)o\n')
            if not more_config:
                break

        if new_config_options:
            config_options[new_test_module] = new_config_options
        if not config_options_file:
            config_options_file = os.path.join(self._get_write_dir(), 'manual_config_options.json')
        write_json_file(config_options_file, config_options)
        logger.info('Configuration option file created at %s' % config_options_file)

    def make_testcase_config_file(self, logger, case_file, testcase_config_file):
        if  testcase_config_file:
            if os.path.exists(testcase_config_file):
                print('\nTest configuration file with name %s already exists. Please provide a unique file name' % (testcase_config_file))
                return 0

        if not testcase_config_file:
            testcase_config_file = os.path.join(self._get_write_dir(), "testconfig_new.json")

        testcase_config = {}
        cases = load_json_file(case_file)
        new_test_module = self._get_test_module(case_file)
        new_testcase_config = {}
        new_testcase_config['testcases'] = []

        print('\nAdd testcases for this configuration file:')
        for case in cases:
            print('\n' + case['test']['@alias'])
            add_tc_config = self._get_true_false_input('\nDo you want to add this test case to test configuration : (Y)es/(N)o\n')
            if add_tc_config:
                new_testcase_config['testcases'].append(case['test']['@alias'])
        write_json_file(testcase_config_file, new_testcase_config)
        logger.info('Testcase Configuration file created at %s' % testcase_config_file)

def manualexecution(args, logger):
    testrunner = ManualTestRunner()
    if args.make_config_options_file:
        testrunner.make_config_option_file(logger, args.file, args.config_options_file)
        return 0
    if args.make_testcase_config_file:
        testrunner.make_testcase_config_file(logger, args.file, args.testcase_config_file)
        return 0
    configurations, result_id, write_dir, test_results = testrunner.run_test(args.file, args.config_options_file, args.testcase_config_file)
    resultjsonhelper = OETestResultJSONHelper()
    resultjsonhelper.dump_testresult_file(write_dir, configurations, result_id, test_results)
    return 0

def register_commands(subparsers):
    """Register subcommands from this plugin"""
    parser_build = subparsers.add_parser('manualexecution', help='helper script for results populating during manual test execution.',
                                         description='helper script for results populating during manual test execution. You can find manual test case JSON file in meta/lib/oeqa/manual/',
                                         group='manualexecution')
    parser_build.set_defaults(func=manualexecution)
    parser_build.add_argument('file', help='specify path to manual test case JSON file.Note: Please use \"\" to encapsulate the file path.')
    parser_build.add_argument('-c', '--config-options-file', default='',
                              help='the config options file to import and used as available configuration option selection or make config option file')
    parser_build.add_argument('-m', '--make-config-options-file', action='store_true',
                              help='make the configuration options file based on provided inputs')
    parser_build.add_argument('-t', '--testcase-config-file', default='',
                              help='the testcase configuration file to enable user to run a selected set of test case or make a testcase configuration file')
    parser_build.add_argument('-d', '--make-testcase-config-file', action='store_true',
                    help='make the testcase configuration file to run a set of test cases based on user selection')