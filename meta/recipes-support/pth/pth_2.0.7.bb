SUMMARY = "GNU Portable Threads library"
HOMEPAGE = "http://www.gnu.org/software/pth/"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;beginline=12;endline=15;md5=a48af114a80c222cafd37f24370a77b1"
PR = "r3"

python __anonymous () {
    import re
    uc_os = (re.match('.*uclibc*', d.getVar('TARGET_OS', True)) != None)
    if uc_os:
        raise bb.parse.SkipPackage("incompatible with uClibc")
}

SRC_URI = "${GNU_MIRROR}/pth/pth-${PV}.tar.gz \
          file://pth-add-pkgconfig-support.patch \
          file://pth-fix-parallel.patch \
          "

SRC_URI[md5sum] = "9cb4a25331a4c4db866a31cbe507c793"
SRC_URI[sha256sum] = "72353660c5a2caafd601b20e12e75d865fd88f6cf1a088b306a3963f0bc77232"

BINCONFIG = "${bindir}/pth-config"

inherit autotools binconfig-disabled pkgconfig

do_configure() {
	( cd ${S}; gnu-configize )
	( cd ${S}; autoconf )
	oe_runconf
}
