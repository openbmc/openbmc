SUMMARY = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5e55731824cf9205cfabeab9a0600887"

SRC_URI[sha256sum] = "6279836d581513a26f1bf235f9acd333bc9115683f14f7e8fae46c98fc50e015"

inherit pypi python_hatchling

DEPENDS += " \
    python3-hatch-vcs-native \
    python3-hatch-fancy-pypi-readme-native \
"

RDEPENDS:${PN}+= " \
    python3-compression \
    python3-ctypes \
    python3-crypt \
"

BBCLASSEXTEND = "native nativesdk"
