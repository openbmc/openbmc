# Copyright (c) 2016, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Build performance test base classes and functionality"""
import json
import logging
import os
import re
import resource
import socket
import shutil
import time
import unittest
import xml.etree.ElementTree as ET
from collections import OrderedDict
from datetime import datetime, timedelta
from functools import partial
from multiprocessing import Process
from multiprocessing import SimpleQueue
from xml.dom import minidom

import oe.path
from oeqa.utils.commands import CommandError, runCmd, get_bb_vars
from oeqa.utils.git import GitError, GitRepo

# Get logger for this module
log = logging.getLogger('build-perf')

# Our own version of runCmd which does not raise AssertErrors which would cause
# errors to interpreted as failures
runCmd2 = partial(runCmd, assert_error=False, limit_exc_output=40)


class KernelDropCaches(object):
    """Container of the functions for dropping kernel caches"""
    sudo_passwd = None

    @classmethod
    def check(cls):
        """Check permssions for dropping kernel caches"""
        from getpass import getpass
        from locale import getdefaultlocale
        cmd = ['sudo', '-k', '-n', 'tee', '/proc/sys/vm/drop_caches']
        ret = runCmd2(cmd, ignore_status=True, data=b'0')
        if ret.output.startswith('sudo:'):
            pass_str = getpass(
                "\nThe script requires sudo access to drop caches between "
                "builds (echo 3 > /proc/sys/vm/drop_caches).\n"
                "Please enter your sudo password: ")
            cls.sudo_passwd = bytes(pass_str, getdefaultlocale()[1])

    @classmethod
    def drop(cls):
        """Drop kernel caches"""
        cmd = ['sudo', '-k']
        if cls.sudo_passwd:
            cmd.append('-S')
            input_data = cls.sudo_passwd + b'\n'
        else:
            cmd.append('-n')
            input_data = b''
        cmd += ['tee', '/proc/sys/vm/drop_caches']
        input_data += b'3'
        runCmd2(cmd, data=input_data)


def str_to_fn(string):
    """Convert string to a sanitized filename"""
    return re.sub(r'(\W+)', '-', string, flags=re.LOCALE)


class ResultsJsonEncoder(json.JSONEncoder):
    """Extended encoder for build perf test results"""
    unix_epoch = datetime.utcfromtimestamp(0)

    def default(self, obj):
        """Encoder for our types"""
        if isinstance(obj, datetime):
            # NOTE: we assume that all timestamps are in UTC time
            return (obj - self.unix_epoch).total_seconds()
        if isinstance(obj, timedelta):
            return obj.total_seconds()
        return json.JSONEncoder.default(self, obj)


class BuildPerfTestResult(unittest.TextTestResult):
    """Runner class for executing the individual tests"""
    # List of test cases to run
    test_run_queue = []

    def __init__(self, out_dir, *args, **kwargs):
        super(BuildPerfTestResult, self).__init__(*args, **kwargs)

        self.out_dir = out_dir
        self.hostname = socket.gethostname()
        self.product = os.getenv('OE_BUILDPERFTEST_PRODUCT', 'oe-core')
        self.start_time = self.elapsed_time = None
        self.successes = []

    def addSuccess(self, test):
        """Record results from successful tests"""
        super(BuildPerfTestResult, self).addSuccess(test)
        self.successes.append(test)

    def addError(self, test, err):
        """Record results from crashed test"""
        test.err = err
        super(BuildPerfTestResult, self).addError(test, err)

    def addFailure(self, test, err):
        """Record results from failed test"""
        test.err = err
        super(BuildPerfTestResult, self).addFailure(test, err)

    def addExpectedFailure(self, test, err):
        """Record results from expectedly failed test"""
        test.err = err
        super(BuildPerfTestResult, self).addExpectedFailure(test, err)

    def startTest(self, test):
        """Pre-test hook"""
        test.base_dir = self.out_dir
        log.info("Executing test %s: %s", test.name, test.shortDescription())
        self.stream.write(datetime.now().strftime("[%Y-%m-%d %H:%M:%S] "))
        super(BuildPerfTestResult, self).startTest(test)

    def startTestRun(self):
        """Pre-run hook"""
        self.start_time = datetime.utcnow()

    def stopTestRun(self):
        """Pre-run hook"""
        self.elapsed_time = datetime.utcnow() - self.start_time

    def all_results(self):
        compound = [('SUCCESS', t, None) for t in self.successes] + \
                   [('FAILURE', t, m) for t, m in self.failures] + \
                   [('ERROR', t, m) for t, m in self.errors] + \
                   [('EXPECTED_FAILURE', t, m) for t, m in self.expectedFailures] + \
                   [('UNEXPECTED_SUCCESS', t, None) for t in self.unexpectedSuccesses] + \
                   [('SKIPPED', t, m) for t, m in self.skipped]
        return sorted(compound, key=lambda info: info[1].start_time)


    def write_buildstats_json(self):
        """Write buildstats file"""
        buildstats = OrderedDict()
        for _, test, _ in self.all_results():
            for key, val in test.buildstats.items():
                buildstats[test.name + '.' + key] = val
        with open(os.path.join(self.out_dir, 'buildstats.json'), 'w') as fobj:
            json.dump(buildstats, fobj, cls=ResultsJsonEncoder)


    def write_results_json(self):
        """Write test results into a json-formatted file"""
        results = OrderedDict([('tester_host', self.hostname),
                               ('start_time', self.start_time),
                               ('elapsed_time', self.elapsed_time),
                               ('tests', OrderedDict())])

        for status, test, reason in self.all_results():
            test_result = OrderedDict([('name', test.name),
                                       ('description', test.shortDescription()),
                                       ('status', status),
                                       ('start_time', test.start_time),
                                       ('elapsed_time', test.elapsed_time),
                                       ('measurements', test.measurements)])
            if status in ('ERROR', 'FAILURE', 'EXPECTED_FAILURE'):
                test_result['message'] = str(test.err[1])
                test_result['err_type'] = test.err[0].__name__
                test_result['err_output'] = reason
            elif reason:
                test_result['message'] = reason

            results['tests'][test.name] = test_result

        with open(os.path.join(self.out_dir, 'results.json'), 'w') as fobj:
            json.dump(results, fobj, indent=4,
                      cls=ResultsJsonEncoder)

    def write_results_xml(self):
        """Write test results into a JUnit XML file"""
        top = ET.Element('testsuites')
        suite = ET.SubElement(top, 'testsuite')
        suite.set('name', 'oeqa.buildperf')
        suite.set('timestamp', self.start_time.isoformat())
        suite.set('time', str(self.elapsed_time.total_seconds()))
        suite.set('hostname', self.hostname)
        suite.set('failures', str(len(self.failures) + len(self.expectedFailures)))
        suite.set('errors', str(len(self.errors)))
        suite.set('skipped', str(len(self.skipped)))

        test_cnt = 0
        for status, test, reason in self.all_results():
            test_cnt += 1
            testcase = ET.SubElement(suite, 'testcase')
            testcase.set('classname', test.__module__ + '.' + test.__class__.__name__)
            testcase.set('name', test.name)
            testcase.set('description', test.shortDescription())
            testcase.set('timestamp', test.start_time.isoformat())
            testcase.set('time', str(test.elapsed_time.total_seconds()))
            if status in ('ERROR', 'FAILURE', 'EXP_FAILURE'):
                if status in ('FAILURE', 'EXP_FAILURE'):
                    result = ET.SubElement(testcase, 'failure')
                else:
                    result = ET.SubElement(testcase, 'error')
                result.set('message', str(test.err[1]))
                result.set('type', test.err[0].__name__)
                result.text = reason
            elif status == 'SKIPPED':
                result = ET.SubElement(testcase, 'skipped')
                result.text = reason
            elif status not in ('SUCCESS', 'UNEXPECTED_SUCCESS'):
                raise TypeError("BUG: invalid test status '%s'" % status)

            for data in test.measurements.values():
                measurement = ET.SubElement(testcase, data['type'])
                measurement.set('name', data['name'])
                measurement.set('legend', data['legend'])
                vals = data['values']
                if data['type'] == BuildPerfTestCase.SYSRES:
                    ET.SubElement(measurement, 'time',
                                  timestamp=vals['start_time'].isoformat()).text = \
                        str(vals['elapsed_time'].total_seconds())
                    attrib = dict((k, str(v)) for k, v in vals['iostat'].items())
                    ET.SubElement(measurement, 'iostat', attrib=attrib)
                    attrib = dict((k, str(v)) for k, v in vals['rusage'].items())
                    ET.SubElement(measurement, 'rusage', attrib=attrib)
                elif data['type'] == BuildPerfTestCase.DISKUSAGE:
                    ET.SubElement(measurement, 'size').text = str(vals['size'])
                else:
                    raise TypeError('BUG: unsupported measurement type')

        suite.set('tests', str(test_cnt))

        # Use minidom for pretty-printing
        dom_doc = minidom.parseString(ET.tostring(top, 'utf-8'))
        with open(os.path.join(self.out_dir, 'results.xml'), 'w') as fobj:
            dom_doc.writexml(fobj, addindent='  ', newl='\n', encoding='utf-8')


class BuildPerfTestCase(unittest.TestCase):
    """Base class for build performance tests"""
    SYSRES = 'sysres'
    DISKUSAGE = 'diskusage'
    build_target = None

    def __init__(self, *args, **kwargs):
        super(BuildPerfTestCase, self).__init__(*args, **kwargs)
        self.name = self._testMethodName
        self.base_dir = None
        self.start_time = None
        self.elapsed_time = None
        self.measurements = OrderedDict()
        self.buildstats = OrderedDict()
        # self.err is supposed to be a tuple from sys.exc_info()
        self.err = None
        self.bb_vars = get_bb_vars()
        # TODO: remove 'times' and 'sizes' arrays when globalres support is
        # removed
        self.times = []
        self.sizes = []

    @property
    def tmp_dir(self):
        return os.path.join(self.base_dir, self.name + '.tmp')

    def shortDescription(self):
        return super(BuildPerfTestCase, self).shortDescription() or ""

    def setUp(self):
        """Set-up fixture for each test"""
        if not os.path.isdir(self.tmp_dir):
            os.mkdir(self.tmp_dir)
        if self.build_target:
            self.run_cmd(['bitbake', self.build_target, '--runall=fetch'])

    def tearDown(self):
        """Tear-down fixture for each test"""
        if os.path.isdir(self.tmp_dir):
            shutil.rmtree(self.tmp_dir)

    def run(self, *args, **kwargs):
        """Run test"""
        self.start_time = datetime.now()
        super(BuildPerfTestCase, self).run(*args, **kwargs)
        self.elapsed_time = datetime.now() - self.start_time

    def run_cmd(self, cmd):
        """Convenience method for running a command"""
        cmd_str = cmd if isinstance(cmd, str) else ' '.join(cmd)
        log.info("Logging command: %s", cmd_str)
        try:
            runCmd2(cmd)
        except CommandError as err:
            log.error("Command failed: %s", err.retcode)
            raise

    def _append_measurement(self, measurement):
        """Simple helper for adding measurements results"""
        if measurement['name'] in self.measurements:
            raise ValueError('BUG: two measurements with the same name in {}'.format(
                self.__class__.__name__))
        self.measurements[measurement['name']] = measurement

    def measure_cmd_resources(self, cmd, name, legend, save_bs=False):
        """Measure system resource usage of a command"""
        def _worker(data_q, cmd, **kwargs):
            """Worker process for measuring resources"""
            try:
                start_time = datetime.now()
                ret = runCmd2(cmd, **kwargs)
                etime = datetime.now() - start_time
                rusage_struct = resource.getrusage(resource.RUSAGE_CHILDREN)
                iostat = OrderedDict()
                with open('/proc/{}/io'.format(os.getpid())) as fobj:
                    for line in fobj.readlines():
                        key, val = line.split(':')
                        iostat[key] = int(val)
                rusage = OrderedDict()
                # Skip unused fields, (i.e. 'ru_ixrss', 'ru_idrss', 'ru_isrss',
                # 'ru_nswap', 'ru_msgsnd', 'ru_msgrcv' and 'ru_nsignals')
                for key in ['ru_utime', 'ru_stime', 'ru_maxrss', 'ru_minflt',
                            'ru_majflt', 'ru_inblock', 'ru_oublock',
                            'ru_nvcsw', 'ru_nivcsw']:
                    rusage[key] = getattr(rusage_struct, key)
                data_q.put({'ret': ret,
                            'start_time': start_time,
                            'elapsed_time': etime,
                            'rusage': rusage,
                            'iostat': iostat})
            except Exception as err:
                data_q.put(err)

        cmd_str = cmd if isinstance(cmd, str) else ' '.join(cmd)
        log.info("Timing command: %s", cmd_str)
        data_q = SimpleQueue()
        try:
            proc = Process(target=_worker, args=(data_q, cmd,))
            proc.start()
            data = data_q.get()
            proc.join()
            if isinstance(data, Exception):
                raise data
        except CommandError:
            log.error("Command '%s' failed", cmd_str)
            raise
        etime = data['elapsed_time']

        measurement = OrderedDict([('type', self.SYSRES),
                                   ('name', name),
                                   ('legend', legend)])
        measurement['values'] = OrderedDict([('start_time', data['start_time']),
                                             ('elapsed_time', etime),
                                             ('rusage', data['rusage']),
                                             ('iostat', data['iostat'])])
        if save_bs:
            self.save_buildstats(name)

        self._append_measurement(measurement)

        # Append to 'times' array for globalres log
        e_sec = etime.total_seconds()
        self.times.append('{:d}:{:02d}:{:05.2f}'.format(int(e_sec / 3600),
                                                      int((e_sec % 3600) / 60),
                                                       e_sec % 60))

    def measure_disk_usage(self, path, name, legend, apparent_size=False):
        """Estimate disk usage of a file or directory"""
        cmd = ['du', '-s', '--block-size', '1024']
        if apparent_size:
            cmd.append('--apparent-size')
        cmd.append(path)

        ret = runCmd2(cmd)
        size = int(ret.output.split()[0])
        log.debug("Size of %s path is %s", path, size)
        measurement = OrderedDict([('type', self.DISKUSAGE),
                                   ('name', name),
                                   ('legend', legend)])
        measurement['values'] = OrderedDict([('size', size)])
        self._append_measurement(measurement)
        # Append to 'sizes' array for globalres log
        self.sizes.append(str(size))

    def save_buildstats(self, measurement_name):
        """Save buildstats"""
        def split_nevr(nevr):
            """Split name and version information from recipe "nevr" string"""
            n_e_v, revision = nevr.rsplit('-', 1)
            match = re.match(r'^(?P<name>\S+)-((?P<epoch>[0-9]{1,5})_)?(?P<version>[0-9]\S*)$',
                             n_e_v)
            if not match:
                # If we're not able to parse a version starting with a number, just
                # take the part after last dash
                match = re.match(r'^(?P<name>\S+)-((?P<epoch>[0-9]{1,5})_)?(?P<version>[^-]+)$',
                                 n_e_v)
            name = match.group('name')
            version = match.group('version')
            epoch = match.group('epoch')
            return name, epoch, version, revision

        def bs_to_json(filename):
            """Convert (task) buildstats file into json format"""
            bs_json = OrderedDict()
            iostat = OrderedDict()
            rusage = OrderedDict()
            with open(filename) as fobj:
                for line in fobj.readlines():
                    key, val = line.split(':', 1)
                    val = val.strip()
                    if key == 'Started':
                        start_time = datetime.utcfromtimestamp(float(val))
                        bs_json['start_time'] = start_time
                    elif key == 'Ended':
                        end_time = datetime.utcfromtimestamp(float(val))
                    elif key.startswith('IO '):
                        split = key.split()
                        iostat[split[1]] = int(val)
                    elif key.find('rusage') >= 0:
                        split = key.split()
                        ru_key = split[-1]
                        if ru_key in ('ru_stime', 'ru_utime'):
                            val = float(val)
                        else:
                            val = int(val)
                        rusage[ru_key] = rusage.get(ru_key, 0) + val
                    elif key == 'Status':
                        bs_json['status'] = val
            bs_json['elapsed_time'] = end_time - start_time
            bs_json['rusage'] = rusage
            bs_json['iostat'] = iostat
            return bs_json

        log.info('Saving buildstats in JSON format')
        bs_dirs = sorted(os.listdir(self.bb_vars['BUILDSTATS_BASE']))
        if len(bs_dirs) > 1:
            log.warning("Multiple buildstats found for test %s, only "
                        "archiving the last one", self.name)
        bs_dir = os.path.join(self.bb_vars['BUILDSTATS_BASE'], bs_dirs[-1])

        buildstats = []
        for fname in os.listdir(bs_dir):
            recipe_dir = os.path.join(bs_dir, fname)
            if not os.path.isdir(recipe_dir) or fname == "reduced_proc_pressure":
                continue
            name, epoch, version, revision = split_nevr(fname)
            recipe_bs = OrderedDict((('name', name),
                                     ('epoch', epoch),
                                     ('version', version),
                                     ('revision', revision),
                                     ('tasks', OrderedDict())))
            for task in os.listdir(recipe_dir):
                recipe_bs['tasks'][task] = bs_to_json(os.path.join(recipe_dir,
                                                                   task))
            buildstats.append(recipe_bs)

        self.buildstats[measurement_name] = buildstats

    def rm_tmp(self):
        """Cleanup temporary/intermediate files and directories"""
        log.debug("Removing temporary and cache files")
        for name in ['bitbake.lock', 'cache/sanity_info',
                     self.bb_vars['TMPDIR']]:
            oe.path.remove(name, recurse=True)

    def rm_sstate(self):
        """Remove sstate directory"""
        log.debug("Removing sstate-cache")
        oe.path.remove(self.bb_vars['SSTATE_DIR'], recurse=True)

    def rm_cache(self):
        """Drop bitbake caches"""
        oe.path.remove(self.bb_vars['PERSISTENT_DIR'], recurse=True)

    @staticmethod
    def sync():
        """Sync and drop kernel caches"""
        runCmd2('bitbake -m', ignore_status=True)
        log.debug("Syncing and dropping kernel caches""")
        KernelDropCaches.drop()
        os.sync()
        # Wait a bit for all the dirty blocks to be written onto disk
        time.sleep(3)


class BuildPerfTestLoader(unittest.TestLoader):
    """Test loader for build performance tests"""
    sortTestMethodsUsing = None


class BuildPerfTestRunner(unittest.TextTestRunner):
    """Test loader for build performance tests"""
    sortTestMethodsUsing = None

    def __init__(self, out_dir, *args, **kwargs):
        super(BuildPerfTestRunner, self).__init__(*args, **kwargs)
        self.out_dir = out_dir

    def _makeResult(self):
        return BuildPerfTestResult(self.out_dir, self.stream, self.descriptions,
                                   self.verbosity)
