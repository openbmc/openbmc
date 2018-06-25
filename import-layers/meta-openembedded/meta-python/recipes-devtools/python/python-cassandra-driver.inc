SUMMARY = "DataStax Python Driver for Apache Cassandra"
DESCRIPTION = "A modern, feature-rich and highly-tunable Python client \
library for Apache Cassandra (1.2+) and DataStax Enterprise (3.1+) using \
exclusively Cassandra's binary protocol and Cassandra Query Language v3."
HOMEPAGE = "https://github.com/datastax/python-driver"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"
SRCNAME = "cassandra-driver"

SRC_URI[md5sum] = "5c8b4a3adfc48180e8ec38b8c4e996fd"
SRC_URI[sha256sum] = "61b670fb2ba95d51d91fa7b589aae3666df494713f5d1ed78bb5c510778d77f0"

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
