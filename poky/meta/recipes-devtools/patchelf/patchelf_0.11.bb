SRC_URI = "git://github.com/NixOS/patchelf;protocol=https \
           file://handle-read-only-files.patch \
           "

LICENSE = "GPLv3"
SUMMARY = "Tool to allow editing of RPATH and interpreter fields in ELF binaries"

SRCREV = "d6b2a72d9ec3bdfde4b1aacdada823ce388968bb"

S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
