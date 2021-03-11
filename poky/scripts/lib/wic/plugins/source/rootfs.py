#
# Copyright (c) 2014, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#
# DESCRIPTION
# This implements the 'rootfs' source plugin class for 'wic'
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
# Joao Henrique Ferreira de Freitas <joaohf (at] gmail.com>
#

import logging
import os
import shutil
import sys

from oe.path import copyhardlinktree

from wic import WicError
from wic.pluginbase import SourcePlugin
from wic.misc import get_bitbake_var, exec_native_cmd

logger = logging.getLogger('wic')

class RootfsPlugin(SourcePlugin):
    """
    Populate partition content from a rootfs directory.
    """

    name = 'rootfs'

    @staticmethod
    def __get_rootfs_dir(rootfs_dir):
        if os.path.isdir(rootfs_dir):
            return os.path.realpath(rootfs_dir)

        image_rootfs_dir = get_bitbake_var("IMAGE_ROOTFS", rootfs_dir)
        if not os.path.isdir(image_rootfs_dir):
            raise WicError("No valid artifact IMAGE_ROOTFS from image "
                           "named %s has been found at %s, exiting." %
                           (rootfs_dir, image_rootfs_dir))

        return os.path.realpath(image_rootfs_dir)

    @staticmethod
    def __get_pseudo(native_sysroot, rootfs, pseudo_dir):
        pseudo = "export PSEUDO_PREFIX=%s/usr;" % native_sysroot
        pseudo += "export PSEUDO_LOCALSTATEDIR=%s;" % pseudo_dir
        pseudo += "export PSEUDO_PASSWD=%s;" % rootfs
        pseudo += "export PSEUDO_NOSYMLINKEXP=1;"
        pseudo += "%s " % get_bitbake_var("FAKEROOTCMD")
        return pseudo

    @classmethod
    def do_prepare_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             krootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        In this case, prepare content for legacy bios boot partition.
        """
        if part.rootfs_dir is None:
            if not 'ROOTFS_DIR' in krootfs_dir:
                raise WicError("Couldn't find --rootfs-dir, exiting")

            rootfs_dir = krootfs_dir['ROOTFS_DIR']
        else:
            if part.rootfs_dir in krootfs_dir:
                rootfs_dir = krootfs_dir[part.rootfs_dir]
            elif part.rootfs_dir:
                rootfs_dir = part.rootfs_dir
            else:
                raise WicError("Couldn't find --rootfs-dir=%s connection or "
                               "it is not a valid path, exiting" % part.rootfs_dir)

        part.rootfs_dir = cls.__get_rootfs_dir(rootfs_dir)
        part.has_fstab = os.path.exists(os.path.join(part.rootfs_dir, "etc/fstab"))
        pseudo_dir = os.path.join(part.rootfs_dir, "../pseudo")
        if not os.path.lexists(pseudo_dir):
            logger.warn("%s folder does not exist. "
                        "Usernames and permissions will be invalid " % pseudo_dir)
            pseudo_dir = None

        new_rootfs = None
        new_pseudo = None
        # Handle excluded paths.
        if part.exclude_path or part.include_path or part.change_directory or part.update_fstab_in_rootfs:
            # We need a new rootfs directory we can safely modify without
            # interfering with other tasks. Copy to workdir.
            new_rootfs = os.path.realpath(os.path.join(cr_workdir, "rootfs%d" % part.lineno))

            if os.path.lexists(new_rootfs):
                shutil.rmtree(os.path.join(new_rootfs))

            if part.change_directory:
                cd = part.change_directory
                if cd[-1] == '/':
                    cd = cd[:-1]
                if os.path.isabs(cd):
                    logger.error("Must be relative: --change-directory=%s" % cd)
                    sys.exit(1)
                orig_dir = os.path.realpath(os.path.join(part.rootfs_dir, cd))
                if not orig_dir.startswith(part.rootfs_dir):
                    logger.error("'%s' points to a path outside the rootfs" % orig_dir)
                    sys.exit(1)

            else:
                orig_dir = part.rootfs_dir
            copyhardlinktree(orig_dir, new_rootfs)

            # Convert the pseudo directory to its new location
            if (pseudo_dir):
                new_pseudo = os.path.realpath(
                             os.path.join(cr_workdir, "pseudo%d" % part.lineno))
                if os.path.lexists(new_pseudo):
                    shutil.rmtree(new_pseudo)
                os.mkdir(new_pseudo)
                shutil.copy(os.path.join(pseudo_dir, "files.db"),
                            os.path.join(new_pseudo, "files.db"))

                pseudo_cmd = "%s -B -m %s -M %s" % (cls.__get_pseudo(native_sysroot,
                                                                     new_rootfs,
                                                                     new_pseudo),
                                                    orig_dir, new_rootfs)
                exec_native_cmd(pseudo_cmd, native_sysroot)

            for path in part.include_path or []:
                copyhardlinktree(path, new_rootfs)

            for orig_path in part.exclude_path or []:
                path = orig_path
                if os.path.isabs(path):
                    logger.error("Must be relative: --exclude-path=%s" % orig_path)
                    sys.exit(1)

                full_path = os.path.realpath(os.path.join(new_rootfs, path))

                # Disallow climbing outside of parent directory using '..',
                # because doing so could be quite disastrous (we will delete the
                # directory).
                if not full_path.startswith(new_rootfs):
                    logger.error("'%s' points to a path outside the rootfs" % orig_path)
                    sys.exit(1)

                if new_pseudo:
                    pseudo = cls.__get_pseudo(native_sysroot, new_rootfs, new_pseudo)
                else:
                    pseudo = None
                if path.endswith(os.sep):
                    # Delete content only.
                    for entry in os.listdir(full_path):
                        full_entry = os.path.join(full_path, entry)
                        rm_cmd = "rm -rf %s" % (full_entry)
                        exec_native_cmd(rm_cmd, native_sysroot, pseudo)
                else:
                    # Delete whole directory.
                    rm_cmd = "rm -rf %s" % (full_path)
                    exec_native_cmd(rm_cmd, native_sysroot, pseudo)

            # Update part.has_fstab here as fstab may have been added or
            # removed by the above modifications.
            part.has_fstab = os.path.exists(os.path.join(new_rootfs, "etc/fstab"))
            if part.update_fstab_in_rootfs and part.has_fstab:
                fstab_path = os.path.join(new_rootfs, "etc/fstab")
                # Assume that fstab should always be owned by root with fixed permissions
                install_cmd = "install -m 0644 %s %s" % (part.updated_fstab_path, fstab_path)
                if new_pseudo:
                    pseudo = cls.__get_pseudo(native_sysroot, new_rootfs, new_pseudo)
                else:
                    pseudo = None
                exec_native_cmd(install_cmd, native_sysroot, pseudo)

        part.prepare_rootfs(cr_workdir, oe_builddir,
                            new_rootfs or part.rootfs_dir, native_sysroot,
                            pseudo_dir = new_pseudo or pseudo_dir)
