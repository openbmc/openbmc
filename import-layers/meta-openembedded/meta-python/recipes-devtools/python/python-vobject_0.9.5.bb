SUMMARY = "Python package for parsing and generating vCard and vCalendar files"
HOMEPAGE = "http://vobject.skyhouseconsulting.com/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "aa629d6ae95db5edfd5b2402eb1073cb"
SRC_URI[sha256sum] = "0f56cae196303d875682b9648b4bb43ffc769d2f0f800958e0a506af867b1243"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-numbers \
    "
