SUMMARY = "performance analyzer"
HOMEPAGE = "https://github.com/iipeace/guider"
BUGTRACKER = "https://github.com/iipeace/guider/issues"
AUTHOR = "Peace Lee <ipeace5@gmail.com>"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c1c00f9d3ed9e24fa69b932b7e7aff2"

PV = "3.9.8+git${SRCPV}"

SRC_URI = "git://github.com/iipeace/${BPN}"
SRCREV = "a502cd93b13235b7539557a91328de00b7c51bc3"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS_${PN} = "python3 python3-core \
        python3-ctypes python3-shell python3-json"
