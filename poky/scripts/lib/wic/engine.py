#
# Copyright (c) 2013, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-only
#
# DESCRIPTION

# This module implements the image creation engine used by 'wic' to
# create images.  The engine parses through the OpenEmbedded kickstart
# (wks) file specified and generates images that can then be directly
# written onto media.
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#

import logging
import os
import tempfile
import json
import subprocess
import re

from collections import namedtuple, OrderedDict
from distutils.spawn import find_executable

from wic import WicError
from wic.filemap import sparse_copy
from wic.pluginbase import PluginMgr
from wic.misc import get_bitbake_var, exec_cmd

logger = logging.getLogger('wic')

def verify_build_env():
    """
    Verify that the build environment is sane.

    Returns True if it is, false otherwise
    """
    if not os.environ.get("BUILDDIR"):
        raise WicError("BUILDDIR not found, exiting. (Did you forget to source oe-init-build-env?)")

    return True


CANNED_IMAGE_DIR = "lib/wic/canned-wks" # relative to scripts
SCRIPTS_CANNED_IMAGE_DIR = "scripts/" + CANNED_IMAGE_DIR
WIC_DIR = "wic"

def build_canned_image_list(path):
    layers_path = get_bitbake_var("BBLAYERS")
    canned_wks_layer_dirs = []

    if layers_path is not None:
        for layer_path in layers_path.split():
            for wks_path in (WIC_DIR, SCRIPTS_CANNED_IMAGE_DIR):
                cpath = os.path.join(layer_path, wks_path)
                if os.path.isdir(cpath):
                    canned_wks_layer_dirs.append(cpath)

    cpath = os.path.join(path, CANNED_IMAGE_DIR)
    canned_wks_layer_dirs.append(cpath)

    return canned_wks_layer_dirs

def find_canned_image(scripts_path, wks_file):
    """
    Find a .wks file with the given name in the canned files dir.

    Return False if not found
    """
    layers_canned_wks_dir = build_canned_image_list(scripts_path)

    for canned_wks_dir in layers_canned_wks_dir:
        for root, dirs, files in os.walk(canned_wks_dir):
            for fname in files:
                if fname.endswith("~") or fname.endswith("#"):
                    continue
                if ((fname.endswith(".wks") and wks_file + ".wks" == fname) or \
                   (fname.endswith(".wks.in") and wks_file + ".wks.in" == fname)):
                    fullpath = os.path.join(canned_wks_dir, fname)
                    return fullpath
    return None


def list_canned_images(scripts_path):
    """
    List the .wks files in the canned image dir, minus the extension.
    """
    layers_canned_wks_dir = build_canned_image_list(scripts_path)

    for canned_wks_dir in layers_canned_wks_dir:
        for root, dirs, files in os.walk(canned_wks_dir):
            for fname in files:
                if fname.endswith("~") or fname.endswith("#"):
                    continue
                if fname.endswith(".wks") or fname.endswith(".wks.in"):
                    fullpath = os.path.join(canned_wks_dir, fname)
                    with open(fullpath) as wks:
                        for line in wks:
                            desc = ""
                            idx = line.find("short-description:")
                            if idx != -1:
                                desc = line[idx + len("short-description:"):].strip()
                                break
                    basename = fname.split('.')[0]
                    print("  %s\t\t%s" % (basename.ljust(30), desc))


def list_canned_image_help(scripts_path, fullpath):
    """
    List the help and params in the specified canned image.
    """
    found = False
    with open(fullpath) as wks:
        for line in wks:
            if not found:
                idx = line.find("long-description:")
                if idx != -1:
                    print()
                    print(line[idx + len("long-description:"):].strip())
                    found = True
                continue
            if not line.strip():
                break
            idx = line.find("#")
            if idx != -1:
                print(line[idx + len("#:"):].rstrip())
            else:
                break


def list_source_plugins():
    """
    List the available source plugins i.e. plugins available for --source.
    """
    plugins = PluginMgr.get_plugins('source')

    for plugin in plugins:
        print("  %s" % plugin)


def wic_create(wks_file, rootfs_dir, bootimg_dir, kernel_dir,
               native_sysroot, options):
    """
    Create image

    wks_file - user-defined OE kickstart file
    rootfs_dir - absolute path to the build's /rootfs dir
    bootimg_dir - absolute path to the build's boot artifacts directory
    kernel_dir - absolute path to the build's kernel directory
    native_sysroot - absolute path to the build's native sysroots dir
    image_output_dir - dirname to create for image
    options - wic command line options (debug, bmap, etc)

    Normally, the values for the build artifacts values are determined
    by 'wic -e' from the output of the 'bitbake -e' command given an
    image name e.g. 'core-image-minimal' and a given machine set in
    local.conf.  If that's the case, the variables get the following
    values from the output of 'bitbake -e':

    rootfs_dir:        IMAGE_ROOTFS
    kernel_dir:        DEPLOY_DIR_IMAGE
    native_sysroot:    STAGING_DIR_NATIVE

    In the above case, bootimg_dir remains unset and the
    plugin-specific image creation code is responsible for finding the
    bootimg artifacts.

    In the case where the values are passed in explicitly i.e 'wic -e'
    is not used but rather the individual 'wic' options are used to
    explicitly specify these values.
    """
    try:
        oe_builddir = os.environ["BUILDDIR"]
    except KeyError:
        raise WicError("BUILDDIR not found, exiting. (Did you forget to source oe-init-build-env?)")

    if not os.path.exists(options.outdir):
        os.makedirs(options.outdir)

    pname = options.imager
    plugin_class = PluginMgr.get_plugins('imager').get(pname)
    if not plugin_class:
        raise WicError('Unknown plugin: %s' % pname)

    plugin = plugin_class(wks_file, rootfs_dir, bootimg_dir, kernel_dir,
                          native_sysroot, oe_builddir, options)

    plugin.do_create()

    logger.info("The image(s) were created using OE kickstart file:\n  %s", wks_file)


def wic_list(args, scripts_path):
    """
    Print the list of images or source plugins.
    """
    if args.list_type is None:
        return False

    if args.list_type == "images":

        list_canned_images(scripts_path)
        return True
    elif args.list_type == "source-plugins":
        list_source_plugins()
        return True
    elif len(args.help_for) == 1 and args.help_for[0] == 'help':
        wks_file = args.list_type
        fullpath = find_canned_image(scripts_path, wks_file)
        if not fullpath:
            raise WicError("No image named %s found, exiting. "
                           "(Use 'wic list images' to list available images, "
                           "or specify a fully-qualified OE kickstart (.wks) "
                           "filename)" % wks_file)

        list_canned_image_help(scripts_path, fullpath)
        return True

    return False


class Disk:
    def __init__(self, imagepath, native_sysroot, fstypes=('fat', 'ext')):
        self.imagepath = imagepath
        self.native_sysroot = native_sysroot
        self.fstypes = fstypes
        self._partitions = None
        self._partimages = {}
        self._lsector_size = None
        self._psector_size = None
        self._ptable_format = None

        # find parted
        # read paths from $PATH environment variable
        # if it fails, use hardcoded paths
        pathlist = "/bin:/usr/bin:/usr/sbin:/sbin/"
        try:
            self.paths = os.environ['PATH'] + ":" + pathlist
        except KeyError:
            self.paths = pathlist

        if native_sysroot:
            for path in pathlist.split(':'):
                self.paths = "%s%s:%s" % (native_sysroot, path, self.paths)

        self.parted = find_executable("parted", self.paths)
        if not self.parted:
            raise WicError("Can't find executable parted")

        self.partitions = self.get_partitions()

    def __del__(self):
        for path in self._partimages.values():
            os.unlink(path)

    def get_partitions(self):
        if self._partitions is None:
            self._partitions = OrderedDict()
            out = exec_cmd("%s -sm %s unit B print" % (self.parted, self.imagepath))
            parttype = namedtuple("Part", "pnum start end size fstype")
            splitted = out.splitlines()
            # skip over possible errors in exec_cmd output
            try:
                idx =splitted.index("BYT;")
            except ValueError:
                raise WicError("Error getting partition information from %s" % (self.parted))
            lsector_size, psector_size, self._ptable_format = splitted[idx + 1].split(":")[3:6]
            self._lsector_size = int(lsector_size)
            self._psector_size = int(psector_size)
            for line in splitted[idx + 2:]:
                pnum, start, end, size, fstype = line.split(':')[:5]
                partition = parttype(int(pnum), int(start[:-1]), int(end[:-1]),
                                     int(size[:-1]), fstype)
                self._partitions[pnum] = partition

        return self._partitions

    def __getattr__(self, name):
        """Get path to the executable in a lazy way."""
        if name in ("mdir", "mcopy", "mdel", "mdeltree", "sfdisk", "e2fsck",
                    "resize2fs", "mkswap", "mkdosfs", "debugfs"):
            aname = "_%s" % name
            if aname not in self.__dict__:
                setattr(self, aname, find_executable(name, self.paths))
                if aname not in self.__dict__ or self.__dict__[aname] is None:
                    raise WicError("Can't find executable '{}'".format(name))
            return self.__dict__[aname]
        return self.__dict__[name]

    def _get_part_image(self, pnum):
        if pnum not in self.partitions:
            raise WicError("Partition %s is not in the image" % pnum)
        part = self.partitions[pnum]
        # check if fstype is supported
        for fstype in self.fstypes:
            if part.fstype.startswith(fstype):
                break
        else:
            raise WicError("Not supported fstype: {}".format(part.fstype))
        if pnum not in self._partimages:
            tmpf = tempfile.NamedTemporaryFile(prefix="wic-part")
            dst_fname = tmpf.name
            tmpf.close()
            sparse_copy(self.imagepath, dst_fname, skip=part.start, length=part.size)
            self._partimages[pnum] = dst_fname

        return self._partimages[pnum]

    def _put_part_image(self, pnum):
        """Put partition image into partitioned image."""
        sparse_copy(self._partimages[pnum], self.imagepath,
                    seek=self.partitions[pnum].start)

    def dir(self, pnum, path):
        if pnum not in self.partitions:
            raise WicError("Partition %s is not in the image" % pnum)

        if self.partitions[pnum].fstype.startswith('ext'):
            return exec_cmd("{} {} -R 'ls -l {}'".format(self.debugfs,
                                                         self._get_part_image(pnum),
                                                         path), as_shell=True)
        else: # fat
            return exec_cmd("{} -i {} ::{}".format(self.mdir,
                                                   self._get_part_image(pnum),
                                                   path))

    def copy(self, src, dest):
        """Copy partition image into wic image."""
        pnum =  dest.part if isinstance(src, str) else src.part

        if self.partitions[pnum].fstype.startswith('ext'):
            if isinstance(src, str):
                cmd = "printf 'cd {}\nwrite {} {}\n' | {} -w {}".\
                      format(os.path.dirname(dest.path), src, os.path.basename(src),
                             self.debugfs, self._get_part_image(pnum))
            else: # copy from wic
                # run both dump and rdump to support both files and directory
                cmd = "printf 'cd {}\ndump /{} {}\nrdump /{} {}\n' | {} {}".\
                      format(os.path.dirname(src.path), src.path,
                             dest, src.path, dest, self.debugfs,
                             self._get_part_image(pnum))
        else: # fat
            if isinstance(src, str):
                cmd = "{} -i {} -snop {} ::{}".format(self.mcopy,
                                                  self._get_part_image(pnum),
                                                  src, dest.path)
            else:
                cmd = "{} -i {} -snop ::{} {}".format(self.mcopy,
                                                  self._get_part_image(pnum),
                                                  src.path, dest)

        exec_cmd(cmd, as_shell=True)
        self._put_part_image(pnum)

    def remove_ext(self, pnum, path, recursive):
        """
        Remove files/dirs and their contents from the partition.
        This only applies to ext* partition.
        """
        abs_path = re.sub('\/\/+', '/', path)
        cmd = "{} {} -wR 'rm \"{}\"'".format(self.debugfs,
                                            self._get_part_image(pnum),
                                            abs_path)
        out = exec_cmd(cmd , as_shell=True)
        for line in out.splitlines():
            if line.startswith("rm:"):
                if "file is a directory" in line:
                    if recursive:
                        # loop through content and delete them one by one if
                        # flaged with -r
                        subdirs = iter(self.dir(pnum, abs_path).splitlines())
                        next(subdirs)
                        for subdir in subdirs:
                            dir = subdir.split(':')[1].split(" ", 1)[1]
                            if not dir == "." and not dir == "..":
                                self.remove_ext(pnum, "%s/%s" % (abs_path, dir), recursive)

                    rmdir_out = exec_cmd("{} {} -wR 'rmdir \"{}\"'".format(self.debugfs,
                                                    self._get_part_image(pnum),
                                                    abs_path.rstrip('/'))
                                                    , as_shell=True)

                    for rmdir_line in rmdir_out.splitlines():
                        if "directory not empty" in rmdir_line:
                            raise WicError("Could not complete operation: \n%s \n"
                                            "use -r to remove non-empty directory" % rmdir_line)
                        if rmdir_line.startswith("rmdir:"):
                            raise WicError("Could not complete operation: \n%s "
                                            "\n%s" % (str(line), rmdir_line))

                else:
                    raise WicError("Could not complete operation: \n%s "
                                    "\nUnable to remove %s" % (str(line), abs_path))

    def remove(self, pnum, path, recursive):
        """Remove files/dirs from the partition."""
        partimg = self._get_part_image(pnum)
        if self.partitions[pnum].fstype.startswith('ext'):
            self.remove_ext(pnum, path, recursive)

        else: # fat
            cmd = "{} -i {} ::{}".format(self.mdel, partimg, path)
            try:
                exec_cmd(cmd)
            except WicError as err:
                if "not found" in str(err) or "non empty" in str(err):
                    # mdel outputs 'File ... not found' or 'directory .. non empty"
                    # try to use mdeltree as path could be a directory
                    cmd = "{} -i {} ::{}".format(self.mdeltree,
                                                 partimg, path)
                    exec_cmd(cmd)
                else:
                    raise err
        self._put_part_image(pnum)

    def write(self, target, expand):
        """Write disk image to the media or file."""
        def write_sfdisk_script(outf, parts):
            for key, val in parts['partitiontable'].items():
                if key in ("partitions", "device", "firstlba", "lastlba"):
                    continue
                if key == "id":
                    key = "label-id"
                outf.write("{}: {}\n".format(key, val))
            outf.write("\n")
            for part in parts['partitiontable']['partitions']:
                line = ''
                for name in ('attrs', 'name', 'size', 'type', 'uuid'):
                    if name == 'size' and part['type'] == 'f':
                        # don't write size for extended partition
                        continue
                    val = part.get(name)
                    if val:
                        line += '{}={}, '.format(name, val)
                if line:
                    line = line[:-2] # strip ', '
                if part.get('bootable'):
                    line += ' ,bootable'
                outf.write("{}\n".format(line))
            outf.flush()

        def read_ptable(path):
            out = exec_cmd("{} -J {}".format(self.sfdisk, path))
            return json.loads(out)

        def write_ptable(parts, target):
            with tempfile.NamedTemporaryFile(prefix="wic-sfdisk-", mode='w') as outf:
                write_sfdisk_script(outf, parts)
                cmd = "{} --no-reread {} < {} ".format(self.sfdisk, target, outf.name)
                exec_cmd(cmd, as_shell=True)

        if expand is None:
            sparse_copy(self.imagepath, target)
        else:
            # copy first sectors that may contain bootloader
            sparse_copy(self.imagepath, target, length=2048 * self._lsector_size)

            # copy source partition table to the target
            parts = read_ptable(self.imagepath)
            write_ptable(parts, target)

            # get size of unpartitioned space
            free = None
            for line in exec_cmd("{} -F {}".format(self.sfdisk, target)).splitlines():
                if line.startswith("Unpartitioned space ") and line.endswith("sectors"):
                    free = int(line.split()[-2])
                    # Align free space to a 2048 sector boundary. YOCTO #12840.
                    free = free - (free % 2048)
            if free is None:
                raise WicError("Can't get size of unpartitioned space")

            # calculate expanded partitions sizes
            sizes = {}
            num_auto_resize = 0
            for num, part in enumerate(parts['partitiontable']['partitions'], 1):
                if num in expand:
                    if expand[num] != 0: # don't resize partition if size is set to 0
                        sectors = expand[num] // self._lsector_size
                        free -= sectors - part['size']
                        part['size'] = sectors
                        sizes[num] = sectors
                elif part['type'] != 'f':
                    sizes[num] = -1
                    num_auto_resize += 1

            for num, part in enumerate(parts['partitiontable']['partitions'], 1):
                if sizes.get(num) == -1:
                    part['size'] += free // num_auto_resize

            # write resized partition table to the target
            write_ptable(parts, target)

            # read resized partition table
            parts = read_ptable(target)

            # copy partitions content
            for num, part in enumerate(parts['partitiontable']['partitions'], 1):
                pnum = str(num)
                fstype = self.partitions[pnum].fstype

                # copy unchanged partition
                if part['size'] == self.partitions[pnum].size // self._lsector_size:
                    logger.info("copying unchanged partition {}".format(pnum))
                    sparse_copy(self._get_part_image(pnum), target, seek=part['start'] * self._lsector_size)
                    continue

                # resize or re-create partitions
                if fstype.startswith('ext') or fstype.startswith('fat') or \
                   fstype.startswith('linux-swap'):

                    partfname = None
                    with tempfile.NamedTemporaryFile(prefix="wic-part{}-".format(pnum)) as partf:
                        partfname = partf.name

                    if fstype.startswith('ext'):
                        logger.info("resizing ext partition {}".format(pnum))
                        partimg = self._get_part_image(pnum)
                        sparse_copy(partimg, partfname)
                        exec_cmd("{} -pf {}".format(self.e2fsck, partfname))
                        exec_cmd("{} {} {}s".format(\
                                 self.resize2fs, partfname, part['size']))
                    elif fstype.startswith('fat'):
                        logger.info("copying content of the fat partition {}".format(pnum))
                        with tempfile.TemporaryDirectory(prefix='wic-fatdir-') as tmpdir:
                            # copy content to the temporary directory
                            cmd = "{} -snompi {} :: {}".format(self.mcopy,
                                                               self._get_part_image(pnum),
                                                               tmpdir)
                            exec_cmd(cmd)
                            # create new msdos partition
                            label = part.get("name")
                            label_str = "-n {}".format(label) if label else ''

                            cmd = "{} {} -C {} {}".format(self.mkdosfs, label_str, partfname,
                                                          part['size'])
                            exec_cmd(cmd)
                            # copy content from the temporary directory to the new partition
                            cmd = "{} -snompi {} {}/* ::".format(self.mcopy, partfname, tmpdir)
                            exec_cmd(cmd, as_shell=True)
                    elif fstype.startswith('linux-swap'):
                        logger.info("creating swap partition {}".format(pnum))
                        label = part.get("name")
                        label_str = "-L {}".format(label) if label else ''
                        uuid = part.get("uuid")
                        uuid_str = "-U {}".format(uuid) if uuid else ''
                        with open(partfname, 'w') as sparse:
                            os.ftruncate(sparse.fileno(), part['size'] * self._lsector_size)
                        exec_cmd("{} {} {} {}".format(self.mkswap, label_str, uuid_str, partfname))
                    sparse_copy(partfname, target, seek=part['start'] * self._lsector_size)
                    os.unlink(partfname)
                elif part['type'] != 'f':
                    logger.warning("skipping partition {}: unsupported fstype {}".format(pnum, fstype))

def wic_ls(args, native_sysroot):
    """List contents of partitioned image or vfat partition."""
    disk = Disk(args.path.image, native_sysroot)
    if not args.path.part:
        if disk.partitions:
            print('Num     Start        End          Size      Fstype')
            for part in disk.partitions.values():
                print("{:2d}  {:12d} {:12d} {:12d}  {}".format(\
                          part.pnum, part.start, part.end,
                          part.size, part.fstype))
    else:
        path = args.path.path or '/'
        print(disk.dir(args.path.part, path))

def wic_cp(args, native_sysroot):
    """
    Copy file or directory to/from the vfat/ext partition of
    partitioned image.
    """
    if isinstance(args.dest, str):
        disk = Disk(args.src.image, native_sysroot)
    else:
        disk = Disk(args.dest.image, native_sysroot)
    disk.copy(args.src, args.dest)


def wic_rm(args, native_sysroot):
    """
    Remove files or directories from the vfat partition of
    partitioned image.
    """
    disk = Disk(args.path.image, native_sysroot)
    disk.remove(args.path.part, args.path.path, args.recursive_delete)

def wic_write(args, native_sysroot):
    """
    Write image to a target device.
    """
    disk = Disk(args.image, native_sysroot, ('fat', 'ext', 'linux-swap'))
    disk.write(args.target, args.expand)

def find_canned(scripts_path, file_name):
    """
    Find a file either by its path or by name in the canned files dir.

    Return None if not found
    """
    if os.path.exists(file_name):
        return file_name

    layers_canned_wks_dir = build_canned_image_list(scripts_path)
    for canned_wks_dir in layers_canned_wks_dir:
        for root, dirs, files in os.walk(canned_wks_dir):
            for fname in files:
                if fname == file_name:
                    fullpath = os.path.join(canned_wks_dir, fname)
                    return fullpath

def get_custom_config(boot_file):
    """
    Get the custom configuration to be used for the bootloader.

    Return None if the file can't be found.
    """
    # Get the scripts path of poky
    scripts_path = os.path.abspath("%s/../.." % os.path.dirname(__file__))

    cfg_file = find_canned(scripts_path, boot_file)
    if cfg_file:
        with open(cfg_file, "r") as f:
            config = f.read()
        return config
