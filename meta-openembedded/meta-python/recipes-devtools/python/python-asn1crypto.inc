DESCRIPTION = "A fast, pure Python library for parsing and serializing ASN.1 structures"
HOMEPAGE = "https://github.com/wbond/asn1crypto"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7439e38f5e04ff62fae436184786b7ca"

PYPI_PACKAGE = "asn1crypto"

SRC_URI[md5sum] = "cf33950b47b5148e29f1d62a6b6c009b"
SRC_URI[sha256sum] = "0b199f211ae690df3db4fd6c1c4ff976497fb1da689193e368eedbadc53d9292"

inherit pypi

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-netclient \
"

BBCLASSEXTEND = "native nativesdk"
