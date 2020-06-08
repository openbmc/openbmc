#
# SPDX-License-Identifier: MIT
#

import sys
import os
import re

# A parser that can be used to identify weather a line is a test result or a section statement.
class PtestParser(object):
    def __init__(self):
        self.results = {}
        self.sections = {}

    def parse(self, logfile):
        test_regex = {}
        test_regex['PASSED'] = re.compile(r"^PASS:(.+)")
        test_regex['FAILED'] = re.compile(r"^FAIL:([^(]+)")
        test_regex['SKIPPED'] = re.compile(r"^SKIP:(.+)")

        section_regex = {}
        section_regex['begin'] = re.compile(r"^BEGIN: .*/(.+)/ptest")
        section_regex['end'] = re.compile(r"^END: .*/(.+)/ptest")
        section_regex['duration'] = re.compile(r"^DURATION: (.+)")
        section_regex['exitcode'] = re.compile(r"^ERROR: Exit status is (.+)")
        section_regex['timeout'] = re.compile(r"^TIMEOUT: .*/(.+)/ptest")

        # Cache markers so we don't take the re.search() hit all the time.
        markers = ("PASS:", "FAIL:", "SKIP:", "BEGIN:", "END:", "DURATION:", "ERROR: Exit", "TIMEOUT:")

        def newsection():
            return { 'name': "No-section", 'log': [] }

        current_section = newsection()

        with open(logfile, errors='replace') as f:
            for line in f:
                if not line.startswith(markers):
                    current_section['log'].append(line)
                    continue

                result = section_regex['begin'].search(line)
                if result:
                    current_section['name'] = result.group(1)
                    continue

                result = section_regex['end'].search(line)
                if result:
                    if current_section['name'] != result.group(1):
                        bb.warn("Ptest END log section mismatch %s vs. %s" % (current_section['name'], result.group(1)))
                    if current_section['name'] in self.sections:
                        bb.warn("Ptest duplicate section for %s" % (current_section['name']))
                    self.sections[current_section['name']] = current_section
                    del self.sections[current_section['name']]['name']
                    current_section = newsection()
                    continue

                result = section_regex['timeout'].search(line)
                if result:
                    if current_section['name'] != result.group(1):
                        bb.warn("Ptest TIMEOUT log section mismatch %s vs. %s" % (current_section['name'], result.group(1)))
                    current_section['timeout'] = True
                    continue

                for t in ['duration', 'exitcode']:
                    result = section_regex[t].search(line)
                    if result:
                        current_section[t] = result.group(1)
                        continue

                current_section['log'].append(line)

                for t in test_regex:
                    result = test_regex[t].search(line)
                    if result:
                        if current_section['name'] not in self.results:
                            self.results[current_section['name']] = {}
                        self.results[current_section['name']][result.group(1).strip()] = t

        # Python performance for repeatedly joining long strings is poor, do it all at once at the end.
        # For 2.1 million lines in a log this reduces 18 hours to 12s.
        for section in self.sections:
            self.sections[section]['log'] = "".join(self.sections[section]['log'])

        return self.results, self.sections

    # Log the results as files. The file name is the section name and the contents are the tests in that section.
    def results_as_files(self, target_dir):
        if not os.path.exists(target_dir):
            raise Exception("Target directory does not exist: %s" % target_dir)

        for section in self.results:
            prefix = 'No-section'
            if section:
                prefix = section
            section_file = os.path.join(target_dir, prefix)
            # purge the file contents if it exists
            with open(section_file, 'w') as f:
                for test_name in sorted(self.results[section]):
                    status = self.results[section][test_name]
                    f.write(status + ": " + test_name + "\n")


# ltp log parsing
class LtpParser(object):
    def __init__(self):
        self.results = {}
        self.section = {'duration': "", 'log': ""}

    def parse(self, logfile):
        test_regex = {}
        test_regex['PASSED'] = re.compile(r"PASS")
        test_regex['FAILED'] = re.compile(r"FAIL")
        test_regex['SKIPPED'] = re.compile(r"SKIP")

        with open(logfile, errors='replace') as f:
            for line in f:
                for t in test_regex:
                    result = test_regex[t].search(line)
                    if result:
                        self.results[line.split()[0].strip()] = t

        for test in self.results:
            result = self.results[test]
            self.section['log'] = self.section['log'] + ("%s: %s\n" % (result.strip()[:-2], test.strip()))

        return self.results, self.section


# ltp Compliance log parsing
class LtpComplianceParser(object):
    def __init__(self):
        self.results = {}
        self.section = {'duration': "", 'log': ""}

    def parse(self, logfile):
        test_regex = {}
        test_regex['PASSED'] = re.compile(r"^PASS")
        test_regex['FAILED'] = re.compile(r"^FAIL")
        test_regex['SKIPPED'] = re.compile(r"(?:UNTESTED)|(?:UNSUPPORTED)")

        section_regex = {}
        section_regex['test'] = re.compile(r"^Testing")

        with open(logfile, errors='replace') as f:
            for line in f:
                result = section_regex['test'].search(line)
                if result:
                    self.name = ""
                    self.name = line.split()[1].strip()
                    self.results[self.name] = "PASSED"
                    failed = 0

                failed_result = test_regex['FAILED'].search(line)
                if failed_result:
                    failed = line.split()[1].strip()
                    if int(failed) > 0:
                        self.results[self.name] = "FAILED"

        for test in self.results:
            result = self.results[test]
            self.section['log'] = self.section['log'] + ("%s: %s\n" % (result.strip()[:-2], test.strip()))

        return self.results, self.section
