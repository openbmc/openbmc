SUMMARY = "Security-focused ELF files checking tool"
DESCRIPTION = "This is a small set of various PaX aware and related \
utilities for ELF binaries. It can check ELF binary files and running \
processes for issues that might be relevant when using ELF binaries \
along with PaX, such as non-PIC code or executable stack and heap."
HOMEPAGE = "http://www.gentoo.org/proj/en/hardened/pax-utils.xml"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "http://gentoo.osuosl.org/distfiles/pax-utils-${PV}.tar.xz \
"

SRC_URI[md5sum] = "bc3614322eaf88f167a608ae85d6b207"
SRC_URI[sha256sum] = "2945b3e4df2e0dc95800bfbd3d88ab2243b2ee4ff064bbfc75734f6d9d986a89"

RDEPENDS_${PN} += "bash"

do_configure_prepend() {
    touch ${S}/NEWS ${S}/AUTHORS ${S}/ChangeLog ${S}/README
}

do_install() {
    oe_runmake PREFIX=${D}${prefix} DESTDIR=${D} install
}

BBCLASSEXTEND = "native"

inherit autotools pkgconfig

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'largefile', 'largefile', '', d)} \
"
PACKAGECONFIG[libcap] = "--with-caps, --without-caps, libcap"
PACKAGECONFIG[libseccomp] = "--with-seccomp, --without-seccomp, libseccomp"
PACKAGECONFIG[largefile] = "--enable-largefile,--disable-largefile,,"
PACKAGECONFIG[pyelftools] = "--with-python, --without-python,, pyelftools"
