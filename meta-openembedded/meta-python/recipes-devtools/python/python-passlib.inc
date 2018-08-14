SUMMARY = "comprehensive password hashing framework supporting over 30 schemes"
DESCRIPTION = "\
Passlib is a password hashing library for Python 2 & 3, which provides cross-platform \
implementations of over 30 password hashing algorithms, as well as a framework for \
managing existing password hashes. It’s designed to be useful for a wide range of \
tasks, from verifying a hash found in /etc/shadow, to providing full-strength password \
hashing for multi-user applications."
HOMEPAGE = "https://bitbucket.org/ecollins/passlib"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=73eed1a5470b969951dac411086c7036"

SRC_URI[md5sum] = "254869dae3fd9f09f0746a3cb29a0b15"
SRC_URI[sha256sum] = "3d948f64138c25633613f303bcc471126eae67c04d5e3f6b7b8ce6242f8653e0"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-netclient \
"
