SUMMARY = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5e55731824cf9205cfabeab9a0600887"

SRC_URI[sha256sum] = "16d5969b87f0859ef33a48b35d55ac1be6e42ae49d5e853b597db70c35c57e11"

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
