#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Helper library to implement streaming compression and decompression using an
# external process
#
# This library should be used directly by end users; a wrapper library for the
# specific compression tool should be created

import builtins
import io
import os
import subprocess


def open_wrap(
    cls, filename, mode="rb", *, encoding=None, errors=None, newline=None, **kwargs
):
    """
    Open a compressed file in binary or text mode.

    Users should not call this directly. A specific compression library can use
    this helper to provide it's own "open" command

    The filename argument can be an actual filename (a str or bytes object), or
    an existing file object to read from or write to.

    The mode argument can be "r", "rb", "w", "wb", "x", "xb", "a" or "ab" for
    binary mode, or "rt", "wt", "xt" or "at" for text mode. The default mode is
    "rb".

    For binary mode, this function is equivalent to the cls constructor:
    cls(filename, mode). In this case, the encoding, errors and newline
    arguments must not be provided.

    For text mode, a cls object is created, and wrapped in an
    io.TextIOWrapper instance with the specified encoding, error handling
    behavior, and line ending(s).
    """
    if "t" in mode:
        if "b" in mode:
            raise ValueError("Invalid mode: %r" % (mode,))
    else:
        if encoding is not None:
            raise ValueError("Argument 'encoding' not supported in binary mode")
        if errors is not None:
            raise ValueError("Argument 'errors' not supported in binary mode")
        if newline is not None:
            raise ValueError("Argument 'newline' not supported in binary mode")

    file_mode = mode.replace("t", "")
    if isinstance(filename, (str, bytes, os.PathLike, int)):
        binary_file = cls(filename, file_mode, **kwargs)
    elif hasattr(filename, "read") or hasattr(filename, "write"):
        binary_file = cls(None, file_mode, fileobj=filename, **kwargs)
    else:
        raise TypeError("filename must be a str or bytes object, or a file")

    if "t" in mode:
        return io.TextIOWrapper(
            binary_file, encoding, errors, newline, write_through=True
        )
    else:
        return binary_file


class CompressionError(OSError):
    pass


class PipeFile(io.RawIOBase):
    """
    Class that implements generically piping to/from a compression program

    Derived classes should add the function get_compress() and get_decompress()
    that return the required commands. Input will be piped into stdin and the
    (de)compressed output should be written to stdout, e.g.:

        class FooFile(PipeCompressionFile):
            def get_decompress(self):
                return ["fooc", "--decompress", "--stdout"]

            def get_compress(self):
                return ["fooc", "--compress", "--stdout"]

    """

    READ = 0
    WRITE = 1

    def __init__(self, filename=None, mode="rb", *, stderr=None, fileobj=None):
        if "t" in mode or "U" in mode:
            raise ValueError("Invalid mode: {!r}".format(mode))

        if not "b" in mode:
            mode += "b"

        if mode.startswith("r"):
            self.mode = self.READ
        elif mode.startswith("w"):
            self.mode = self.WRITE
        else:
            raise ValueError("Invalid mode %r" % mode)

        if fileobj is not None:
            self.fileobj = fileobj
        else:
            self.fileobj = builtins.open(filename, mode or "rb")

        if self.mode == self.READ:
            self.p = subprocess.Popen(
                self.get_decompress(),
                stdin=self.fileobj,
                stdout=subprocess.PIPE,
                stderr=stderr,
                close_fds=True,
            )
            self.pipe = self.p.stdout
        else:
            self.p = subprocess.Popen(
                self.get_compress(),
                stdin=subprocess.PIPE,
                stdout=self.fileobj,
                stderr=stderr,
                close_fds=True,
            )
            self.pipe = self.p.stdin

        self.__closed = False

    def _check_process(self):
        if self.p is None:
            return

        returncode = self.p.wait()
        if returncode:
            raise CompressionError("Process died with %d" % returncode)
        self.p = None

    def close(self):
        if self.closed:
            return

        self.pipe.close()
        if self.p is not None:
            self._check_process()
        self.fileobj.close()

        self.__closed = True

    @property
    def closed(self):
        return self.__closed

    def fileno(self):
        return self.pipe.fileno()

    def flush(self):
        self.pipe.flush()

    def isatty(self):
        return self.pipe.isatty()

    def readable(self):
        return self.mode == self.READ

    def writable(self):
        return self.mode == self.WRITE

    def readinto(self, b):
        if self.mode != self.READ:
            import errno

            raise OSError(
                errno.EBADF, "read() on write-only %s object" % self.__class__.__name__
            )
        size = self.pipe.readinto(b)
        if size == 0:
            self._check_process()
        return size

    def write(self, data):
        if self.mode != self.WRITE:
            import errno

            raise OSError(
                errno.EBADF, "write() on read-only %s object" % self.__class__.__name__
            )
        data = self.pipe.write(data)

        if not data:
            self._check_process()

        return data
