# reproducible_build.bbclass
#
# Sets the default SOURCE_DATE_EPOCH in each component's build environment.
# The format is number of seconds since the system epoch.
#
# Upstream components (generally) respect this environment variable,
# using it in place of the "current" date and time.
# See https://reproducible-builds.org/specs/source-date-epoch/
#
# The default value of SOURCE_DATE_EPOCH comes from the function
# get_source_date_epoch_value which reads from the SDE_FILE, or if the file
# is not available (or set to 0) will use the fallback of
# SOURCE_DATE_EPOCH_FALLBACK.
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

BUILD_REPRODUCIBLE_BINARIES ??= '1'
inherit reproducible_build_simple

SDE_DIR = "${WORKDIR}/source-date-epoch"
SDE_FILE = "${SDE_DIR}/__source_date_epoch.txt"
SDE_DEPLOYDIR = "${WORKDIR}/deploy-source-date-epoch"

# A SOURCE_DATE_EPOCH of '0' might be misinterpreted as no SDE
export SOURCE_DATE_EPOCH_FALLBACK ??= "1302044400"

SSTATETASKS += "do_deploy_source_date_epoch"

do_deploy_source_date_epoch () {
    mkdir -p ${SDE_DEPLOYDIR}
    if [ -e ${SDE_FILE} ]; then
        echo "Deploying SDE from ${SDE_FILE} -> ${SDE_DEPLOYDIR}."
        cp -p ${SDE_FILE} ${SDE_DEPLOYDIR}/__source_date_epoch.txt
    else
        echo "${SDE_FILE} not found!"
    fi
}

python do_deploy_source_date_epoch_setscene () {
    sstate_setscene(d)
    bb.utils.mkdirhier(d.getVar('SDE_DIR'))
    sde_file = os.path.join(d.getVar('SDE_DEPLOYDIR'), '__source_date_epoch.txt')
    if os.path.exists(sde_file):
        target = d.getVar('SDE_FILE')
        bb.debug(1, "Moving setscene SDE file %s -> %s" % (sde_file, target))
        os.rename(sde_file, target)
    else:
        bb.debug(1, "%s not found!" % sde_file)
}

do_deploy_source_date_epoch[dirs] = "${SDE_DEPLOYDIR}"
do_deploy_source_date_epoch[sstate-plaindirs] = "${SDE_DEPLOYDIR}"
addtask do_deploy_source_date_epoch_setscene
addtask do_deploy_source_date_epoch before do_configure after do_patch

python create_source_date_epoch_stamp() {
    import oe.reproducible

    epochfile = d.getVar('SDE_FILE')
    tmp_file = "%s.new" % epochfile

    source_date_epoch = oe.reproducible.get_source_date_epoch(d, d.getVar('S'))

    bb.debug(1, "SOURCE_DATE_EPOCH: %d" % source_date_epoch)
    bb.utils.mkdirhier(d.getVar('SDE_DIR'))
    with open(tmp_file, 'w') as f:
        f.write(str(source_date_epoch))

    os.rename(tmp_file, epochfile)
}

EPOCHTASK = "do_deploy_source_date_epoch"

# Generate the stamp after do_unpack runs
do_unpack[postfuncs] += "create_source_date_epoch_stamp"

def get_source_date_epoch_value(d):
    epochfile = d.getVar('SDE_FILE')
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

export SOURCE_DATE_EPOCH ?= "${@get_source_date_epoch_value(d)}"
BB_HASHBASE_WHITELIST += "SOURCE_DATE_EPOCH"
