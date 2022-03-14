SUMMARY = "Provider for integration of TPM 2.0 to OpenSSL 3.0"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b75785ac083d3c3ca04d99d9e4e1fbab"

SRC_URI = "git://github.com/tpm2-software/tpm2-openssl.git;protocol=https;branch=master"

SRCREV = "66e34f9e45c3697590cced1e4d3f35993a822f8b"

S = "${WORKDIR}/git"

inherit pkgconfig
