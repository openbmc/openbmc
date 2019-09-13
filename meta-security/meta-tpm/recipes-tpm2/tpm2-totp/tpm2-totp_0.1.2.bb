SUMMARY = "Attest the trustworthiness of a device against a human using time-based one-time passwords"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed23833e93c95173c8d8913745e4b4e1"

SECTION = "security/tpm"

DEPENDS = "autoconf-archive libtss2-dev qrencode"

PE = "1"

SRCREV = "15cc8fbc8fe71be9c04c3169ee1f70450d52a51a"
SRC_URI = "git://github.com/tpm2-software/tpm2-totp.git;branch=v0.1.x \
           file://litpm2_totp_build_fix.patch "

inherit autotools-brokensep pkgconfig

S = "${WORKDIR}/git"
