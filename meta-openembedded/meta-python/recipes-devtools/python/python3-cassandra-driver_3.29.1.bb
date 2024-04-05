SUMMARY = "DataStax Python Driver for Apache Cassandra"
DESCRIPTION = "A modern, feature-rich and highly-tunable Python client \
library for Apache Cassandra (1.2+) and DataStax Enterprise (3.1+) using \
exclusively Cassandra's binary protocol and Cassandra Query Language v3."
HOMEPAGE = "https://github.com/datastax/python-driver"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"
SRCNAME = "cassandra-driver"

SRC_URI[sha256sum] = "38e9c2a2f2a9664bb03f1f852d5fccaeff2163942b5db35dffcf8bf32a51cfe5"

inherit pypi setuptools3

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
