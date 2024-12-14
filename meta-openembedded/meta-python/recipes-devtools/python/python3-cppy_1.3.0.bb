SUMMARY = "C++ headers for C extension development"
HOMEPAGE = "https://cppy.readthedocs.io/en/latest/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0bfb3e39b13587f0028f17baf0e42371"

SRC_URI[sha256sum] = "da7413a286d5d31626ba35ed2c70ddfb033520cc81310088ba5a57d34039f604"

RDEPENDS:${PN} += "python3-setuptools"

inherit pypi python_setuptools_build_meta

SRC_URI += " file://0001-Fix-build-error-as-following.patch \
           "

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
