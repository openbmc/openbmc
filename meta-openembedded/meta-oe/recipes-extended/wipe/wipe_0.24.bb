SUMMARY = "A UNIX tool for secure deletion"
DESCRIPTION = "Wipe is a little command for securely erasing files from \
magnetic media. It compiles under various unix platforms, \
including Linux 2. * , (Open, Net, Free)BSD, aix 4.1, SunOS \
5.5.1, Solaris 2.6. wipe is released under the GPL. Pre-compiled \
packages are available on most Linux distributions. \
Under Debian, the package name is wipe"
HOMEPAGE = "http://lambda-diode.com/software/wipe/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://GPL;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://github.com/berke/wipe.git;branch=master;protocol=https \
    file://support-cross-compile-for-linux.patch \
    file://makefile-add-ldflags.patch \
"
SRCREV = "796b62293e007546e051619bd03f5ba338ef28e5"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "linux"

do_install() {
    make install DESTDIR=${D}
}

do_configure[noexec] = "1"
