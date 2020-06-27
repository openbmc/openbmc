DESCRIPTION = "Colored terminal output for Python's logging module"
HOMEPAGE = "https://coloredlogs.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=764e737b117a38d773609885e8d04f0b"

SRC_URI[md5sum] = "34cd8ef3f758b10c6f1482b2614a81f2"
SRC_URI[sha256sum] = "a1fab193d2053aa6c0a97608c4342d031f1f93a3d1218432c59322441d31a505"

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
