SUMMARY = "A network authentication protocol"
DESCRIPTION = "Kerberos is a system for authenticating users and services on a network. \
 Kerberos is a trusted third-party service.  That means that there is a \
 third party (the Kerberos server) that is trusted by all the entities on \
 the network (users and services, usually called "principals"). \
 . \
 This is the MIT reference implementation of Kerberos V5. \
 . \
 This package contains the Kerberos key server (KDC).  The KDC manages all \
 authentication credentials for a Kerberos realm, holds the master keys \
 for the realm, and responds to authentication requests.  This package \
 should be installed on both master and slave KDCs."

HOMEPAGE = "http://web.mit.edu/Kerberos/"
SECTION = "console/network"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/../NOTICE;md5=f64248328d2d9928e1f04158b5243e7f"
DEPENDS = "ncurses util-linux e2fsprogs e2fsprogs-native"

inherit autotools-brokensep binconfig perlnative

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"
SRC_URI = "http://web.mit.edu/kerberos/dist/${BPN}/${SHRT_VER}/${BP}-signed.tar \
           file://0001-aclocal-Add-parameter-to-disable-keyutils-detection.patch \
           file://debian-suppress-usr-lib-in-krb5-config.patch;striplevel=2 \
           file://Fix-SPNEGO-context-aliasing-bugs-CVE-2015-2695.patch;striplevel=2 \
           file://Fix-IAKERB-context-aliasing-bugs-CVE-2015-2696.patch;striplevel=2 \
           file://Fix-build_principal-memory-bug-CVE-2015-2697.patch;striplevel=2 \
           file://Fix-IAKERB-context-export-import-CVE-2015-2698.patch;striplevel=2 \
           file://crosscompile_nm.patch \
           file://etc/init.d/krb5-kdc \
           file://etc/init.d/krb5-admin-server \
           file://etc/default/krb5-kdc \
           file://etc/default/krb5-admin-server \
           file://krb5-CVE-2016-3119.patch;striplevel=2 \
"
SRC_URI[md5sum] = "f7ebfa6c99c10b16979ebf9a98343189"
SRC_URI[sha256sum] = "e528c30b0209c741f6f320cb83122ded92f291802b6a1a1dc1a01dcdb3ff6de1"

S = "${WORKDIR}/${BP}/src"

PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[libedit] = "--with-libedit,--without-libedit,libedit"
PACKAGECONFIG[openssl] = "--with-pkinit-crypto-impl=openssl,,openssl"
PACKAGECONFIG[keyutils] = "--enable-keyutils,--disable-keyutils,keyutils"
PACKAGECONFIG[ldap] = "--with-ldap,--without-ldap,openldap"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"

EXTRA_OECONF += " --without-tcl --with-system-et --disable-rpath"
CACHED_CONFIGUREVARS += "krb5_cv_attr_constructor_destructor=yes ac_cv_func_regcomp=yes \
                  ac_cv_printf_positional=yes ac_cv_file__etc_environment=yes \
                  ac_cv_file__etc_TIMEZONE=no"

CFLAGS_append += "-DDESTRUCTOR_ATTR_WORKS=1 -I${STAGING_INCDIR}/et"
LDFLAGS_append += "-lpthread"

FILES_${PN} += "${datadir}/gnats"
FILES_${PN}-doc += "${datadir}/examples"
FILES_${PN}-dbg += "${libdir}/krb5/plugins/*/.debug"

# As this recipe doesn't inherit update-rc.d, we need to add this dependency here
RDEPENDS_${PN}_class-target += "initscripts-functions"

krb5_do_unpack() {
    # ${P}-signed.tar contains ${P}.tar.gz.asc and ${P}.tar.gz
    tar xzf ${WORKDIR}/${BP}.tar.gz -C ${WORKDIR}/
}

python do_unpack() {
    bb.build.exec_func('base_do_unpack', d)
    bb.build.exec_func('krb5_do_unpack', d)
}

do_configure() {
    gnu-configize --force
    autoreconf
    oe_runconf
}

do_install_append() {
    mkdir -p ${D}/${sysconfdir}/init.d ${D}/${sysconfdir}/default
    install -m 0755 ${WORKDIR}/etc/init.d/* ${D}/${sysconfdir}/init.d
    install -m 0644 ${WORKDIR}/etc/default/* ${D}/${sysconfdir}/default

    rm -rf ${D}/${localstatedir}/run
    mkdir -p ${D}/${sysconfdir}/default/volatiles
    echo "d root root 0755 ${localstatedir}/run/krb5kdc none" \
           > ${D}${sysconfdir}/default/volatiles/87_krb5
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /run/krb5kdc - - - -" \
              > ${D}${sysconfdir}/tmpfiles.d/krb5.conf
    fi

}

pkg_postinst_${PN} () {
    if [ -z "$D" ]; then
        if command -v systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/krb5.conf
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}

BBCLASSEXTEND = "native nativesdk"
