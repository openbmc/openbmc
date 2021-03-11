SUMMARY = "Tool to allow editing of RPATH and interpreter fields in ELF binaries"
DESCRIPTION = "PatchELF is a simple utility for modifying existing ELF executables and libraries."
HOMEPAGE = "https://github.com/NixOS/patchelf"

LICENSE = "GPLv3"

SRC_URI = "git://github.com/NixOS/patchelf;protocol=https \
           file://handle-read-only-files.patch \
           file://fix-adjusting-startPage.patch \
           file://fix-phdrs.patch \
           "

SRCREV = "e1e39f3639e39360ceebb2f7ed533cede4623070"

S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
