DESCRIPTION = "Colored terminal output for Python's logging module"
HOMEPAGE = "https://coloredlogs.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=764e737b117a38d773609885e8d04f0b"

SRC_URI[sha256sum] = "5e78691e2673a8e294499e1832bb13efcfb44a86b92e18109fa18951093218ab"

inherit pypi setuptools3

do_compile_prepend() {
    sed -ie "s/find_pth_directory(),/'',/g" ${S}/setup.py
}

do_install_append() {
    rm -rf ${D}${datadir}
}

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-humanfriendly \
"

BBCLASSEXTEND = "native"
