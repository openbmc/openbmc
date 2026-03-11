SUMMARY = "A fast, pure Python library for parsing and serializing ASN.1 structures"
HOMEPAGE = "https://github.com/wbond/asn1crypto"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b5cda97fbd7959ad47a952651a87051a"

PYPI_PACKAGE = "asn1crypto"

SRC_URI[sha256sum] = "13ae38502be632115abf8a24cbe5f4da52e3b5231990aff31123c805306ccb9c"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target += " \
    python3-codecs \
    python3-crypt \
    python3-ctypes \
    python3-datetime \
    python3-io \
    python3-netclient \
    python3-numbers \
    python3-shell \
"

BBCLASSEXTEND = "native nativesdk"
