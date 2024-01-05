DESCRIPTION = "strongSwan is an OpenSource IPsec implementation for the \
Linux operating system."
SUMMARY = "strongSwan is an OpenSource IPsec implementation"
HOMEPAGE = "http://www.strongswan.org"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "flex-native flex bison-native"
DEPENDS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', '  tpm2-tss', '', d)}"

SRC_URI = "https://download.strongswan.org/strongswan-${PV}.tar.bz2 \
          "

SRC_URI[sha256sum] = "56e30effb578fd9426d8457e3b76c8c3728cd8a5589594b55649b2719308ba55"

UPSTREAM_CHECK_REGEX = "strongswan-(?P<pver>\d+(\.\d+)+)\.tar"

EXTRA_OECONF = " \
        --without-lib-prefix \
        --with-dev-headers=${includedir}/strongswan \
"

EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--with-systemdsystemunitdir=${systemd_unitdir}/system/', '--without-systemdsystemunitdir', d)}"

PACKAGECONFIG ?= "curl gmp openssl sqlite3 swanctl curve25519\
        ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-charon', 'charon', d)} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', 'tpm2', '', d)} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'tnc-imc imc-hcd imc-os imc-scanner imc-attestation', '', d)} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'tnc-imv imv-hcd imv-os imv-scanner imv-attestation', '', d)} \
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
PACKAGECONFIG[nm] = "--enable-nm,--disable-nm,networkmanager,${PN}-nm"
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl,${PN}-plugin-openssl"
PACKAGECONFIG[soup] = "--enable-soup,--disable-soup,libsoup-2.4,${PN}-plugin-soup"
PACKAGECONFIG[sqlite3] = "--enable-sqlite,--disable-sqlite,sqlite3,${PN}-plugin-sqlite"
PACKAGECONFIG[stroke] = "--enable-stroke,--disable-stroke,,${PN}-plugin-stroke"
PACKAGECONFIG[swanctl] = "--enable-swanctl,--disable-swanctl,,libgcc"
PACKAGECONFIG[curve25519] = "--enable-curve25519,--disable-curve25519,, ${PN}-plugin-curve25519"

# requires swanctl
PACKAGECONFIG[systemd-charon] = "--enable-systemd,--disable-systemd,systemd,"

# tpm needs meta-tpm layer
PACKAGECONFIG[tpm2] = "--enable-tpm,--disable-tpm,,${PN}-plugin-tpm"


# integraty configuration needs meta-integraty
#imc 
PACKAGECONFIG[tnc-imc] = "--enable-tnc-imc,--disable-tnc-imc,,  ${PN}-plugin-tnc-imc ${PN}-plugin-tnc-tnccs"
PACKAGECONFIG[imc-test] = "--enable-imc-test,--disable-imc-test,,"
PACKAGECONFIG[imc-scanner] = "--enable-imc-scanner,--disable-imc-scanner,,"
PACKAGECONFIG[imc-os] = "--enable-imc-os,--disable-imc-os,,"
PACKAGECONFIG[imc-attestation] = "--enable-imc-attestation,--disable-imc-attestation,,"
PACKAGECONFIG[imc-swima] = "--enable-imc-swima, --disable-imc-swima, json-c,"
PACKAGECONFIG[imc-hcd] = "--enable-imc-hcd, --disable-imc-hcd,,"

#imv set
PACKAGECONFIG[tnc-imv] = "--enable-tnc-imv,--disable-tnc-imv,, ${PN}-plugin-tnc-imv ${PN}-plugin-tnc-tnccs"
PACKAGECONFIG[imv-test] = "--enable-imv-test,--disable-imv-test,,"
PACKAGECONFIG[imv-scanner] = "--enable-imv-scanner,--disable-imv-scanner,,"
PACKAGECONFIG[imv-os] = "--enable-imv-os,--disable-imv-os,,"
PACKAGECONFIG[imv-attestation] = "--enable-imv-attestation,--disable-imv-attestation,,"
PACKAGECONFIG[imv-swima] = "--enable-imv-swima, --disable-imv-swima, json-c,"
PACKAGECONFIG[imv-hcd] = "--enable-imv-hcd, --disable-imv-hcd,,"

PACKAGECONFIG[tnc-ifmap] = "--enable-tnc-ifmap,--disable-tnc-ifmap, libxml2, ${PN}-plugin-tnc-ifmap"
PACKAGECONFIG[tnc-pdp] = "--enable-tnc-pdp,--disable-tnc-pdp,, ${PN}-plugin-tnc-pdp"

PACKAGECONFIG[tnccs-11] = "--enable-tnccs-11,--disable-tnccs-11,libxml2, ${PN}-plugin-tnccs-11"
PACKAGECONFIG[tnccs-20] = "--enable-tnccs-20,--disable-tnccs-20,, ${PN}-plugin-tnccs-20"
PACKAGECONFIG[tnccs-dynamic] = "--enable-tnccs-dynamic,--disable-tnccs-dynamic,,${PN}-plugin-tnccs-dynamic"

inherit autotools systemd pkgconfig

RRECOMMENDS:${PN} = "kernel-module-ah4 \
                     kernel-module-esp4 \
                     kernel-module-xfrm-user \
                    "

FILES:${PN} += "${libdir}/ipsec/lib*${SOLIBS}"
FILES:${PN}-dbg += "${bindir}/.debug ${sbindir}/.debug ${libdir}/ipsec/.debug ${libexecdir}/ipsec/.debug"
FILES:${PN}-dev += "${libdir}/ipsec/lib*${SOLIBSDEV} ${libdir}/ipsec/*.la ${libdir}/ipsec/include/config.h"
FILES:${PN}-staticdev += "${libdir}/ipsec/*.a"

CONFFILES:${PN} = "${sysconfdir}/*.conf ${sysconfdir}/ipsec.d/*.conf ${sysconfdir}/strongswan.d/*.conf"

PACKAGES += "${PN}-plugins"
ALLOW_EMPTY:${PN}-plugins = "1"

PACKAGE_BEFORE_PN = "${PN}-imcvs ${PN}-imcvs-dbg"
ALLOW_EMPTY:${PN}-imcvs = "1"

FILES:${PN}-imcvs = "${libdir}/ipsec/imcvs/*.so"
FILES:${PN}-imcvs-dbg += "${libdir}/ipsec/imcvs/.debug"

PACKAGES =+ "${PN}-nm ${PN}-nm-dbg"
FILES:${PN}-nm = "${libexecdir}/ipsec/charon-nm ${datadir}/dbus-1/system.d/nm-strongswan-service.conf"
FILES:${PN}-nm-dbg = "${libexecdir}/ipsec/.debug/charon-nm"

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

    split_packages = do_split_packages(d, libdir, r'libstrongswan-(.*)\.so', '${PN}-plugin-%s', 'strongSwan %s plugin', prepend=True)
    do_split_packages(d, sysconfdir, r'(.*)\.conf', '${PN}-plugin-%s', 'strongSwan %s plugin', prepend=True, hook=add_plugin_conf)

    split_dbg_packages = do_split_packages(d, dbglibdir, r'libstrongswan-(.*)\.so', '${PN}-plugin-%s-dbg', 'strongSwan %s plugin - Debugging files', prepend=True, extra_depends='${PN}-dbg')
    split_dev_packages = do_split_packages(d, libdir, r'libstrongswan-(.*)\.la', '${PN}-plugin-%s-dev', 'strongSwan %s plugin - Development files', prepend=True, extra_depends='${PN}-dev')
    split_staticdev_packages = do_split_packages(d, libdir, r'libstrongswan-(.*)\.a', '${PN}-plugin-%s-staticdev', 'strongSwan %s plugin - Development files (Static Libraries)', prepend=True, extra_depends='${PN}-staticdev')

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
    ${PN}-plugin-drbg \
    ${PN}-plugin-fips-prf \
    ${PN}-plugin-gcm \
    ${PN}-plugin-hmac \
    ${PN}-plugin-kdf \
    ${PN}-plugin-kernel-netlink \
    ${PN}-plugin-md5 \
    ${PN}-plugin-mgf1 \
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
