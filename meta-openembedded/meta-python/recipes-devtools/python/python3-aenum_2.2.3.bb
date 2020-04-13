SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[md5sum] = "026786dbb37c15c2c8dc91fbf5828e97"
SRC_URI[sha256sum] = "a4334cabf47c167d44ab5a6198837b80deec5d5bad1b5cf70c966c3a330260e8"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
