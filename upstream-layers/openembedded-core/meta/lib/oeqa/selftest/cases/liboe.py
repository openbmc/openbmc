#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import get_bb_var, get_bb_vars, bitbake, runCmd
import oe.path
import errno
import os
import tempfile
import unittest.mock

class CopyTreeTests(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super().setUpClass()
        cls.tmp_dir = get_bb_var('TMPDIR')

    def test_copy_tree_special(self):
        """
        Summary:    oe.path.copytree() should copy files with special character
        Expected:   'test file with sp£c!al @nd spaces' should exist in
                    copy destination
        Product:    OE-Core
        Author:     Joshua Lock <joshua.g.lock@intel.com>
        """
        testloc = oe.path.join(self.tmp_dir, 'liboetests')
        src = oe.path.join(testloc, 'src')
        dst = oe.path.join(testloc, 'dst')
        bb.utils.mkdirhier(testloc)
        bb.utils.mkdirhier(src)
        testfilename = 'test file with sp£c!al @nd spaces'

        # create the test file and copy it
        open(oe.path.join(src, testfilename), 'w+b').close()
        oe.path.copytree(src, dst)

        # ensure path exists in dest
        fileindst = os.path.isfile(oe.path.join(dst, testfilename))
        self.assertTrue(fileindst, "File with spaces doesn't exist in dst")

        oe.path.remove(testloc)

    def test_copy_tree_xattr(self):
        """
        Summary:    oe.path.copytree() should preserve xattr on copied files
        Expected:   testxattr file in destination should have user.oetest
                    extended attribute
        Product:    OE-Core
        Author:     Joshua Lock <joshua.g.lock@intel.com>
        """
        testloc = oe.path.join(self.tmp_dir, 'liboetests')
        src = oe.path.join(testloc, 'src')
        dst = oe.path.join(testloc, 'dst')
        bb.utils.mkdirhier(testloc)
        bb.utils.mkdirhier(src)
        testfilename = 'testxattr'

        # ensure we have setfattr available
        bitbake("attr-native")

        bb_vars = get_bb_vars(['SYSROOT_DESTDIR', 'bindir'], 'attr-native')
        destdir = bb_vars['SYSROOT_DESTDIR']
        bindir = bb_vars['bindir']
        bindir = destdir + bindir

        # create a file with xattr and copy it
        open(oe.path.join(src, testfilename), 'w+b').close()
        runCmd('%s/setfattr -n user.oetest -v "testing liboe" %s' % (bindir, oe.path.join(src, testfilename)))
        oe.path.copytree(src, dst)

        # ensure file in dest has user.oetest xattr
        result = runCmd('%s/getfattr -n user.oetest %s' % (bindir, oe.path.join(dst, testfilename)))
        self.assertIn('user.oetest="testing liboe"', result.output, 'Extended attribute not sert in dst')

        oe.path.remove(testloc)

    def test_copy_hardlink_tree_count(self):
        """
        Summary:    oe.path.copyhardlinktree() shouldn't miss out files
        Expected:   src and dst should have the same number of files
        Product:    OE-Core
        Author:     Joshua Lock <joshua.g.lock@intel.com>
        """
        testloc = oe.path.join(self.tmp_dir, 'liboetests')
        src = oe.path.join(testloc, 'src')
        dst = oe.path.join(testloc, 'dst')
        bb.utils.mkdirhier(testloc)
        bb.utils.mkdirhier(src)
        testfiles = ['foo', 'bar', '.baz', 'quux']

        def touchfile(tf):
            open(oe.path.join(src, tf), 'w+b').close()

        for f in testfiles:
            touchfile(f)

        oe.path.copyhardlinktree(src, dst)

        dstcnt = len(os.listdir(dst))
        srccnt = len(os.listdir(src))
        self.assertEqual(dstcnt, len(testfiles), "Number of files in dst (%s) differs from number of files in src(%s)." % (dstcnt, srccnt))

        oe.path.remove(testloc)

class SubprocessTests(OESelftestTestCase):

    def test_subprocess_tweak(self):
        """
        Test that the string representation of
        oeqa.utils.subprocesstweak.OETestCalledProcessError includes stdout and
        stderr, as expected.
        """
        script = """
#! /bin/sh
echo Ivn fgqbhg | tr '[a-zA-Z]' '[n-za-mN-ZA-M]'
echo Ivn fgqree | tr '[a-zA-Z]' '[n-za-mN-ZA-M]' >&2
exit 42
        """

        import subprocess
        import unittest.mock
        from oeqa.utils.subprocesstweak import OETestCalledProcessError

        with self.assertRaises(OETestCalledProcessError) as cm:
            with unittest.mock.patch("subprocess.CalledProcessError", OETestCalledProcessError):
                subprocess.run(["bash", "-"], input=script, text=True, capture_output=True, check=True)

        e = cm.exception
        self.assertEqual(e.returncode, 42)
        self.assertEqual("Via stdout\n", e.stdout)
        self.assertEqual("Via stderr\n", e.stderr)

        string = str(e)
        self.assertIn("exit status 42", string)
        self.assertIn("Standard Output: Via stdout", string)
        self.assertIn("Standard Error: Via stderr", string)

class PathTests(OESelftestTestCase):
    """
    In-process coverage for oe.path's own path logic: join, is_path_parent,
    symlink, make_relative_symlink, canonicalize, which_wild and realpath.
    Thin standard-library wrappers (relative -> os.path.relpath,
    find -> os.walk) are left to the standard library.
    """

    def setUp(self):
        super().setUp()
        self._tmp = tempfile.TemporaryDirectory(prefix="oeqa-oepath-")
        self.addCleanup(self._tmp.cleanup)
        self.tmp_path = self._tmp.name

    # --- join --------------------------------------------------------------
    # join() is os.path.normpath("/".join(paths)); unlike os.path.join it
    # does not treat an absolute right-hand component specially.

    def test_join_two_paths(self):
        self.assertEqual(oe.path.join("a", "b"), "a/b")

    def test_join_absolute_rhs_is_not_special(self):
        # os.path.join("a", "/b") == "/b"; oe.path.join() keeps it relative.
        self.assertEqual(oe.path.join("a", "/b"), "a/b")

    def test_join_redundant_separators_normalised(self):
        self.assertEqual(oe.path.join("a//", "b"), "a/b")

    def test_join_pardir_normalised(self):
        self.assertEqual(oe.path.join("a", "..", "b"), "b")

    def test_join_empty_leading_component_is_absolute(self):
        # "/".join(["", "b"]) is "/b", which normpath leaves absolute.
        self.assertEqual(oe.path.join("", "b"), "/b")

    # --- is_path_parent ----------------------------------------------------

    def test_is_path_parent_direct_child(self):
        self.assertTrue(oe.path.is_path_parent("/usr", "/usr/bin"))

    def test_is_path_parent_unrelated_path(self):
        self.assertFalse(oe.path.is_path_parent("/usr", "/tmp"))

    def test_is_path_parent_child_is_not_parent_of_its_parent(self):
        self.assertFalse(oe.path.is_path_parent("/usr/bin", "/usr"))

    def test_is_path_parent_prefix_string_is_not_a_path_parent(self):
        # "/usrlocal" shares a string prefix with "/usr" but is not below it;
        # the trailing-separator handling must reject it.
        self.assertFalse(oe.path.is_path_parent("/usr", "/usrlocal"))

    def test_is_path_parent_no_paths_returns_false(self):
        self.assertFalse(oe.path.is_path_parent("/usr"))

    def test_is_path_parent_all_paths_must_be_below(self):
        self.assertTrue(oe.path.is_path_parent("/usr", "/usr/bin", "/usr/lib"))

    def test_is_path_parent_one_path_outside_fails(self):
        self.assertFalse(oe.path.is_path_parent("/usr", "/usr/bin", "/tmp"))

    # --- symlink -----------------------------------------------------------

    def test_symlink_creates_symlink(self):
        src = os.path.join(self.tmp_path, "target.txt")
        with open(src, "w") as f:
            f.write("content")
        dst = os.path.join(self.tmp_path, "link")
        oe.path.symlink(src, dst)
        self.assertTrue(os.path.islink(dst))
        self.assertEqual(os.readlink(dst), src)

    def test_symlink_matching_existing_is_idempotent(self):
        src = os.path.join(self.tmp_path, "target.txt")
        with open(src, "w") as f:
            f.write("content")
        dst = os.path.join(self.tmp_path, "link")
        oe.path.symlink(src, dst)
        oe.path.symlink(src, dst)
        self.assertEqual(os.readlink(dst), src)

    def test_symlink_conflicting_existing_raises(self):
        src1 = os.path.join(self.tmp_path, "t1.txt")
        src2 = os.path.join(self.tmp_path, "t2.txt")
        for p in (src1, src2):
            with open(p, "w") as f:
                f.write(p)
        dst = os.path.join(self.tmp_path, "link")
        oe.path.symlink(src1, dst)
        with self.assertRaises(OSError):
            oe.path.symlink(src2, dst)

    def test_symlink_force_overwrites_existing_symlink(self):
        src1 = os.path.join(self.tmp_path, "t1")
        src2 = os.path.join(self.tmp_path, "t2")
        for p in (src1, src2):
            with open(p, "w") as f:
                f.write(p)
        dst = os.path.join(self.tmp_path, "link")
        oe.path.symlink(src1, dst)
        oe.path.symlink(src2, dst, force=True)
        self.assertEqual(os.readlink(dst), src2)

    def test_symlink_force_replaces_destination_with_glob_metacharacters(self):
        # A destination whose name contains glob metacharacters must still be
        # replaced. Routing the removal through remove() (which globs) fails
        # to match "link[1]" against itself, leaving the stale entry in place.
        src = os.path.join(self.tmp_path, "target")
        with open(src, "w") as f:
            f.write("content")
        dst = os.path.join(self.tmp_path, "link[1]")
        with open(dst, "w") as f:
            f.write("stale regular file")
        oe.path.symlink(src, dst, force=True)
        self.assertTrue(os.path.islink(dst))
        self.assertEqual(os.readlink(dst), src)

    def test_symlink_force_does_not_delete_glob_siblings(self):
        # The destination "keep?.txt" does not exist, but a sibling
        # "keepX.txt" matches it as a glob pattern. force=True must remove
        # only the literal destination, never a pattern sibling.
        sibling = os.path.join(self.tmp_path, "keepX.txt")
        with open(sibling, "w") as f:
            f.write("do not delete me")
        src = os.path.join(self.tmp_path, "target")
        with open(src, "w") as f:
            f.write("content")
        dst = os.path.join(self.tmp_path, "keep?.txt")
        oe.path.symlink(src, dst, force=True)
        self.assertTrue(os.path.exists(sibling))
        self.assertEqual(os.readlink(dst), src)

    def test_symlink_force_replaces_existing_directory(self):
        # An existing directory at the destination is torn down (EISDIR ->
        # rmtree) before the link is created.
        src = os.path.join(self.tmp_path, "target")
        with open(src, "w") as f:
            f.write("content")
        dst = os.path.join(self.tmp_path, "dir")
        os.mkdir(dst)
        with open(os.path.join(dst, "child"), "w") as f:
            f.write("x")
        oe.path.symlink(src, dst, force=True)
        self.assertTrue(os.path.islink(dst))
        self.assertEqual(os.readlink(dst), src)

    def test_symlink_creates_dangling_symlink(self):
        # symlink() does not require the source to exist; callers rely on
        # being able to stage a link before its target is populated.
        src = os.path.join(self.tmp_path, "not_created_yet")
        dst = os.path.join(self.tmp_path, "link")
        oe.path.symlink(src, dst)
        self.assertTrue(os.path.islink(dst))
        self.assertEqual(os.readlink(dst), src)
        self.assertFalse(os.path.exists(dst))  # dangling: target is absent

    # --- make_relative_symlink ---------------------------------------------

    def test_make_relative_symlink_non_symlink_is_ignored(self):
        regular = os.path.join(self.tmp_path, "file.txt")
        with open(regular, "w") as f:
            f.write("content")
        oe.path.make_relative_symlink(regular)
        self.assertTrue(os.path.isfile(regular))

    def test_make_relative_symlink_already_relative_unchanged(self):
        target = os.path.join(self.tmp_path, "target.txt")
        with open(target, "w") as f:
            f.write("content")
        link = os.path.join(self.tmp_path, "link")
        os.symlink("target.txt", link)
        oe.path.make_relative_symlink(link)
        self.assertEqual(os.readlink(link), "target.txt")

    def test_make_relative_symlink_absolute_becomes_relative_and_resolves(self):
        target = os.path.join(self.tmp_path, "target.txt")
        with open(target, "w") as f:
            f.write("content")
        link = os.path.join(self.tmp_path, "link")
        os.symlink(target, link)
        self.assertTrue(os.path.isabs(os.readlink(link)))
        oe.path.make_relative_symlink(link)
        result = os.readlink(link)
        self.assertFalse(os.path.isabs(result))
        self.assertTrue(os.path.exists(link))
        with open(link) as f:
            self.assertEqual(f.read(), "content")

    def test_make_relative_symlink_several_levels_deep_becomes_relative(self):
        # The link sits several directories below the target, so the depth
        # loop has to prepend more than one '../' segment. A single-level
        # case never exercises that loop body.
        target = os.path.join(self.tmp_path, "target.txt")
        with open(target, "w") as f:
            f.write("content")
        deep = os.path.join(self.tmp_path, "a", "b", "c")
        os.makedirs(deep)
        link = os.path.join(deep, "link")
        os.symlink(target, link)
        oe.path.make_relative_symlink(link)
        result = os.readlink(link)
        self.assertFalse(os.path.isabs(result))
        self.assertEqual(result, "../../../target.txt")
        with open(link) as f:
            self.assertEqual(f.read(), "content")

    # --- canonicalize ------------------------------------------------------

    def test_canonicalize_real_path_returned(self):
        self.assertEqual(oe.path.canonicalize(self.tmp_path),
                         os.path.realpath(self.tmp_path))

    def test_canonicalize_unexpanded_variable_is_skipped(self):
        self.assertEqual(oe.path.canonicalize("$SOME_VAR"), "")

    def test_canonicalize_variable_token_dropped_leaving_real_path(self):
        result = oe.path.canonicalize("$VAR," + self.tmp_path)
        # The "$VAR" token is dropped entirely: no empty placeholder, no
        # leading separator, just the canonical real path.
        self.assertEqual(result, os.path.realpath(self.tmp_path))

    def test_canonicalize_empty_string_returns_empty_string(self):
        # os.path.realpath("") is the cwd; canonicalize("") must not leak it.
        self.assertEqual(oe.path.canonicalize(""), "")

    def test_canonicalize_none_returns_empty_string(self):
        self.assertEqual(oe.path.canonicalize(None), "")

    def test_canonicalize_empty_token_between_paths_is_dropped(self):
        p1 = os.path.join(self.tmp_path, "a")
        p2 = os.path.join(self.tmp_path, "b")
        os.mkdir(p1)
        os.mkdir(p2)
        # The stray separator in "a,,b" must not inject a cwd entry.
        result = oe.path.canonicalize("%s,,%s" % (p1, p2))
        self.assertEqual(result, "%s,%s" % (os.path.realpath(p1),
                                            os.path.realpath(p2)))

    def test_canonicalize_trailing_slash_preserved(self):
        result = oe.path.canonicalize(self.tmp_path + "/")
        self.assertEqual(result, os.path.realpath(self.tmp_path) + "/")

    def test_canonicalize_multiple_variable_tokens_all_skipped(self):
        # Every "$"-bearing token is dropped, so a list made only of them
        # canonicalizes to the empty string (no separators, no cwd).
        self.assertEqual(oe.path.canonicalize("$A,$B,$C"), "")

    def test_canonicalize_variable_and_trailing_slash_path(self):
        # A dropped "$" token followed by a real path with a trailing slash:
        # the slash is preserved and no phantom leading separator appears.
        result = oe.path.canonicalize("$VAR,%s/" % self.tmp_path)
        self.assertEqual(result, os.path.realpath(self.tmp_path) + "/")

    # --- which_wild --------------------------------------------------------

    def test_which_wild_missing_tool_returns_empty(self):
        self.assertEqual(oe.path.which_wild("totally_nonexistent_tool_xyz"), [])

    def test_which_wild_explicit_search_path(self):
        tool = os.path.join(self.tmp_path, "mytool")
        with open(tool, "w") as f:
            f.write("#!/bin/sh\n")
        os.chmod(tool, 0o755)
        self.assertEqual(oe.path.which_wild("mytool", path=self.tmp_path),
                         [tool])

    def test_which_wild_wildcard_pattern(self):
        for name in ("foo-a", "foo-b", "bar"):
            with open(os.path.join(self.tmp_path, name), "w") as f:
                f.write("")
        results = oe.path.which_wild("foo-*", path=self.tmp_path)
        self.assertEqual(sorted(os.path.basename(p) for p in results),
                         ["foo-a", "foo-b"])

    def test_which_wild_first_match_per_name_wins(self):
        # A name found in an earlier PATH element shadows the same name later;
        # reverse=True walks PATH the other way, so the other copy wins.
        first = os.path.join(self.tmp_path, "first")
        second = os.path.join(self.tmp_path, "second")
        os.mkdir(first)
        os.mkdir(second)
        for d in (first, second):
            with open(os.path.join(d, "tool"), "w") as f:
                f.write("")
        search = "%s:%s" % (first, second)
        self.assertEqual(oe.path.which_wild("tool", path=search),
                         [os.path.join(first, "tool")])
        self.assertEqual(oe.path.which_wild("tool", path=search, reverse=True),
                         [os.path.join(second, "tool")])

    # --- realpath ----------------------------------------------------------

    def test_realpath_resolves_symlink_below_root(self):
        target = os.path.join(self.tmp_path, "real")
        os.mkdir(target)
        link = os.path.join(self.tmp_path, "lnk")
        os.symlink("real", link)
        self.assertEqual(oe.path.realpath(link, self.tmp_path), target)

    def test_realpath_plain_path_is_returned(self):
        sub = os.path.join(self.tmp_path, "d")
        os.mkdir(sub)
        self.assertEqual(oe.path.realpath(sub, self.tmp_path), sub)

    def test_realpath_path_outside_root_raises(self):
        root = os.path.join(self.tmp_path, "root")
        outside = os.path.join(self.tmp_path, "outside")
        os.mkdir(root)
        os.mkdir(outside)
        with self.assertRaises(OSError):
            oe.path.realpath(outside, root)

    def test_realpath_resolves_symlink_several_components_deep(self):
        # With use_physdir=True realpath resolves each intermediate
        # component, so a link buried under real subdirectories must still
        # resolve to its final target.
        os.makedirs(os.path.join(self.tmp_path, "a", "b", "c"))
        link = os.path.join(self.tmp_path, "link_to_c")
        os.symlink("a/b/c", link)
        result = oe.path.realpath(os.path.join(link, "file"),
                                  self.tmp_path, assume_dir=True)
        self.assertEqual(result,
                         os.path.join(self.tmp_path, "a", "b", "c", "file"))

    def test_realpath_self_referential_symlink_raises_eloop(self):
        # A link that points at itself must be reported as ELOOP rather than
        # spinning until the recursion limit or the stack blows.
        link = os.path.join(self.tmp_path, "loop")
        os.symlink("loop", link)
        with self.assertRaises(OSError) as cm:
            oe.path.realpath(link, self.tmp_path)
        self.assertEqual(cm.exception.errno, errno.ELOOP)

    def test_realpath_mutual_symlink_cycle_raises_eloop(self):
        # An A -> B -> A cycle is the same contract as the self-loop.
        a = os.path.join(self.tmp_path, "a")
        b = os.path.join(self.tmp_path, "b")
        os.symlink("b", a)
        os.symlink("a", b)
        with self.assertRaises(OSError) as cm:
            oe.path.realpath(a, self.tmp_path)
        self.assertEqual(cm.exception.errno, errno.ELOOP)

    def test_realpath_missing_component_raises_enoent_without_assume_dir(self):
        # By default a missing path component is an error.
        with self.assertRaises(OSError) as cm:
            oe.path.realpath(os.path.join(self.tmp_path, "nope", "missing"),
                             self.tmp_path)
        self.assertEqual(cm.exception.errno, errno.ENOENT)

    def test_realpath_missing_component_tolerated_with_assume_dir(self):
        # assume_dir=True lets realpath resolve a path whose trailing
        # components do not exist yet, returning the composed path.
        result = oe.path.realpath(
            os.path.join(self.tmp_path, "nope", "missing"),
            self.tmp_path, assume_dir=True)
        self.assertEqual(result,
                         os.path.join(self.tmp_path, "nope", "missing"))

    def test_realpath_isdir_failure_falls_back_without_nameerror(self):
        # The final stanza of __realpath tolerates any failure from
        # os.path.isdir by treating the path as not-a-directory. Force isdir
        # to raise and confirm the fallback returns the resolved path rather
        # than raising NameError from an undefined fallback value.
        sub = os.path.join(self.tmp_path, "d")
        os.mkdir(sub)

        def boom(_path):
            raise OSError("synthetic stat failure")

        with unittest.mock.patch("os.path.isdir", boom):
            self.assertEqual(
                oe.path.realpath(sub, self.tmp_path, use_physdir=False), sub)
