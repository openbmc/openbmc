#! /usr/bin/env python3
#
# Copyright 2023 by Garmin Ltd. or its subsidiaries
#
# SPDX-License-Identifier: MIT


import sys
import ctypes
import os
import errno
import pwd
import grp

libacl = ctypes.CDLL("libacl.so.1", use_errno=True)


ACL_TYPE_ACCESS = 0x8000
ACL_TYPE_DEFAULT = 0x4000

ACL_FIRST_ENTRY = 0
ACL_NEXT_ENTRY = 1

ACL_UNDEFINED_TAG = 0x00
ACL_USER_OBJ = 0x01
ACL_USER = 0x02
ACL_GROUP_OBJ = 0x04
ACL_GROUP = 0x08
ACL_MASK = 0x10
ACL_OTHER = 0x20

ACL_READ = 0x04
ACL_WRITE = 0x02
ACL_EXECUTE = 0x01

acl_t = ctypes.c_void_p
acl_entry_t = ctypes.c_void_p
acl_permset_t = ctypes.c_void_p
acl_perm_t = ctypes.c_uint

acl_tag_t = ctypes.c_int

libacl.acl_free.argtypes = [acl_t]


def acl_free(acl):
    libacl.acl_free(acl)


libacl.acl_get_file.restype = acl_t
libacl.acl_get_file.argtypes = [ctypes.c_char_p, ctypes.c_uint]


def acl_get_file(path, typ):
    acl = libacl.acl_get_file(os.fsencode(path), typ)
    if acl is None:
        err = ctypes.get_errno()
        raise OSError(err, os.strerror(err), str(path))

    return acl


libacl.acl_get_entry.argtypes = [acl_t, ctypes.c_int, ctypes.c_void_p]


def acl_get_entry(acl, entry_id):
    entry = acl_entry_t()
    ret = libacl.acl_get_entry(acl, entry_id, ctypes.byref(entry))
    if ret < 0:
        err = ctypes.get_errno()
        raise OSError(err, os.strerror(err))

    if ret == 0:
        return None

    return entry


libacl.acl_get_tag_type.argtypes = [acl_entry_t, ctypes.c_void_p]


def acl_get_tag_type(entry_d):
    tag = acl_tag_t()
    ret = libacl.acl_get_tag_type(entry_d, ctypes.byref(tag))
    if ret < 0:
        err = ctypes.get_errno()
        raise OSError(err, os.strerror(err))
    return tag.value


libacl.acl_get_qualifier.restype = ctypes.c_void_p
libacl.acl_get_qualifier.argtypes = [acl_entry_t]


def acl_get_qualifier(entry_d):
    ret = libacl.acl_get_qualifier(entry_d)
    if ret is None:
        err = ctypes.get_errno()
        raise OSError(err, os.strerror(err))
    return ctypes.c_void_p(ret)


libacl.acl_get_permset.argtypes = [acl_entry_t, ctypes.c_void_p]


def acl_get_permset(entry_d):
    permset = acl_permset_t()
    ret = libacl.acl_get_permset(entry_d, ctypes.byref(permset))
    if ret < 0:
        err = ctypes.get_errno()
        raise OSError(err, os.strerror(err))

    return permset


libacl.acl_get_perm.argtypes = [acl_permset_t, acl_perm_t]


def acl_get_perm(permset_d, perm):
    ret = libacl.acl_get_perm(permset_d, perm)
    if ret < 0:
        err = ctypes.get_errno()
        raise OSError(err, os.strerror(err))
    return bool(ret)


class Entry(object):
    def __init__(self, tag, qualifier, mode):
        self.tag = tag
        self.qualifier = qualifier
        self.mode = mode

    def __str__(self):
        typ = ""
        qual = ""
        if self.tag == ACL_USER:
            typ = "user"
            qual = pwd.getpwuid(self.qualifier).pw_name
        elif self.tag == ACL_GROUP:
            typ = "group"
            qual = grp.getgrgid(self.qualifier).gr_name
        elif self.tag == ACL_USER_OBJ:
            typ = "user"
        elif self.tag == ACL_GROUP_OBJ:
            typ = "group"
        elif self.tag == ACL_MASK:
            typ = "mask"
        elif self.tag == ACL_OTHER:
            typ = "other"

        r = "r" if self.mode & ACL_READ else "-"
        w = "w" if self.mode & ACL_WRITE else "-"
        x = "x" if self.mode & ACL_EXECUTE else "-"

        return f"{typ}:{qual}:{r}{w}{x}"


class ACL(object):
    def __init__(self, acl):
        self.acl = acl

    def __del__(self):
        acl_free(self.acl)

    def entries(self):
        entry_id = ACL_FIRST_ENTRY
        while True:
            entry = acl_get_entry(self.acl, entry_id)
            if entry is None:
                break

            permset = acl_get_permset(entry)

            mode = 0
            for m in (ACL_READ, ACL_WRITE, ACL_EXECUTE):
                if acl_get_perm(permset, m):
                    mode |= m

            qualifier = None
            tag = acl_get_tag_type(entry)

            if tag == ACL_USER or tag == ACL_GROUP:
                qual = acl_get_qualifier(entry)
                qualifier = ctypes.cast(qual, ctypes.POINTER(ctypes.c_int))[0]

            yield Entry(tag, qualifier, mode)

            entry_id = ACL_NEXT_ENTRY

    @classmethod
    def from_path(cls, path, typ):
        acl = acl_get_file(path, typ)
        return cls(acl)


def main():
    import argparse
    import pwd
    import grp
    from pathlib import Path

    parser = argparse.ArgumentParser()
    parser.add_argument("path", help="File Path", type=Path)

    args = parser.parse_args()

    acl = ACL.from_path(args.path, ACL_TYPE_ACCESS)
    for entry in acl.entries():
        print(str(entry))

    return 0


if __name__ == "__main__":
    sys.exit(main())
