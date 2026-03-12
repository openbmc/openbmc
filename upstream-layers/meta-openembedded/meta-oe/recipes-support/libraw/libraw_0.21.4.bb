SUMMARY = "raw image decoder"
LICENSE = "LGPL-2.1-only | CDDL-1.0"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=1501ae0aa3c8544e63f08d6f7bf88a6f"

SRC_URI = "git://github.com/LibRaw/LibRaw.git;branch=0.21-stable;protocol=https;tag=${PV}"
SRCREV = "9646d776c7c61976080a8f2be67928df0750493e"

inherit autotools pkgconfig

DEPENDS = "jpeg jasper lcms"
