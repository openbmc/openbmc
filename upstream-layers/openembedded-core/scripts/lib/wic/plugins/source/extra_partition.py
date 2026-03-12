import logging
import os
import re

from glob import glob

from wic import WicError
from wic.pluginbase import SourcePlugin
from wic.misc import exec_cmd, get_bitbake_var

logger = logging.getLogger('wic')

class ExtraPartitionPlugin(SourcePlugin):
    """
    Populates an extra partition with files listed in the IMAGE_EXTRA_PARTITION_FILES
    BitBake variable. Files should be deployed to the DEPLOY_DIR_IMAGE directory.

    The plugin supports:
    - Glob pattern matching for file selection.
    - File renaming.
    - Suffixes to specify the target partition (by params-name, label, UUID,
      or partname), enabling multiple extra partitions to coexist.

    For example:

        IMAGE_EXTRA_PARTITION_FILES_name-randomname = "bar.conf;foo.conf"
        IMAGE_EXTRA_PARTITION_FILES_label-foo = "bar.conf;foo.conf"
        IMAGE_EXTRA_PARTITION_FILES_uuid-e7d0824e-cda3-4bed-9f54-9ef5312d105d = "bar.conf;foobar.conf"
        IMAGE_EXTRA_PARTITION_FILES = "foo/*"
        WICVARS:append = "\
            IMAGE_EXTRA_PARTITION_FILES_name-randomname \
            IMAGE_EXTRA_PARTITION_FILES_label-foo \
            IMAGE_EXTRA_PARTITION_FILES_uuid-e7d0824e-cda3-4bed-9f54-9ef5312d105d \
        "

    """

    name = 'extra_partition'
    image_extra_partition_files_var_name = 'IMAGE_EXTRA_PARTITION_FILES'

    @classmethod
    def do_configure_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             native_sysroot):
        """
        Called before do_prepare_partition(), list the files to copy
        """
        extradir = "%s/extra.%d" % (cr_workdir, part.lineno)
        install_cmd = "install -d %s" % extradir
        exec_cmd(install_cmd)

        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        extra_files = None
        for (fmt, part_id) in (
                ("_name-%s", source_params.get("name")),
                ("_uuid-%s", part.uuid),
                ("_label-%s", part.label),
                ("_part-name-%s", part.part_name),
                (None, None)
        ):
            if fmt is None:
                var = ""
            elif part_id is not None:
                var = fmt % part_id
            else:
                continue

            logger.debug("Looking for extra files in %s" % cls.image_extra_partition_files_var_name + var)
            extra_files = get_bitbake_var(cls.image_extra_partition_files_var_name + var)
            if extra_files is not None:
                break

        if extra_files is None:
            raise WicError('No extra files defined, %s unset for entry #%d' % (cls.image_extra_partition_files_var_name, part.lineno))

        logger.info('Extra files: %s', extra_files)

        # list of tuples (src_name, dst_name)
        deploy_files = []
        for src_entry in re.findall(r'[\w;\-\./\*]+', extra_files):
            if ';' in src_entry:
                dst_entry = tuple(src_entry.split(';'))
                if not dst_entry[0] or not dst_entry[1]:
                    raise WicError('Malformed extra file entry: %s' % src_entry)
            else:
                dst_entry = (src_entry, src_entry)

            logger.debug('Destination entry: %r', dst_entry)
            deploy_files.append(dst_entry)

        cls.install_task = [];
        for deploy_entry in deploy_files:
            src, dst = deploy_entry
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
                    src = os.path.relpath(entry, kernel_dir)
                    entry_dst_name = entry_name_fn(entry)
                    cls.install_task.append((src, entry_dst_name))
            else:
                cls.install_task.append((src, dst))


    @classmethod
    def do_prepare_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        In this case, we copies all files listed in IMAGE_EXTRA_PARTITION_FILES variable.
        """
        extradir = "%s/extra.%d" % (cr_workdir, part.lineno)

        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        for task in cls.install_task:
            src_path, dst_path = task
            logger.debug('Install %s as %s', src_path, dst_path)
            install_cmd = "install -m 0644 -D %s %s" \
                          % (os.path.join(kernel_dir, src_path),
                             os.path.join(extradir, dst_path))
            exec_cmd(install_cmd)

        logger.debug('Prepare extra partition using rootfs in %s', extradir)
        part.prepare_rootfs(cr_workdir, oe_builddir, extradir,
                            native_sysroot, False)

