SUMMARY = "Terminal based 'The Matrix' screen implementation"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = " \
    git://github.com/abishekvashok/cmatrix.git;branch=stable;protocol=https \
    file://0001-reproducibility-Prevent-configuration-from-reading-h.patch \
    file://0002-allow-build-with-cmake-4.patch \
"
SRCREV = "adfdf1656f23e5ab3b52c7d7edf91249a4477e8d"

inherit cmake

DEPENDS += "ncurses"

FILES:${PN} += "${datadir}/* ${libdir}/kbd/* ${libdir}/X11/fonts/"
