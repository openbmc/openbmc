SUMMARY = "Attest the trustworthiness of a device against a human using time-based one-time passwords"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed23833e93c95173c8d8913745e4b4e1"

SECTION = "security/tpm"

DEPENDS = "autoconf-archive libtss2-dev qrencode"

PE = "1"

SRCREV = "994b4203e4769baefa6e7719915629bc8210e90a"
SRC_URI = "git://github.com/tpm2-software/tpm2-totp.git;branch=v0.2.x \
          "

inherit autotools-brokensep pkgconfig

S = "${WORKDIR}/git"
