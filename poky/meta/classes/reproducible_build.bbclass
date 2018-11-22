# reproducible_build.bbclass
#
# Sets SOURCE_DATE_EPOCH in each component's build environment.
# Upstream components (generally) respect this environment variable,
# using it in place of the "current" date and time.
# See https://reproducible-builds.org/specs/source-date-epoch/
#
# After sources are unpacked but before they are patched, we set a reproducible value for SOURCE_DATE_EPOCH.
# This value should be reproducible for anyone who builds the same revision from the same sources.
#
# There are 4 ways we determine SOURCE_DATE_EPOCH:
#
# 1. Use the value from __source_date_epoch.txt file if this file exists.
#    This file was most likely created in the previous build by one of the following methods 2,3,4.
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
# 4. Use the modification time of the youngest file in the source tree, if there is one.
#    This will be the newest file from the distribution tarball, if any.
#
# 5. Fall back to a fixed timestamp.
#
# Once the value of SOURCE_DATE_EPOCH is determined, it is stored in the recipe's SDE_FILE.
# If none of these mechanisms are suitable, replace the do_deploy_source_date_epoch task
# with recipe-specific functionality to write the appropriate SOURCE_DATE_EPOCH into the SDE_FILE.
#
# If this file is found by other tasks, the value is exported in the SOURCE_DATE_EPOCH variable.
# SOURCE_DATE_EPOCH is set for all tasks that might use it (do_configure, do_compile, do_package, ...)

BUILD_REPRODUCIBLE_BINARIES ??= '1'
inherit ${@oe.utils.ifelse(d.getVar('BUILD_REPRODUCIBLE_BINARIES') == '1', 'reproducible_build_simple', '')}

SDE_DIR ="${WORKDIR}/source-date-epoch"
SDE_FILE = "${SDE_DIR}/__source_date_epoch.txt"

SSTATETASKS += "do_deploy_source_date_epoch"

do_deploy_source_date_epoch () {
    echo "Deploying SDE to ${SDE_DIR}."
}

python do_deploy_source_date_epoch_setscene () {
    sstate_setscene(d)
}

do_deploy_source_date_epoch[dirs] = "${SDE_DIR}"
do_deploy_source_date_epoch[sstate-plaindirs] = "${SDE_DIR}"
addtask do_deploy_source_date_epoch_setscene
addtask do_deploy_source_date_epoch before do_configure after do_patch

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
    source_date_epoch = None
    if "git://" in d.getVar('SRC_URI'):
        gitpath = find_git_folder(d, sourcedir)
        if gitpath:
            import subprocess
            source_date_epoch = int(subprocess.check_output(['git','log','-1','--pretty=%ct'], cwd=gitpath))
            bb.debug(1, "git repository: %s" % gitpath)
    return source_date_epoch

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

python do_create_source_date_epoch_stamp() {
    epochfile = d.getVar('SDE_FILE')
    if os.path.isfile(epochfile):
        bb.debug(1, "Reusing SOURCE_DATE_EPOCH from: %s" % epochfile)
        return

    sourcedir = d.getVar('S')
    source_date_epoch = (
        get_source_date_epoch_from_git(d, sourcedir) or
        get_source_date_epoch_from_known_files(d, sourcedir) or
        get_source_date_epoch_from_youngest_file(d, sourcedir) or
        fixed_source_date_epoch()       # Last resort
    )

    bb.debug(1, "SOURCE_DATE_EPOCH: %d" % source_date_epoch)
    bb.utils.mkdirhier(d.getVar('SDE_DIR'))
    with open(epochfile, 'w') as f:
        f.write(str(source_date_epoch))
}

BB_HASHBASE_WHITELIST += "SOURCE_DATE_EPOCH"

python () {
    if d.getVar('BUILD_REPRODUCIBLE_BINARIES') == '1':
        d.appendVarFlag("do_unpack", "postfuncs", " do_create_source_date_epoch_stamp")
        epochfile = d.getVar('SDE_FILE')
        source_date_epoch = "0"
        if os.path.isfile(epochfile):
            with open(epochfile, 'r') as f:
                source_date_epoch = f.read()
            bb.debug(1, "SOURCE_DATE_EPOCH: %s" % source_date_epoch)
        d.setVar('SOURCE_DATE_EPOCH', source_date_epoch)
}
