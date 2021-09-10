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
           file://0001-memory.h-Include-stdint.h-for-uintptr_t.patch \
           "

SRC_URI[sha256sum] = "9325ab56a0a4e97e379401e1d942ce3e0d8b6372291350ab2caae0755862c6f7"

UPSTREAM_CHECK_REGEX = "strongswan-(?P<pver>\d+(\.\d+)+)\.tar"

EXTRA_OECONF = " \
        --without-lib-prefix \
        --with-dev-headers=${includedir}/strongswan \
"

EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--with-systemdsystemunitdir=${systemd_unitdir}/system/', '--without-systemdsystemunitdir', d)}"

PACKAGECONFIG ?= "curl gmp openssl sqlite3 swanctl \
        ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-charon', 'charon', d)} \
"
PACKAGECONFIG[aesni] = "--enable-aesni,--disable-aesni,,${PN}-plugin-aesni"
PACKAGECONFIG[bfd] = "--enable-bfd-backtraces,--disable-bfd-backtraces,binutils"
PACKAGECONFIG[charon] = "--enable-charon,--disable-charon,"
PACKAGECONFIG[curl] = "--enable-curl,--disable-curl,curl,${PN}-plugin-curl"
PACKAGECONFIG[eap-identity] = "--enable-eap-identity,--disable-eap-identity,,${PN}-plugin-eap-identity"
PACKAGECONFIG[eap-mschapv2] = "--enable-eap-mschapv2,--disable-eap-mschapv2,,${PN}-plugin-eap-mschapv2"
PACKAGECONFIG[gmp] = "--enable-gmp,--disable-gmp,gmp,${PN}-plugin-gmp"
PACKAGECONFIG[ldap] = "--enable-ldap,--disable-ldap,openldap,${PN}-plugin-ldap"
PACKAGECONFIG[mysql] = "--enable-mysql,--disable-mysql,mysql5,${PN}-plugin-mysql"
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl,${PN}-plugin-openssl"
PACKAGECONFIG[scep] = "--enable-scepclient,--disable-scepclient,"
PACKAGECONFIG[soup] = "--enable-soup,--disable-soup,libsoup-2.4,${PN}-plugin-soup"
PACKAGECONFIG[sqlite3] = "--enable-sqlite,--disable-sqlite,sqlite3,${PN}-plugin-sqlite"
PACKAGECONFIG[stroke] = "--enable-stroke,--disable-stroke,,${PN}-plugin-stroke"
PACKAGECONFIG[swanctl] = "--enable-swanctl,--disable-swanctl,,libgcc"

# requires swanctl
PACKAGECONFIG[systemd-charon] = "--enable-systemd,--disable-systemd,systemd,"

inherit autotools systemd pkgconfig

RRECOMMENDS:${PN} = "kernel-module-ipsec"

FILES:${PN} += "${libdir}/ipsec/lib*${SOLIBS}"
FILES:${PN}-dbg += "${bindir}/.debug ${sbindir}/.debug ${libdir}/ipsec/.debug ${libexecdir}/ipsec/.debug"
FILES:${PN}-dev += "${libdir}/ipsec/lib*${SOLIBSDEV} ${libdir}/ipsec/*.la ${libdir}/ipsec/include/config.h"
FILES:${PN}-staticdev += "${libdir}/ipsec/*.a"

CONFFILES:${PN} = "${sysconfdir}/*.conf ${sysconfdir}/ipsec.d/*.conf ${sysconfdir}/strongswan.d/*.conf"

PACKAGES += "${PN}-plugins"
ALLOW_EMPTY:${PN}-plugins = "1"

PACKAGES_DYNAMIC += "^${PN}-plugin-.*$"
NOAUTOPACKAGEDEBUG = "1"

python split_strongswan_plugins () {
    sysconfdir = d.expand('${sysconfdir}/strongswan.d/charon')
    libdir = d.expand('${libdir}/ipsec/plugins')
    dbglibdir = os.path.join(libdir, '.debug')

    def add_plugin_conf(f, pkg, file_regex, output_pattern, modulename):
        dvar = d.getVar('PKGD')
        oldfiles = d.getVar('CONFFILES:' + pkg)
        newfile = '/' + os.path.relpath(f, dvar)

        if not oldfiles:
            d.setVar('CONFFILES:' + pkg, newfile)
        else:
            d.setVar('CONFFILES:' + pkg, oldfiles + " " + newfile)

    split_packages = do_split_packages(d, libdir, 'libstrongswan-(.*)\.so', '${PN}-plugin-%s', 'strongSwan %s plugin', prepend=True)
    do_split_packages(d, sysconfdir, '(.*)\.conf', '${PN}-plugin-%s', 'strongSwan %s plugin', prepend=True, hook=add_plugin_conf)

    split_dbg_packages = do_split_packages(d, dbglibdir, 'libstrongswan-(.*)\.so', '${PN}-plugin-%s-dbg', 'strongSwan %s plugin - Debugging files', prepend=True, extra_depends='${PN}-dbg')
    split_dev_packages = do_split_packages(d, libdir, 'libstrongswan-(.*)\.la', '${PN}-plugin-%s-dev', 'strongSwan %s plugin - Development files', prepend=True, extra_depends='${PN}-dev')
    split_staticdev_packages = do_split_packages(d, libdir, 'libstrongswan-(.*)\.a', '${PN}-plugin-%s-staticdev', 'strongSwan %s plugin - Development files (Static Libraries)', prepend=True, extra_depends='${PN}-staticdev')

    if split_packages:
        pn = d.getVar('PN')
        d.setVar('RRECOMMENDS:' + pn + '-plugins', ' '.join(split_packages))
        d.appendVar('RRECOMMENDS:' + pn + '-dbg', ' ' + ' '.join(split_dbg_packages))
        d.appendVar('RRECOMMENDS:' + pn + '-dev', ' ' + ' '.join(split_dev_packages))
        d.appendVar('RRECOMMENDS:' + pn + '-staticdev', ' ' + ' '.join(split_staticdev_packages))
}

PACKAGESPLITFUNCS:prepend = "split_strongswan_plugins "

# Install some default plugins based on default strongSwan ./configure options
# See https://wiki.strongswan.org/projects/strongswan/wiki/Pluginlist
RDEPENDS:${PN} += "\
    ${PN}-plugin-aes \
    ${PN}-plugin-attr \
    ${PN}-plugin-cmac \
    ${PN}-plugin-constraints \
    ${PN}-plugin-des \
    ${PN}-plugin-dnskey \
    ${PN}-plugin-hmac \
    ${PN}-plugin-kernel-netlink \
    ${PN}-plugin-md5 \
    ${PN}-plugin-nonce \
    ${PN}-plugin-pem \
    ${PN}-plugin-pgp \
    ${PN}-plugin-pkcs1 \
    ${PN}-plugin-pkcs7 \
    ${PN}-plugin-pkcs8 \
    ${PN}-plugin-pkcs12 \
    ${PN}-plugin-pubkey \
    ${PN}-plugin-random \
    ${PN}-plugin-rc2 \
    ${PN}-plugin-resolve \
    ${PN}-plugin-revocation \
    ${PN}-plugin-sha1 \
    ${PN}-plugin-sha2 \
    ${PN}-plugin-socket-default \
    ${PN}-plugin-sshkey \
    ${PN}-plugin-updown \
    ${PN}-plugin-vici \
    ${PN}-plugin-x509 \
    ${PN}-plugin-xauth-generic \
    ${PN}-plugin-xcbc \
    ${PN}-plugin-curve25519 \
    "

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"

# The deprecated legacy 'strongswan-starter' service should only be used when charon and
# stroke are enabled. When swanctl is in use, 'strongswan.service' is needed.
# See: https://wiki.strongswan.org/projects/strongswan/wiki/Charon-systemd
SYSTEMD_SERVICE:${PN} = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'swanctl', '${BPN}.service', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'charon', '${BPN}-starter.service', '', d)} \
"
