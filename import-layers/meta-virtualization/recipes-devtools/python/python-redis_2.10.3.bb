HOMEPAGE = "https://pypi.python.org/pypi/redis/"
SUMMARY = "Python client for Redis key-value store"
DESCRIPTION = "The Python interface to the Redis key-value store."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51d9ad56299ab60ba7be65a621004f27"

PR = "r0"
SRCNAME = "redis"

SRC_URI = "https://pypi.python.org/packages/source/r/redis/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "7619221ad0cbd124a5687458ea3f5289"
SRC_URI[sha256sum] = "a4fb37b02860f6b1617f6469487471fd086dd2d38bbce640c2055862b9c4019c"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

RDEPENDS_${PN} = "redis"
