SUMMARY = "DataStax Python Driver for Apache Cassandra"
DESCRIPTION = "A modern, feature-rich and highly-tunable Python client \
library for Apache Cassandra (1.2+) and DataStax Enterprise (3.1+) using \
exclusively Cassandra's binary protocol and Cassandra Query Language v3."
HOMEPAGE = "https://github.com/datastax/python-driver"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"
SRCNAME = "cassandra-driver"

SRC_URI[md5sum] = "7561a89cd8a432a22cf33abef76b64ee"
SRC_URI[sha256sum] = "10b0c88854f3bddcfd994cb208d780507de62524ef8dcb4f068c084d335b7054"

DISTUTILS_BUILD_ARGS += " \
    --no-libev \
"
DISTUTILS_INSTALL_ARGS += " \
    --no-libev \
"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-cython \
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-json \
    libevent \
"

DEPENDS_${PN} += "\
    ${PYTHON_PN}-cython \
"
