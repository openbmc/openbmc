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

SRC_URI[md5sum] = "4afffe3c219bb2e04f09510905af836b"
SRC_URI[sha256sum] = "c5ea54b199174708de11af9b8f4ecf28b5b0743d4bc0e380e741f25b28c0f8d4"

EXTRA_OECONF = " \
        --without-lib-prefix \
"

EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--with-systemdsystemunitdir=${systemd_unitdir}/system/', '--without-systemdsystemunitdir', d)}"


PACKAGECONFIG ??= "charon curl gmp openssl stroke sqlite3 \
        ${@bb.utils.filter('DISTRO_FEATURES', 'ldap', d)} \
"
PACKAGECONFIG[aesni] = "--enable-aesni,--disable-aesni,,${PN}-plugin-aesni"
PACKAGECONFIG[charon] = "--enable-charon,--disable-charon,"
PACKAGECONFIG[curl] = "--enable-curl,--disable-curl,curl,${PN}-plugin-curl"
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

RRECOMMENDS_${PN} = "kernel-module-ipsec"

FILES_${PN} += "${libdir}/ipsec/lib*${SOLIBS}"
FILES_${PN}-dbg += "${bindir}/.debug ${libdir}/ipsec/.debug ${libexecdir}/ipsec/.debug"
FILES_${PN}-dev += "${libdir}/ipsec/lib*${SOLIBSDEV} ${libdir}/ipsec/*.la"
FILES_${PN}-staticdev += "${libdir}/ipsec/*.a"

CONFFILES_${PN} = "${sysconfdir}/*.conf ${sysconfdir}/ipsec.d/*.conf ${sysconfdir}/strongswan.d/*.conf"

PACKAGES += "${PN}-plugins"
ALLOW_EMPTY_${PN}-plugins = "1"

PACKAGES_DYNAMIC += "^${PN}-plugin-.*$"
NOAUTOPACKAGEDEBUG = "1"

python split_strongswan_plugins () {
    sysconfdir = d.expand('${sysconfdir}/strongswan.d/charon')
    libdir = d.expand('${libdir}/ipsec/plugins')
    dbglibdir = os.path.join(libdir, '.debug')

    def add_plugin_conf(f, pkg, file_regex, output_pattern, modulename):
        dvar = d.getVar('PKGD', True)
        oldfiles = d.getVar('CONFFILES_' + pkg, True)
        newfile = '/' + os.path.relpath(f, dvar)

        if not oldfiles:
            d.setVar('CONFFILES_' + pkg, newfile)
        else:
            d.setVar('CONFFILES_' + pkg, oldfiles + " " + newfile)

    split_packages = do_split_packages(d, libdir, 'libstrongswan-(.*)\.so', '${PN}-plugin-%s', 'strongSwan %s plugin', prepend=True)
    do_split_packages(d, sysconfdir, '(.*)\.conf', '${PN}-plugin-%s', 'strongSwan %s plugin', prepend=True, hook=add_plugin_conf)

    split_dbg_packages = do_split_packages(d, dbglibdir, 'libstrongswan-(.*)\.so', '${PN}-plugin-%s-dbg', 'strongSwan %s plugin - Debugging files', prepend=True, extra_depends='${PN}-dbg')
    split_dev_packages = do_split_packages(d, libdir, 'libstrongswan-(.*)\.la', '${PN}-plugin-%s-dev', 'strongSwan %s plugin - Development files', prepend=True, extra_depends='${PN}-dev')
    split_staticdev_packages = do_split_packages(d, libdir, 'libstrongswan-(.*)\.a', '${PN}-plugin-%s-staticdev', 'strongSwan %s plugin - Development files (Static Libraries)', prepend=True, extra_depends='${PN}-staticdev')

    if split_packages:
        pn = d.getVar('PN', True)
        d.setVar('RRECOMMENDS_' + pn + '-plugins', ' '.join(split_packages))
        d.appendVar('RRECOMMENDS_' + pn + '-dbg', ' ' + ' '.join(split_dbg_packages))
        d.appendVar('RRECOMMENDS_' + pn + '-dev', ' ' + ' '.join(split_dev_packages))
        d.appendVar('RRECOMMENDS_' + pn + '-staticdev', ' ' + ' '.join(split_staticdev_packages))
}

PACKAGESPLITFUNCS_prepend = "split_strongswan_plugins "

# Install some default plugins based on default strongSwan ./configure options
# See https://wiki.strongswan.org/projects/strongswan/wiki/Pluginlist
RDEPENDS_${PN} += "\
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
    "

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'swanctl', '${BPN}-swanctl.service', '${BPN}.service', d)}"
