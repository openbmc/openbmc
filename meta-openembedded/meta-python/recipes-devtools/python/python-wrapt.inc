SUMMARY = "A Python module for decorators, wrappers and monkey patching."
HOMEPAGE = "http://wrapt.readthedocs.org/"
LICENSE = "BSD"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fdfc019b57affbe1d7a32e3d34e83db4"

SRC_URI[md5sum] = "cc15c001b129f81eb2f79b15eb99ffe5"
SRC_URI[sha256sum] = "565a021fd19419476b9362b05eeaa094178de64f8361e44468f9e9d7843901e1"

inherit pypi

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"
