LINUX_VERSION = "5.4"
SRCREV ?= "22b71b41620dac13c69267d2b7898ebfb14c954e"

include linux-xlnx.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append = " file://perf-fix-build-with-binutils.patch"


