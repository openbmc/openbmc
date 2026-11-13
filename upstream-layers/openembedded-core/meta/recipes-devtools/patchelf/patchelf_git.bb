SUMMARY = "Tool to allow editing of RPATH and interpreter fields in ELF binaries"
DESCRIPTION = "PatchELF is a simple utility for modifying existing ELF executables and libraries."
HOMEPAGE = "https://github.com/NixOS/patchelf"

LICENSE = "GPL-3.0-only"

SRC_URI = "git://github.com/NixOS/patchelf;protocol=https;branch=master;tag=${PV}"
SRCREV = "2b4b49d36b62690860414dd127f567be4f1f3208"

PV = "0.19.3"

LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e"

inherit meson

PACKAGES += "${PN}-zsh-completion"
FILES:${PN}-zsh-completion = "${datadir}/zsh"

BBCLASSEXTEND = "native nativesdk"
