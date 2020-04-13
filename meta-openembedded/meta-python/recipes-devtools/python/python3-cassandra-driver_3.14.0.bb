SUMMARY = "DataStax Python Driver for Apache Cassandra"
DESCRIPTION = "A modern, feature-rich and highly-tunable Python client \
library for Apache Cassandra (1.2+) and DataStax Enterprise (3.1+) using \
exclusively Cassandra's binary protocol and Cassandra Query Language v3."
HOMEPAGE = "https://github.com/datastax/python-driver"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"
SRCNAME = "cassandra-driver"

SRC_URI[md5sum] = "c5bed026bf48c821424c1f6296193908"
SRC_URI[sha256sum] = "b65218e2582277f5b77d1436e420db8616f63e3437a9e839cdcd7172d760e861"

DISTUTILS_BUILD_ARGS += " \
    --no-libev \
"
DISTUTILS_INSTALL_ARGS += " \
    --no-libev \
"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-cython \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-six \
    libevent \
"

DEPENDS += "\
    ${PYTHON_PN}-cython \
"
