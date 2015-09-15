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

import os

from wic import msger
from wic.pluginbase import SourcePlugin
from wic.utils.oe.misc import get_bitbake_var

class FSImagePlugin(SourcePlugin):
    """
    Add an already existing filesystem image to the partition layout.
    """

    name = 'fsimage'

    @classmethod
    def do_install_disk(cls, disk, disk_name, cr, workdir, oe_builddir,
                        bootimg_dir, kernel_dir, native_sysroot):
        """
        Called after all partitions have been prepared and assembled into a
        disk image. Do nothing.
        """
        pass

    @classmethod
    def do_configure_partition(cls, part, source_params, cr, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        """
        Called before do_prepare_partition(). Possibly prepare
        configuration files of some sort.
        """
        pass

    @classmethod
    def do_prepare_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        """
        if not bootimg_dir:
            bootimg_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not bootimg_dir:
                msger.error("Couldn't find DEPLOY_DIR_IMAGE, exiting\n")

        msger.debug('Bootimg dir: %s' % bootimg_dir)

        if 'file' not in source_params:
            msger.error("No file specified\n")
            return

        src = os.path.join(bootimg_dir, source_params['file'])


        msger.debug('Preparing partition using image %s' % (src))
        part.prepare_rootfs_from_fs_image(cr_workdir, src, "")
