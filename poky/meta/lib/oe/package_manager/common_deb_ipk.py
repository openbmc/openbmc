#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import glob
import os
import subprocess
import tempfile

import bb

from oe.package_manager import opkg_query, PackageManager

class OpkgDpkgPM(PackageManager):
    def __init__(self, d, target_rootfs):
        """
        This is an abstract class. Do not instantiate this directly.
        """
        super(OpkgDpkgPM, self).__init__(d, target_rootfs)

    def package_info(self, pkg):
        """
        Returns a dictionary with the package info.
        """
        raise NotImplementedError

    def _common_package_info(self, cmd):
        """
       "Returns a dictionary with the package info.

        This method extracts the common parts for Opkg and Dpkg
        """

        proc = subprocess.run(cmd, capture_output=True, encoding="utf-8", shell=True)
        if proc.returncode:
            bb.fatal("Unable to list available packages. Command '%s' "
                     "returned %d:\n%s" % (cmd, proc.returncode, proc.stderr))
        elif proc.stderr:
            bb.note("Command '%s' returned stderr: %s" % (cmd, proc.stderr))

        return opkg_query(proc.stdout)

    def extract(self, pkg):
        """
        Returns the path to a tmpdir where resides the contents of a package.

        Deleting the tmpdir is responsability of the caller.
        """
        pkg_info = self.package_info(pkg)
        if not pkg_info:
            bb.fatal("Unable to get information for package '%s' while "
                     "trying to extract the package."  % pkg)

        ar_cmd = bb.utils.which(os.getenv("PATH"), "ar")
        tar_cmd = bb.utils.which(os.getenv("PATH"), "tar")
        pkg_path = pkg_info[pkg]["filepath"]

        if not os.path.isfile(pkg_path):
            bb.fatal("Unable to extract package for '%s'."
                     "File %s doesn't exists" % (pkg, pkg_path))

        tmp_dir = tempfile.mkdtemp()
        current_dir = os.getcwd()
        os.chdir(tmp_dir)

        try:
            cmd = [ar_cmd, 'x', pkg_path]
            output = subprocess.check_output(cmd, stderr=subprocess.STDOUT)
            data_tar = glob.glob("data.tar.*")
            if len(data_tar) != 1:
                bb.fatal("Unable to extract %s package. Failed to identify "
                         "data tarball (found tarballs '%s').",
                         pkg_path, data_tar)
            data_tar = data_tar[0]
            cmd = [tar_cmd, 'xf', data_tar]
            output = subprocess.check_output(cmd, stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            bb.utils.remove(tmp_dir, recurse=True)
            bb.fatal("Unable to extract %s package. Command '%s' "
                     "returned %d:\n%s" % (pkg_path, ' '.join(cmd), e.returncode, e.output.decode("utf-8")))
        except OSError as e:
            bb.utils.remove(tmp_dir, recurse=True)
            bb.fatal("Unable to extract %s package. Command '%s' "
                     "returned %d:\n%s at %s" % (pkg_path, ' '.join(cmd), e.errno, e.strerror, e.filename))

        bb.note("Extracted %s to %s" % (pkg_path, tmp_dir))
        bb.utils.remove(os.path.join(tmp_dir, "debian-binary"))
        bb.utils.remove(os.path.join(tmp_dir, "control.tar.gz"))
        bb.utils.remove(os.path.join(tmp_dir, data_tar))
        os.chdir(current_dir)

        return tmp_dir

    def _handle_intercept_failure(self, registered_pkgs):
        self.mark_packages("unpacked", registered_pkgs.split())
