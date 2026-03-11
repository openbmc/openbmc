SUMMARY = "transaction support for TinyDB"
DESCRIPTION = "\
Tinyrecord is a library which implements atomic transaction \
support for the TinyDB NoSQL database. It uses a record-first \
then execute architecture which allows us to minimize the time \
that we are within a thread lock."
HOMEPAGE = "https://github.com/eugene-eeo/tinyrecord"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ee157eec4b228c8d5b7a6e0feab2864a"

SRC_URI[sha256sum] = "eb6dc23601be359ee00f5a3d31a46adf3bad0a16f8d60af216cd67982ca75cf4"

PYPI_PACKAGE = "tinyrecord"
inherit pypi setuptools3
