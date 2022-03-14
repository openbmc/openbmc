DESCRIPTION = "Python Jinja2: A small but fast and easy to use stand-alone template engine written in pure python."
HOMEPAGE = "https://pypi.org/project/Jinja/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=5dc88300786f1c214c1e9827a5229462"

SRC_URI[sha256sum] = "611bb273cd68f3b993fabdc4064fc858c5b47a973cb5aa7999ec1ba405c87cd7"

PYPI_PACKAGE = "Jinja2"

CVE_PRODUCT = "jinja2 jinja"

CLEANBROKEN = "1"

inherit pypi setuptools3
inherit ${@bb.utils.filter('DISTRO_FEATURES', 'ptest', d)}

SRC_URI += " \
	file://run-ptest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
        ${PYTHON_PN}-toml \
	${PYTHON_PN}-unixadmin \
"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-asyncio \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-markupsafe \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers\
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
