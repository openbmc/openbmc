SUMMARY = "Zile is lossy Emacs"
HOMEPAGE = "http://zile.sourceforge.net/"
DEPENDS = "ncurses bdwgc"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://ftp.gnu.org/gnu/zile/${BP}.tar.gz \
           file://remove-help2man.patch \
"

SRC_URI[md5sum] = "7a460ccec64e3bec2835697b2eae533c"
SRC_URI[sha256sum] = "1fd27bbddc61491b1fbb29a345d0d344734aa9e80cfa07b02892eedf831fa9cc"

inherit autotools pkgconfig

do_install_append() {
    rm -rf ${D}${libdir}/charset.alias
    rmdir --ignore-fail-on-non-empty ${D}${libdir} || true
}

PACKAGECONFIG ??= ""
PACKAGECONFIG += "${@bb.utils.filter('DISTRO_FEATURES', 'acl', d)}"

PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"
