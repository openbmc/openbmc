SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

PR = "r0"

SRC_URI[md5sum] = "eb2aad1081534ef3a9f32a0ecd350b9b"
SRC_URI[sha256sum] = "5e001229a54180a02dcdd59db23c9978351af55b1290c27bc549e381f43acd6b"

PYPI_PACKAGE = "msgpack-python"
inherit pypi setuptools
