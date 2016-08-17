SUMMARY = "Secure and configurable FTP server"
SECTION = "net"
HOMEPAGE = "http://www.proftpd.org"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fb0d1484d11915fa88a6a7702f1dc184"

SRC_URI = "ftp://ftp.proftpd.org/distrib/source/${BPN}-${PV}.tar.gz \
           file://basic.conf.patch \
           file://proftpd-basic.init \
           file://default \
           file://close-RequireValidShell-check.patch \
           file://contrib.patch  \
           file://build_fixup.patch \
           file://proftpd.service \
           file://CVE-2016-3125.patch \
           "

SRC_URI[md5sum] = "b9d3092411478415b31d435f8e26d173"
SRC_URI[sha256sum] = "a1f48df8539c414ec56e0cea63dcf4b8e16e606c05f10156f030a4a67fae5696"

inherit autotools-brokensep useradd update-rc.d systemd

PACKAGECONFIG ??= "sia shadow"
PACKAGECONFIG += " ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)}"
PACKAGECONFIG += " ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"

PACKAGECONFIG[curses] = "--enable-curses --enable-ncurses, --disable-curses --disable-ncurses, ncurses"
PACKAGECONFIG[openssl] = "--enable-openssl, --disable-openssl, openssl, openssl"
PACKAGECONFIG[pam] = "--enable-auth-pam, --disable-auth-pam, libpam, libpam"
PACKAGECONFIG[ipv6] = "--enable-ipv6, --disable-ipv6"
PACKAGECONFIG[shadow] = "--enable-shadow, --disable-shadow"
PACKAGECONFIG[pcre] = "--enable-pcre, --disable-pcre, libpcre "

# enable POSIX.1e capabilities
PACKAGECONFIG[cap] = "--enable-cap, --disable-cap, libcap, libcap"

#enable support for POSIX ACLs
PACKAGECONFIG[acl] = "--enable-facl, --disable-facl"

#enable proftpd controls via ftpdct
PACKAGECONFIG[ctrls] = "--enable-ctrls, --disable-crtls"

#prevent proftpd from using its bundled getopt implementation.
PACKAGECONFIG[getopt] = "--with-getopt, --without-getopt"

#do not strip debugging symbols from installed code
PACKAGECONFIG[strip] = "--enable-strip, --disable-strip"

#enable SIA authentication support (Tru64)
PACKAGECONFIG[sia] = "--enable-sia, --disable-sia"
PACKAGECONFIG[sendfile] = "-enable-sendfile, --disable-sendfile"

#enable Native Language Support (NLS)
PACKAGECONFIG[nls] = "--enable-nls, --disable-nls"

#add mod_dso to core modules
PACKAGECONFIG[dso] = "--enable-dso, --disable-dso"
PACKAGECONFIG[largefile] = "--enable-largefile, --disable-largefile"

#omit mod_auth_file from core modules
PACKAGECONFIG[auth] = "--enable-auth-file, --disable-auth-file"


# proftpd uses libltdl which currently makes configuring using
# autotools.bbclass a pain...
do_configure () {
    oe_runconf
    cp ${STAGING_BINDIR_CROSS}/${HOST_SYS}-libtool ${S}/libtool
}

FTPUSER = "ftp"
FTPGROUP = "ftp"

do_install () {
    oe_runmake DESTDIR=${D} install
    rmdir ${D}${libdir}/proftpd ${D}${datadir}/locale
    [ -d ${D}${libexecdir} ] && rmdir ${D}${libexecdir}
    sed -i '/ *User[ \t]*/s/ftp/${FTPUSER}/' ${D}${sysconfdir}/proftpd.conf
    sed -i '/ *Group[ \t]*/s/ftp/${FTPGROUP}/' ${D}${sysconfdir}/proftpd.conf
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/proftpd-basic.init ${D}${sysconfdir}/init.d/proftpd
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/proftpd
    sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sysconfdir}/init.d/proftpd
    sed -i 's!/var/!${localstatedir}/!g' ${D}${sysconfdir}/init.d/proftpd
    sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${sysconfdir}/init.d/proftpd

    install -d ${D}${sysconfdir}/default
    install -m 0755 ${WORKDIR}/default ${D}${sysconfdir}/default/proftpd

    # create the pub directory
    mkdir -p ${D}/home/${FTPUSER}/pub/
    chown -R ${FTPUSER}:${FTPGROUP} ${D}/home/${FTPUSER}/pub

    install -d ${D}/${systemd_unitdir}/system
    install -m 644 ${WORKDIR}/proftpd.service ${D}/${systemd_unitdir}/system
    sed -e 's,@BASE_SBINDIR@,${base_sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        -e 's,@SBINDIR@,${sbindir},g' \
        -i ${D}${systemd_unitdir}/system/*.service
}

INITSCRIPT_NAME = "proftpd"
INITSCRIPT_PARAM = "defaults 85 15"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "proftpd.service"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ${FTPGROUP}"
USERADD_PARAM_${PN} = "--system -g ${FTPGROUP} --home-dir /var/lib/${FTPUSER} --no-create-home \
                       --shell /bin/false ${FTPUSER}"

FILES_${PN} += "/home/${FTPUSER}"

RDEPENDS_${PN} += "perl"
