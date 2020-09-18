SUMMARY = "Little cms is a small-footprint, speed optimized color management engine"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=9391499b030def18e7bb25bab4bee052"

SRC_URI = "${SOURCEFORGE_MIRROR}/lcms/lcms2-${PV}.tar.gz"
SRC_URI[md5sum] = "598dae499e58f877ff6788254320f43e"
SRC_URI[sha256sum] = "dc49b9c8e4d7cdff376040571a722902b682a795bf92985a85b48854c270772e"

DEPENDS = "tiff"

BBCLASSEXTEND = "native"

S = "${WORKDIR}/lcms2-${PV}"

inherit autotools
