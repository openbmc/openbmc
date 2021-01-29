SUMMARY = "Library to parse JSON with js-style comments."
HOMEPAGE = "https://github.com/linjackson78/jstyleson"

SRC_URI += " file://LICENSE "

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=d97b96c7337934ee448ffd4392f32079"

PYPI_PACKAGE_EXT = "tar.gz"

inherit pypi setuptools3

SRC_URI[sha256sum] = "680003f3b15a2959e4e6a351f3b858e3c07dd3e073a0d54954e34d8ea5e1308e"

BBCLASSEXTEND = "native nativesdk"
