#! /usr/bin/env python3
#
# Copyright 2023 by Garmin Ltd. or its subsidiaries
#
# SPDX-License-Identifier: MIT

import sys
import ctypes
import os
import errno

libc = ctypes.CDLL("libc.so.6", use_errno=True)
fsencoding = sys.getfilesystemencoding()


libc.listxattr.argtypes = [ctypes.c_char_p, ctypes.c_char_p, ctypes.c_size_t]
libc.llistxattr.argtypes = [ctypes.c_char_p, ctypes.c_char_p, ctypes.c_size_t]


def listxattr(path, follow=True):
    func = libc.listxattr if follow else libc.llistxattr

    os_path = os.fsencode(path)

    while True:
        length = func(os_path, None, 0)

        if length < 0:
            err = ctypes.get_errno()
            raise OSError(err, os.strerror(err), str(path))

        if length == 0:
            return []

        arr = ctypes.create_string_buffer(length)

        read_length = func(os_path, arr, length)
        if read_length != length:
            # Race!
            continue

        return [a.decode(fsencoding) for a in arr.raw.split(b"\x00") if a]


libc.getxattr.argtypes = [
    ctypes.c_char_p,
    ctypes.c_char_p,
    ctypes.c_char_p,
    ctypes.c_size_t,
]
libc.lgetxattr.argtypes = [
    ctypes.c_char_p,
    ctypes.c_char_p,
    ctypes.c_char_p,
    ctypes.c_size_t,
]


def getxattr(path, name, follow=True):
    func = libc.getxattr if follow else libc.lgetxattr

    os_path = os.fsencode(path)
    os_name = os.fsencode(name)

    while True:
        length = func(os_path, os_name, None, 0)

        if length < 0:
            err = ctypes.get_errno()
            if err == errno.ENODATA:
                return None
            raise OSError(err, os.strerror(err), str(path))

        if length == 0:
            return ""

        arr = ctypes.create_string_buffer(length)

        read_length = func(os_path, os_name, arr, length)
        if read_length != length:
            # Race!
            continue

        return arr.raw


def get_all_xattr(path, follow=True):
    attrs = {}

    names = listxattr(path, follow)

    for name in names:
        value = getxattr(path, name, follow)
        if value is None:
            # This can happen if a value is erased after listxattr is called,
            # so ignore it
            continue
        attrs[name] = value

    return attrs


def main():
    import argparse
    from pathlib import Path

    parser = argparse.ArgumentParser()
    parser.add_argument("path", help="File Path", type=Path)

    args = parser.parse_args()

    attrs = get_all_xattr(args.path)

    for name, value in attrs.items():
        try:
            value = value.decode(fsencoding)
        except UnicodeDecodeError:
            pass

        print(f"{name} = {value}")

    return 0


if __name__ == "__main__":
    sys.exit(main())
