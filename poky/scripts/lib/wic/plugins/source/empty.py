#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# The empty wic plugin is used to create unformatted empty partitions for wic
# images.
# To use it you must pass "empty" as argument for the "--source" parameter in
# the wks file. For example:
# part foo --source empty --ondisk sda --size="1024" --align 1024
#
# The plugin supports writing zeros to the start of the
# partition. This is useful to overwrite old content like
# filesystem signatures which may be re-recognized otherwise.
# This feature can be enabled with
# '--sourceparams="[fill|size=<N>[S|s|K|k|M|G]][,][bs=<N>[S|s|K|k|M|G]]"'
# Conflicting or missing options throw errors.

import logging
import os

from wic import WicError
from wic.ksparser import sizetype
from wic.pluginbase import SourcePlugin

logger = logging.getLogger('wic')

class EmptyPartitionPlugin(SourcePlugin):
    """
    Populate unformatted empty partition.

    The following sourceparams are supported:
    - fill
      Fill the entire partition with zeros. Requires '--fixed-size' option
      to be set.
    - size=<N>[S|s|K|k|M|G]
      Set the first N bytes of the partition to zero. Default unit is 'K'.
    - bs=<N>[S|s|K|k|M|G]
      Write at most N bytes at a time during source file creation.
      Defaults to '1M'. Default unit is 'K'.
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
        get_byte_count = sizetype('K', True)
        size = 0

        if 'fill' in source_params and 'size' in source_params:
            raise WicError("Conflicting source parameters 'fill' and 'size' specified, exiting.")

        # Set the size of the zeros to be written to the partition
        if 'fill' in source_params:
            if part.fixed_size == 0:
                raise WicError("Source parameter 'fill' only works with the '--fixed-size' option, exiting.")
            size = get_byte_count(part.fixed_size)
        elif 'size' in source_params:
            size = get_byte_count(source_params['size'])

        if size == 0:
            # Nothing to do, create empty partition
            return

        if 'bs' in source_params:
            bs = get_byte_count(source_params['bs'])
        else:
            bs = get_byte_count('1M')

        # Create a binary file of the requested size filled with zeros
        source_file = os.path.join(cr_workdir, 'empty-plugin-zeros%s.bin' % part.lineno)
        if not os.path.exists(os.path.dirname(source_file)):
            os.makedirs(os.path.dirname(source_file))

        quotient, remainder = divmod(size, bs)
        with open(source_file, 'wb') as file:
            for _ in range(quotient):
                file.write(bytearray(bs))
            file.write(bytearray(remainder))

        part.size = (size + 1024 - 1) // 1024  # size in KB rounded up
        part.source_file = source_file
