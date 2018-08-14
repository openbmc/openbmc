# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (c) 2014, Intel Corporation.
# All rights reserved.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
from wic.misc import get_bitbake_var

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

        new_rootfs = None
        # Handle excluded paths.
        if part.exclude_path is not None:
            # We need a new rootfs directory we can delete files from. Copy to
            # workdir.
            new_rootfs = os.path.realpath(os.path.join(cr_workdir, "rootfs%d" % part.lineno))

            if os.path.lexists(new_rootfs):
                shutil.rmtree(os.path.join(new_rootfs))

            copyhardlinktree(part.rootfs_dir, new_rootfs)

            for orig_path in part.exclude_path:
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

                if path.endswith(os.sep):
                    # Delete content only.
                    for entry in os.listdir(full_path):
                        full_entry = os.path.join(full_path, entry)
                        if os.path.isdir(full_entry) and not os.path.islink(full_entry):
                            shutil.rmtree(full_entry)
                        else:
                            os.remove(full_entry)
                else:
                    # Delete whole directory.
                    shutil.rmtree(full_path)

        part.prepare_rootfs(cr_workdir, oe_builddir,
                            new_rootfs or part.rootfs_dir, native_sysroot)
