SRC_URI = "http://nixos.org/releases/${BPN}/${BPN}-${PV}/${BPN}-${PV}.tar.bz2 \
           file://Skip-empty-section-fixes-66.patch \
           file://handle-read-only-files.patch \
           file://Increase-maxSize-to-64MB.patch \
           file://avoidholes.patch \
           file://fix-adjusting-startPage.patch \
"

LICENSE = "GPLv3"
SUMMARY = "Tool to allow editing of RPATH and interpreter fields in ELF binaries"

SRC_URI[md5sum] = "d02687629c7e1698a486a93a0d607947"
SRC_URI[sha256sum] = "a0f65c1ba148890e9f2f7823f4bedf7ecad5417772f64f994004f59a39014f83"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
