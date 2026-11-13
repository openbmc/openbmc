SUMMARY = "Tools to manipulate font files"
DESCRIPTION = "A library to manipulate font files from Python."
HOMEPAGE = "http://github.com/fonttools/fonttools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=211c9e4671bde3881351f22a2901f692"

SRC_URI[sha256sum] = "73294e233070f71e2b3653b6bd8564af676d57f26d075761fb06d1ab2e4a7a5d"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core"

BBCLASSEXTEND = "native nativesdk"
