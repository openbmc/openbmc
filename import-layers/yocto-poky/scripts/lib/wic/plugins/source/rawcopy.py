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

import logging
import os

from wic import WicError
from wic.pluginbase import SourcePlugin
from wic.misc import exec_cmd, get_bitbake_var
from wic.filemap import sparse_copy

logger = logging.getLogger('wic')

class RawCopyPlugin(SourcePlugin):
    """
    Populate partition content from raw image file.
    """

    name = 'rawcopy'

    @staticmethod
    def do_image_label(fstype, dst, label):
        if fstype.startswith('ext'):
            cmd = 'tune2fs -L %s %s' % (label, dst)
        elif fstype in ('msdos', 'vfat'):
            cmd = 'dosfslabel %s %s' % (dst, label)
        elif fstype == 'btrfs':
            cmd = 'btrfs filesystem label %s %s' % (dst, label)
        elif fstype == 'swap':
            cmd = 'mkswap -L %s %s' % (label, dst)
        elif fstype == 'squashfs':
            raise WicError("It's not possible to update a squashfs "
                           "filesystem label '%s'" % (label))
        else:
            raise WicError("Cannot update filesystem label: "
                           "Unknown fstype: '%s'" % (fstype))

        exec_cmd(cmd)

    @classmethod
    def do_prepare_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        """
        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        logger.debug('Kernel dir: %s', kernel_dir)

        if 'file' not in source_params:
            raise WicError("No file specified")

        src = os.path.join(kernel_dir, source_params['file'])
        dst = os.path.join(cr_workdir, "%s.%s" % (source_params['file'], part.lineno))

        if 'skip' in source_params:
            sparse_copy(src, dst, skip=int(source_params['skip']))
        else:
            sparse_copy(src, dst)

        # get the size in the right units for kickstart (kB)
        du_cmd = "du -Lbks %s" % dst
        out = exec_cmd(du_cmd)
        filesize = int(out.split()[0])

        if filesize > part.size:
            part.size = filesize

        if part.label:
            RawCopyPlugin.do_image_label(part.fstype, dst, part.label)

        part.source_file = dst
