SUMMARY = "A Python module for decorators, wrappers and monkey patching."
HOMEPAGE = "http://wrapt.readthedocs.org/"
LICENSE = "BSD"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fdfc019b57affbe1d7a32e3d34e83db4"

SRC_URI[md5sum] = "9f21acd7c0a57bf3057715c4665db3c7"
SRC_URI[sha256sum] = "4aea003270831cceb8a90ff27c4031da6ead7ec1886023b80ce0dfe0adf61533"

inherit pypi

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"
