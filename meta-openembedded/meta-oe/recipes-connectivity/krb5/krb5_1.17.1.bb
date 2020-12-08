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
LIC_FILES_CHKSUM = "file://${S}/../NOTICE;md5=aff541e7261f1926ac6a2a9a7bbab839"
DEPENDS = "bison-native ncurses util-linux e2fsprogs e2fsprogs-native openssl"

inherit autotools-brokensep binconfig perlnative systemd update-rc.d

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"
SRC_URI = "http://web.mit.edu/kerberos/dist/${BPN}/${SHRT_VER}/${BP}.tar.gz \
           file://0001-aclocal-Add-parameter-to-disable-keyutils-detection.patch \
           file://debian-suppress-usr-lib-in-krb5-config.patch;striplevel=2 \
           file://crosscompile_nm.patch \
           file://etc/init.d/krb5-kdc \
           file://etc/init.d/krb5-admin-server \
           file://etc/default/krb5-kdc \
           file://etc/default/krb5-admin-server \
           file://krb5-kdc.service \
           file://krb5-admin-server.service \
"
SRC_URI[md5sum] = "417d654c72526ac51466e7fe84608878"
SRC_URI[sha256sum] = "3706d7ec2eaa773e0e32d3a87bf742ebaecae7d064e190443a3acddfd8afb181"

CVE_PRODUCT = "kerberos"
CVE_VERSION = "5-${PV}"

S = "${WORKDIR}/${BP}/src"

PACKAGECONFIG ??= "pkinit"
PACKAGECONFIG[libedit] = "--with-libedit,--without-libedit,libedit"
PACKAGECONFIG[openssl] = "--with-crypto-impl=openssl,,openssl"
PACKAGECONFIG[keyutils] = "--enable-keyutils,--disable-keyutils,keyutils"
PACKAGECONFIG[ldap] = "--with-ldap,--without-ldap,openldap"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[pkinit] = "--enable-pkinit, --disable-pkinit"

EXTRA_OECONF += " --without-tcl --with-system-et --disable-rpath"
CACHED_CONFIGUREVARS += "krb5_cv_attr_constructor_destructor=yes ac_cv_func_regcomp=yes \
                  ac_cv_printf_positional=yes ac_cv_file__etc_environment=yes \
                  ac_cv_file__etc_TIMEZONE=no"

CFLAGS_append = " -fPIC -DDESTRUCTOR_ATTR_WORKS=1 -I${STAGING_INCDIR}/et"
CFLAGS_append_riscv64 = " -D_REENTRANT -pthread"
LDFLAGS_append = " -pthread"

do_configure() {
    gnu-configize --force
    autoreconf
    oe_runconf
}

do_install_append() {
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
        install -m 0755 ${WORKDIR}/etc/init.d/* ${D}/${sysconfdir}/init.d
        install -m 0644 ${WORKDIR}/etc/default/* ${D}/${sysconfdir}/default

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
        install -m 0644 ${WORKDIR}/etc/default/* ${D}/${sysconfdir}/default

        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/krb5-admin-server.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/krb5-kdc.service ${D}${systemd_system_unitdir}
    fi
}

PACKAGES =+ "${PN}-admin-server \
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

FILES_${PN} = "${libdir}/krb5/plugins/preauth/test.so"
FILES_${PN}-doc += "${datadir}/examples"
FILES_${PN}-dbg += "${libdir}/krb5/plugins/*/.debug"

FILES_${PN}-admin-server = "${sbindir}/kadmin.local \
                            ${sbindir}/kadmind \
                            ${sbindir}/kprop \
                            ${sysconfdir}/default/krb5-admin-server \
                            ${sysconfdir}/init.d/krb5-admin-server \
                            ${systemd_system_unitdir}/krb5-admin-server.service"

FILES_${PN}-gss-samples = "${bindir}/gss-client \
                           ${sbindir}/gss-server"

FILES_${PN}-k5tls = "${libdir}/krb5/plugins/tls/k5tls.so"

FILES_${PN}-kdc = "${libdir}/krb5/plugins/kdb/db2.so \
                   ${localstatedir}/krb5kdc \
                   ${sbindir}/kdb5_util \
                   ${sbindir}/kproplog \
                   ${sbindir}/krb5kdc \
                   ${sysconfdir}/default/krb5-kdc \
                   ${sysconfdir}/default/volatiles/87_krb5 \
                   ${sysconfdir}/init.d/krb5-kdc \
                   ${sysconfdir}/tmpfiles.d/krb5.conf \
                   ${systemd_system_unitdir}/krb5-kdc.service"

FILES_${PN}-kdc-ldap = "${libdir}/krb5/libkdb_ldap${SOLIBS} \
                        ${libdir}/krb5/plugins/kdb/kldap.so \
                        ${sbindir}/kdb5_ldap_util"

FILES_${PN}-kpropd = "${sbindir}/kpropd"
FILES_${PN}-otp = "${libdir}/krb5/plugins/preauth/otp.so"
FILES_${PN}-pkinit = "${libdir}/krb5/plugins/preauth/pkinit.so"
FILES_${PN}-spake = "${libdir}/krb5/plugins/preauth/spake.so"
FILES_${PN}-user = "${bindir}/k*"

FILES_libgssapi-krb5 = "${libdir}/libgssapi_krb5${SOLIBS}"
FILES_libgssrpc = "${libdir}/libgssrpc${SOLIBS}"
FILES_libk5crypto = "${libdir}/libk5crypto${SOLIBS}"
FILES_libkadm5clnt-mit = "${libdir}/libkadm5clnt_mit${SOLIBS}"
FILES_libkadm5srv-mit = "${libdir}/libkadm5srv_mit${SOLIBS}"
FILES_libkdb5 = "${libdir}/libkdb5${SOLIBS}"
FILES_libkrad = "${libdir}/libkrad${SOLIBS}"
FILES_libkrb5 = "${libdir}/libkrb5${SOLIBS} \
                 ${libdir}/krb5/plugins/authdata \
                 ${libdir}/krb5/plugins/libkrb5"
FILES_libkrb5support = "${libdir}/libkrb5support${SOLIBS}"
FILES_libverto = "${libdir}/libverto${SOLIBS}"

RDEPENDS_${PN}-kadmin-server = "${PN}-kdc"
RDEPENDS_${PN}-kpropd = "${PN}-kdc"

INITSCRIPT_PACKAGES = "${PN}-admin-server ${PN}-kdc"
INITSCRIPT_NAME_${PN}-admin-server = "krb5-admin-server"
INITSCRIPT_NAME_${PN}-kdc = "krb5-kdc"

SYSTEMD_PACKAGES = "${PN}-admin-server ${PN}-kdc"
SYSTEMD_SERVICE_${PN}-admin-server = "krb5-admin-server.service"
SYSTEMD_SERVICE_${PN}-kdc = "krb5-kdc.service"

pkg_postinst_${PN}-kdc () {
    if [ -z "$D" ]; then
        if command -v systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/krb5.conf
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}

BBCLASSEXTEND = "native nativesdk"
