SUMMARY = "runtime performance analyzer"
HOMEPAGE = "https://github.com/iipeace/guider"
BUGTRACKER = "https://github.com/iipeace/guider/issues"
AUTHOR = "Peace Lee <ipeace5@gmail.com>"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c1c00f9d3ed9e24fa69b932b7e7aff2"

PV = "3.9.7+git${SRCPV}"

SRC_URI = "git://github.com/iipeace/${BPN};branch=master;protocol=https"
SRCREV = "459b5189a46023fc98e19888b196bdc2674022fd"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS_${PN} = "python3 python3-core \
        python3-ctypes python3-shell python3-json"
