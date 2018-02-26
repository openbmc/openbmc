# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
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
# This implements the 'bootimg-partition' source plugin class for
# 'wic'. The plugin creates an image of boot partition, copying over
# files listed in IMAGE_BOOT_FILES bitbake variable.
#
# AUTHORS
# Maciej Borzecki <maciej.borzecki (at] open-rnd.pl>
#

import logging
import os
import re

from glob import glob

from wic import WicError
from wic.pluginbase import SourcePlugin
from wic.misc import exec_cmd, get_bitbake_var

logger = logging.getLogger('wic')

class BootimgPartitionPlugin(SourcePlugin):
    """
    Create an image of boot partition, copying over files
    listed in IMAGE_BOOT_FILES bitbake variable.
    """

    name = 'bootimg-partition'

    @classmethod
    def do_prepare_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        In this case, does the following:
        - sets up a vfat partition
        - copies all files listed in IMAGE_BOOT_FILES variable
        """
        hdddir = "%s/boot.%d" % (cr_workdir, part.lineno)
        install_cmd = "install -d %s" % hdddir
        exec_cmd(install_cmd)

        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        logger.debug('Kernel dir: %s', bootimg_dir)

        boot_files = None
        for (fmt, id) in (("_uuid-%s", part.uuid), ("_label-%s", part.label), (None, None)):
            if fmt:
                var = fmt % id
            else:
                var = ""

            boot_files = get_bitbake_var("IMAGE_BOOT_FILES" + var)
            if boot_files is not None:
                break

        if boot_files is None:
            raise WicError('No boot files defined, IMAGE_BOOT_FILES unset for entry #%d' % part.lineno)

        logger.debug('Boot files: %s', boot_files)

        # list of tuples (src_name, dst_name)
        deploy_files = []
        for src_entry in re.findall(r'[\w;\-\./\*]+', boot_files):
            if ';' in src_entry:
                dst_entry = tuple(src_entry.split(';'))
                if not dst_entry[0] or not dst_entry[1]:
                    raise WicError('Malformed boot file entry: %s' % src_entry)
            else:
                dst_entry = (src_entry, src_entry)

            logger.debug('Destination entry: %r', dst_entry)
            deploy_files.append(dst_entry)

        for deploy_entry in deploy_files:
            src, dst = deploy_entry
            install_task = []
            if '*' in src:
                # by default install files under their basename
                entry_name_fn = os.path.basename
                if dst != src:
                    # unless a target name was given, then treat name
                    # as a directory and append a basename
                    entry_name_fn = lambda name: \
                                    os.path.join(dst,
                                                 os.path.basename(name))

                srcs = glob(os.path.join(kernel_dir, src))

                logger.debug('Globbed sources: %s', ', '.join(srcs))
                for entry in srcs:
                    entry_dst_name = entry_name_fn(entry)
                    install_task.append((entry,
                                         os.path.join(hdddir,
                                                      entry_dst_name)))
            else:
                install_task = [(os.path.join(kernel_dir, src),
                                 os.path.join(hdddir, dst))]

            for task in install_task:
                src_path, dst_path = task
                logger.debug('Install %s as %s',
                             os.path.basename(src_path), dst_path)
                install_cmd = "install -m 0644 -D %s %s" \
                              % (src_path, dst_path)
                exec_cmd(install_cmd)

        logger.debug('Prepare boot partition using rootfs in %s', hdddir)
        part.prepare_rootfs(cr_workdir, oe_builddir, hdddir,
                            native_sysroot)
