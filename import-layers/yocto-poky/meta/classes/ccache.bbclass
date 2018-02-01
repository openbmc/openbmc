CCACHE = "${@bb.utils.which(d.getVar('PATH'), 'ccache') and 'ccache '}"
export CCACHE_DIR ?= "${TMPDIR}/ccache/${MULTIMACH_TARGET_SYS}/${PN}"
CCACHE_DISABLE[unexport] = "1"

# We need to stop ccache considering the current directory or the
# debug-prefix-map target directory to be significant when calculating
# its hash. Without this the cache would be invalidated every time
# ${PV} or ${PR} change.
export CCACHE_NOHASHDIR ?= "1"

DEPENDS_append_class-target = " ccache-native"
DEPENDS[vardepvalueexclude] = " ccache-native"

do_configure[dirs] =+ "${CCACHE_DIR}"
do_kernel_configme[dirs] =+ "${CCACHE_DIR}"
