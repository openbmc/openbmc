SUMMARY	    = "PowerPC FSI Debugger"
DESCRIPTION = "pdbg allows JTAG-like debugging of the host POWER processors from the BMC."
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/open-power/pdbg.git"
SRC_URI = "file://0001-Makefile-Allow-out-of-tree-builds.patch"

FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"

SRCREV = "90a7370a11e727f1482dea6ff2bd6aec20c64805"
PV = "git${SRCREV}"

S = "${WORKDIR}/git"

inherit autotools
