SUMMARY = "Python package for parsing and generating vCard and vCalendar files"
HOMEPAGE = "http://vobject.skyhouseconsulting.com/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "ccf66aeb1c896d8c34ac62a8b4e7ecfb"
SRC_URI[sha256sum] = "8b310c21a4d58e13aeb7e60fd846a1748e1c9c3374f3e2acc96f728c3ae5d6e1"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-numbers \
    "
