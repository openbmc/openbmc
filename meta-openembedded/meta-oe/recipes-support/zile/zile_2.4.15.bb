SUMMARY = "Zile is lossy Emacs"
HOMEPAGE = "http://zile.sourceforge.net/"
DEPENDS = "ncurses bdwgc"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "${GNU_MIRROR}/zile/${BP}.tar.gz \
           file://remove-help2man.patch \
"

SRC_URI[md5sum] = "05efa90dfee1821ca018b0b2ef8f50a8"
SRC_URI[sha256sum] = "39c300a34f78c37ba67793cf74685935a15568e14237a3a66fda8fcf40e3035e"

inherit autotools pkgconfig

do_install:append() {
    rm -rf ${D}${libdir}/charset.alias
    rmdir --ignore-fail-on-non-empty ${D}${libdir} || true
}

PACKAGECONFIG ??= ""
PACKAGECONFIG:append = " ${@bb.utils.filter('DISTRO_FEATURES', 'acl', d)}"

PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"
