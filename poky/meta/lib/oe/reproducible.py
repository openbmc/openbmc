#
# SPDX-License-Identifier: GPL-2.0-only
#
import os
import subprocess
import bb

def get_source_date_epoch_from_known_files(d, sourcedir):
    source_date_epoch = None
    newest_file = None
    known_files = set(["NEWS", "ChangeLog", "Changelog", "CHANGES"])
    for file in known_files:
        filepath = os.path.join(sourcedir, file)
        if os.path.isfile(filepath):
            mtime = int(os.lstat(filepath).st_mtime)
            # There may be more than one "known_file" present, if so, use the youngest one
            if not source_date_epoch or mtime > source_date_epoch:
                source_date_epoch = mtime
                newest_file = filepath
    if newest_file:
        bb.debug(1, "SOURCE_DATE_EPOCH taken from: %s" % newest_file)
    return source_date_epoch

def find_git_folder(d, sourcedir):
    # First guess: WORKDIR/git
    # This is the default git fetcher unpack path
    workdir = d.getVar('WORKDIR')
    gitpath = os.path.join(workdir, "git/.git")
    if os.path.isdir(gitpath):
        return gitpath

    # Second guess: ${S}
    gitpath = os.path.join(sourcedir, ".git")
    if os.path.isdir(gitpath):
        return gitpath

    # Perhaps there was a subpath or destsuffix specified.
    # Go looking in the WORKDIR
    exclude = set(["build", "image", "license-destdir", "patches", "pseudo",
                   "recipe-sysroot", "recipe-sysroot-native", "sysroot-destdir", "temp"])
    for root, dirs, files in os.walk(workdir, topdown=True):
        dirs[:] = [d for d in dirs if d not in exclude]
        if '.git' in dirs:
            return root

    bb.warn("Failed to find a git repository in WORKDIR: %s" % workdir)
    return None

def get_source_date_epoch_from_git(d, sourcedir):
    if not "git://" in d.getVar('SRC_URI'):
        return None

    gitpath = find_git_folder(d, sourcedir)
    if not gitpath:
        return None

    # Check that the repository has a valid HEAD; it may not if subdir is used
    # in SRC_URI
    p = subprocess.run(['git', '--git-dir', gitpath, 'rev-parse', 'HEAD'], stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if p.returncode != 0:
        bb.debug(1, "%s does not have a valid HEAD: %s" % (gitpath, p.stdout.decode('utf-8')))
        return None

    bb.debug(1, "git repository: %s" % gitpath)
    p = subprocess.run(['git', '--git-dir', gitpath, 'log', '-1', '--pretty=%ct'], check=True, stdout=subprocess.PIPE)
    return int(p.stdout.decode('utf-8'))

def get_source_date_epoch_from_youngest_file(d, sourcedir):
    if sourcedir == d.getVar('WORKDIR'):
       # These sources are almost certainly not from a tarball
       return None

    # Do it the hard way: check all files and find the youngest one...
    source_date_epoch = None
    newest_file = None
    for root, dirs, files in os.walk(sourcedir, topdown=True):
        files = [f for f in files if not f[0] == '.']

        for fname in files:
            filename = os.path.join(root, fname)
            try:
                mtime = int(os.lstat(filename).st_mtime)
            except ValueError:
                mtime = 0
            if not source_date_epoch or mtime > source_date_epoch:
                source_date_epoch = mtime
                newest_file = filename

    if newest_file:
        bb.debug(1, "Newest file found: %s" % newest_file)
    return source_date_epoch

def fixed_source_date_epoch():
    bb.debug(1, "No tarball or git repo found to determine SOURCE_DATE_EPOCH")
    return 0

def get_source_date_epoch(d, sourcedir):
    return (
        get_source_date_epoch_from_git(d, sourcedir) or
        get_source_date_epoch_from_known_files(d, sourcedir) or
        get_source_date_epoch_from_youngest_file(d, sourcedir) or
        fixed_source_date_epoch()       # Last resort
    )

