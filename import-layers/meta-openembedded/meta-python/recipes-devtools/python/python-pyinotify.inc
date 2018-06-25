DESCRIPTION = "Python pyinotify: Linux filesystem events monitoring"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ab173cade7965b411528464589a08382"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-shell \
"

SRC_URI[md5sum] = "8e580fa1ff3971f94a6f81672b76c406"
SRC_URI[sha256sum] = "9c998a5d7606ca835065cdabc013ae6c66eb9ea76a00a1e3bc6e0cfe2b4f71f4"

inherit pypi
