SUMMARY = "comprehensive password hashing framework supporting over 30 schemes"
DESCRIPTION = "\
Passlib is a password hashing library for Python 2 & 3, which provides cross-platform \
implementations of over 30 password hashing algorithms, as well as a framework for \
managing existing password hashes. It’s designed to be useful for a wide range of \
tasks, from verifying a hash found in /etc/shadow, to providing full-strength password \
hashing for multi-user applications."
HOMEPAGE = "https://bitbucket.org/ecollins/passlib"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=44fd7dcd5d42b48d6dea59ac643a0179"

SRC_URI[md5sum] = "b908529cfd4c33057c244324c692eae7"
SRC_URI[sha256sum] = "8d666cef936198bc2ab47ee9b0410c94adf2ba798e5a84bf220be079ae7ab6a8"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-netclient \
"
