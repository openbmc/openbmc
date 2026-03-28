SUMMARY = "DataStax Python Driver for Apache Cassandra"
DESCRIPTION = "A modern, feature-rich and highly-tunable Python client \
library for Apache Cassandra (1.2+) and DataStax Enterprise (3.1+) using \
exclusively Cassandra's binary protocol and Cassandra Query Language v3."
HOMEPAGE = "https://github.com/datastax/python-driver"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"
SRCNAME = "cassandra-driver"

SRC_URI += "file://0001-skip-verifying-the-availability-of-setuptools.patch"
SRC_URI[sha256sum] = "ff6b82ee4533f6fd4474d833e693b44b984f58337173ee98ed76bce08721a636"

PYPI_PACKAGE = "cassandra_driver"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-cython \
    python3-geomet \
    python3-json \
    python3-misc \
    python3-multiprocessing \
    python3-numbers \
    python3-six \
    libevent \
"

DEPENDS += "\
    python3-cython \
"
