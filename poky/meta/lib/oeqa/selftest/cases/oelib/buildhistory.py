#
# SPDX-License-Identifier: MIT
#

import os
from oeqa.selftest.case import OESelftestTestCase
import tempfile
from oeqa.utils.commands import get_bb_var

class TestBlobParsing(OESelftestTestCase):

    def setUp(self):
        import time
        self.repo_path = tempfile.mkdtemp(prefix='selftest-buildhistory',
            dir=get_bb_var('TOPDIR'))

        try:
            from git import Repo
            self.repo = Repo.init(self.repo_path)
        except ImportError:
            self.skipTest('Python module GitPython is not present')

        self.test_file = "test"
        self.var_map = {}

    def tearDown(self):
        import shutil
        shutil.rmtree(self.repo_path)

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
        blob1 = self.repo.heads.master.commit.tree.blobs[0]

        self.commit_vars(to_add = { "foo-2" : "8", "bar" : "4", "bar-2" : "5" })
        blob2 = self.repo.heads.master.commit.tree.blobs[0]

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
        blob1 = self.repo.heads.master.commit.tree.blobs[0]

        self.commit_vars(to_add = { "PKG" : "1", "PKGE" : "1", "PKGV" : "1", "PKGR" : "1" })
        blob2 = self.repo.heads.master.commit.tree.blobs[0]

        change_records = compare_dict_blobs(os.path.join(self.repo_path, self.test_file),
            blob1, blob2, False, False)

        var_changes = {}
        for x in change_records:
            oldvalue = "default" if ("default" in x.oldvalue) else x.oldvalue
            var_changes[x.fieldname] = (oldvalue, x.newvalue)

        self.assertEqual(defaultmap, var_changes, "Defaults not set properly")
