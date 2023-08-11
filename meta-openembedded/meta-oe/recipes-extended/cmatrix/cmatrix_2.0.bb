SUMMARY = "Terminal based 'The Matrix' screen implementation"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "git://github.com/abishekvashok/cmatrix.git;branch=stable;protocol=https"
SRCREV = "adfdf1656f23e5ab3b52c7d7edf91249a4477e8d"
S = "${WORKDIR}/git"

inherit cmake

DEPENDS += "ncurses"

FILES:${PN} += "${datadir}/* ${libdir}/kbd/*"
