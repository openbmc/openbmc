SUMMARY = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5e55731824cf9205cfabeab9a0600887"

SRC_URI[sha256sum] = "d03ceb89cb322a8fd706d4fb91940737b6642aa36998fe130a9bc96c985eff32"

inherit pypi ptest-python-pytest python_hatchling

DEPENDS += " \
    python3-hatch-vcs-native \
    python3-hatch-fancy-pypi-readme-native \
"

RDEPENDS:${PN} += " \
    python3-compression \
    python3-crypt \
"

RDEPENDS:${PN}-ptest += " \
    python3-hypothesis \
"

BBCLASSEXTEND = "native nativesdk"
