SUMMARY = "raw image decoder"
LICENSE = "LGPL-2.1-only | CDDL-1.0"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=1d66195044cfbe4327c055d1c9c1a229"

SRC_URI = "git://github.com/LibRaw/LibRaw.git;branch=0.22-stable;protocol=https;tag=${PV}"
SRCREV = "b860248a89d9082b8e0a1e202e516f46af9adb29"

inherit autotools pkgconfig

DEPENDS = "jpeg jasper lcms"

CVE_STATUS[CVE-2026-5318] = "fixed-version: fixed since 0.22.1"
CVE_STATUS[CVE-2026-5342] = "fixed-version: fixed since 0.22.1"
