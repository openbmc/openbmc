#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
import os
import subprocess
import bb

# For reproducible builds, this code sets the default SOURCE_DATE_EPOCH in each
# component's build environment. The format is number of seconds since the
# system epoch.
#
# Upstream components (generally) respect this environment variable,
# using it in place of the "current" date and time.
# See https://reproducible-builds.org/specs/source-date-epoch/
#
# The default value of SOURCE_DATE_EPOCH comes from the function
# get_source_date_epoch_value which reads from the SDE_FILE, or if the file
# is not available will use the fallback of SOURCE_DATE_EPOCH_FALLBACK.
#
# The SDE_FILE is normally constructed from the function
# create_source_date_epoch_stamp which is typically added as a postfuncs to
# the do_unpack task.  If a recipe does NOT have do_unpack, it should be added
# to a task that runs after the source is available and before the
# do_deploy_source_date_epoch task is executed.
#
# If a recipe wishes to override the default behavior it should set it's own
# SOURCE_DATE_EPOCH or override the do_deploy_source_date_epoch_stamp task
# with recipe-specific functionality to write the appropriate
# SOURCE_DATE_EPOCH into the SDE_FILE.
#
# SOURCE_DATE_EPOCH is intended to be a reproducible value.  This value should
# be reproducible for anyone who builds the same revision from the same
# sources.
#
# There are 4 ways the create_source_date_epoch_stamp function determines what
# becomes SOURCE_DATE_EPOCH:
#
# 1. Use the value from __source_date_epoch.txt file if this file exists.
#    This file was most likely created in the previous build by one of the
#    following methods 2,3,4.
#    Alternatively, it can be provided by a recipe via SRC_URI.
#
# If the file does not exist:
#
# 2. If there is a git checkout, use the last git commit timestamp.
#    Git does not preserve file timestamps on checkout.
#
# 3. Use the mtime of "known" files such as NEWS, CHANGLELOG, ...
#    This works for well-kept repositories distributed via tarball.
#
# 4. Use the modification time of the youngest file in the source tree, if
#    there is one.
#    This will be the newest file from the distribution tarball, if any.
#
# 5. Fall back to a fixed timestamp (SOURCE_DATE_EPOCH_FALLBACK).
#
# Once the value is determined, it is stored in the recipe's SDE_FILE.

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
            return os.path.join(root, ".git")

    bb.warn("Failed to find a git repository in WORKDIR: %s" % workdir)
    return None

def get_source_date_epoch_from_git(d, sourcedir):
    if not "git://" in d.getVar('SRC_URI') and not "gitsm://" in d.getVar('SRC_URI'):
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
    p = subprocess.run(['git', '-c', 'log.showSignature=false', '--git-dir', gitpath, 'log', '-1', '--pretty=%ct'],
                       check=True, stdout=subprocess.PIPE)
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
            if fname == "singletask.lock":
                 # Ignore externalsrc/devtool lockfile [YOCTO #14921]
                 continue
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

def fixed_source_date_epoch(d):
    bb.debug(1, "No tarball or git repo found to determine SOURCE_DATE_EPOCH")
    source_date_epoch = d.getVar('SOURCE_DATE_EPOCH_FALLBACK')
    if source_date_epoch:
        bb.debug(1, "Using SOURCE_DATE_EPOCH_FALLBACK")
        return int(source_date_epoch)
    return 0

def get_source_date_epoch(d, sourcedir):
    return (
        get_source_date_epoch_from_git(d, sourcedir) or
        get_source_date_epoch_from_youngest_file(d, sourcedir) or
        fixed_source_date_epoch(d)       # Last resort
    )

def epochfile_read(epochfile, d):
    cached, efile = d.getVar('__CACHED_SOURCE_DATE_EPOCH') or (None, None)
    if cached and efile == epochfile:
        return cached

    if cached and epochfile != efile:
        bb.debug(1, "Epoch file changed from %s to %s" % (efile, epochfile))

    source_date_epoch = int(d.getVar('SOURCE_DATE_EPOCH_FALLBACK'))
    try:
        with open(epochfile, 'r') as f:
            s = f.read()
            try:
                source_date_epoch = int(s)
            except ValueError:
                bb.warn("SOURCE_DATE_EPOCH value '%s' is invalid. Reverting to SOURCE_DATE_EPOCH_FALLBACK" % s)
                source_date_epoch = int(d.getVar('SOURCE_DATE_EPOCH_FALLBACK'))
        bb.debug(1, "SOURCE_DATE_EPOCH: %d" % source_date_epoch)
    except FileNotFoundError:
        bb.debug(1, "Cannot find %s. SOURCE_DATE_EPOCH will default to %d" % (epochfile, source_date_epoch))

    d.setVar('__CACHED_SOURCE_DATE_EPOCH', (str(source_date_epoch), epochfile))
    return str(source_date_epoch)

def epochfile_write(source_date_epoch, epochfile, d):

    bb.debug(1, "SOURCE_DATE_EPOCH: %d" % source_date_epoch)
    bb.utils.mkdirhier(os.path.dirname(epochfile))

    tmp_file = "%s.new" % epochfile
    with open(tmp_file, 'w') as f:
        f.write(str(source_date_epoch))
    os.rename(tmp_file, epochfile)
