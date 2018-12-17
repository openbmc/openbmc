SUMMARY = "File locking library"
HOMEPAGE = "http://packages.qa.debian.org/libl/liblockfile.html"
SECTION = "libs"
LICENSE = "LGPLv2+ & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=f4ba6ad04fcb05cc30a4cfa5062c55a3"

SRC_URI = "${DEBIAN_MIRROR}/main/libl/liblockfile/liblockfile_1.14.orig.tar.gz \
    ${DEBIAN_MIRROR}/main/libl/liblockfile/liblockfile_1.14-1.debian.tar.bz2;name=1.14-1 \
    file://configure.patch \
    file://0001-Makefile.in-add-DESTDIR.patch \
    file://0001-Makefile.in-install-nfslock.so-and-nfslock.so.0.patch \
    file://liblockfile-fix-install-so-to-man-dir.patch \
"

SRC_URI[md5sum] = "420c056ba0cc4d1477e402f70ba2f5eb"
SRC_URI[sha256sum] = "ab40d4a3e8cbc204f7e87fea637a4e4ddf9a1149aaa0a723a4267febd0b1d060"

SRC_URI[1.14-1.md5sum] = "f9a44928c3477d218c56252712ebc479"
SRC_URI[1.14-1.sha256sum] = "73f9be769e602149391588c28f0f4f5cda131e30fb94c0777dbb23d811ac21ff"

S = "${WORKDIR}/${BPN}"

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

FILES_${PN} += "${libdir}/nfslock.so.*"
FILES_${PN}-dev += "${libdir}/nfslock.so"
