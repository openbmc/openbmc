SUMMARY = "Python bindings for the Apache Thrift RPC system"
HOMEPAGE = "https://pypi.python.org/pypi/amqp/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=df17a59cc9e1327ec49c2285e13ac477"

SRCNAME = "thrift"

SRC_URI = "https://pypi.python.org/packages/source/t/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "91f1c224c46a257bb428431943387dfd"
SRC_URI[sha256sum] = "08f665e4b033c9d2d0b6174d869273104362c80e77ee4c01054a74141e378afa"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
