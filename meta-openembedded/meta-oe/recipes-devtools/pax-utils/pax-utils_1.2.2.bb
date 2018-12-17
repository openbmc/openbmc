SUMMARY = "Security-focused ELF files checking tool"
DESCRIPTION = "This is a small set of various PaX aware and related \
utilities for ELF binaries. It can check ELF binary files and running \
processes for issues that might be relevant when using ELF binaries \
along with PaX, such as non-PIC code or executable stack and heap."
HOMEPAGE = "http://www.gentoo.org/proj/en/hardened/pax-utils.xml"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "https://dev.gentoo.org/~vapier/dist/pax-utils-${PV}.tar.xz"
SRC_URI[md5sum] = "a580468318f0ff42edf4a8cd314cc942"
SRC_URI[sha256sum] = "7f4a7f8db6b4743adde7582fa48992ad01776796fcde030683732f56221337d9"

RDEPENDS_${PN} += "bash"

export GNULIB_OVERRIDES_WINT_T = "0"

do_configure_prepend() {
    touch ${S}/NEWS ${S}/AUTHORS ${S}/ChangeLog ${S}/README
}

do_install() {
    oe_runmake PREFIX=${D}${prefix} DESTDIR=${D} install
}

BBCLASSEXTEND = "native"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""

PACKAGECONFIG[libcap] = "--with-caps, --without-caps, libcap"
PACKAGECONFIG[libseccomp] = "--with-seccomp, --without-seccomp, libseccomp"
PACKAGECONFIG[pyelftools] = "--with-python, --without-python,, pyelftools"

EXTRA_OECONF += "--enable-largefile"
