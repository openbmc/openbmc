# Base class to be used by all test cases defined in the suite
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only

import unittest
import logging
import json
import unidiff
from data import PatchTestInput
import mailbox
import collections
import sys
import os
import re

sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'pyparsing'))

logger = logging.getLogger('patchtest')
debug=logger.debug
info=logger.info
warn=logger.warn
error=logger.error

Commit = collections.namedtuple('Commit', ['author', 'subject', 'commit_message', 'shortlog', 'payload'])

class PatchtestOEError(Exception):
    """Exception for handling patchtest-oe errors"""
    def __init__(self, message, exitcode=1):
        super().__init__(message)
        self.exitcode = exitcode

class Base(unittest.TestCase):
    # if unit test fails, fail message will throw at least the following JSON: {"id": <testid>}

    endcommit_messages_regex = re.compile(r'\(From \w+-\w+ rev:|(?<!\S)Signed-off-by|(?<!\S)---\n')
    patchmetadata_regex   = re.compile(r'-{3} \S+|\+{3} \S+|@{2} -\d+,\d+ \+\d+,\d+ @{2} \S+')


    @staticmethod
    def msg_to_commit(msg):
        payload = msg.get_payload()
        return Commit(subject=msg['subject'].replace('\n', ' ').replace('  ', ' '),
                      author=msg.get('From'),
                      shortlog=Base.shortlog(msg['subject']),
                      commit_message=Base.commit_message(payload),
                      payload=payload)

    @staticmethod
    def commit_message(payload):
        commit_message = payload.__str__()
        match = Base.endcommit_messages_regex.search(payload)
        if match:
            commit_message = payload[:match.start()]
        return commit_message

    @staticmethod
    def shortlog(shlog):
        # remove possible prefix (between brackets) before colon
        start = shlog.find(']', 0, shlog.find(':'))
        # remove also newlines and spaces at both sides
        return shlog[start + 1:].replace('\n', '').strip()

    @classmethod
    def setUpClass(cls):

        # General objects: mailbox.mbox and patchset
        cls.mbox = mailbox.mbox(PatchTestInput.repo.patch)

        # Patch may be malformed, so try parsing it
        cls.unidiff_parse_error = ''
        cls.patchset = None
        try:
            cls.patchset = unidiff.PatchSet.from_filename(PatchTestInput.repo.patch, encoding=u'UTF-8')
        except unidiff.UnidiffParseError as upe:
            cls.patchset = []
            cls.unidiff_parse_error = str(upe)

        # Easy to iterate list of commits
        cls.commits = []
        for msg in cls.mbox:
            if msg['subject'] and msg.get_payload():
                cls.commits.append(Base.msg_to_commit(msg))

        cls.setUpClassLocal()

    @classmethod
    def tearDownClass(cls):
        cls.tearDownClassLocal()

    @classmethod
    def setUpClassLocal(cls):
        pass

    @classmethod
    def tearDownClassLocal(cls):
        pass

    def fail(self, issue, fix=None, commit=None, data=None):
        """ Convert to a JSON string failure data"""
        value = {'id': self.id(),
                 'issue': issue}

        if fix:
            value['fix'] = fix
        if commit:
            value['commit'] = {'subject': commit.subject,
                               'shortlog': commit.shortlog}

        # extend return value with other useful info
        if data:
            value['data'] = data

        return super(Base, self).fail(json.dumps(value))

    def skip(self, issue, data=None):
        """ Convert the skip string to JSON"""
        value = {'id': self.id(),
                 'issue': issue}

        # extend return value with other useful info
        if data:
            value['data'] = data

        return super(Base, self).skipTest(json.dumps(value))

    def shortid(self):
        return self.id().split('.')[-1]

    def __str__(self):
        return json.dumps({'id': self.id()})

class Metadata(Base):
    @classmethod
    def setUpClassLocal(cls):
        cls.tinfoil = cls.setup_tinfoil()

        # get info about added/modified/remove recipes
        cls.added, cls.modified, cls.removed = cls.get_metadata_stats(cls.patchset)

    @classmethod
    def tearDownClassLocal(cls):
        cls.tinfoil.shutdown()

    @classmethod
    def setup_tinfoil(cls, config_only=False):
        """Initialize tinfoil api from bitbake"""

        # import relevant libraries
        try:
            scripts_path = os.path.join(PatchTestInput.repodir, 'scripts', 'lib')
            if scripts_path not in sys.path:
                sys.path.insert(0, scripts_path)
            import scriptpath
            scriptpath.add_bitbake_lib_path()
            import bb.tinfoil
        except ImportError:
            raise PatchtestOEError('Could not import tinfoil module')

        orig_cwd = os.path.abspath(os.curdir)

        # Load tinfoil
        tinfoil = None
        try:
            builddir = os.environ.get('BUILDDIR')
            if not builddir:
                logger.warn('Bitbake environment not loaded?')
                return tinfoil
            os.chdir(builddir)
            tinfoil = bb.tinfoil.Tinfoil()
            tinfoil.prepare(config_only=config_only)
        except bb.tinfoil.TinfoilUIException as te:
            if tinfoil:
                tinfoil.shutdown()
            raise PatchtestOEError('Could not prepare properly tinfoil (TinfoilUIException)')
        except Exception as e:
            if tinfoil:
                tinfoil.shutdown()
            raise e
        finally:
            os.chdir(orig_cwd)

        return tinfoil

    @classmethod
    def get_metadata_stats(cls, patchset):
        """Get lists of added, modified and removed metadata files"""

        def find_pn(data, path):
            """Find the PN from data"""
            pn = None
            pn_native = None
            for _path, _pn in data:
                if path in _path:
                    if 'native' in _pn:
                        # store the native PN but look for the non-native one first
                        pn_native = _pn
                    else:
                        pn = _pn
                        break
            else:
                # sent the native PN if found previously
                if pn_native:
                    return pn_native

                # on renames (usually upgrades), we need to check (FILE) base names
                # because the unidiff library does not provided the new filename, just the modified one
                # and tinfoil datastore, once the patch is merged, will contain the new filename
                path_basename = path.split('_')[0]
                for _path, _pn in data:
                    _path_basename = _path.split('_')[0]
                    if path_basename == _path_basename:
                        pn = _pn
            return pn

        if not cls.tinfoil:
            cls.tinfoil = cls.setup_tinfoil()

        added_paths, modified_paths, removed_paths = [], [], []
        added, modified, removed = [], [], []

        # get metadata filename additions, modification and removals
        for patch in patchset:
            if patch.path.endswith('.bb') or patch.path.endswith('.bbappend') or patch.path.endswith('.inc'):
                if patch.is_added_file:
                    added_paths.append(os.path.join(os.path.abspath(PatchTestInput.repodir), patch.path))
                elif patch.is_modified_file:
                    modified_paths.append(os.path.join(os.path.abspath(PatchTestInput.repodir), patch.path))
                elif patch.is_removed_file:
                    removed_paths.append(os.path.join(os.path.abspath(PatchTestInput.repodir), patch.path))

        data = cls.tinfoil.cooker.recipecaches[''].pkg_fn.items()

        added = [find_pn(data,path) for path in added_paths]
        modified = [find_pn(data,path) for path in modified_paths]
        removed = [find_pn(data,path) for path in removed_paths]

        return [a for a in added if a], [m for m in modified if m], [r for r in removed if r]
