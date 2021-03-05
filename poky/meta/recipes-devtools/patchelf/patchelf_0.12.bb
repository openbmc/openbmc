SUMMARY = "Tool to allow editing of RPATH and interpreter fields in ELF binaries"
DESCRIPTION = "PatchELF is a simple utility for modifying existing ELF executables and libraries."
HOMEPAGE = "https://github.com/NixOS/patchelf"

LICENSE = "GPLv3"

SRC_URI = "git://github.com/NixOS/patchelf;protocol=https \
           file://handle-read-only-files.patch \
           "
SRCREV = "8d3a16e97294e3c5521c61b4c8835499c9918264"

S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
