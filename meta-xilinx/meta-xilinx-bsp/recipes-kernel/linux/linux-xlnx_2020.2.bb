LINUX_VERSION = "5.4"
SRCREV ?= "62ea514294a0c9a80455e51f1f4de36e66e8c546"

include linux-xlnx.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append = " \
       file://perf-fix-build-with-binutils.patch \
       file://0001-perf-bench-Share-some-global-variables-to-fix-build-.patch \
       file://0001-perf-tests-bp_account-Make-global-variable-static.patch \
       file://0001-perf-cs-etm-Move-definition-of-traceid_list-global-v.patch \
       file://0001-libtraceevent-Fix-build-with-binutils-2.35.patch \
       file://0001-scripts-dtc-Remove-redundant-YYLOC-global-declaratio.patch \
"

