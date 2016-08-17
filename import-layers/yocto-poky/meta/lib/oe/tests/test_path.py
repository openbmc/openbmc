import unittest
import oe, oe.path
import tempfile
import os
import errno
import shutil

class TestRealPath(unittest.TestCase):
    DIRS = [ "a", "b", "etc", "sbin", "usr", "usr/bin", "usr/binX", "usr/sbin", "usr/include", "usr/include/gdbm" ]
    FILES = [ "etc/passwd", "b/file" ]
    LINKS = [
        ( "bin",             "/usr/bin",             "/usr/bin" ),
        ( "binX",            "usr/binX",             "/usr/binX" ),
        ( "c",               "broken",               "/broken" ),
        ( "etc/passwd-1",    "passwd",               "/etc/passwd" ),
        ( "etc/passwd-2",    "passwd-1",             "/etc/passwd" ),
        ( "etc/passwd-3",    "/etc/passwd-1",        "/etc/passwd" ),
        ( "etc/shadow-1",    "/etc/shadow",          "/etc/shadow" ),
        ( "etc/shadow-2",    "/etc/shadow-1",        "/etc/shadow" ),
        ( "prog-A",          "bin/prog-A",           "/usr/bin/prog-A" ),
        ( "prog-B",          "/bin/prog-B",          "/usr/bin/prog-B" ),
        ( "usr/bin/prog-C",  "../../sbin/prog-C",    "/sbin/prog-C" ),
        ( "usr/bin/prog-D",  "/sbin/prog-D",         "/sbin/prog-D" ),
        ( "usr/binX/prog-E", "../sbin/prog-E",       None ),
        ( "usr/bin/prog-F",  "../../../sbin/prog-F", "/sbin/prog-F" ),
        ( "loop",            "a/loop",               None ),
        ( "a/loop",          "../loop",              None ),
        ( "b/test",          "file/foo",             "/b/file/foo" ),
    ]

    LINKS_PHYS = [
        ( "./",          "/",                "" ),
        ( "binX/prog-E", "/usr/sbin/prog-E", "/sbin/prog-E" ),
    ]

    EXCEPTIONS = [
        ( "loop",   errno.ELOOP ),
        ( "b/test", errno.ENOENT ),
    ]

    def __del__(self):
        try:
            #os.system("tree -F %s" % self.tmpdir)
            shutil.rmtree(self.tmpdir)
        except:
            pass

    def setUp(self):
        self.tmpdir = tempfile.mkdtemp(prefix = "oe-test_path")
        self.root = os.path.join(self.tmpdir, "R")

        os.mkdir(os.path.join(self.tmpdir, "_real"))
        os.symlink("_real", self.root)

        for d in self.DIRS:
            os.mkdir(os.path.join(self.root, d))
        for f in self.FILES:
            file(os.path.join(self.root, f), "w")
        for l in self.LINKS:
            os.symlink(l[1], os.path.join(self.root, l[0]))

    def __realpath(self, file, use_physdir, assume_dir = True):
        return oe.path.realpath(os.path.join(self.root, file), self.root,
                                use_physdir, assume_dir = assume_dir)

    def test_norm(self):
        for l in self.LINKS:
            if l[2] == None:
                continue

            target_p = self.__realpath(l[0], True)
            target_l = self.__realpath(l[0], False)

            if l[2] != False:
                self.assertEqual(target_p, target_l)
                self.assertEqual(l[2], target_p[len(self.root):])

    def test_phys(self):
        for l in self.LINKS_PHYS:
            target_p = self.__realpath(l[0], True)
            target_l = self.__realpath(l[0], False)

            self.assertEqual(l[1], target_p[len(self.root):])
            self.assertEqual(l[2], target_l[len(self.root):])

    def test_loop(self):
        for e in self.EXCEPTIONS:
            self.assertRaisesRegexp(OSError, r'\[Errno %u\]' % e[1],
                                    self.__realpath, e[0], False, False)
