SUMMARY = "raw image decoder"
LICENSE = "LGPL-2.1-only | CDDL-1.0"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=1501ae0aa3c8544e63f08d6f7bf88a6f"

SRC_URI = "git://github.com/LibRaw/LibRaw.git;branch=master;protocol=https"
SRCREV = "cccb97647fcee56801fa68231fa8a38aa8b52ef7"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS = "jpeg jasper lcms"
