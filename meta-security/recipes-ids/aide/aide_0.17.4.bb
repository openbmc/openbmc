SUMMARY = "Advanced Intrusion Detection Environment"
HOMEPAGE = "https://aide.github.io"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
LICENSE = "GPL-2.0-only"

DEPENDS = "bison-native libpcre"

SRC_URI = "https://github.com/aide/aide/releases/download/v${PV}/${BPN}-${PV}.tar.gz \
           file://aide.conf"

SRC_URI[sha256sum] = "c81505246f3ffc2e76036d43a77212ae82895b5881d9b9e25c1361b1a9b7a846"

inherit autotools pkgconfig aide-base

PACKAGECONFIG ??=" mhash zlib e2fsattrs posix capabilities curl \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux audit', '', d)} \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'xattr', '', d)} \
                 "
PACKAGECONFIG[selinux] = "--with-selinux, --without-selinux, libselinux, libselinux"
PACKAGECONFIG[zlib] = "--with-zlib, --without-zlib, zlib, zlib "
PACKAGECONFIG[xattr] = "--with-xattr, --without-xattr, attr, attr"
PACKAGECONFIG[curl] = "--with-curl, --without-curl, curl, libcurl"
PACKAGECONFIG[audit] = "--with-audit, --without-audit,audit"
PACKAGECONFIG[gcrypt] = "--with-gcrypt, --without-gcrypt, libgcrypt, libgcrypt"
PACKAGECONFIG[mhash] = "--with-mhash, --without-mhash, libmhash, libmhash"
PACKAGECONFIG[e2fsattrs] = "--with-e2fsattrs, --without-e2fsattrs, e2fsprogs, e2fsprogs"
PACKAGECONFIG[capabilities] = "--with-capabilities, --without-capabilities, libcap, libcap"
PACKAGECONFIG[posix] = "--with-posix-acl, --without-posix-acl, acl, acl"


do_install[nostamp] = "1"

do_install:append () {
    install -d ${D}${libdir}/${PN}/logs   
    install -d ${D}${sysconfdir}   
    install ${WORKDIR}/aide.conf ${D}${sysconfdir}/

    for dir in ${AIDE_INCLUDE_DIRS}; do
        echo "${dir} NORMAL" >> ${D}${sysconfdir}/aide.conf
    done
    for dir in ${AIDE_SKIP_DIRS}; do
        echo "!${dir}" >> ${D}${sysconfdir}/aide.conf
    done
}

do_install:class-native () {
    install -d ${STAGING_AIDE_DIR}/bin
    install -d ${STAGING_AIDE_DIR}/lib/logs

    install ${B}/aide ${STAGING_AIDE_DIR}/bin
    install ${WORKDIR}/aide.conf ${STAGING_AIDE_DIR}/

    sed -i -s "s:\@\@define DBDIR.*:\@\@define DBDIR ${STAGING_AIDE_DIR}/lib:" ${STAGING_AIDE_DIR}/aide.conf
    sed -i -e "s:\@\@define LOGDIR.*:\@\@define LOGDIR ${STAGING_AIDE_DIR}/lib/logs:" ${STAGING_AIDE_DIR}/aide.conf
}

CONF_FILE = "${sysconfdir}/aide.conf"

FILES:${PN} += "${libdir}/${PN} ${sysconfdir}/aide.conf"

pkg_postinst_ontarget:${PN} () {
    if [ ${AIDE_SCAN_POSTINIT} ]; then
        ${bindir}/aide -i
    fi
    if [ ${AIDE_RESCAN_POSTINIT}  && -e ${libdir}/aide/aide.db.gz ]; then
        ${bindir}/aide -C
    fi
}

RDEPENDS:${PN} = "bison libpcre"

BBCLASSEXTEND = "native"
