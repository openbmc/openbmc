SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2-tools"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://doc/LICENSE;md5=a846608d090aa64494c45fc147cc12e3"
SECTION = "tpm"

DEPENDS = "tpm2-abrmd tpm2-tss openssl curl autoconf-archive"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "ae009b3495b44a16faa3d94d41ac9c9d99c71723482efad53c5eea17eeed80fc"

inherit autotools pkgconfig bash-completion
