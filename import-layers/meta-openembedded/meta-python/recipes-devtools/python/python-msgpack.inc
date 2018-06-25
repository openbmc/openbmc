SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

SRC_URI[md5sum] = "6d644c06a87a5a111bbbf5b34b4be440"
SRC_URI[sha256sum] = "378cc8a6d3545b532dfd149da715abae4fda2a3adb6d74e525d0d5e51f46909b"

PYPI_PACKAGE = "msgpack-python"
inherit pypi

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-io \
"
