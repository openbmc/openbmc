import unittest
import os
import tempfile
import logging
import fnmatch

import oeqa.utils.ftools as ftools
from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd, bitbake, get_bb_var
from oeqa.utils.decorators import testcase

class OePkgdataUtilTests(oeSelfTest):

    @classmethod
    def setUpClass(cls):
        # Ensure we have the right data in pkgdata
        logger = logging.getLogger("selftest")
        logger.info('Running bitbake to generate pkgdata')
        bitbake('glibc busybox zlib bash')

    @testcase(1203)
    def test_lookup_pkg(self):
        # Forward tests
        result = runCmd('oe-pkgdata-util lookup-pkg "glibc busybox"')
        self.assertEqual(result.output, 'libc6\nbusybox')
        result = runCmd('oe-pkgdata-util lookup-pkg zlib-dev')
        self.assertEqual(result.output, 'libz-dev')
        result = runCmd('oe-pkgdata-util lookup-pkg nonexistentpkg', ignore_status=True)
        self.assertEqual(result.status, 1, "Status different than 1. output: %s" % result.output)
        self.assertEqual(result.output, 'ERROR: The following packages could not be found: nonexistentpkg')
        # Reverse tests
        result = runCmd('oe-pkgdata-util lookup-pkg -r "libc6 busybox"')
        self.assertEqual(result.output, 'glibc\nbusybox')
        result = runCmd('oe-pkgdata-util lookup-pkg -r libz-dev')
        self.assertEqual(result.output, 'zlib-dev')
        result = runCmd('oe-pkgdata-util lookup-pkg -r nonexistentpkg', ignore_status=True)
        self.assertEqual(result.status, 1, "Status different than 1. output: %s" % result.output)
        self.assertEqual(result.output, 'ERROR: The following packages could not be found: nonexistentpkg')

    @testcase(1205)
    def test_read_value(self):
        result = runCmd('oe-pkgdata-util read-value PN libz1')
        self.assertEqual(result.output, 'zlib')
        result = runCmd('oe-pkgdata-util read-value PKGSIZE bash')
        pkgsize = int(result.output.strip())
        self.assertGreater(pkgsize, 1, "Size should be greater than 1. %s" % result.output)

    @testcase(1198)
    def test_find_path(self):
        result = runCmd('oe-pkgdata-util find-path /lib/libc.so.6')
        self.assertEqual(result.output, 'glibc: /lib/libc.so.6')
        result = runCmd('oe-pkgdata-util find-path /bin/bash')
        self.assertEqual(result.output, 'bash: /bin/bash')
        result = runCmd('oe-pkgdata-util find-path /not/exist', ignore_status=True)
        self.assertEqual(result.status, 1, "Status different than 1. output: %s" % result.output)
        self.assertEqual(result.output, 'ERROR: Unable to find any package producing path /not/exist')

    @testcase(1204)
    def test_lookup_recipe(self):
        result = runCmd('oe-pkgdata-util lookup-recipe "libc6-staticdev busybox"')
        self.assertEqual(result.output, 'glibc\nbusybox')
        result = runCmd('oe-pkgdata-util lookup-recipe libz-dbg')
        self.assertEqual(result.output, 'zlib')
        result = runCmd('oe-pkgdata-util lookup-recipe nonexistentpkg', ignore_status=True)
        self.assertEqual(result.status, 1, "Status different than 1. output: %s" % result.output)
        self.assertEqual(result.output, 'ERROR: The following packages could not be found: nonexistentpkg')

    @testcase(1202)
    def test_list_pkgs(self):
        # No arguments
        result = runCmd('oe-pkgdata-util list-pkgs')
        pkglist = result.output.split()
        self.assertIn('glibc-utils', pkglist, "Listed packages: %s" % result.output)
        self.assertIn('zlib-dev', pkglist, "Listed packages: %s" % result.output)
        # No pkgspec, runtime
        result = runCmd('oe-pkgdata-util list-pkgs -r')
        pkglist = result.output.split()
        self.assertIn('libc6-utils', pkglist, "Listed packages: %s" % result.output)
        self.assertIn('libz-dev', pkglist, "Listed packages: %s" % result.output)
        # With recipe specified
        result = runCmd('oe-pkgdata-util list-pkgs -p zlib')
        pkglist = sorted(result.output.split())
        try:
            pkglist.remove('zlib-ptest') # in case ptest is disabled
        except ValueError:
            pass
        self.assertEqual(pkglist, ['zlib', 'zlib-dbg', 'zlib-dev', 'zlib-doc', 'zlib-staticdev'], "Packages listed after remove: %s" % result.output)
        # With recipe specified, runtime
        result = runCmd('oe-pkgdata-util list-pkgs -p zlib -r')
        pkglist = sorted(result.output.split())
        try:
            pkglist.remove('libz-ptest') # in case ptest is disabled
        except ValueError:
            pass
        self.assertEqual(pkglist, ['libz-dbg', 'libz-dev', 'libz-doc', 'libz-staticdev', 'libz1'], "Packages listed after remove: %s" % result.output)
        # With recipe specified and unpackaged
        result = runCmd('oe-pkgdata-util list-pkgs -p zlib -u')
        pkglist = sorted(result.output.split())
        self.assertIn('zlib-locale', pkglist, "Listed packages: %s" % result.output)
        # With recipe specified and unpackaged, runtime
        result = runCmd('oe-pkgdata-util list-pkgs -p zlib -u -r')
        pkglist = sorted(result.output.split())
        self.assertIn('libz-locale', pkglist, "Listed packages: %s" % result.output)
        # With recipe specified and pkgspec
        result = runCmd('oe-pkgdata-util list-pkgs -p zlib "*-d*"')
        pkglist = sorted(result.output.split())
        self.assertEqual(pkglist, ['zlib-dbg', 'zlib-dev', 'zlib-doc'], "Packages listed: %s" % result.output)
        # With recipe specified and pkgspec, runtime
        result = runCmd('oe-pkgdata-util list-pkgs -p zlib -r "*-d*"')
        pkglist = sorted(result.output.split())
        self.assertEqual(pkglist, ['libz-dbg', 'libz-dev', 'libz-doc'], "Packages listed: %s" % result.output)

    @testcase(1201)
    def test_list_pkg_files(self):
        def splitoutput(output):
            files = {}
            curpkg = None
            for line in output.splitlines():
                if line.startswith('\t'):
                    self.assertTrue(curpkg, 'Unexpected non-package line:\n%s' % line)
                    files[curpkg].append(line.strip())
                else:
                    self.assertTrue(line.rstrip().endswith(':'), 'Invalid package line in output:\n%s' % line)
                    curpkg = line.split(':')[0]
                    files[curpkg] = []
            return files
        base_libdir = get_bb_var('base_libdir')
        libdir = get_bb_var('libdir')
        includedir = get_bb_var('includedir')
        mandir = get_bb_var('mandir')
        # Test recipe-space package name
        result = runCmd('oe-pkgdata-util list-pkg-files zlib-dev zlib-doc')
        files = splitoutput(result.output)
        self.assertIn('zlib-dev', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('zlib-doc', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn(os.path.join(includedir, 'zlib.h'), files['zlib-dev'])
        self.assertIn(os.path.join(mandir, 'man3/zlib.3'), files['zlib-doc'])
        # Test runtime package name
        result = runCmd('oe-pkgdata-util list-pkg-files -r libz1 libz-dev')
        files = splitoutput(result.output)
        self.assertIn('libz1', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('libz-dev', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertGreater(len(files['libz1']), 1)
        libspec = os.path.join(base_libdir, 'libz.so.1.*')
        found = False
        for fileitem in files['libz1']:
            if fnmatch.fnmatchcase(fileitem, libspec):
                found = True
                break
        self.assertTrue(found, 'Could not find zlib library file %s in libz1 package file list: %s' % (libspec, files['libz1']))
        self.assertIn(os.path.join(includedir, 'zlib.h'), files['libz-dev'])
        # Test recipe
        result = runCmd('oe-pkgdata-util list-pkg-files -p zlib')
        files = splitoutput(result.output)
        self.assertIn('zlib-dbg', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('zlib-doc', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('zlib-dev', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('zlib-staticdev', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('zlib', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertNotIn('zlib-locale', files.keys(), "listed pkgs. files: %s" %result.output)
        # (ignore ptest, might not be there depending on config)
        self.assertIn(os.path.join(includedir, 'zlib.h'), files['zlib-dev'])
        self.assertIn(os.path.join(mandir, 'man3/zlib.3'), files['zlib-doc'])
        self.assertIn(os.path.join(libdir, 'libz.a'), files['zlib-staticdev'])
        # Test recipe, runtime
        result = runCmd('oe-pkgdata-util list-pkg-files -p zlib -r')
        files = splitoutput(result.output)
        self.assertIn('libz-dbg', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('libz-doc', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('libz-dev', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('libz-staticdev', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('libz1', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertNotIn('libz-locale', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn(os.path.join(includedir, 'zlib.h'), files['libz-dev'])
        self.assertIn(os.path.join(mandir, 'man3/zlib.3'), files['libz-doc'])
        self.assertIn(os.path.join(libdir, 'libz.a'), files['libz-staticdev'])
        # Test recipe, unpackaged
        result = runCmd('oe-pkgdata-util list-pkg-files -p zlib -u')
        files = splitoutput(result.output)
        self.assertIn('zlib-dbg', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('zlib-doc', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('zlib-dev', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('zlib-staticdev', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('zlib', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('zlib-locale', files.keys(), "listed pkgs. files: %s" %result.output) # this is the key one
        self.assertIn(os.path.join(includedir, 'zlib.h'), files['zlib-dev'])
        self.assertIn(os.path.join(mandir, 'man3/zlib.3'), files['zlib-doc'])
        self.assertIn(os.path.join(libdir, 'libz.a'), files['zlib-staticdev'])
        # Test recipe, runtime, unpackaged
        result = runCmd('oe-pkgdata-util list-pkg-files -p zlib -r -u')
        files = splitoutput(result.output)
        self.assertIn('libz-dbg', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('libz-doc', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('libz-dev', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('libz-staticdev', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('libz1', files.keys(), "listed pkgs. files: %s" %result.output)
        self.assertIn('libz-locale', files.keys(), "listed pkgs. files: %s" %result.output) # this is the key one
        self.assertIn(os.path.join(includedir, 'zlib.h'), files['libz-dev'])
        self.assertIn(os.path.join(mandir, 'man3/zlib.3'), files['libz-doc'])
        self.assertIn(os.path.join(libdir, 'libz.a'), files['libz-staticdev'])

    @testcase(1200)
    def test_glob(self):
        tempdir = tempfile.mkdtemp(prefix='pkgdataqa')
        self.track_for_cleanup(tempdir)
        pkglistfile = os.path.join(tempdir, 'pkglist')
        with open(pkglistfile, 'w') as f:
            f.write('libc6\n')
            f.write('libz1\n')
            f.write('busybox\n')
        result = runCmd('oe-pkgdata-util glob %s "*-dev"' % pkglistfile)
        desiredresult = ['libc6-dev', 'libz-dev', 'busybox-dev']
        self.assertEqual(sorted(result.output.split()), sorted(desiredresult))
        # The following should not error (because when we use this during rootfs construction, sometimes the complementary package won't exist)
        result = runCmd('oe-pkgdata-util glob %s "*-nonexistent"' % pkglistfile)
        self.assertEqual(result.output, '')
        # Test exclude option
        result = runCmd('oe-pkgdata-util glob %s "*-dev *-dbg" -x "^libz"' % pkglistfile)
        resultlist = result.output.split()
        self.assertNotIn('libz-dev', resultlist)
        self.assertNotIn('libz-dbg', resultlist)

    @testcase(1206)
    def test_specify_pkgdatadir(self):
        result = runCmd('oe-pkgdata-util -p %s lookup-pkg glibc' % get_bb_var('PKGDATA_DIR'))
        self.assertEqual(result.output, 'libc6')
