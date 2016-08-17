DESCRIPTION = "Simple integration of Flask and WTForms."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=507e8635f25a06dc4f041a3a1b3359b3"

SRC_URI[md5sum] = "c53a74e8ba481bf53405fd5efdf0339e"
SRC_URI[sha256sum] = "bd99316c97ed1d1cb90b8f0c242c86420a891a6a2058f20717e424bf5b0bb80e"

SRC_URI += " file://import-simplejson-as-json.patch"

PYPI_PACKAGE = "Flask-WTF"

inherit pypi setuptools

RDEPENDS_${PN} = "\
    python-wtforms \
    python-simplejson \
"
