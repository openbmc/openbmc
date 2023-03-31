#
# Copyright (c) 2012 Intel, Inc.
#
# SPDX-License-Identifier: GPL-2.0-only
#

"""
This module implements python implements a way to get file block. Two methods
are supported - the FIEMAP ioctl and the 'SEEK_HOLE / SEEK_DATA' features of
the file seek syscall. The former is implemented by the 'FilemapFiemap' class,
the latter is implemented by the 'FilemapSeek' class. Both classes provide the
same API. The 'filemap' function automatically selects which class can be used
and returns an instance of the class.
"""

# Disable the following pylint recommendations:
#   * Too many instance attributes (R0902)
# pylint: disable=R0902

import errno
import os
import struct
import array
import fcntl
import tempfile
import logging

def get_block_size(file_obj):
    """
    Returns block size for file object 'file_obj'. Errors are indicated by the
    'IOError' exception.
    """
    # Get the block size of the host file-system for the image file by calling
    # the FIGETBSZ ioctl (number 2).
    try:
        binary_data = fcntl.ioctl(file_obj, 2, struct.pack('I', 0))
        bsize = struct.unpack('I', binary_data)[0]
    except OSError:
        bsize = None

    # If ioctl causes OSError or give bsize to zero failback to os.fstat
    if not bsize:
        import os
        stat = os.fstat(file_obj.fileno())
        if hasattr(stat, 'st_blksize'):
            bsize = stat.st_blksize
        else:
            raise IOError("Unable to determine block size")

    # The logic in this script only supports a maximum of a 4KB
    # block size
    max_block_size = 4 * 1024
    if bsize > max_block_size:
        bsize = max_block_size

    return bsize

class ErrorNotSupp(Exception):
    """
    An exception of this type is raised when the 'FIEMAP' or 'SEEK_HOLE' feature
    is not supported either by the kernel or the file-system.
    """
    pass

class Error(Exception):
    """A class for all the other exceptions raised by this module."""
    pass


class _FilemapBase(object):
    """
    This is a base class for a couple of other classes in this module. This
    class simply performs the common parts of the initialization process: opens
    the image file, gets its size, etc. The 'log' parameter is the logger object
    to use for printing messages.
    """

    def __init__(self, image, log=None):
        """
        Initialize a class instance. The 'image' argument is full path to the
        file or file object to operate on.
        """

        self._log = log
        if self._log is None:
            self._log = logging.getLogger(__name__)

        self._f_image_needs_close = False

        if hasattr(image, "fileno"):
            self._f_image = image
            self._image_path = image.name
        else:
            self._image_path = image
            self._open_image_file()

        try:
            self.image_size = os.fstat(self._f_image.fileno()).st_size
        except IOError as err:
            raise Error("cannot get information about file '%s': %s"
                        % (self._f_image.name, err))

        try:
            self.block_size = get_block_size(self._f_image)
        except IOError as err:
            raise Error("cannot get block size for '%s': %s"
                        % (self._image_path, err))

        self.blocks_cnt = self.image_size + self.block_size - 1
        self.blocks_cnt //= self.block_size

        try:
            self._f_image.flush()
        except IOError as err:
            raise Error("cannot flush image file '%s': %s"
                        % (self._image_path, err))

        try:
            os.fsync(self._f_image.fileno()),
        except OSError as err:
            raise Error("cannot synchronize image file '%s': %s "
                        % (self._image_path, err.strerror))

        self._log.debug("opened image \"%s\"" % self._image_path)
        self._log.debug("block size %d, blocks count %d, image size %d"
                        % (self.block_size, self.blocks_cnt, self.image_size))

    def __del__(self):
        """The class destructor which just closes the image file."""
        if self._f_image_needs_close:
            self._f_image.close()

    def _open_image_file(self):
        """Open the image file."""
        try:
            self._f_image = open(self._image_path, 'rb')
        except IOError as err:
            raise Error("cannot open image file '%s': %s"
                        % (self._image_path, err))

        self._f_image_needs_close = True

    def block_is_mapped(self, block): # pylint: disable=W0613,R0201
        """
        This method has has to be implemented by child classes. It returns
        'True' if block number 'block' of the image file is mapped and 'False'
        otherwise.
        """

        raise Error("the method is not implemented")

    def get_mapped_ranges(self, start, count): # pylint: disable=W0613,R0201
        """
        This method has has to be implemented by child classes. This is a
        generator which yields ranges of mapped blocks in the file. The ranges
        are tuples of 2 elements: [first, last], where 'first' is the first
        mapped block and 'last' is the last mapped block.

        The ranges are yielded for the area of the file of size 'count' blocks,
        starting from block 'start'.
        """

        raise Error("the method is not implemented")


# The 'SEEK_HOLE' and 'SEEK_DATA' options of the file seek system call
_SEEK_DATA = 3
_SEEK_HOLE = 4

def _lseek(file_obj, offset, whence):
    """This is a helper function which invokes 'os.lseek' for file object
    'file_obj' and with specified 'offset' and 'whence'. The 'whence'
    argument is supposed to be either '_SEEK_DATA' or '_SEEK_HOLE'. When
    there is no more data or hole starting from 'offset', this function
    returns '-1'.  Otherwise the data or hole position is returned."""

    try:
        return os.lseek(file_obj.fileno(), offset, whence)
    except OSError as err:
        # The 'lseek' system call returns the ENXIO if there is no data or
        # hole starting from the specified offset.
        if err.errno == errno.ENXIO:
            return -1
        elif err.errno == errno.EINVAL:
            raise ErrorNotSupp("the kernel or file-system does not support "
                               "\"SEEK_HOLE\" and \"SEEK_DATA\"")
        else:
            raise

class FilemapSeek(_FilemapBase):
    """
    This class uses the 'SEEK_HOLE' and 'SEEK_DATA' to find file block mapping.
    Unfortunately, the current implementation requires the caller to have write
    access to the image file.
    """

    def __init__(self, image, log=None):
        """Refer the '_FilemapBase' class for the documentation."""

        # Call the base class constructor first
        _FilemapBase.__init__(self, image, log)
        self._log.debug("FilemapSeek: initializing")

        self._probe_seek_hole()

    def _probe_seek_hole(self):
        """
        Check whether the system implements 'SEEK_HOLE' and 'SEEK_DATA'.
        Unfortunately, there seems to be no clean way for detecting this,
        because often the system just fakes them by just assuming that all
        files are fully mapped, so 'SEEK_HOLE' always returns EOF and
        'SEEK_DATA' always returns the requested offset.

        I could not invent a better way of detecting the fake 'SEEK_HOLE'
        implementation than just to create a temporary file in the same
        directory where the image file resides. It would be nice to change this
        to something better.
        """

        directory = os.path.dirname(self._image_path)

        try:
            tmp_obj = tempfile.TemporaryFile("w+", dir=directory)
        except IOError as err:
            raise ErrorNotSupp("cannot create a temporary in \"%s\": %s" \
                              % (directory, err))

        try:
            os.ftruncate(tmp_obj.fileno(), self.block_size)
        except OSError as err:
            raise ErrorNotSupp("cannot truncate temporary file in \"%s\": %s"
                               % (directory, err))

        offs = _lseek(tmp_obj, 0, _SEEK_HOLE)
        if offs != 0:
            # We are dealing with the stub 'SEEK_HOLE' implementation which
            # always returns EOF.
            self._log.debug("lseek(0, SEEK_HOLE) returned %d" % offs)
            raise ErrorNotSupp("the file-system does not support "
                               "\"SEEK_HOLE\" and \"SEEK_DATA\" but only "
                               "provides a stub implementation")

        tmp_obj.close()

    def block_is_mapped(self, block):
        """Refer the '_FilemapBase' class for the documentation."""
        offs = _lseek(self._f_image, block * self.block_size, _SEEK_DATA)
        if offs == -1:
            result = False
        else:
            result = (offs // self.block_size == block)

        self._log.debug("FilemapSeek: block_is_mapped(%d) returns %s"
                        % (block, result))
        return result

    def _get_ranges(self, start, count, whence1, whence2):
        """
        This function implements 'get_mapped_ranges()' depending
        on what is passed in the 'whence1' and 'whence2' arguments.
        """

        assert whence1 != whence2
        end = start * self.block_size
        limit = end + count * self.block_size

        while True:
            start = _lseek(self._f_image, end, whence1)
            if start == -1 or start >= limit or start == self.image_size:
                break

            end = _lseek(self._f_image, start, whence2)
            if end == -1 or end == self.image_size:
                end = self.blocks_cnt * self.block_size
            if end > limit:
                end = limit

            start_blk = start // self.block_size
            end_blk = end // self.block_size - 1
            self._log.debug("FilemapSeek: yielding range (%d, %d)"
                            % (start_blk, end_blk))
            yield (start_blk, end_blk)

    def get_mapped_ranges(self, start, count):
        """Refer the '_FilemapBase' class for the documentation."""
        self._log.debug("FilemapSeek: get_mapped_ranges(%d,  %d(%d))"
                        % (start, count, start + count - 1))
        return self._get_ranges(start, count, _SEEK_DATA, _SEEK_HOLE)


# Below goes the FIEMAP ioctl implementation, which is not very readable
# because it deals with the rather complex FIEMAP ioctl. To understand the
# code, you need to know the FIEMAP interface, which is documented in the
# "Documentation/filesystems/fiemap.txt" file in the Linux kernel sources.

# Format string for 'struct fiemap'
_FIEMAP_FORMAT = "=QQLLLL"
# sizeof(struct fiemap)
_FIEMAP_SIZE = struct.calcsize(_FIEMAP_FORMAT)
# Format string for 'struct fiemap_extent'
_FIEMAP_EXTENT_FORMAT = "=QQQQQLLLL"
# sizeof(struct fiemap_extent)
_FIEMAP_EXTENT_SIZE = struct.calcsize(_FIEMAP_EXTENT_FORMAT)
# The FIEMAP ioctl number
_FIEMAP_IOCTL = 0xC020660B
# This FIEMAP ioctl flag which instructs the kernel to sync the file before
# reading the block map
_FIEMAP_FLAG_SYNC = 0x00000001
# Size of the buffer for 'struct fiemap_extent' elements which will be used
# when invoking the FIEMAP ioctl. The larger is the buffer, the less times the
# FIEMAP ioctl will be invoked.
_FIEMAP_BUFFER_SIZE = 256 * 1024

class FilemapFiemap(_FilemapBase):
    """
    This class provides API to the FIEMAP ioctl. Namely, it allows to iterate
    over all mapped blocks and over all holes.

    This class synchronizes the image file every time it invokes the FIEMAP
    ioctl in order to work-around early FIEMAP implementation kernel bugs.
    """

    def __init__(self, image, log=None):
        """
        Initialize a class instance. The 'image' argument is full the file
        object to operate on.
        """

        # Call the base class constructor first
        _FilemapBase.__init__(self, image, log)
        self._log.debug("FilemapFiemap: initializing")

        self._buf_size = _FIEMAP_BUFFER_SIZE

        # Calculate how many 'struct fiemap_extent' elements fit the buffer
        self._buf_size -= _FIEMAP_SIZE
        self._fiemap_extent_cnt = self._buf_size // _FIEMAP_EXTENT_SIZE
        assert self._fiemap_extent_cnt > 0
        self._buf_size = self._fiemap_extent_cnt * _FIEMAP_EXTENT_SIZE
        self._buf_size += _FIEMAP_SIZE

        # Allocate a mutable buffer for the FIEMAP ioctl
        self._buf = array.array('B', [0] * self._buf_size)

        # Check if the FIEMAP ioctl is supported
        self.block_is_mapped(0)

    def _invoke_fiemap(self, block, count):
        """
        Invoke the FIEMAP ioctl for 'count' blocks of the file starting from
        block number 'block'.

        The full result of the operation is stored in 'self._buf' on exit.
        Returns the unpacked 'struct fiemap' data structure in form of a python
        list (just like 'struct.upack()').
        """

        if self.blocks_cnt != 0 and (block < 0 or block >= self.blocks_cnt):
            raise Error("bad block number %d, should be within [0, %d]"
                        % (block, self.blocks_cnt))

        # Initialize the 'struct fiemap' part of the buffer. We use the
        # '_FIEMAP_FLAG_SYNC' flag in order to make sure the file is
        # synchronized. The reason for this is that early FIEMAP
        # implementations had many bugs related to cached dirty data, and
        # synchronizing the file is a necessary work-around.
        struct.pack_into(_FIEMAP_FORMAT, self._buf, 0, block * self.block_size,
                         count * self.block_size, _FIEMAP_FLAG_SYNC, 0,
                         self._fiemap_extent_cnt, 0)

        try:
            fcntl.ioctl(self._f_image, _FIEMAP_IOCTL, self._buf, 1)
        except IOError as err:
            # Note, the FIEMAP ioctl is supported by the Linux kernel starting
            # from version 2.6.28 (year 2008).
            if err.errno == errno.EOPNOTSUPP:
                errstr = "FilemapFiemap: the FIEMAP ioctl is not supported " \
                         "by the file-system"
                self._log.debug(errstr)
                raise ErrorNotSupp(errstr)
            if err.errno == errno.ENOTTY:
                errstr = "FilemapFiemap: the FIEMAP ioctl is not supported " \
                         "by the kernel"
                self._log.debug(errstr)
                raise ErrorNotSupp(errstr)
            raise Error("the FIEMAP ioctl failed for '%s': %s"
                        % (self._image_path, err))

        return struct.unpack(_FIEMAP_FORMAT, self._buf[:_FIEMAP_SIZE])

    def block_is_mapped(self, block):
        """Refer the '_FilemapBase' class for the documentation."""
        struct_fiemap = self._invoke_fiemap(block, 1)

        # The 3rd element of 'struct_fiemap' is the 'fm_mapped_extents' field.
        # If it contains zero, the block is not mapped, otherwise it is
        # mapped.
        result = bool(struct_fiemap[3])
        self._log.debug("FilemapFiemap: block_is_mapped(%d) returns %s"
                        % (block, result))
        return result

    def _unpack_fiemap_extent(self, index):
        """
        Unpack a 'struct fiemap_extent' structure object number 'index' from
        the internal 'self._buf' buffer.
        """

        offset = _FIEMAP_SIZE + _FIEMAP_EXTENT_SIZE * index
        return struct.unpack(_FIEMAP_EXTENT_FORMAT,
                             self._buf[offset : offset + _FIEMAP_EXTENT_SIZE])

    def _do_get_mapped_ranges(self, start, count):
        """
        Implements most the functionality for the  'get_mapped_ranges()'
        generator: invokes the FIEMAP ioctl, walks through the mapped extents
        and yields mapped block ranges. However, the ranges may be consecutive
        (e.g., (1, 100), (100, 200)) and 'get_mapped_ranges()' simply merges
        them.
        """

        block = start
        while block < start + count:
            struct_fiemap = self._invoke_fiemap(block, count)

            mapped_extents = struct_fiemap[3]
            if mapped_extents == 0:
                # No more mapped blocks
                return

            extent = 0
            while extent < mapped_extents:
                fiemap_extent = self._unpack_fiemap_extent(extent)

                # Start of the extent
                extent_start = fiemap_extent[0]
                # Starting block number of the extent
                extent_block = extent_start // self.block_size
                # Length of the extent
                extent_len = fiemap_extent[2]
                # Count of blocks in the extent
                extent_count = extent_len // self.block_size

                # Extent length and offset have to be block-aligned
                assert extent_start % self.block_size == 0
                assert extent_len % self.block_size == 0

                if extent_block > start + count - 1:
                    return

                first = max(extent_block, block)
                last = min(extent_block + extent_count, start + count) - 1
                yield (first, last)

                extent += 1

            block = extent_block + extent_count

    def get_mapped_ranges(self, start, count):
        """Refer the '_FilemapBase' class for the documentation."""
        self._log.debug("FilemapFiemap: get_mapped_ranges(%d,  %d(%d))"
                        % (start, count, start + count - 1))
        iterator = self._do_get_mapped_ranges(start, count)
        first_prev, last_prev = next(iterator)

        for first, last in iterator:
            if last_prev == first - 1:
                last_prev = last
            else:
                self._log.debug("FilemapFiemap: yielding range (%d, %d)"
                                % (first_prev, last_prev))
                yield (first_prev, last_prev)
                first_prev, last_prev = first, last

        self._log.debug("FilemapFiemap: yielding range (%d, %d)"
                        % (first_prev, last_prev))
        yield (first_prev, last_prev)

class FilemapNobmap(_FilemapBase):
    """
    This class is used when both the 'SEEK_DATA/HOLE' and FIEMAP are not
    supported by the filesystem or kernel.
    """

    def __init__(self, image, log=None):
        """Refer the '_FilemapBase' class for the documentation."""

        # Call the base class constructor first
        _FilemapBase.__init__(self, image, log)
        self._log.debug("FilemapNobmap: initializing")

    def block_is_mapped(self, block):
        """Refer the '_FilemapBase' class for the documentation."""
        return True

    def get_mapped_ranges(self, start, count):
        """Refer the '_FilemapBase' class for the documentation."""
        self._log.debug("FilemapNobmap: get_mapped_ranges(%d,  %d(%d))"
                        % (start, count, start + count - 1))
        yield (start, start + count -1)

def filemap(image, log=None):
    """
    Create and return an instance of a Filemap class - 'FilemapFiemap' or
    'FilemapSeek', depending on what the system we run on supports. If the
    FIEMAP ioctl is supported, an instance of the 'FilemapFiemap' class is
    returned. Otherwise, if 'SEEK_HOLE' is supported an instance of the
    'FilemapSeek' class is returned. If none of these are supported, the
    function generates an 'Error' type exception.
    """

    try:
        return FilemapFiemap(image, log)
    except ErrorNotSupp:
        try:
            return FilemapSeek(image, log)
        except ErrorNotSupp:
            return FilemapNobmap(image, log)

def sparse_copy(src_fname, dst_fname, skip=0, seek=0,
                length=0, api=None):
    """
    Efficiently copy sparse file to or into another file.

    src_fname: path to source file
    dst_fname: path to destination file
    skip: skip N bytes at thestart of src
    seek: seek N bytes from the start of dst
    length: read N bytes from src and write them to dst
    api: FilemapFiemap or FilemapSeek object
    """
    if not api:
        api = filemap
    fmap = api(src_fname)
    try:
        dst_file = open(dst_fname, 'r+b')
    except IOError:
        dst_file = open(dst_fname, 'wb')
        if length:
            dst_size = length + seek
        else:
            dst_size = os.path.getsize(src_fname) + seek - skip
        dst_file.truncate(dst_size)

    written = 0
    for first, last in fmap.get_mapped_ranges(0, fmap.blocks_cnt):
        start = first * fmap.block_size
        end = (last + 1) * fmap.block_size

        if skip >= end:
            continue

        if start < skip < end:
            start = skip

        fmap._f_image.seek(start, os.SEEK_SET)

        written += start - skip - written
        if length and written >= length:
            dst_file.seek(seek + length, os.SEEK_SET)
            dst_file.close()
            return

        dst_file.seek(seek + start - skip, os.SEEK_SET)

        chunk_size = 1024 * 1024
        to_read = end - start
        read = 0

        while read < to_read:
            if read + chunk_size > to_read:
                chunk_size = to_read - read
            size = chunk_size
            if length and written + size > length:
                size = length - written
            chunk = fmap._f_image.read(size)
            dst_file.write(chunk)
            read += size
            written += size
            if written == length:
                dst_file.close()
                return
    dst_file.close()
