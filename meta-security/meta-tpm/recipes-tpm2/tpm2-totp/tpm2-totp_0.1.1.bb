SUMMARY = "Attest the trustworthiness of a device against a human using time-based one-time passwords"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed23833e93c95173c8d8913745e4b4e1"

SECTION = "security/tpm"

DEPENDS = "autoconf-archive libtss2-dev qrencode"

PE = "1"

SRCREV = "2807a509a9da383e14dc0f759e71fd676db04ab1"
SRC_URI = "git://github.com/tpm2-software/tpm2-totp.git;branch=v0.1.x \
           file://litpm2_totp_build_fix.patch "

inherit autotools-brokensep pkgconfig

S = "${WORKDIR}/git"
