SUMMARY = "A UNIX tool for secure deletion"
DESCRIPTION = "Wipe is a little command for securely erasing files from \
magnetic media. It compiles under various unix platforms, \
including Linux 2. * , (Open, Net, Free)BSD, aix 4.1, SunOS \
5.5.1, Solaris 2.6. wipe is released under the GPL. Pre-compiled \
packages are available on most Linux distributions. \
Under Debian, the package name is wipe"
HOMEPAGE = "http://lambda-diode.com/software/wipe/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://GPL;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://github.com/berke/wipe.git;branch=master \
    file://support-cross-compile-for-linux.patch \
    file://makefile-add-ldflags.patch \
"
SRCREV = "d9c100c9cd0b1cbbe4359e4d6c9a035d11e7597c"
PV = "0.23+git${SRCPV}"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "linux"

do_install() {
    make install DESTDIR=${D}
}

do_configure[noexec] = "1"
