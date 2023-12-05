#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import os
import signal
import subprocess

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
        # don't create label when fstype is none
        if fstype == 'none':
            return

        if fstype.startswith('ext'):
            cmd = 'tune2fs -L %s %s' % (label, dst)
        elif fstype in ('msdos', 'vfat'):
            cmd = 'dosfslabel %s %s' % (dst, label)
        elif fstype == 'btrfs':
            cmd = 'btrfs filesystem label %s %s' % (dst, label)
        elif fstype == 'swap':
            cmd = 'mkswap -L %s %s' % (label, dst)
        elif fstype in ('squashfs', 'erofs'):
            raise WicError("It's not possible to update a %s "
                           "filesystem label '%s'" % (fstype, label))
        else:
            raise WicError("Cannot update filesystem label: "
                           "Unknown fstype: '%s'" % (fstype))

        exec_cmd(cmd)

    @staticmethod
    def do_image_uncompression(src, dst, workdir):
        def subprocess_setup():
            # Python installs a SIGPIPE handler by default. This is usually not what
            # non-Python subprocesses expect.
            # SIGPIPE errors are known issues with gzip/bash
            signal.signal(signal.SIGPIPE, signal.SIG_DFL)

        extension = os.path.splitext(src)[1]
        decompressor = {
            ".bz2": "bzip2",
            ".gz": "gzip",
            ".xz": "xz",
            ".zst": "zstd -f",
        }.get(extension)
        if not decompressor:
            raise WicError("Not supported compressor filename extension: %s" % extension)
        cmd = "%s -dc %s > %s" % (decompressor, src, dst)
        subprocess.call(cmd, preexec_fn=subprocess_setup, shell=True, cwd=workdir)

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

        if 'unpack' in source_params:
            img = os.path.join(kernel_dir, source_params['file'])
            src = os.path.join(cr_workdir, os.path.splitext(source_params['file'])[0])
            RawCopyPlugin.do_image_uncompression(img, src, cr_workdir)
        else:
            src = os.path.join(kernel_dir, source_params['file'])

        dst = os.path.join(cr_workdir, "%s.%s" % (os.path.basename(source_params['file']), part.lineno))

        if not os.path.exists(os.path.dirname(dst)):
            os.makedirs(os.path.dirname(dst))

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
