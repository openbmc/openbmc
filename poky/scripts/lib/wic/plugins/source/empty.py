#
# SPDX-License-Identifier: MIT
#

# The empty wic plugin is used to create unformatted empty partitions for wic
# images.
# To use it you must pass "empty" as argument for the "--source" parameter in
# the wks file. For example:
# part foo --source empty --ondisk sda --size="1024" --align 1024

import logging

from wic.pluginbase import SourcePlugin

logger = logging.getLogger('wic')

class EmptyPartitionPlugin(SourcePlugin):
    """
    Populate unformatted empty partition.
    """

    name = 'empty'

    @classmethod
    def do_prepare_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        """
        return
