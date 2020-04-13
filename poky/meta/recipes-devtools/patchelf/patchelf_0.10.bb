SRC_URI = "https://nixos.org/releases/${BPN}/${BPN}-${PV}/${BPN}-${PV}.tar.bz2 \
           file://handle-read-only-files.patch \
           file://fix-adjusting-startPage.patch \
           "

LICENSE = "GPLv3"
SUMMARY = "Tool to allow editing of RPATH and interpreter fields in ELF binaries"

SRC_URI[md5sum] = "6c3f3a06a95705870d129494a6880106"
SRC_URI[sha256sum] = "f670cd462ac7161588c28f45349bc20fb9bd842805e3f71387a320e7a9ddfcf3"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
