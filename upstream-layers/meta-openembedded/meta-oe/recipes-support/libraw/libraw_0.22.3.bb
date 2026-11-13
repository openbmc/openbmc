SUMMARY = "raw image decoder"
LICENSE = "CDDL-1.0 OR LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=1d66195044cfbe4327c055d1c9c1a229"

SRC_URI = "git://github.com/LibRaw/LibRaw.git;branch=0.22-stable;protocol=https;tag=${PV}"
SRCREV = "cf02d66eb5c19d4e46df7aee0582e47a4918509a"

inherit autotools pkgconfig

DEPENDS = "jpeg jasper lcms"

CVE_STATUS[CVE-2026-5318] = "fixed-version: fixed since 0.22.1"
CVE_STATUS[CVE-2026-5342] = "fixed-version: fixed since 0.22.1"
CVE_STATUS[CVE-2026-20884] = "fixed-version: fixed since 0.22.1"
CVE_STATUS[CVE-2026-24450] = "fixed-version: fixed since 0.22.1"
CVE_STATUS[CVE-2026-20911] = "fixed-version: fixed since 0.22.1"
CVE_STATUS[CVE-2026-21413] = "fixed-version: fixed since 0.22.1"
