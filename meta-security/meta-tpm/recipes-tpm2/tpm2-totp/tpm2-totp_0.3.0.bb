SUMMARY = "Attest the trustworthiness of a device against a human using time-based one-time passwords"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed23833e93c95173c8d8913745e4b4e1"

SECTION = "security/tpm"

DEPENDS = "autoconf-archive libtss2-dev qrencode"

PE = "1"

SRCREV = "96a1448753a48974149003bc90ea3990ae8e8d0b"
SRC_URI = "git://github.com/tpm2-software/tpm2-totp.git;branch=master;protocol=https"

inherit autotools-brokensep pkgconfig

S = "${WORKDIR}/git"
