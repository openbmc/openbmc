#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import subprocess
import sys
import tempfile
from unittest.case import TestCase

import oe.patch


class PatchTestDataStore:
    def __init__(self, workdir):
        self.vars = {
            "PATCH_GIT_USER_NAME": "OE Test",
            "PATCH_GIT_USER_EMAIL": "oe-test@example.com",
            "WORKDIR": workdir,
        }

    def getVar(self, name):
        return self.vars.get(name, "")


class TestRunCmd(TestCase):
    def test_runcmd_preserves_argv_elements(self):
        output = oe.patch.runcmd([
            sys.executable,
            "-c",
            "import sys; print(sys.argv[1])",
            "value with spaces;$(false)*",
        ])
        self.assertEqual(output, "value with spaces;$(false)*\n")

    def test_runcmd_allows_explicit_shell(self):
        output = oe.patch.runcmd([
            "sh", "-c", 'printf "%s" "$1"', "sh", "shell value",
        ])
        self.assertEqual(output, "shell value")

    def test_runcmd_reports_exit_status(self):
        with self.assertRaises(oe.patch.CmdError) as ctx:
            oe.patch.runcmd([
                sys.executable,
                "-c",
                "raise SystemExit(7)",
            ])

        self.assertEqual(ctx.exception.status, 7)

    def test_runcmd_wraps_exec_failure(self):
        with self.assertRaises(oe.patch.CmdError) as ctx:
            oe.patch.runcmd([
                "/definitely-not-an-existing-executable",
            ])

        self.assertEqual(ctx.exception.status, 127)


class TestPatchTree(TestCase):
    def test_push_run_false_returns_argv(self):
        with tempfile.TemporaryDirectory(prefix="oe-patchtree-run-false-") as tmpdir:
            srcdir = os.path.join(tmpdir, "source")
            os.mkdir(srcdir)
            with open(os.path.join(srcdir, "file.txt"), "w") as f:
                f.write("base\n")

            patch = os.path.join(tmpdir, "patch with spaces.patch")
            with open(patch, "w") as f:
                f.write(
                    "--- a/file.txt\n"
                    "+++ b/file.txt\n"
                    "@@ -1 +1 @@\n"
                    "-base\n"
                    "+changed\n"
                )

            tree = oe.patch.PatchTree(srcdir, PatchTestDataStore(tmpdir))
            tree.Import({"file": patch, "strippath": "1"}, False)

            cmd = tree.Push(False, run=False)

            self.assertEqual(cmd[:5], [
                "patch", "--no-backup-if-mismatch", "-p", "1", "-i",
            ])
            self.assertEqual(cmd[-1], patch)
            self.assertIsNone(tree.current())
            with open(os.path.join(srcdir, "file.txt")) as f:
                self.assertEqual(f.read(), "base\n")

class RecordingGitApplyTree(oe.patch.GitApplyTree):
    def __init__(self, *args, **kwargs):
        self.commitpatch_called = False
        super().__init__(*args, **kwargs)

    def _commitpatch(self, patch, *args):
        self.commitpatch_called = True
        return super()._commitpatch(patch, *args)


class TestGitApplyTree(TestCase):
    def setUp(self):
        if shutil.which("git") is None:
            self.skipTest("git not found")
        if shutil.which("patch") is None:
            self.skipTest("patch not found")

    def git(self, cwd, *args):
        subprocess.check_call(
            ["git"] + list(args),
            cwd=cwd,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        )

    def make_repo(self, tmpdir, name, text="base\n"):
        repo = os.path.join(tmpdir, name)
        os.mkdir(repo)
        self.git(repo, "init")
        self.git(repo, "config", "user.name", "OE Test")
        self.git(repo, "config", "user.email", "oe-test@example.com")
        with open(os.path.join(repo, "file.txt"), "w") as f:
            f.write(text)
        self.git(repo, "add", "file.txt")
        self.git(repo, "commit", "-m", "base")
        return repo

    def make_git_am_patch(self, tmpdir, basename):
        repo = self.make_repo(tmpdir, "source")
        with open(os.path.join(repo, "file.txt"), "w") as f:
            f.write("git am change\n")
        self.git(repo, "commit", "-am", "git am change")
        patchdir = os.path.join(tmpdir, "patches")
        os.mkdir(patchdir)
        subprocess.check_call(
            ["git", "format-patch", "-1", "HEAD", "-o", patchdir],
            cwd=repo,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        )
        patch = os.path.join(patchdir, os.listdir(patchdir)[0])
        renamed = os.path.join(patchdir, basename)
        os.rename(patch, renamed)
        return renamed

    def make_plain_diff_patch(self, tmpdir, basename):
        patch = os.path.join(tmpdir, basename)
        with open(patch, "w") as f:
            f.write(
                "Author: Fallback Author <fallback.author@example.com>\n"
                "Date: Fri, 01 Jan 2021 12:34:56 +0000\n"
                "Subject: [PATCH] plain diff change\n"
                "\n"
                "--- a/file.txt\n"
                "+++ b/file.txt\n"
                "@@ -1 +1 @@\n"
                "-base\n"
                "+plain diff change\n"
            )
        return patch

    def apply_patch(self, tree, patch):
        tree._need_dirty_check = lambda: False
        tree.Import({"file": patch, "strippath": "1"}, False)
        tree.Push(False)

    def assert_note_and_extract(self, repo, patchname, expected):
        note = oe.patch.runcmd(
            ["git", "notes", "--ref", oe.patch.GitApplyTree.notes_ref,
             "show", "HEAD"],
            repo,
        )
        self.assertIn("%s: %s" % (oe.patch.GitApplyTree.original_patch,
                                  patchname), note)

        with tempfile.TemporaryDirectory(prefix="oe-patch-extract-") as outdir:
            patches = oe.patch.GitApplyTree.extractPatches(
                repo, {"": "HEAD~1"}, outdir
            )
            self.assertEqual([os.path.basename(p) for p in patches], [patchname])
            with open(patches[0]) as f:
                self.assertIn(expected, f.read())

    def assert_no_note(self, repo):
        with self.assertRaises(oe.patch.CmdError):
            oe.patch.runcmd(
                ["git", "notes", "--ref", oe.patch.GitApplyTree.notes_ref,
                 "show", "HEAD"],
                repo,
            )

    def test_git_am_preserves_original_patch_name(self):
        with tempfile.TemporaryDirectory(prefix="oe-gitapply-am-") as tmpdir:
            patchname = "0001-distinct-original-name.patch"
            patch = self.make_git_am_patch(tmpdir, patchname)
            repo = self.make_repo(tmpdir, "target")
            tree = RecordingGitApplyTree(repo, PatchTestDataStore(tmpdir))

            self.apply_patch(tree, patch)

            self.assertFalse(tree.commitpatch_called)
            with open(os.path.join(repo, "file.txt")) as f:
                self.assertEqual(f.read(), "git am change\n")
            self.assert_note_and_extract(repo, patchname, "+git am change")

    def test_push_run_false_returns_argv(self):
        with tempfile.TemporaryDirectory(prefix="oe-gitapply-run-false-") as tmpdir:
            patchname = "0001-distinct original name.patch"
            patch = self.make_git_am_patch(tmpdir, patchname)
            repo = self.make_repo(tmpdir, "target")
            tree = RecordingGitApplyTree(repo, PatchTestDataStore(tmpdir))
            tree._need_dirty_check = lambda: False
            tree.Import({"file": patch, "strippath": "1"}, False)

            cmd = tree.Push(False, run=False)

            self.assertIsInstance(cmd, list)
            self.assertEqual(cmd[0], "git")
            self.assertIn("am", cmd)
            self.assertEqual(cmd[-1], patch)
            self.assertFalse(tree.commitpatch_called)
            self.assertIsNone(tree.current())
            self.assert_no_note(repo)
            with open(os.path.join(repo, "file.txt")) as f:
                self.assertEqual(f.read(), "base\n")

    def test_dirty_push_run_false_returns_argv(self):
        with tempfile.TemporaryDirectory(prefix="oe-gitapply-run-false-") as tmpdir:
            patchname = "plain-diff original name.patch"
            patch = self.make_plain_diff_patch(tmpdir, patchname)
            repo = self.make_repo(tmpdir, "target")
            with open(os.path.join(repo, "file.txt"), "a") as f:
                f.write("dirty\n")

            tree = RecordingGitApplyTree(repo, PatchTestDataStore(tmpdir))
            tree._need_dirty_check = lambda: True
            tree.Import({"file": patch, "strippath": "1"}, False)

            cmd = tree.Push(False, run=False)

            self.assertEqual(cmd[:5], [
                "patch", "--no-backup-if-mismatch", "-p", "1", "-i",
            ])
            self.assertEqual(cmd[-1], patch)
            self.assertFalse(tree.commitpatch_called)
            self.assertIsNone(tree.current())
            self.assert_no_note(repo)
            with open(os.path.join(repo, "file.txt")) as f:
                self.assertEqual(f.read(), "base\ndirty\n")

    def test_fallback_preserves_original_patch_name(self):
        with tempfile.TemporaryDirectory(prefix="oe-gitapply-fallback-") as tmpdir:
            patchname = "plain-diff-original-name.patch"
            patch = self.make_plain_diff_patch(tmpdir, patchname)
            repo = self.make_repo(tmpdir, "target")
            tree = RecordingGitApplyTree(repo, PatchTestDataStore(tmpdir))

            self.apply_patch(tree, patch)

            self.assertTrue(tree.commitpatch_called)
            with open(os.path.join(repo, "file.txt")) as f:
                self.assertEqual(f.read(), "plain diff change\n")
            metadata = oe.patch.runcmd([
                "git", "show", "-s",
                "--format=%an%n%ae%n%cn%n%ce%n%aI",
                "HEAD",
            ], repo).splitlines()
            self.assertEqual(metadata[:4], [
                "Fallback Author",
                "fallback.author@example.com",
                "OE Test",
                "oe-test@example.com",
            ])
            self.assertIn(metadata[4], (
                "2021-01-01T12:34:56+00:00",
                "2021-01-01T12:34:56Z",
            ))
            self.assert_note_and_extract(repo, patchname, "+plain diff change")
