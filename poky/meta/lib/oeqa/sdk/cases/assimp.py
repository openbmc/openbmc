import os, subprocess, unittest
import bb
from oeqa.sdk.case import OESDKTestCase

from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class BuildAssimp(OESDKTestCase):
    """
    Test case to build a project using cmake.
    """

    td_vars = ['DATETIME', 'TARGET_OS', 'TARGET_ARCH']

    @classmethod
    def setUpClass(self):
        if not (self.tc.hasHostPackage("nativesdk-cmake") or
                self.tc.hasHostPackage("cmake-native")):
            raise unittest.SkipTest("Needs cmake")

    def fetch(self, workdir, dl_dir, url, archive=None):
        if not archive:
            from urllib.parse import urlparse
            archive = os.path.basename(urlparse(url).path)

        if dl_dir:
            tarball = os.path.join(dl_dir, archive)
            if os.path.exists(tarball):
                return tarball

        tarball = os.path.join(workdir, archive)
        subprocess.check_output(["wget", "-O", tarball, url])
        return tarball

    def test_assimp(self):
        import tempfile
        import oe.qa, oe.elf

        with tempfile.TemporaryDirectory(prefix="assimp", dir=self.tc.sdk_dir) as testdir:
            dl_dir = self.td.get('DL_DIR', None)
            tarball = self.fetch(testdir, dl_dir, "https://github.com/assimp/assimp/archive/v4.1.0.tar.gz")
            subprocess.check_output(["tar", "xf", tarball, "-C", testdir])

            sourcedir = os.path.join(testdir, "assimp-4.1.0") 
            builddir = os.path.join(testdir, "build")
            installdir = os.path.join(testdir, "install")
            bb.utils.mkdirhier(builddir)

            self._run("cd %s && cmake -DCMAKE_VERBOSE_MAKEFILE:BOOL=ON %s " % (builddir, sourcedir))
            self._run("cmake --build %s -- -j" % builddir)
            self._run("cmake --build %s --target install -- DESTDIR=%s" % (builddir, installdir))

            elf = oe.qa.ELFFile(os.path.join(installdir, "usr", "local", "lib", "libassimp.so.4.1.0"))
            elf.open()

            output = self._run("echo $OECORE_TARGET_OS:$OECORE_TARGET_ARCH")
            target_os, target_arch = output.strip().split(":")
            machine_data = oe.elf.machine_dict(None)[target_os][target_arch]
            (machine, osabi, abiversion, endian, bits) = machine_data

            self.assertEqual(machine, elf.machine())
            self.assertEqual(bits, elf.abiSize())
            self.assertEqual(endian, elf.isLittleEndian())
