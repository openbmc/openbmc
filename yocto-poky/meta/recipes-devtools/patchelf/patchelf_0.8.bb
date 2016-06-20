SRC_URI = "http://nixos.org/releases/${BPN}/${BPN}-${PV}/${BPN}-${PV}.tar.bz2 \
           file://maxsize.patch"
LICENSE = "GPLv3"
SUMMARY = "Tool to allow editing of RPATH and interpreter fields in ELF binaries"

SRC_URI[md5sum] = "5b151e3c83b31f5931b4a9fc01635bfd"
SRC_URI[sha256sum] = "c99f84d124347340c36707089ec8f70530abd56e7827c54d506eb4cc097a17e7"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
