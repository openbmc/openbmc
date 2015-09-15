CCACHE = "${@bb.utils.which(d.getVar('PATH', True), 'ccache') and 'ccache '}"
export CCACHE_DIR ?= "${TMPDIR}/ccache/${MULTIMACH_HOST_SYS}/${PN}"
CCACHE_DISABLE[unexport] = "1"

do_configure[dirs] =+ "${CCACHE_DIR}"
do_kernel_configme[dirs] =+ "${CCACHE_DIR}"

do_clean[cleandirs] += "${CCACHE_DIR}"
