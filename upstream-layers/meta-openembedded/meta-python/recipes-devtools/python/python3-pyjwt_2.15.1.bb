SUMMARY = "JSON Web Token implementation in Python"
DESCRIPTION = "A Python implementation of JSON Web Token draft 32.\
 Original implementation was written by https://github.com/progrium"
HOMEPAGE = "https://github.com/jpadilla/pyjwt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4b56d2c9973d8cf54655555be06e551"

SRC_URI[sha256sum] = "4f259e80cdfb6b3fc18a7de51fd1ef9ec79652f25019bae68975ca2468a34df8"

CVE_PRODUCT = "pyjwt"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "\
    python3-cryptography \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"

