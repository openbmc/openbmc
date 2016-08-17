#!/usr/bin/env python

import sys
import os
import re
import ftools


# A parser that can be used to identify weather a line is a test result or a section statement.
class Lparser(object):

    def __init__(self, test_0_pass_regex, test_0_fail_regex, section_0_begin_regex=None, section_0_end_regex=None, **kwargs):
        # Initialize the arguments dictionary
        if kwargs:
            self.args = kwargs
        else:
            self.args = {}

        # Add the default args to the dictionary
        self.args['test_0_pass_regex'] = test_0_pass_regex
        self.args['test_0_fail_regex'] = test_0_fail_regex
        if section_0_begin_regex:
            self.args['section_0_begin_regex'] = section_0_begin_regex
        if section_0_end_regex:
            self.args['section_0_end_regex'] = section_0_end_regex

        self.test_possible_status = ['pass', 'fail', 'error']
        self.section_possible_status = ['begin', 'end']

        self.initialized = False


    # Initialize the parser with the current configuration
    def init(self):

        # extra arguments can be added by the user to define new test and section categories. They must follow a pre-defined pattern: <type>_<category_name>_<status>_regex
        self.test_argument_pattern = "^test_(.+?)_(%s)_regex" % '|'.join(map(str, self.test_possible_status))
        self.section_argument_pattern = "^section_(.+?)_(%s)_regex" % '|'.join(map(str, self.section_possible_status))

        # Initialize the test and section regex dictionaries
        self.test_regex = {}
        self.section_regex ={}

        for arg, value in self.args.items():
            if not value:
                raise Exception('The value of provided argument %s is %s. Should have a valid value.' % (key, value))
            is_test =  re.search(self.test_argument_pattern, arg)
            is_section = re.search(self.section_argument_pattern, arg)
            if is_test:
                if not is_test.group(1) in self.test_regex:
                    self.test_regex[is_test.group(1)] = {}
                self.test_regex[is_test.group(1)][is_test.group(2)] = re.compile(value)
            elif is_section:
                if not is_section.group(1) in self.section_regex:
                    self.section_regex[is_section.group(1)] = {}
                self.section_regex[is_section.group(1)][is_section.group(2)] = re.compile(value)
            else:
                # TODO: Make these call a traceback instead of a simple exception..
                raise Exception("The provided argument name does not correspond to any valid type. Please give one of the following types:\nfor tests: %s\nfor sections: %s" % (self.test_argument_pattern, self.section_argument_pattern))

        self.initialized = True

    # Parse a line and return a tuple containing the type of result (test/section) and its category, status and name
    def parse_line(self, line):
        if not self.initialized:
            raise Exception("The parser is not initialized..")

        for test_category, test_status_list in self.test_regex.items():
            for test_status, status_regex in test_status_list.items():
                test_name = status_regex.search(line)
                if test_name:
                    return ['test', test_category, test_status, test_name.group(1)]

        for section_category, section_status_list in self.section_regex.items():
            for section_status, status_regex in section_status_list.items():
                section_name = status_regex.search(line)
                if section_name:
                    return ['section', section_category, section_status, section_name.group(1)]
        return None


class Result(object):

    def __init__(self):
        self.result_dict = {}

    def store(self, section, test, status):
        if not section in self.result_dict:
            self.result_dict[section] = []

        self.result_dict[section].append((test, status))

    # sort tests by the test name(the first element of the tuple), for each section. This can be helpful when using git to diff for changes by making sure they are always in the same order.
    def sort_tests(self):
        for package in self.result_dict:
            sorted_results = sorted(self.result_dict[package], key=lambda tup: tup[0])
            self.result_dict[package] = sorted_results

    # Log the results as files. The file name is the section name and the contents are the tests in that section.
    def log_as_files(self, target_dir, test_status):
        status_regex = re.compile('|'.join(map(str, test_status)))
        if not type(test_status) == type([]):
            raise Exception("test_status should be a list. Got " + str(test_status) + " instead.")
        if not os.path.exists(target_dir):
            raise Exception("Target directory does not exist: %s" % target_dir)

        for section, test_results in self.result_dict.items():
            prefix = ''
            for x in test_status:
                prefix +=x+'.'
            if (section != ''):
                prefix += section
            section_file = os.path.join(target_dir, prefix)
            # purge the file contents if it exists
            open(section_file, 'w').close()
            for test_result in test_results:
                (test_name, status) = test_result
                # we log only the tests with status in the test_status list
                match_status = status_regex.search(status)
                if match_status:
                    ftools.append_file(section_file, status + ": " + test_name)

    # Not yet implemented!
    def log_to_lava(self):
        pass
