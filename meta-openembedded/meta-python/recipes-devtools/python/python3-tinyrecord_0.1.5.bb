SUMMARY = "transaction support for TinyDB"
DESCRIPTION = "\
Tinyrecord is a library which implements atomic transaction \
support for the TinyDB NoSQL database. It uses a record-first \
then execute architecture which allows us to minimize the time \
that we are within a thread lock."
HOMEPAGE = "https://github.com/eugene-eeo/tinyrecord"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README;md5=31c1dc11b4ae83546538de4c16bceabc"

SRC_URI[md5sum] = "e47dcfe299686cd3fa7ffaa7cb2ee8b1"
SRC_URI[sha256sum] = "bc7e6a8e78600df234d7a85c2f5d584130f2c6ffd7cd310f9d3a1d976d3373c8"

PYPI_PACKAGE = "tinyrecord"
inherit pypi setuptools3
