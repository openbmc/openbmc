#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# Usage:
# - Enable ccache
#   Add the following line to a conffile such as conf/local.conf:
#   INHERIT += "ccache"
#
# - Disable ccache for a recipe
#   Add the following line to the recipe if it can't be built with ccache:
#   CCACHE_DISABLE = '1'
#
# - Share ccache files between different builds
#   Set CCACHE_TOP_DIR to a shared dir
#   CCACHE_TOP_DIR = /path/to/shared_ccache/
#
# - TO debug ccahe
#   export CCACHE_DEBUG = "1"
#   export CCACHE_LOGFILE = "${CCACHE_DIR}/logfile.log"
#   And also set PARALLEL_MAKE = "-j 1" to get make the log in order
#

# Set it to a shared location for different builds, so that cache files can
# be shared between different builds.
CCACHE_TOP_DIR ?= "${TMPDIR}/ccache"

# ccache-native and cmake-native have a circular dependency
# that affects other native recipes, but not all.
# Allows to use ccache in specified native recipes.
CCACHE_NATIVE_RECIPES_ALLOWED ?= ""

# ccahe removes CCACHE_BASEDIR from file path, so that hashes will be the same
# in different builds.
export CCACHE_BASEDIR ?= "${TMPDIR}"

# Used for sharing cache files after compiler is rebuilt
export CCACHE_COMPILERCHECK ?= "%compiler% -dumpspecs"

export CCACHE_CONFIGPATH ?= "${COREBASE}/meta/conf/ccache.conf"

export CCACHE_DIR ?= "${CCACHE_TOP_DIR}/${MULTIMACH_TARGET_SYS}/${PN}"

# Fixed errors:
# ccache: error: Failed to create directory /run/user/0/ccache-tmp: Permission denied
export CCACHE_TEMPDIR ?= "${CCACHE_DIR}/tmp"

# We need to stop ccache considering the current directory or the
# debug-prefix-map target directory to be significant when calculating
# its hash. Without this the cache would be invalidated every time
# ${PV} or ${PR} change.
export CCACHE_NOHASHDIR ?= "1"

python() {
    """
    Enable ccache for the recipe
    """
    pn = d.getVar('PN')
    if (pn in d.getVar('CCACHE_NATIVE_RECIPES_ALLOWED') or
        not (bb.data.inherits_class("native", d) or
        bb.utils.to_boolean(d.getVar('CCACHE_DISABLE')))):
        d.appendVar('DEPENDS', ' ccache-native')
        d.setVar('CCACHE', 'ccache ')
}

addtask cleanccache after do_clean
python do_cleanccache() {
    import shutil

    ccache_dir = d.getVar('CCACHE_DIR')
    if os.path.exists(ccache_dir):
        bb.note("Removing %s" % ccache_dir)
        shutil.rmtree(ccache_dir)
    else:
        bb.note("%s doesn't exist" % ccache_dir)
}
addtask cleanall after do_cleanccache
do_cleanccache[nostamp] = "1"
