#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import sys
from oeqa.selftest.case import OESelftestTestCase
import tempfile
import operator
from oeqa.utils.commands import get_bb_var

class TestBlobParsing(OESelftestTestCase):

    def setUp(self):
        self.repo_path = tempfile.mkdtemp(prefix='selftest-buildhistory',
            dir=get_bb_var('TOPDIR'))

        try:
            from git import Repo
            self.repo = Repo.init(self.repo_path)
        except ImportError as e:
            self.skipTest('Python module GitPython is not present (%s)  (%s)' % (e, sys.path))

        self.test_file = "test"
        self.var_map = {}

    def tearDown(self):
        import shutil
        shutil.rmtree(self.repo_path)

    @property
    def heads_default(self):
        """
        Support repos defaulting to master or to main branch
        """
        try:
            return self.repo.heads.main
        except AttributeError:
            return self.repo.heads.master

    def commit_vars(self, to_add={}, to_remove = [], msg="A commit message"):
        if len(to_add) == 0 and len(to_remove) == 0:
            return

        for k in to_remove:
            self.var_map.pop(x,None)
        for k in to_add:
            self.var_map[k] = to_add[k]

        with open(os.path.join(self.repo_path, self.test_file), 'w') as repo_file:
            for k in self.var_map:
                repo_file.write("%s = %s\n" % (k, self.var_map[k]))

        self.repo.git.add("--all")
        self.repo.git.commit(message=msg)

    def test_blob_to_dict(self):
        """
        Test conversion of git blobs to dictionary
        """
        from oe.buildhistory_analysis import blob_to_dict
        valuesmap = { "foo" : "1", "bar" : "2" }
        self.commit_vars(to_add = valuesmap)

        blob = self.repo.head.commit.tree.blobs[0]
        self.assertEqual(valuesmap, blob_to_dict(blob),
            "commit was not translated correctly to dictionary")

    def test_compare_dict_blobs(self):
        """
        Test comparisson of dictionaries extracted from git blobs
        """
        from oe.buildhistory_analysis import compare_dict_blobs

        changesmap = { "foo-2" : ("2", "8"), "bar" : ("","4"), "bar-2" : ("","5")}

        self.commit_vars(to_add = { "foo" : "1", "foo-2" : "2", "foo-3" : "3" })
        blob1 = self.heads_default.commit.tree.blobs[0]

        self.commit_vars(to_add = { "foo-2" : "8", "bar" : "4", "bar-2" : "5" })
        blob2 = self.heads_default.commit.tree.blobs[0]

        change_records = compare_dict_blobs(os.path.join(self.repo_path, self.test_file),
            blob1, blob2, False, False)

        var_changes = { x.fieldname : (x.oldvalue, x.newvalue) for x in change_records}
        self.assertEqual(changesmap, var_changes, "Changes not reported correctly")

    def test_compare_dict_blobs_default(self):
        """
        Test default values for comparisson of git blob dictionaries
        """
        from oe.buildhistory_analysis import compare_dict_blobs
        defaultmap = { x : ("default", "1")  for x in ["PKG", "PKGE", "PKGV", "PKGR"]}

        self.commit_vars(to_add = { "foo" : "1" })
        blob1 = self.heads_default.commit.tree.blobs[0]

        self.commit_vars(to_add = { "PKG" : "1", "PKGE" : "1", "PKGV" : "1", "PKGR" : "1" })
        blob2 = self.heads_default.commit.tree.blobs[0]

        change_records = compare_dict_blobs(os.path.join(self.repo_path, self.test_file),
            blob1, blob2, False, False)

        var_changes = {}
        for x in change_records:
            oldvalue = "default" if ("default" in x.oldvalue) else x.oldvalue
            var_changes[x.fieldname] = (oldvalue, x.newvalue)

        self.assertEqual(defaultmap, var_changes, "Defaults not set properly")

class TestFileListCompare(OESelftestTestCase):

    def test_compare_file_lists(self):
        # Test that a directory tree that moves location such as /lib/modules/5.4.40-yocto-standard -> /lib/modules/5.4.43-yocto-standard
        # is correctly identified as a move
        from oe.buildhistory_analysis import compare_file_lists, FileChange

        with open(self.tc.files_dir + "/buildhistory_filelist1.txt", "r") as f:
            filelist1 = f.readlines()
        with open(self.tc.files_dir + "/buildhistory_filelist2.txt", "r") as f:
            filelist2 = f.readlines()

        expectedResult = [
            '/lib/libcap.so.2 changed symlink target from libcap.so.2.33 to libcap.so.2.34',
            '/lib/libcap.so.2.33 moved to /lib/libcap.so.2.34',
            '/lib/modules/5.4.40-yocto-standard moved to /lib/modules/5.4.43-yocto-standard',
            '/lib/modules/5.4.43-yocto-standard/modules.builtin.alias.bin was added',
            '/usr/bin/gawk-5.0.1 moved to /usr/bin/gawk-5.1.0',
            '/usr/lib/libbtrfsutil.so changed symlink target from libbtrfsutil.so.1.1.1 to libbtrfsutil.so.1.2.0',
            '/usr/lib/libbtrfsutil.so.1 changed symlink target from libbtrfsutil.so.1.1.1 to libbtrfsutil.so.1.2.0',
            '/usr/lib/libbtrfsutil.so.1.1.1 moved to /usr/lib/libbtrfsutil.so.1.2.0',
            '/usr/lib/libkmod.so changed symlink target from libkmod.so.2.3.4 to libkmod.so.2.3.5',
            '/usr/lib/libkmod.so.2 changed symlink target from libkmod.so.2.3.4 to libkmod.so.2.3.5',
            '/usr/lib/libkmod.so.2.3.4 moved to /usr/lib/libkmod.so.2.3.5',
            '/usr/lib/libpixman-1.so.0 changed symlink target from libpixman-1.so.0.38.4 to libpixman-1.so.0.40.0',
            '/usr/lib/libpixman-1.so.0.38.4 moved to /usr/lib/libpixman-1.so.0.40.0',
            '/usr/lib/opkg/alternatives/rtcwake was added',
            '/usr/lib/python3.8/site-packages/PyGObject-3.34.0.egg-info moved to /usr/lib/python3.8/site-packages/PyGObject-3.36.1.egg-info',
            '/usr/lib/python3.8/site-packages/btrfsutil-1.1.1-py3.8.egg-info moved to /usr/lib/python3.8/site-packages/btrfsutil-1.2.0-py3.8.egg-info',
            '/usr/lib/python3.8/site-packages/pycairo-1.19.0.egg-info moved to /usr/lib/python3.8/site-packages/pycairo-1.19.1.egg-info',
            '/usr/sbin/rtcwake changed type from file to symlink',
            '/usr/sbin/rtcwake changed permissions from rwxr-xr-x to rwxrwxrwx',
            '/usr/sbin/rtcwake changed symlink target from None to /usr/sbin/rtcwake.util-linux',
            '/usr/sbin/rtcwake.util-linux was added'
        ]

        result = compare_file_lists(filelist1, filelist2)
        rendered = []
        for entry in sorted(result, key=operator.attrgetter("path")):
            rendered.append(str(entry))

        self.maxDiff = None
        self.assertCountEqual(rendered, expectedResult)

