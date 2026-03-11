#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os

def sort_shadowutils_file(filename, mapping):
    """
    Sorts a passwd or group file based on the numeric ID in the third column.
    If a mapping is given, the name from the first column is mapped via that
    dictionary instead (necessary for /etc/shadow and /etc/gshadow). If not,
    a new mapping is created on the fly and returned.
    """

    new_mapping = {}
    with open(filename, 'rb+') as f:
        lines = f.readlines()
        # No explicit error checking for the sake of simplicity. /etc
        # files are assumed to be well-formed, causing exceptions if
        # not.
        for line in lines:
            entries = line.split(b':')
            name = entries[0]
            if mapping is None:
                id = int(entries[2])
            else:
                id = mapping[name]
            new_mapping[name] = id
        # Sort by numeric id first, with entire line as secondary key
        # (just in case that there is more than one entry for the same id).
        lines.sort(key=lambda line: (new_mapping[line.split(b':')[0]], line))
        # We overwrite the entire file, i.e. no truncate() necessary.
        f.seek(0)
        f.write(b''.join(lines))

    return new_mapping

def sort_shadowutils_files(sysconfdir):
    """
    Sorts shadow-utils 'passwd' and 'group' files in a rootfs' /etc directory
    by ID.
    """

    for main, shadow in (('passwd', 'shadow'),
                         ('group', 'gshadow')):
        filename = os.path.join(sysconfdir, main)
        if os.path.exists(filename):
            mapping = sort_shadowutils_file(filename, None)
            filename = os.path.join(sysconfdir, shadow)
            if os.path.exists(filename):
                 sort_shadowutils_file(filename, mapping)

def remove_shadowutils_backup_file(filename):
    """
    Remove shadow-utils backup file for files like /etc/passwd.
    """

    backup_filename = filename + '-'
    if os.path.exists(backup_filename):
        os.unlink(backup_filename)

def remove_shadowutils_backup_files(sysconfdir):
    """
    Remove shadow-utils backup files in a rootfs /etc directory. They are not
    needed in the initial root filesystem and sorting them can be inconsistent
    (YOCTO #11043).
    """

    for filename in (
            'group',
            'gshadow',
            'passwd',
            'shadow',
            'subgid',
            'subuid',
        ):
        filepath = os.path.join(sysconfdir, filename)
        remove_shadowutils_backup_file(filepath)

def tidy_shadowutils_files(sysconfdir):
    """
    Tidy up shadow-utils files.
    """

    remove_shadowutils_backup_files(sysconfdir)
    sort_shadowutils_files(sysconfdir)

    return True
