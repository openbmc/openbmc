SUMMARY = "Python module to interface with the pkg-config command line too"
HOMEPAGE = "http://github.com/matze/pkgconfig"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faa7f82be8f220bff6156be4790344fc"

SRC_URI[sha256sum] = "38d612488f0633755a2e7a8acab6c01d20d63dbc31af75e2a9ac98a6f638ca94"

RDEPENDS_${PN} = "pkgconfig \
                 ${PYTHON_PN}-shell \
                 "

inherit pypi setuptools3

BBCLASSEXTEND = "native"

