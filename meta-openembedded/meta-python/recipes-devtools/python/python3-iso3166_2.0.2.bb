SUMMARY = "Self-contained ISO 3166-1 country definitions"
HOMEPAGE = "https://pypi.org/project/iso3166/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5e2f4edc7e7408a82e4a1d05f229b695"

SRC_URI[sha256sum] = "04d02cfcfc18a6f8a9a4edb4d0a55e2e4fc575626c29d702f750de415e88d372"

inherit pypi python_setuptools_build_meta ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += "python3-numbers"

BBCLASSEXTEND = "native nativesdk"
