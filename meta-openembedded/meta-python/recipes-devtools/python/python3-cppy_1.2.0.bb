SUMMARY = "C++ headers for C extension development"
HOMEPAGE = "https://cppy.readthedocs.io/en/latest/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0bfb3e39b13587f0028f17baf0e42371"

SRC_URI[sha256sum] = "95e8862e4f826c3f2a6b7b658333b162f80cbe9f943aa0d0a7a6b2ef850aeffc"

RDEPENDS:${PN} += "python3-setuptools python3-distutils"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
