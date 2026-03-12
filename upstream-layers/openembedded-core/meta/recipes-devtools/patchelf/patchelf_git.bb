SUMMARY = "Tool to allow editing of RPATH and interpreter fields in ELF binaries"
DESCRIPTION = "PatchELF is a simple utility for modifying existing ELF executables and libraries."
HOMEPAGE = "https://github.com/NixOS/patchelf"

LICENSE = "GPL-3.0-only"

SRC_URI = "git://github.com/NixOS/patchelf;protocol=https;branch=master \
"
SRCREV = "b49de1b3384e7928bf0df9a889fe5a4e7b3fbddf"

PV = "0.18.0+git"

LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e"

inherit autotools

PACKAGES += "${PN}-zsh-completion"
FILES:${PN}-zsh-completion = "${datadir}/zsh"

BBCLASSEXTEND = "native nativesdk"
