SUMMARY = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5e55731824cf9205cfabeab9a0600887"

SRC_URI[sha256sum] = "1c97078a80c814273a76b2a298a932eb681c87415c11dee0a6921de7f1b02c3e"

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

do_install_ptest:append() {
    install ${S}/conftest.py ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
