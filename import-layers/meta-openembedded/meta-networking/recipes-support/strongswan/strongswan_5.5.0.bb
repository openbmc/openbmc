DESCRIPTION = "strongSwan is an OpenSource IPsec implementation for the \
Linux operating system."
SUMMARY = "strongSwan is an OpenSource IPsec implementation"
HOMEPAGE = "http://www.strongswan.org"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "gmp openssl flex-native flex bison-native"

SRC_URI = "http://download.strongswan.org/strongswan-${PV}.tar.bz2 \
        file://fix-funtion-parameter.patch \
"

SRC_URI[md5sum] = "a96fa7eb6c62b40143dadb064b6bd586"
SRC_URI[sha256sum] = "58463998ac6725eac3687e8a20c1f69803c3772657076d06c43386a24b4c8454"

EXTRA_OECONF = " \
        --without-lib-prefix \
"

EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--with-systemdsystemunitdir=${systemd_unitdir}/system/', '--without-systemdsystemunitdir', d)}"


PACKAGECONFIG ??= "charon curl gmp openssl stroke sqlite3 \
        ${@bb.utils.contains('DISTRO_FEATURES', 'ldap', 'ldap', '', d)} \
"
PACKAGECONFIG[aesni] = "--enable-aesni,--disable-aesni,"
PACKAGECONFIG[charon] = "--enable-charon,--disable-charon,"
PACKAGECONFIG[curl] = "--enable-curl,--disable-curl,curl,"
PACKAGECONFIG[gmp] = "--enable-gmp,--disable-gmp,gmp,"
PACKAGECONFIG[ldap] = "--enable-ldap,--disable-ldap,openldap,"
PACKAGECONFIG[mysql] = "--enable-mysql,--disable-mysql,mysql5,"
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl,"
PACKAGECONFIG[scep] = "--enable-scepclient,--disable-scepclient,"
PACKAGECONFIG[soup] = "--enable-soup,--disable-soup,libsoup-2.4,"
PACKAGECONFIG[sqlite3] = "--enable-sqlite,--disable-sqlite,sqlite3,"
PACKAGECONFIG[stroke] = "--enable-stroke,--disable-stroke,"
PACKAGECONFIG[swanctl] = "--enable-swanctl,--disable-swanctl,,libgcc"

# requires swanctl
PACKAGECONFIG[systemd-charon] = "--enable-systemd,--disable-systemd,systemd,"

inherit autotools systemd pkgconfig

RRECOMMENDS_${PN} = "kernel-module-ipsec"

FILES_${PN} += "${libdir}/ipsec/lib*${SOLIBS} ${libdir}/ipsec/plugins/*.so"
FILES_${PN}-dbg += "${libdir}/ipsec/.debug ${libdir}/ipsec/plugins/.debug ${libexecdir}/ipsec/.debug"
FILES_${PN}-dev += "${libdir}/ipsec/lib*${SOLIBSDEV} ${libdir}/ipsec/*.la ${libdir}/ipsec/plugins/*.la"
FILES_${PN}-staticdev += "${libdir}/ipsec/*.a ${libdir}/ipsec/plugins/*.a"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'swanctl', '${BPN}-swanctl.service', '${BPN}.service', d)}"
