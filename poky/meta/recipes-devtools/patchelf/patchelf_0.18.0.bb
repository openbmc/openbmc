SUMMARY = "Tool to allow editing of RPATH and interpreter fields in ELF binaries"
DESCRIPTION = "PatchELF is a simple utility for modifying existing ELF executables and libraries."
HOMEPAGE = "https://github.com/NixOS/patchelf"

LICENSE = "GPL-3.0-only"

SRC_URI = "git://github.com/NixOS/patchelf;protocol=https;branch=master \
    file://0001-Set-interpreter-only-when-necessary.patch \
    file://0002-align-startOffset-with-p_align-instead-of-pagesize-f.patch \
    file://0003-make-LOAD-segment-extensions-based-on-p_align-instea.patch \
"
SRCREV = "99c24238981b7b1084313aca8f5c493bb46f302c"

S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools

PACKAGES += "${PN}-zsh-completion"
FILES:${PN}-zsh-completion = "${datadir}/zsh"

BBCLASSEXTEND = "native nativesdk"
