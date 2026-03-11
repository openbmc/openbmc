#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import sys
basepath = os.path.abspath(os.path.dirname(__file__) + '/../../../../../')
lib_path = basepath + '/scripts/lib'
sys.path = sys.path + [lib_path]
import oeqa.utils.gitarchive as ga
from oeqa.utils.git import GitError
import tempfile
import shutil
import scriptutils
import logging
from oeqa.selftest.case import OESelftestTestCase

logger = scriptutils.logger_create('resulttool')

def create_fake_repository(commit, tag_list=[], add_remote=True):
    """ Create a testing git directory

    Initialize a simple git repository with one initial commit, and as many
    tags on this commit as listed in tag_list
    Returns both git directory path and gitarchive git object
    If commit is true, fake data will be commited, otherwise it will stay in staging area
    If commit is true and tag_lsit is non empty, all tags in tag_list will be
    created on the initial commit
    Fake remote will also be added to make git ls-remote work
    """
    fake_data_file = "fake_data.txt"
    tempdir = tempfile.mkdtemp(prefix='fake_results.')
    repo = ga.init_git_repo(tempdir, False, False, logger)
    if add_remote:
        repo.run_cmd(["remote", "add", "origin", "."])
    with open(os.path.join(tempdir, fake_data_file), "w") as fake_data:
        fake_data.write("Fake data")
    if commit:
        repo.run_cmd(["add", fake_data_file])
        repo.run_cmd(["commit", "-m", "\"Add fake data\""])
        for tag in tag_list:
            repo.run_cmd(["tag", tag])
    
    return tempdir, repo

def delete_fake_repository(path):
    shutil.rmtree(path)

def tag_exists(git_obj, target_tag):
    for tag in git_obj.run_cmd(["tag"]).splitlines():
        if target_tag == tag:
            return True
    return False

class GitArchiveTests(OESelftestTestCase):
    TEST_BRANCH="main"
    TEST_COMMIT="0f7d5df"
    TEST_COMMIT_COUNT="42"

    @classmethod
    def setUpClass(cls):
        super().setUpClass()
        cls.log = logging.getLogger('gitarchivetests')
        cls.log.setLevel(logging.DEBUG)

    def test_create_first_test_tag(self):
        path, git_obj = create_fake_repository(False)
        keywords = {'commit': self.TEST_COMMIT, 'branch': self.TEST_BRANCH, "commit_count": self.TEST_COMMIT_COUNT}
        target_tag = f"{self.TEST_BRANCH}/{self.TEST_COMMIT_COUNT}-g{self.TEST_COMMIT}/0"

        ga.gitarchive(path, path, True, False,
                              "Results of {branch}:{commit}", "branch: {branch}\ncommit: {commit}", "{branch}",
                              False, "{branch}/{commit_count}-g{commit}/{tag_number}",
                              'Test run #{tag_number} of {branch}:{commit}', '',
                              [], [], False, keywords, logger)
        self.assertTrue(tag_exists(git_obj, target_tag), msg=f"Tag {target_tag} has not been created")
        delete_fake_repository(path)

    def test_create_second_test_tag(self):
        first_tag = f"{self.TEST_BRANCH}/{self.TEST_COMMIT_COUNT}-g{self.TEST_COMMIT}/0"
        second_tag = f"{self.TEST_BRANCH}/{self.TEST_COMMIT_COUNT}-g{self.TEST_COMMIT}/1"
        keywords = {'commit': self.TEST_COMMIT, 'branch': self.TEST_BRANCH, "commit_count": self.TEST_COMMIT_COUNT}

        path, git_obj = create_fake_repository(True, [first_tag])
        ga.gitarchive(path, path, True, False,
                              "Results of {branch}:{commit}", "branch: {branch}\ncommit: {commit}", "{branch}",
                              False, "{branch}/{commit_count}-g{commit}/{tag_number}",
                              'Test run #{tag_number} of {branch}:{commit}', '',
                              [], [], False, keywords, logger)
        self.assertTrue(tag_exists(git_obj, second_tag), msg=f"Second tag {second_tag} has not been created")
        delete_fake_repository(path)

    def test_get_revs_on_branch(self):
        fake_tags_list=["main/10-g0f7d5df/0", "main/10-g0f7d5df/1", "foo/20-g2468f5d/0"]
        tag_name = "{branch}/{commit_number}-g{commit}/{tag_number}"

        path, git_obj = create_fake_repository(True, fake_tags_list)
        revs = ga.get_test_revs(logger, git_obj, tag_name, branch="main")
        self.assertEqual(len(revs), 1)
        self.assertEqual(revs[0].commit, "0f7d5df")
        self.assertEqual(len(revs[0].tags), 2)
        self.assertEqual(revs[0].tags, ['main/10-g0f7d5df/0', 'main/10-g0f7d5df/1'])
        delete_fake_repository(path)

    def test_get_tags_without_valid_remote(self):
        url = 'git://git.yoctoproject.org/poky'
        path, git_obj = create_fake_repository(False, None, False)

        tags = ga.get_tags(git_obj, self.log, pattern="yocto-*", url=url)
        """Test for some well established tags (released tags)"""
        self.assertIn("yocto-4.0", tags)
        self.assertIn("yocto-4.1", tags)
        self.assertIn("yocto-4.2", tags)
        delete_fake_repository(path)

    def test_get_tags_with_only_local_tag(self):
        fake_tags_list=["main/10-g0f7d5df/0", "main/10-g0f7d5df/1", "foo/20-g2468f5d/0"]
        path, git_obj = create_fake_repository(True, fake_tags_list, False)

        """No remote is configured and no url is passed: get_tags must fall
        back to local tags
        """
        tags = ga.get_tags(git_obj, self.log)
        self.assertCountEqual(tags, fake_tags_list)
        delete_fake_repository(path)

    def test_get_tags_without_valid_remote_and_wrong_url(self):
        url = 'git://git.foo.org/bar'
        path, git_obj = create_fake_repository(False, None, False)

        """Test for some well established tags (released tags)"""
        with self.assertRaises(GitError):
            tags = ga.get_tags(git_obj, self.log, pattern="yocto-*", url=url)
        delete_fake_repository(path)
