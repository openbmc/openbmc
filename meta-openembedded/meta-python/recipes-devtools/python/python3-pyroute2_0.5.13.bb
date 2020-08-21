SUMMARY = "A pure Python netlink and Linux network configuration library"
LICENSE = "GPLv2 & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.GPL.v2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.Apache.v2;md5=34281e312165f843a2b7d1f114fe65ce"

SRC_URI[md5sum] = "6c72ffa7147bbfa1610942e5734c8c87"
SRC_URI[sha256sum] = "518365f3313e73b0f024b9fa7a580b29bfa2fe2c5230be0bc69c068bbf6637e9"

inherit setuptools3 pypi ptest

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-distutils \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-pkgutil \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-unixadmin \
"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
	${PYTHON_PN}-fcntl \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
