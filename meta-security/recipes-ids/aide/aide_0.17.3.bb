SUMMARY = "Advanced Intrusion Detection Environment"
HOMEPAGE = "https://aide.github.io"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
LICENSE = "GPL-2.0"

DEPENDS = "bison-native libpcre"

SRC_URI = "https://github.com/aide/aide/releases/download/v${PV}/${BPN}-${PV}.tar.gz \
           file://aide.conf"

SRC_URI[sha256sum] = "a2eb1883cafaad056fbe43ee1e8ae09fd36caa30a0bc8edfea5d47bd67c464f8"

inherit autotools pkgconfig

PACKAGECONFIG ??=" mhash zlib e2fsattrs \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux audit', '', d)} \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'xattr', '', d)} \
                 "
PACKAGECONFIG[selinux] = "--with-selinux, --without-selinux, libselinux, libselinux"
PACKAGECONFIG[zlib] = "--with-zlib, --without-zlib, zlib, zlib "
PACKAGECONFIG[xattr] = "--with-xattr, --without-xattr, attr, attr"
PACKAGECONFIG[curl] = "--with-curl, --without-curl, curl, libcurl"
PACKAGECONFIG[audit] = "--with-audit, --without-audit,"
PACKAGECONFIG[gcrypt] = "--with-gcrypt, --without-gcrypt, libgcrypt, libgcrypt"
PACKAGECONFIG[mhash] = "--with-mhash, --without-mhash, libmhash, libmhash"
PACKAGECONFIG[e2fsattrs] = "--with-e2fsattrs, --without-e2fsattrs, e2fsprogs, e2fsprogs"

do_install:append () {
    install -d ${D}${libdir}/${PN}/logs   
    install -d ${D}${sysconfdir}   
    install ${WORKDIR}/aide.conf ${D}${sysconfdir}/
}

CONF_FILE = "${sysconfdir}/aide.conf"

FILES:${PN} += "${libdir}/${PN} ${sysconfdir}/aide.conf"

pkg_postinst_ontarget:${PN} () {
    /usr/bin/aide -i
}
RDPENDS_${PN} = "bison, libpcre"
