SUMMARY  = "Xilinx Runtime(XRT) driver module"
DESCRIPTION = "Xilinx Runtime driver module provides memory management and compute unit schedule"

LICENSE = "GPLv2 & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7d040f51aae6ac6208de74e88a3795f8"

BRANCH ?= "2020.1"
REPO ?= "git://github.com/Xilinx/XRT.git;protocol=https"
BRANCHARG = "${@['nobranch=1', 'branch=${BRANCH}'][d.getVar('BRANCH', True) != '']}"
SRC_URI = "${REPO};${BRANCHARG}"

PV = "202010.2.6.0"
SRCREV ?= "2d6bfe4ce91051d4e5b499d38fc493586dd4859a"

S = "${WORKDIR}/git/src/runtime_src/core/edge/drm/zocl"

inherit module

pkg_postinst_ontarget_${PN}() {
  #!/bin/sh
  echo "Unloading old XRT Linux kernel modules"
  ( rmmod zocl || true ) > /dev/null 2>&1
  echo "Loading new XRT Linux kernel modules"
  modprobe zocl
}
