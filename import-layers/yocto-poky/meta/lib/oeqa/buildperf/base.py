# Copyright (c) 2016, Intel Corporation.
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
"""Build performance test base classes and functionality"""
import glob
import json
import logging
import os
import re
import resource
import shutil
import socket
import time
import traceback
import unittest
from datetime import datetime, timedelta
from functools import partial
from multiprocessing import Process
from multiprocessing import SimpleQueue

import oe.path
from oeqa.utils.commands import CommandError, runCmd, get_bb_vars
from oeqa.utils.git import GitError, GitRepo

# Get logger for this module
log = logging.getLogger('build-perf')

# Our own version of runCmd which does not raise AssertErrors which would cause
# errors to interpreted as failures
runCmd2 = partial(runCmd, assert_error=False)


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
        # Get Git parameters
        try:
            self.repo = GitRepo('.')
        except GitError:
            self.repo = None
        self.git_commit, self.git_commit_count, self.git_branch = \
                self.get_git_revision()
        self.hostname = socket.gethostname()
        self.product = os.getenv('OE_BUILDPERFTEST_PRODUCT', 'oe-core')
        self.start_time = self.elapsed_time = None
        self.successes = []
        log.info("Using Git branch:commit %s:%s (%s)", self.git_branch,
                 self.git_commit, self.git_commit_count)

    def get_git_revision(self):
        """Get git branch and commit under testing"""
        commit = os.getenv('OE_BUILDPERFTEST_GIT_COMMIT')
        commit_cnt = os.getenv('OE_BUILDPERFTEST_GIT_COMMIT_COUNT')
        branch = os.getenv('OE_BUILDPERFTEST_GIT_BRANCH')
        if not self.repo and (not commit or not commit_cnt or not branch):
            log.info("The current working directory doesn't seem to be a Git "
                     "repository clone. You can specify branch and commit "
                     "displayed in test results with OE_BUILDPERFTEST_GIT_BRANCH, "
                     "OE_BUILDPERFTEST_GIT_COMMIT and "
                     "OE_BUILDPERFTEST_GIT_COMMIT_COUNT environment variables")
        else:
            if not commit:
                commit = self.repo.rev_parse('HEAD^0')
                commit_cnt = self.repo.run_cmd(['rev-list', '--count', 'HEAD^0'])
            if not branch:
                branch = self.repo.get_current_branch()
                if not branch:
                    log.debug('Currently on detached HEAD')
        return str(commit), str(commit_cnt), str(branch)

    def addSuccess(self, test):
        """Record results from successful tests"""
        super(BuildPerfTestResult, self).addSuccess(test)
        self.successes.append((test, None))

    def startTest(self, test):
        """Pre-test hook"""
        test.base_dir = self.out_dir
        os.mkdir(test.out_dir)
        log.info("Executing test %s: %s", test.name, test.shortDescription())
        self.stream.write(datetime.now().strftime("[%Y-%m-%d %H:%M:%S] "))
        super(BuildPerfTestResult, self).startTest(test)

    def startTestRun(self):
        """Pre-run hook"""
        self.start_time = datetime.utcnow()

    def stopTestRun(self):
        """Pre-run hook"""
        self.elapsed_time = datetime.utcnow() - self.start_time
        self.write_results_json()

    def all_results(self):
        result_map = {'SUCCESS': self.successes,
                      'FAIL': self.failures,
                      'ERROR': self.errors,
                      'EXP_FAIL': self.expectedFailures,
                      'UNEXP_SUCCESS': self.unexpectedSuccesses,
                      'SKIPPED': self.skipped}
        for status, tests in result_map.items():
            for test in tests:
                yield (status, test)


    def update_globalres_file(self, filename):
        """Write results to globalres csv file"""
        # Map test names to time and size columns in globalres
        # The tuples represent index and length of times and sizes
        # respectively
        gr_map = {'test1': ((0, 1), (8, 1)),
                  'test12': ((1, 1), (None, None)),
                  'test13': ((2, 1), (9, 1)),
                  'test2': ((3, 1), (None, None)),
                  'test3': ((4, 3), (None, None)),
                  'test4': ((7, 1), (10, 2))}

        if self.repo:
            git_tag_rev = self.repo.run_cmd(['describe', self.git_commit])
        else:
            git_tag_rev = self.git_commit

        values = ['0'] * 12
        for status, (test, msg) in self.all_results():
            if status in ['ERROR', 'SKIPPED']:
                continue
            (t_ind, t_len), (s_ind, s_len) = gr_map[test.name]
            if t_ind is not None:
                values[t_ind:t_ind + t_len] = test.times
            if s_ind is not None:
                values[s_ind:s_ind + s_len] = test.sizes

        log.debug("Writing globalres log to %s", filename)
        with open(filename, 'a') as fobj:
            fobj.write('{},{}:{},{},'.format(self.hostname,
                                             self.git_branch,
                                             self.git_commit,
                                             git_tag_rev))
            fobj.write(','.join(values) + '\n')

    def write_results_json(self):
        """Write test results into a json-formatted file"""
        results = {'tester_host': self.hostname,
                   'git_branch': self.git_branch,
                   'git_commit': self.git_commit,
                   'git_commit_count': self.git_commit_count,
                   'product': self.product,
                   'start_time': self.start_time,
                   'elapsed_time': self.elapsed_time}

        tests = {}
        for status, (test, reason) in self.all_results():
            tests[test.name] = {'name': test.name,
                                'description': test.shortDescription(),
                                'status': status,
                                'start_time': test.start_time,
                                'elapsed_time': test.elapsed_time,
                                'cmd_log_file': os.path.relpath(test.cmd_log_file,
                                                                self.out_dir),
                                'measurements': test.measurements}
        results['tests'] = tests

        with open(os.path.join(self.out_dir, 'results.json'), 'w') as fobj:
            json.dump(results, fobj, indent=4, sort_keys=True,
                      cls=ResultsJsonEncoder)


    def git_commit_results(self, repo_path, branch=None, tag=None):
        """Commit results into a Git repository"""
        repo = GitRepo(repo_path, is_topdir=True)
        if not branch:
            branch = self.git_branch
        else:
            # Replace keywords
            branch = branch.format(git_branch=self.git_branch,
                                   tester_host=self.hostname)

        log.info("Committing test results into %s %s", repo_path, branch)
        tmp_index = os.path.join(repo_path, '.git', 'index.oe-build-perf')
        try:
            # Create new commit object from the new results
            env_update = {'GIT_INDEX_FILE': tmp_index,
                          'GIT_WORK_TREE': self.out_dir}
            repo.run_cmd('add .', env_update)
            tree = repo.run_cmd('write-tree', env_update)
            parent = repo.rev_parse(branch)
            msg = "Results of {}:{}\n".format(self.git_branch, self.git_commit)
            git_cmd = ['commit-tree', tree, '-m', msg]
            if parent:
                git_cmd += ['-p', parent]
            commit = repo.run_cmd(git_cmd, env_update)

            # Update branch head
            git_cmd = ['update-ref', 'refs/heads/' + branch, commit]
            if parent:
                git_cmd.append(parent)
            repo.run_cmd(git_cmd)

            # Update current HEAD, if we're on branch 'branch'
            if repo.get_current_branch() == branch:
                log.info("Updating %s HEAD to latest commit", repo_path)
                repo.run_cmd('reset --hard')

            # Create (annotated) tag
            if tag:
                # Find tags matching the pattern
                tag_keywords = dict(git_branch=self.git_branch,
                                    git_commit=self.git_commit,
                                    git_commit_count=self.git_commit_count,
                                    tester_host=self.hostname,
                                    tag_num='[0-9]{1,5}')
                tag_re = re.compile(tag.format(**tag_keywords) + '$')
                tag_keywords['tag_num'] = 0
                for existing_tag in repo.run_cmd('tag').splitlines():
                    if tag_re.match(existing_tag):
                        tag_keywords['tag_num'] += 1

                tag = tag.format(**tag_keywords)
                msg = "Test run #{} of {}:{}\n".format(tag_keywords['tag_num'],
                                                       self.git_branch,
                                                       self.git_commit)
                repo.run_cmd(['tag', '-a', '-m', msg, tag, commit])

        finally:
            if os.path.exists(tmp_index):
                os.unlink(tmp_index)


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
        self.measurements = []
        self.bb_vars = get_bb_vars()
        # TODO: remove 'times' and 'sizes' arrays when globalres support is
        # removed
        self.times = []
        self.sizes = []

    @property
    def out_dir(self):
        return os.path.join(self.base_dir, self.name)

    @property
    def cmd_log_file(self):
        return os.path.join(self.out_dir, 'commands.log')

    def setUp(self):
        """Set-up fixture for each test"""
        if self.build_target:
            self.log_cmd_output(['bitbake', self.build_target,
                                 '-c', 'fetchall'])

    def run(self, *args, **kwargs):
        """Run test"""
        self.start_time = datetime.now()
        super(BuildPerfTestCase, self).run(*args, **kwargs)
        self.elapsed_time = datetime.now() - self.start_time

    def log_cmd_output(self, cmd):
        """Run a command and log it's output"""
        cmd_str = cmd if isinstance(cmd, str) else ' '.join(cmd)
        log.info("Logging command: %s", cmd_str)
        try:
            with open(self.cmd_log_file, 'a') as fobj:
                runCmd2(cmd, stdout=fobj)
        except CommandError as err:
            log.error("Command failed: %s", err.retcode)
            raise

    def measure_cmd_resources(self, cmd, name, legend, save_bs=False):
        """Measure system resource usage of a command"""
        def _worker(data_q, cmd, **kwargs):
            """Worker process for measuring resources"""
            try:
                start_time = datetime.now()
                ret = runCmd2(cmd, **kwargs)
                etime = datetime.now() - start_time
                rusage_struct = resource.getrusage(resource.RUSAGE_CHILDREN)
                iostat = {}
                with open('/proc/{}/io'.format(os.getpid())) as fobj:
                    for line in fobj.readlines():
                        key, val = line.split(':')
                        iostat[key] = int(val)
                rusage = {}
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
            with open(self.cmd_log_file, 'a') as fobj:
                proc = Process(target=_worker, args=(data_q, cmd,),
                               kwargs={'stdout': fobj})
                proc.start()
                data = data_q.get()
                proc.join()
            if isinstance(data, Exception):
                raise data
        except CommandError:
            log.error("Command '%s' failed, see %s for more details", cmd_str,
                      self.cmd_log_file)
            raise
        etime = data['elapsed_time']

        measurement = {'type': self.SYSRES,
                       'name': name,
                       'legend': legend}
        measurement['values'] = {'start_time': data['start_time'],
                                 'elapsed_time': etime,
                                 'rusage': data['rusage'],
                                 'iostat': data['iostat']}
        if save_bs:
            bs_file = self.save_buildstats(legend)
            measurement['values']['buildstats_file'] = \
                    os.path.relpath(bs_file, self.base_dir)

        self.measurements.append(measurement)

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
        measurement = {'type': self.DISKUSAGE,
                       'name': name,
                       'legend': legend}
        measurement['values'] = {'size': size}
        self.measurements.append(measurement)
        # Append to 'sizes' array for globalres log
        self.sizes.append(str(size))

    def save_buildstats(self, label=None):
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
            bs_json = {'iostat': {},
                       'rusage': {},
                       'child_rusage': {}}
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
                        bs_json['iostat'][split[1]] = int(val)
                    elif key.find('rusage') >= 0:
                        split = key.split()
                        ru_key = split[-1]
                        if ru_key in ('ru_stime', 'ru_utime'):
                            val = float(val)
                        else:
                            val = int(val)
                        ru_type = 'rusage' if split[0] == 'rusage' else \
                                                          'child_rusage'
                        bs_json[ru_type][ru_key] = val
                    elif key == 'Status':
                        bs_json['status'] = val
            bs_json['elapsed_time'] = end_time - start_time
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
            if not os.path.isdir(recipe_dir):
                continue
            name, epoch, version, revision = split_nevr(fname)
            recipe_bs = {'name': name,
                         'epoch': epoch,
                         'version': version,
                         'revision': revision,
                         'tasks': {}}
            for task in os.listdir(recipe_dir):
                recipe_bs['tasks'][task] = bs_to_json(os.path.join(recipe_dir,
                                                                   task))
            buildstats.append(recipe_bs)

        # Write buildstats into json file
        postfix = '.' + str_to_fn(label) if label else ''
        postfix += '.json'
        outfile = os.path.join(self.out_dir, 'buildstats' + postfix)
        with open(outfile, 'w') as fobj:
            json.dump(buildstats, fobj, indent=4, sort_keys=True,
                      cls=ResultsJsonEncoder)
        return outfile

    def rm_tmp(self):
        """Cleanup temporary/intermediate files and directories"""
        log.debug("Removing temporary and cache files")
        for name in ['bitbake.lock', 'conf/sanity_info',
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
