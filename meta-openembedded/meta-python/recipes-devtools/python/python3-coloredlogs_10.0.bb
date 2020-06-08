DESCRIPTION = "Colored terminal output for Python's logging module"
HOMEPAGE = "https://coloredlogs.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=690da298a43805797a4fa7bbe180b3c6"

SRC_URI[md5sum] = "0a186966a1955fff8cf9489373e691d9"
SRC_URI[sha256sum] = "b869a2dda3fa88154b9dd850e27828d8755bfab5a838a1c97fbc850c6e377c36"

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
