SUMMARY = "Manages connections of multiple users to a serial console or TCP/unix sockets"
DESCRIPTION = "\
    Conserver allows multiple users to watch a serial console at the same \
    time. It can log the data, allows users to take write-access of a console \
    (one at a time), and has a variety of bells and whistles to accentuate \
    that basic functionality. The idea is that conserver will log all your \
    serial traffic so you can go back and review why something crashed, look \
    at changes (if done on the console), or tie the console logs into a \
    monitoring system (just watch the logfiles it creates). \
"
HOMEPAGE = "https://www.conserver.com/"
BUGTRACKER = "https://github.com/bstansell/conserver/issues"
SECTION = "console/network"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b28513e7b696027d3d2b8dbf117f9fe5"

DEPENDS = "libxcrypt"

inherit autotools ptest systemd useradd

SRC_URI = "\
    git://github.com/bstansell/conserver;protocol=https;branch=master;tag=v${PV} \
    file://conserver.service \
    file://run-ptest \
"
SRCREV = "fe9aac337554f95721dc9f3da721092a81092089"

# In 8.3.0, conserver fails to build with uds and ipv6
# https://github.com/bstansell/conserver/issues/112
PACKAGECONFIG ?= "\
    openssl \
    ${@bb.utils.contains_any('DISTRO_FEATURES', 'ipv4 ipv6', '', 'uds trust-uds-cred', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 pam', d)} \
"

# Trust reverse DNS information
PACKAGECONFIG[trustrevdns] = "--with-trustrevdns,--without-trustrevdns"
# Produce extended messages
PACKAGECONFIG[extmsgs] = "--with-extmsgs,--without-extmsgs"
# Use Unix domain sockets for client/server communication [/tmp/conserver]
PACKAGECONFIG[uds] = "--with-uds=/run/${PN},--without-uds"
# Trust UDS credentials obtained via socket
PACKAGECONFIG[trust-uds-cred] = "--with-trust-uds-cred,--without-trust-uds-cred"
# Compile in libwrap (tcp_wrappers) support
PACKAGECONFIG[libwrap] = "--with-libwrap,--without-libwrap,tcp-wrappers"
# Compile in OpenSSL support
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl"
# Require server SSL certificate by client
PACKAGECONFIG[req-server-cert] = "--with-req-server-cert,--without-req-server-cert"
# Compile in GSS-API support
PACKAGECONFIG[gssapi] = "--with-gssapi,--without-gssapi,krb5"
# retry username without @REALM with gss-api authentication
PACKAGECONFIG[striprealm] = "--with-striprealm,--without-striprealm"
# Compile in FreeIPMI support
PACKAGECONFIG[freeipmi] = "--with-freeipmi,--without-freeipmi,freeipmi"
# Compile in dmalloc support
PACKAGECONFIG[dmalloc] = "--with-dmalloc,--without-dmalloc,dmalloc"
# Enable PAM support
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam"
# (experimental) Use IPv6 for client/server communication
PACKAGECONFIG[ipv6] = "--with-ipv6,--without-ipv6"

EXTRA_OECONF += "\
    INSTALL_PROGRAM='install --strip-program=true' \
    --with-pidfile=/run/conserver/conserver.pid \
    --without-rpath \
"

PACKAGE_BEFORE_PN += "${PN}-client"
SUMMARY:${PN}-client = "Client to connect to conserver"
FILES:${PN}-client += "${bindir}/console"

PACKAGE_BEFORE_PN += "${PN}-convert"
SUMMARY:${PN}-convert = "Converter for old config files of conserver"
FILES:${PN}-convert += "${libdir}/${PN}"

# tests fail with ash
RDEPENDS:${PN}-ptest += "bash"

SYSTEMD_SERVICE:${PN} = "${PN}.service"

USERADD_PACKAGES = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${PN}', '', d)} \
"
USERADD_PARAM:${PN} = "\
    -M -d /invalid -r -U -s ${sbindir}/nologin conserver; \
"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}
    then
        rm ${D}${datadir}/examples/${PN}/conserver.rc

        install -m 644 -D -t ${D}/${systemd_unitdir}/system ${UNPACKDIR}/conserver.service
    else
        install -d ${D}${sysconfdir}/init.d
        mv ${D}${datadir}/examples/${PN}/conserver.rc ${D}${sysconfdir}/init.d/${PN}
    fi

    install -d ${D}${datadir}/doc/${PN}/examples
    mv ${D}${datadir}/examples/${PN}/* ${D}${datadir}/doc/${PN}/examples
    rmdir ${D}${datadir}/examples/${PN} ${D}${datadir}/examples
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/conserver ${D}${PTEST_PATH}/console
    ln -s ${sbindir}/conserver ${D}${PTEST_PATH}/conserver
    ln -s ${bindir}/console ${D}${PTEST_PATH}/console
    cp -a ${S}/test ${D}${PTEST_PATH}
}
