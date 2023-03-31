SUMMARY = "File locking library"
HOMEPAGE = "http://packages.qa.debian.org/libl/liblockfile.html"
SECTION = "libs"
LICENSE = "LGPL-2.0-or-later & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=f4ba6ad04fcb05cc30a4cfa5062c55a3"

SRC_URI = "${DEBIAN_MIRROR}/main/libl/liblockfile/liblockfile_1.17.orig.tar.gz \
    ${DEBIAN_MIRROR}/main/libl/liblockfile/liblockfile_1.17-1.debian.tar.bz2;name=1.17-1 \
    file://configure.patch \
    file://0001-Makefile.in-add-DESTDIR.patch \
    file://0001-Makefile.in-install-nfslock-libs.patch \
    file://liblockfile-fix-install-so-to-man-dir.patch \
    file://0001-Makefile.in-redefine-LOCKPROG.patch \
    file://0001-Makefile.in-fix-install-failure-on-host-without-ldco.patch \
"

SRC_URI[sha256sum] = "6e937f3650afab4aac198f348b89b1ca42edceb17fb6bb0918f642143ccfd15e"
SRC_URI[1.17-1.sha256sum] = "e3657c0e3facfeccb58900c0b48d56cd68ad5f9f24d1b4c6eaa69c26490fb673"

S = "${WORKDIR}/${BP}"

inherit autotools-brokensep

# set default mailgroup to mail
# --with-libnfslock specify where to install nfslock.so.NVER
EXTRA_OECONF = "--enable-shared \
                --with-mailgroup=mail \
                --with-libnfslock=${libdir} \
"

# Makefile using DESTDIR as the change in e35f9eabcbba224ecc70b145d5d2a2d81064c195
# at https://github.com/miquels/liblockfile.git
EXTRA_OEMAKE += "DESTDIR=${D}"

FILES:${PN} += "${libdir}/nfslock.so.*"
FILES:${PN}-dev += "${libdir}/nfslock.so"
