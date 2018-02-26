SUMMARY = "File locking library"
HOMEPAGE = "http://packages.qa.debian.org/libl/liblockfile.html"
SECTION = "libs"
LICENSE = "LGPLv2+ & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=ac284a60d48eaa4bc811cddc377fa341"

SRC_URI = "${DEBIAN_MIRROR}/main/libl/liblockfile/liblockfile_1.09.orig.tar.gz \
    ${DEBIAN_MIRROR}/main/libl/liblockfile/liblockfile_1.09-6.debian.tar.bz2;name=1.09-6 \
    file://install.patch \
    file://configure.patch \
    file://ldflags.patch \
    file://liblockfile-fix-nfslib-and-soname.patch \
    file://liblockfile-fix-install-so-to-man-dir.patch \
"

SRC_URI[md5sum] = "2aa269e4405ee8235ff17d1b357c6ae8"
SRC_URI[sha256sum] = "16979eba05396365e1d6af7100431ae9d32f9bc063930d1de66298a0695f1b7f"

SRC_URI[1.09-6.md5sum] = "a2811807e63a526c07b0f60626e329a2"
SRC_URI[1.09-6.sha256sum] = "d45eacb7c637c16d03c777c55989d98da494ae9584a0783fe6dbf0db60fa290f"

inherit autotools-brokensep

# set default mailgroup to mail
# --with-libnfslock specify where to install nfslock.so.NVER
EXTRA_OECONF = "--enable-shared \
                --with-mailgroup=mail \
                --with-libnfslock=${libdir} \
"

# Makefile using ROOT not DESTDIR
EXTRA_OEMAKE += "ROOT=${D}"

FILES_${PN} += "${libdir}/nfslock.so.*"
FILES_${PN}-dev += "${libdir}/nfslock.so"
