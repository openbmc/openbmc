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
    # If it exists we need to regenerate as the sources may have changed
    if os.path.isfile(epochfile):
        bb.debug(1, "Deleting existing SOURCE_DATE_EPOCH from: %s" % epochfile)
        os.remove(epochfile)

    source_date_epoch = oe.reproducible.get_source_date_epoch(d, d.getVar('S'))

    bb.debug(1, "SOURCE_DATE_EPOCH: %d" % source_date_epoch)
    bb.utils.mkdirhier(d.getVar('SDE_DIR'))
    with open(epochfile, 'w') as f:
        f.write(str(source_date_epoch))
}

def get_source_date_epoch_value(d):
    cached = d.getVar('__CACHED_SOURCE_DATE_EPOCH')
    if cached:
        return cached

    epochfile = d.getVar('SDE_FILE')
    source_date_epoch = int(d.getVar('SOURCE_DATE_EPOCH_FALLBACK'))
    if os.path.isfile(epochfile):
        with open(epochfile, 'r') as f:
            s = f.read()
            try:
                source_date_epoch = int(s)
                # workaround for old sstate with SDE_FILE content being 0 - use SOURCE_DATE_EPOCH_FALLBACK
                if source_date_epoch == 0 :
                    source_date_epoch = int(d.getVar('SOURCE_DATE_EPOCH_FALLBACK'))
                    bb.warn("SOURCE_DATE_EPOCH value from sstate '%s' is deprecated/invalid. Reverting to SOURCE_DATE_EPOCH_FALLBACK '%s'" % (s, source_date_epoch))
            except ValueError:
                bb.warn("SOURCE_DATE_EPOCH value '%s' is invalid. Reverting to SOURCE_DATE_EPOCH_FALLBACK" % s)
                source_date_epoch = int(d.getVar('SOURCE_DATE_EPOCH_FALLBACK'))
        bb.debug(1, "SOURCE_DATE_EPOCH: %d" % source_date_epoch)
    else:
        bb.debug(1, "Cannot find %s. SOURCE_DATE_EPOCH will default to %d" % (epochfile, source_date_epoch))

    d.setVar('__CACHED_SOURCE_DATE_EPOCH', str(source_date_epoch))
    return str(source_date_epoch)

export SOURCE_DATE_EPOCH ?= "${@get_source_date_epoch_value(d)}"
BB_HASHBASE_WHITELIST += "SOURCE_DATE_EPOCH"

python () {
    if d.getVar('BUILD_REPRODUCIBLE_BINARIES') == '1':
        d.appendVarFlag("do_unpack", "postfuncs", " create_source_date_epoch_stamp")
}
