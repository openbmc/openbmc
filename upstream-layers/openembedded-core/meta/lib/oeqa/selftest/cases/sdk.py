#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import subprocess
import glob
import time
import select
from tempfile import TemporaryDirectory

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_vars

class SDKTests(OESelftestTestCase):

    def load_manifest(self, filename):
        manifest = {}
        with open(filename) as f:
            for line in f:
                name, arch, version = line.split(maxsplit=3)
                manifest[name] = (version, arch)
        return manifest

    def test_sdk_manifests(self):
        image = "core-image-minimal"

        self.write_config("""
TOOLCHAIN_HOST_TASK:append = " nativesdk-selftest-hello"
IMAGE_INSTALL:append = " selftest-hello"
""")

        bitbake(f"{image} -c populate_sdk")
        vars = get_bb_vars(['SDK_DEPLOY', 'TOOLCHAIN_OUTPUTNAME'], image)

        path = os.path.join(vars["SDK_DEPLOY"], vars["TOOLCHAIN_OUTPUTNAME"] + ".host.manifest")
        self.assertNotEqual(os.path.getsize(path), 0, msg="Host manifest is empty")
        self.assertIn("nativesdk-selftest-hello", self.load_manifest(path))

        path = os.path.join(vars["SDK_DEPLOY"], vars["TOOLCHAIN_OUTPUTNAME"] + ".target.manifest")
        self.assertNotEqual(os.path.getsize(path), 0, msg="Target manifest is empty")
        self.assertIn("selftest-hello", self.load_manifest(path))

    def test_sdk_runqemu(self):
        """Test using runqemu from SDK which has no bitbake available"""

        def path_with_no_bitbake():
            orig_paths = os.environ["PATH"].split(":")
            new_paths = []
            for orig_path in orig_paths:
                if not os.access(os.path.join(orig_path, "bitbake"), os.X_OK):
                    new_paths.append(orig_path)
            return ":".join(new_paths)

        def runqemu_works_from_sdk(sdk_dir, sdk_env, machine, path, timeout=360):
            env = os.environ.copy()
            env["PATH"] = path

            cmd = f'cd {sdk_dir}; . {sdk_env}; runqemu {machine} snapshot slirp nographic'
            self.logger.info(f"Running {cmd}")
            proc = subprocess.Popen(
                ['bash', '-c', cmd],
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
                bufsize=1,
                env=env
            )
            buffer = ""
            start = time.time()
            while time.time() - start < timeout:
                if proc.poll() is not None:
                    chunk = proc.stdout.read()
                    if chunk:
                        buffer += chunk
                    return ('Linux version' in buffer, "\n".join(buffer.split('\n')[-10:]))
                ready, _, _ = select.select([proc.stdout], [], [], 0.1)
                if ready:
                    chunk = proc.stdout.read(4096)
                    if chunk:
                        buffer += chunk
                        if "Linux version" in buffer:
                            proc.terminate()
                            return (True, "\n".join(buffer.split('\n')[-10:]))
                time.sleep(0.1)
            proc.terminate()
            time.sleep(5)
            if proc.poll() is None:
                proc.kill()
            return (False, "\n".join(buffer.split('\n')[-10:]))

        image = "core-image-minimal"

        self.write_config('QEMU_USE_KVM = "1"')

        # generate image and SDK
        bitbake(f"{image}")
        bitbake(f"{image} -c populate_sdk")

        # create a temporary destdir to test runqemu from SDK
        with TemporaryDirectory() as tmpdir:
            self.logger.debug(f"Created tempdir: {tmpdir}")
            vars = get_bb_vars(["DEPLOY_DIR_IMAGE", "SDK_DEPLOY", "TOOLCHAIN_OUTPUTNAME", "MACHINE"], image)
            machine = vars["MACHINE"]
            image_dir = vars["DEPLOY_DIR_IMAGE"]
            sdk_path = os.path.join(vars["SDK_DEPLOY"], vars["TOOLCHAIN_OUTPUTNAME"] + ".sh")
            self.logger.debug(f"machine: {machine}")
            self.logger.debug(f"image_dir: {image_dir}")
            self.logger.debug(f"sdk_path: {sdk_path}")
            dst_image_dir = os.path.join(tmpdir, os.path.basename(image_dir))
            shutil.copytree(image_dir, dst_image_dir)
            subprocess.check_call(f"{sdk_path} -d {tmpdir} -y", shell=True)
            sdk_env = glob.glob(tmpdir + '/environment-setup-*')[0]
            self.logger.debug(f"sdk_env: {sdk_env}")

            path = path_with_no_bitbake()
            self.logger.debug(f"path: {path}")

            runqemu_works, output = runqemu_works_from_sdk(tmpdir, sdk_env, machine, path)
            self.logger.debug(f"runqemu works in SDK: {runqemu_works}\n{output}")

            self.assertTrue(runqemu_works, f"runqemu does not work in SDK\n{output}")
