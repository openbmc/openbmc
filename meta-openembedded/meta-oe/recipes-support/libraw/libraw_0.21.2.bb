SUMMARY = "raw image decoder"
LICENSE = "LGPL-2.1-only | CDDL-1.0"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=1501ae0aa3c8544e63f08d6f7bf88a6f"

SRC_URI = "git://github.com/LibRaw/LibRaw.git;branch=0.21-stable;protocol=https"
SRCREV = "1ef70158d7fde1ced6aaddb0b9443c32a7121d3d"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS = "jpeg jasper lcms"
