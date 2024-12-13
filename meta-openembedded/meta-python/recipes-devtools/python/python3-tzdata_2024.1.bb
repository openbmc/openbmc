SUMMARY = "Provider of IANA time zone data"
HOMEPAGE = "https://github.com/python/tzdata"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fca9fd5c15a28eb874ba38577a585d48 \
                    file://licenses/LICENSE_APACHE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "2674120f8d891909751c38abcdfd386ac0a5a1127954fbc332af6b5ceae07efd"

inherit pypi python_setuptools_build_meta

inherit ptest

SRC_URI += "file://run-ptest"

RDEPENDS:${PN}-ptest += "\
    python3-attrs \
    python3-pytest \
    python3-pytest-subtests \
    python3-unittest-automake-output \
"

do_install_ptest() {
    cp -rf ${S}/tests/ ${D}${PTEST_PATH}/
	install ${S}/VERSION ${D}${PTEST_PATH}/
}
