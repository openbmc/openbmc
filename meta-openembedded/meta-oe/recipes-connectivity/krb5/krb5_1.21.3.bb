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
LIC_FILES_CHKSUM = "file://${S}/../NOTICE;md5=71c06694263581762668e88b7b77a1a5"

inherit autotools-brokensep binconfig perlnative systemd update-rc.d pkgconfig

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"
SRC_URI = "http://web.mit.edu/kerberos/dist/${BPN}/${SHRT_VER}/${BP}.tar.gz \
           file://debian-suppress-usr-lib-in-krb5-config.patch;striplevel=2 \
           file://crosscompile_nm.patch \
           file://etc/init.d/krb5-kdc \
           file://etc/init.d/krb5-admin-server \
           file://etc/default/krb5-kdc \
           file://etc/default/krb5-admin-server \
           file://krb5-kdc.service \
           file://krb5-admin-server.service \
           file://CVE-2024-26458_CVE-2024-26461.patch;striplevel=2 \
"

SRC_URI[sha256sum] = "b7a4cd5ead67fb08b980b21abd150ff7217e85ea320c9ed0c6dadd304840ad35"

CVE_PRODUCT = "kerberos"
CVE_VERSION = "5-${PV}"

S = "${WORKDIR}/${BP}/src"

DEPENDS = "bison-native ncurses util-linux e2fsprogs e2fsprogs-native openssl"

PACKAGECONFIG ??= "pkinit"
PACKAGECONFIG[libedit] = "--with-libedit,--without-libedit,libedit"
PACKAGECONFIG[openssl] = "--with-crypto-impl=openssl,,openssl"
PACKAGECONFIG[keyutils] = "--with-keyutils,--without-keyutils,keyutils"
PACKAGECONFIG[ldap] = "--with-ldap,--without-ldap,openldap"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[pkinit] = "--enable-pkinit, --disable-pkinit"

EXTRA_OECONF += "--with-system-et --disable-rpath"
CACHED_CONFIGUREVARS += "krb5_cv_attr_constructor_destructor=yes ac_cv_func_regcomp=yes \
                  ac_cv_printf_positional=yes ac_cv_file__etc_environment=yes \
                  ac_cv_file__etc_TIMEZONE=no"

CFLAGS:append = " -fPIC -DDESTRUCTOR_ATTR_WORKS=1 -I${STAGING_INCDIR}/et"
CFLAGS:append:riscv64 = " -D_REENTRANT -pthread"
LDFLAGS:append = " -pthread"

do_configure() {
    gnu-configize --force
    autoreconf
    oe_runconf
}

do_install:append() {
    rm -rf ${D}/${localstatedir}/run
    rm -f ${D}${bindir}/sclient
    rm -f ${D}${bindir}/sim_client
    rm -f ${D}${bindir}/uuclient
    rm -f ${D}${sbindir}/krb5-send-pr
    rm -f ${D}${sbindir}/sim_server
    rm -f ${D}${sbindir}/sserver
    rm -f ${D}${sbindir}/uuserver

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        mkdir -p ${D}/${sysconfdir}/init.d ${D}/${sysconfdir}/default
        install -m 0755 ${UNPACKDIR}/etc/init.d/* ${D}/${sysconfdir}/init.d
        install -m 0644 ${UNPACKDIR}/etc/default/* ${D}/${sysconfdir}/default

        mkdir -p ${D}/${sysconfdir}/default/volatiles
        echo "d root root 0755 ${localstatedir}/run/krb5kdc none" \
              > ${D}${sysconfdir}/default/volatiles/87_krb5

        echo "RUN_KADMIND=true" >> ${D}/${sysconfdir}/default/krb5-admin-server
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /run/krb5kdc - - - -" \
              > ${D}${sysconfdir}/tmpfiles.d/krb5.conf

        mkdir -p ${D}/${sysconfdir}/default
        install -m 0644 ${UNPACKDIR}/etc/default/* ${D}/${sysconfdir}/default

        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/krb5-admin-server.service ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/krb5-kdc.service ${D}${systemd_system_unitdir}
    fi

    sed -e 's@[^ ]*-ffile-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fdebug-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fmacro-prefix-map=[^ "]*@@g' \
        -i ${D}${bindir}/krb5-config
}

PACKAGE_BEFORE_PN =+ "${PN}-admin-server \
                      ${PN}-gss-samples \
                      ${PN}-k5tls \
                      ${PN}-kdc \
                      ${PN}-kdc-ldap \
                      ${PN}-kpropd \
                      ${PN}-otp \
                      ${PN}-pkinit \
                      ${PN}-spake \
                      ${PN}-user \
                      libgssapi-krb5 \
                      libgssrpc \
                      libk5crypto \
                      libkadm5clnt-mit \
                      libkadm5srv-mit \
                      libkdb5 \
                      libkrad \
                      libkrb5 \
                      libkrb5support \
                      libverto"

FILES:${PN} = "${libdir}/krb5/plugins/preauth/test.so"
FILES:${PN}-doc += "${datadir}/examples"
FILES:${PN}-dbg += "${libdir}/krb5/plugins/*/.debug"

FILES:${PN}-admin-server = "${sbindir}/kadmin.local \
                            ${sbindir}/kadmind \
                            ${sbindir}/kprop \
                            ${sysconfdir}/default/krb5-admin-server \
                            ${sysconfdir}/init.d/krb5-admin-server \
                            ${systemd_system_unitdir}/krb5-admin-server.service"

FILES:${PN}-gss-samples = "${bindir}/gss-client \
                           ${sbindir}/gss-server"

FILES:${PN}-k5tls = "${libdir}/krb5/plugins/tls/k5tls.so"

FILES:${PN}-kdc = "${libdir}/krb5/plugins/kdb/db2.so \
                   ${localstatedir}/krb5kdc \
                   ${sbindir}/kdb5_util \
                   ${sbindir}/kproplog \
                   ${sbindir}/krb5kdc \
                   ${sysconfdir}/default/krb5-kdc \
                   ${sysconfdir}/default/volatiles/87_krb5 \
                   ${sysconfdir}/init.d/krb5-kdc \
                   ${sysconfdir}/tmpfiles.d/krb5.conf \
                   ${systemd_system_unitdir}/krb5-kdc.service"

FILES:${PN}-kdc-ldap = "${libdir}/krb5/libkdb_ldap${SOLIBS} \
                        ${libdir}/krb5/plugins/kdb/kldap.so \
                        ${sbindir}/kdb5_ldap_util"

FILES:${PN}-kpropd = "${sbindir}/kpropd"
FILES:${PN}-otp = "${libdir}/krb5/plugins/preauth/otp.so"
FILES:${PN}-pkinit = "${libdir}/krb5/plugins/preauth/pkinit.so"
FILES:${PN}-spake = "${libdir}/krb5/plugins/preauth/spake.so"
FILES:${PN}-user = "${bindir}/k*"

FILES:libgssapi-krb5 = "${libdir}/libgssapi_krb5${SOLIBS}"
FILES:libgssrpc = "${libdir}/libgssrpc${SOLIBS}"
FILES:libk5crypto = "${libdir}/libk5crypto${SOLIBS}"
FILES:libkadm5clnt-mit = "${libdir}/libkadm5clnt_mit${SOLIBS}"
FILES:libkadm5srv-mit = "${libdir}/libkadm5srv_mit${SOLIBS}"
FILES:libkdb5 = "${libdir}/libkdb5${SOLIBS}"
FILES:libkrad = "${libdir}/libkrad${SOLIBS}"
FILES:libkrb5 = "${libdir}/libkrb5${SOLIBS} \
                 ${libdir}/krb5/plugins/authdata \
                 ${libdir}/krb5/plugins/libkrb5"
FILES:libkrb5support = "${libdir}/libkrb5support${SOLIBS}"
FILES:libverto = "${libdir}/libverto${SOLIBS}"

RDEPENDS:${PN}-kadmin-server = "${PN}-kdc"
RDEPENDS:${PN}-kpropd = "${PN}-kdc"

INITSCRIPT_PACKAGES = "${PN}-admin-server ${PN}-kdc"
INITSCRIPT_NAME:${PN}-admin-server = "krb5-admin-server"
INITSCRIPT_NAME:${PN}-kdc = "krb5-kdc"

SYSTEMD_PACKAGES = "${PN}-admin-server ${PN}-kdc"
SYSTEMD_SERVICE:${PN}-admin-server = "krb5-admin-server.service"
SYSTEMD_SERVICE:${PN}-kdc = "krb5-kdc.service"

pkg_postinst:${PN}-kdc () {
    if [ -z "$D" ]; then
        if command -v systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/krb5.conf
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}

BBCLASSEXTEND = "native nativesdk"

inherit multilib_script
MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/krb5-config"
