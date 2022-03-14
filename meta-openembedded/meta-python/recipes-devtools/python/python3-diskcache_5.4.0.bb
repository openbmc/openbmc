DESCRIPTION = "Disk Cache -- Disk and file backed persistent cache."
HOMEPAGE = "http://www.grantjenks.com/docs/diskcache/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c60ef82f0f40155453f6d5f2c94b6e8e"

SRC_URI[sha256sum] = "8879eb8c9b4a2509a5e633d2008634fb2b0b35c2b36192d89655dbde02419644"

PYPI_PACKAGE = "diskcache"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-sqlite3 \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-threading \
"

CLEANBROKEN = "1"
