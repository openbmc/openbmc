#
# SPDX-License-Identifier: MIT
#
# Copyright (C) 2024 Marcus Folkesson
# Author: Marcus Folkesson <marcus.folkesson@gmail.com>
#
# Utility functions handling boot files
#
# Look into deploy_dir and search for boot_files.
# Returns a list of tuples with (original filepath relative to
# deploy_dir, desired filepath renaming)
#
# Heavily inspired of bootimg-partition.py
#
def get_boot_files(deploy_dir, boot_files):
    import re
    import os
    from glob import glob

    if boot_files is None:
        return None

    # list of tuples (src_name, dst_name)
    deploy_files = []
    for src_entry in re.findall(r'[\w;\-\./\*]+', boot_files):
        if ';' in src_entry:
            dst_entry = tuple(src_entry.split(';'))
            if not dst_entry[0] or not dst_entry[1]:
                raise ValueError('Malformed boot file entry: %s' % src_entry)
        else:
            dst_entry = (src_entry, src_entry)

        deploy_files.append(dst_entry)

    install_files = []
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

            srcs = glob(os.path.join(deploy_dir, src))

            for entry in srcs:
                src = os.path.relpath(entry, deploy_dir)
                entry_dst_name = entry_name_fn(entry)
                install_files.append((src, entry_dst_name))
        else:
            install_files.append((src, dst))

    return install_files
