SUMMARY = "Classes Without Boilerplate"
HOMEPAGE = "http://www.attrs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5e55731824cf9205cfabeab9a0600887"

SRC_URI[sha256sum] = "8f5c07333d543103541ba7be0e2ce16eeee8130cb0b3f9238ab904ce1e85baff"

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
