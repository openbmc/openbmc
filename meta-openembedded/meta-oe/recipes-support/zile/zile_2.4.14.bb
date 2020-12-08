SUMMARY = "Zile is lossy Emacs"
HOMEPAGE = "http://zile.sourceforge.net/"
DEPENDS = "ncurses bdwgc"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "${GNU_MIRROR}/zile/${BP}.tar.gz \
           file://remove-help2man.patch \
"

SRC_URI[md5sum] = "c7d7eec93231c6878f255978d9747a73"
SRC_URI[sha256sum] = "7a78742795ca32480f2bab697fd5e328618d9997d6f417cf1b14e9da9af26b74"

inherit autotools pkgconfig

do_install_append() {
    rm -rf ${D}${libdir}/charset.alias
    rmdir --ignore-fail-on-non-empty ${D}${libdir} || true
}

PACKAGECONFIG ??= ""
PACKAGECONFIG_append = " ${@bb.utils.filter('DISTRO_FEATURES', 'acl', d)}"

PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"
