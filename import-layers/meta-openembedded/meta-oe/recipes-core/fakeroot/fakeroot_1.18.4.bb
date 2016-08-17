SUMMARY = "Gives a fake root environment"
HOMEPAGE = "http://fakeroot.alioth.debian.org"
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

SRC_URI = "\
    ${DEBIAN_MIRROR}/main/f/fakeroot/fakeroot_${PV}.orig.tar.bz2 \
"

inherit autotools

do_configure_prepend() {
    mkdir -p ${S}/build-aux
}

do_install_append() {
    install -d ${D}${includedir}/fakeroot
    install -m 644 *.h ${D}${includedir}/fakeroot
}

# fakeroot needs getopt which is provided by the util-linux package
RDEPENDS_${PN} = "util-linux"


SRC_URI[md5sum] = "706171d8d520b1ca1576ac73f2ceb4f3"
SRC_URI[sha256sum] = "0a359efa3e9496c33234b3e9c89306a09bb4da9d33de43c261f1d8447e6ebea2"

# http://errors.yoctoproject.org/Errors/Details/35143/
PNBLACKLIST[fakeroot] ?= "BROKEN: QA Issue: -dev package contains non-symlink .so"
