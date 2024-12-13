# Creates a tarball of the work directory for a recipe when one of its
# tasks fails, or any other nominated directories.
# Useful in cases where the environment in which builds are run is
# ephemeral or otherwise inaccessible for examination during
# debugging.
#
# To enable, simply add the following to your configuration:
#
# INHERIT += "retain"
#
# You can specify the recipe-specific directories to save upon failure
# or always (space-separated) e.g.:
#
# RETAIN_DIRS_FAILURE = "${WORKDIR};prefix=workdir"    # default
# RETAIN_DIRS_ALWAYS = "${T}"
#
# Naturally you can use overrides to limit it to a specific recipe:
# RETAIN_DIRS_ALWAYS:pn-somerecipe = "${T}"
#
# You can also specify global (non-recipe-specific) directories to save:
#
# RETAIN_DIRS_GLOBAL_FAILURE = "${LOG_DIR}"
# RETAIN_DIRS_GLOBAL_ALWAYS = "${BUILDSTATS_BASE}"
#
# If you wish to use a different tarball name prefix than the default of
# the directory name, you can do so by specifying a ;prefix= followed by
# the desired prefix (no spaces) in any of the RETAIN_DIRS_* variables.
# e.g. to always save the log files with a "recipelogs" as the prefix for
# the tarball of ${T} you would do this:
#
# RETAIN_DIRS_ALWAYS = "${T};prefix=recipelogs"
#
# Notes:
# * For this to be useful you also need corresponding logic in your build
#   orchestration tool to pick up any files written out to RETAIN_OUTDIR
#   (with the other assumption being that no files are present there at
#   the start of the build, since there is no logic to purge old files).
# * Work directories can be quite large, so saving them can take some time
#   and of course space.
# * Tarball creation is deferred to the end of the build, thus you will
#   get the state at the end, not immediately upon failure.
# * Extra directories must naturally be populated at the time the retain
#   class goes to save them (build completion); to try ensure this for
#   things that are also saved on build completion (e.g. buildstats), put
#   the INHERIT += "retain" after the INHERIT += lines for the class that
#   is writing out the data that you wish to save.
# * The tarballs have the tarball name as a top-level directory so that
#   multiple tarballs can be extracted side-by-side easily.
#
# Copyright (c) 2020, 2024 Microsoft Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

RETAIN_OUTDIR ?= "${TMPDIR}/retained"
RETAIN_DIRS_FAILURE ?= "${WORKDIR};prefix=workdir"
RETAIN_DIRS_ALWAYS ?= ""
RETAIN_DIRS_GLOBAL_FAILURE ?= ""
RETAIN_DIRS_GLOBAL_ALWAYS ?= ""
RETAIN_TARBALL_SUFFIX ?= "${DATETIME}.tar.gz"
RETAIN_ENABLED ?= "1"


def retain_retain_dir(desc, tarprefix, path, tarbasepath, d):
    import datetime

    outdir = d.getVar('RETAIN_OUTDIR')
    bb.utils.mkdirhier(outdir)
    suffix = d.getVar('RETAIN_TARBALL_SUFFIX')
    tarname = '%s_%s' % (tarprefix, suffix)
    tarfp = os.path.join(outdir, '%s' % tarname)
    tardir = os.path.relpath(path, tarbasepath)
    cmdargs = ['tar', 'cfa', tarfp]
    # Prefix paths within the tarball with the tarball name so that
    # multiple tarballs can be extracted side-by-side
    tarname_noext = os.path.splitext(tarname)[0]
    if tarname_noext.endswith('.tar'):
        tarname_noext = tarname_noext[:-4]
    cmdargs += ['--transform', 's:^:%s/:' % tarname_noext]
    cmdargs += [tardir]
    try:
        bb.process.run(cmdargs, cwd=tarbasepath)
    except bb.process.ExecutionError as e:
        # It is possible for other tasks to be writing to the workdir
        # while we are tarring it up, in which case tar will return 1,
        # but we don't care in this situation (tar returns 2 for other
        # errors so we we will see those)
        if e.exitcode != 1:
            bb.warn('retain: error saving %s: %s' % (desc, str(e)))


addhandler retain_task_handler
retain_task_handler[eventmask] = "bb.build.TaskFailed bb.build.TaskSucceeded"

addhandler retain_build_handler
retain_build_handler[eventmask] = "bb.event.BuildStarted bb.event.BuildCompleted"

python retain_task_handler() {
    if d.getVar('RETAIN_ENABLED') != '1':
        return

    dirs = d.getVar('RETAIN_DIRS_ALWAYS')
    if isinstance(e, bb.build.TaskFailed):
        dirs += ' ' + d.getVar('RETAIN_DIRS_FAILURE')

    dirs = dirs.strip().split()
    if dirs:
        outdir = d.getVar('RETAIN_OUTDIR')
        bb.utils.mkdirhier(outdir)
        dirlist_file = os.path.join(outdir, 'retain_dirs.list')
        pn = d.getVar('PN')
        taskname = d.getVar('BB_CURRENTTASK')
        with open(dirlist_file, 'a') as f:
            for entry in dirs:
                f.write('%s %s %s\n' % (pn, taskname, entry))
}

python retain_build_handler() {
    outdir = d.getVar('RETAIN_OUTDIR')
    dirlist_file = os.path.join(outdir, 'retain_dirs.list')

    if isinstance(e, bb.event.BuildStarted):
        if os.path.exists(dirlist_file):
            os.remove(dirlist_file)
        return

    if d.getVar('RETAIN_ENABLED') != '1':
        return

    savedirs = {}
    try:
        with open(dirlist_file, 'r') as f:
            for line in f:
                pn, _, path = line.rstrip().split()
                if not path in savedirs:
                    savedirs[path] = pn
        os.remove(dirlist_file)
    except FileNotFoundError:
        pass

    if e.getFailures():
        for path in (d.getVar('RETAIN_DIRS_GLOBAL_FAILURE') or '').strip().split():
            savedirs[path] = ''

    for path in (d.getVar('RETAIN_DIRS_GLOBAL_ALWAYS') or '').strip().split():
        savedirs[path] = ''

    if savedirs:
        bb.plain('NOTE: retain: retaining build output...')
        count = 0
        for path, pn in savedirs.items():
            prefix = None
            if ';' in path:
                pathsplit = path.split(';')
                path = pathsplit[0]
                for param in pathsplit[1:]:
                    if '=' in param:
                        name, value = param.split('=', 1)
                        if name == 'prefix':
                            prefix = value
                        else:
                            bb.error('retain: invalid parameter "%s" in RETAIN_* variable value' % param)
                            return
                    else:
                        bb.error('retain: parameter "%s" missing value in RETAIN_* variable value' % param)
                        return
            if prefix:
                itemname = prefix
            else:
                itemname = os.path.basename(path)
            if pn:
                # Always add the recipe name in front
                itemname = pn + '_' + itemname
            if os.path.exists(path):
                retain_retain_dir(itemname, itemname, path, os.path.dirname(path), d)
                count += 1
            else:
                bb.warn('retain: path %s does not currently exist' % path)
        if count:
            item = 'archive' if count == 1 else 'archives'
            bb.plain('NOTE: retain: saved %d %s to %s' % (count, item, outdir))
}
