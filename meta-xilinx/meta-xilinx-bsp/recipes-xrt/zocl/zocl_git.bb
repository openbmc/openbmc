SUMMARY  = "Xilinx Runtime(XRT) driver module"
DESCRIPTION = "Xilinx Runtime driver module provides memory management and compute unit schedule"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/Xilinx/XRT.git;protocol=https"

PV = "2.2.0+git${SRCPV}"
SRCREV = "69a7e181d8d53c3b5dde9e8f17ace790141eaa01"

S = "${WORKDIR}/git/src/runtime_src/driver/zynq/drm/zocl"

inherit module
