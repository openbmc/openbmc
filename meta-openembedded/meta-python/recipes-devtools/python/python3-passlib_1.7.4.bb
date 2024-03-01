SUMMARY = "comprehensive password hashing framework supporting over 30 schemes"
DESCRIPTION = "\
Passlib is a password hashing library for Python 2 & 3, which provides cross-platform \
implementations of over 30 password hashing algorithms, as well as a framework for \
managing existing password hashes. It’s designed to be useful for a wide range of \
tasks, from verifying a hash found in /etc/shadow, to providing full-strength password \
hashing for multi-user applications."
HOMEPAGE = "https://foss.heptapod.net/python-libs/passlib/wikis/home"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c8449c5e10474d113ab787ed2753bafe"

SRC_URI[md5sum] = "3a229cbd00dfb33e99885b43325e0591"
SRC_URI[sha256sum] = "defd50f72b65c5402ab2c573830a6978e5f202ad0d984793c8dde2c4152ebe04"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-crypt \
    python3-logging \
    python3-netclient \
"
