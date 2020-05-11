SUMMARY = "Simple top-like I/O monitor"
DESCRIPTION = "iotop does for I/O usage what top(1) does for CPU usage. \
    It watches I/O usage information output by the Linux kernel and displays \
    a table of current I/O usage by processes on the system."
HOMEPAGE = "http://guichaz.free.fr/iotop/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

PV .= "+git${SRCPV}"

SRCREV = "1bfb3bc70febb1ffb95146b6dcd65257228099a3"
SRC_URI = "git://repo.or.cz/iotop.git"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_URI = "http://repo.or.cz/iotop.git/tags"
UPSTREAM_CHECK_REGEX = "iotop-(?P<pver>\d+(\.\d+)+)"

inherit distutils3

do_install_append() {
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/site.pyo || true
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/site.py  || true
}

RDEPENDS_${PN} = "python3-curses \
                  python3-codecs python3-ctypes python3-pprint \
                  python3-shell python3-core"
