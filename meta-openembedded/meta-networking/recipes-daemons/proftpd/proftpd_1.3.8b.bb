SUMMARY = "Secure and configurable FTP server"
SECTION = "net"
HOMEPAGE = "http://www.proftpd.org"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=fb0d1484d11915fa88a6a7702f1dc184"

SRCREV = "0a7ea9b0ba9fcdf368374a226370d08f10397d99"
BRANCH = "1.3.8"

SRC_URI = "git://github.com/proftpd/proftpd.git;branch=${BRANCH};protocol=https \
           file://basic.conf.patch \
           file://proftpd-basic.init \
           file://default \
           file://close-RequireValidShell-check.patch \
           file://contrib.patch  \
           file://build_fixup.patch \
           file://proftpd.service \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep useradd update-rc.d systemd multilib_script

CVE_STATUS[CVE-2001-0027] = "fixed-version: version 1.2.0rc3 removed affected module"

EXTRA_OECONF += "--enable-largefile INSTALL=install"

PACKAGECONFIG ??= "shadow \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 pam', d)} \
                   static \
                  "

PACKAGECONFIG[curses] = "--enable-curses --enable-ncurses, --disable-curses --disable-ncurses, ncurses"
PACKAGECONFIG[openssl] = "--enable-openssl, --disable-openssl, openssl, openssl"
PACKAGECONFIG[pam] = "--enable-auth-pam, --disable-auth-pam, libpam, libpam"
PACKAGECONFIG[ipv6] = "--enable-ipv6, --disable-ipv6"
PACKAGECONFIG[shadow] = "--enable-shadow, --disable-shadow"
PACKAGECONFIG[pcre] = "--enable-pcre, --disable-pcre, libpcre "
PACKAGECONFIG[static] = "--enable-static=yes, --enable-static=no"

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

#omit mod_auth_file from core modules
PACKAGECONFIG[auth] = "--enable-auth-file, --disable-auth-file"

# proftpd uses libltdl which currently makes configuring using
# autotools.bbclass a pain...
do_configure () {
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
    oe_runconf
    sed -e 's|--sysroot=${STAGING_DIR_HOST}||g' \
        -e 's|${STAGING_DIR_NATIVE}||g' \
        -e 's|-ffile-prefix-map=[^ ]*||g' \
        -e 's|-fdebug-prefix-map=[^ ]*||g' \
        -e 's|-fmacro-prefix-map=[^ ]*||g' \
        -i ${B}/config.h
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
    install -m 0755 ${UNPACKDIR}/proftpd-basic.init ${D}${sysconfdir}/init.d/proftpd
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/proftpd
    sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sysconfdir}/init.d/proftpd
    sed -i 's!/var/!${localstatedir}/!g' ${D}${sysconfdir}/init.d/proftpd
    sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${sysconfdir}/init.d/proftpd

    install -d ${D}${sysconfdir}/default
    install -m 0755 ${UNPACKDIR}/default ${D}${sysconfdir}/default/proftpd

    # create the pub directory
    mkdir -p ${D}/home/${FTPUSER}/pub/
    chown -R ${FTPUSER}:${FTPGROUP} ${D}/home/${FTPUSER}/pub
    if ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'true', 'false', d)}; then
        # install proftpd pam configuration
        install -d ${D}${sysconfdir}/pam.d
        install -m 644 ${S}/contrib/dist/rpm/ftp.pamd ${D}${sysconfdir}/pam.d/proftpd
        sed -i '/ftpusers/d' ${D}${sysconfdir}/pam.d/proftpd
        # specify the user Authentication config
        sed -i '/^MaxInstances/a\AuthPAM                         on\nAuthPAMConfig                   proftpd' \
            ${D}${sysconfdir}/proftpd.conf
    fi

    install -d ${D}/${systemd_unitdir}/system
    install -m 644 ${UNPACKDIR}/proftpd.service ${D}/${systemd_unitdir}/system
    sed -e 's,@BASE_SBINDIR@,${base_sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        -e 's,@SBINDIR@,${sbindir},g' \
        -i ${D}${systemd_unitdir}/system/*.service

    sed -e 's|--sysroot=${STAGING_DIR_HOST}||g' \
        -e 's|${STAGING_DIR_NATIVE}||g' \
        -e 's|-ffile-prefix-map=[^ ]*||g' \
        -e 's|-fdebug-prefix-map=[^ ]*||g' \
        -e 's|-fmacro-prefix-map=[^ ]*||g' \
        -i ${D}/${bindir}/prxs ${D}${includedir}/proftpd/Make.rules ${D}${includedir}/proftpd/config.h 

    # ftpmail perl script, which reads the proftpd log file and sends
    # automatic email notifications once an upload finishs,
    # depends on an old perl Mail::Sendmail
    # The Mail::Sendmail has not been maintained for almost 10 years
    # Other distribution not ship with ftpmail, so do the same to
    # avoid confusion about having it fails to run
    rm -rf ${D}${bindir}/ftpmail
    rm -rf ${D}${mandir}/man1/ftpmail.1
}

INITSCRIPT_NAME = "proftpd"
INITSCRIPT_PARAM = "defaults 85 15"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "proftpd.service"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${FTPGROUP}"
USERADD_PARAM:${PN} = "--system -g ${FTPGROUP} --home-dir /var/lib/${FTPUSER} --no-create-home \
                       --shell /bin/false ${FTPUSER}"

MULTILIB_SCRIPTS = "${PN}:${bindir}/prxs"

FILES:${PN} += "/home/${FTPUSER}"

RDEPENDS:${PN} += "perl"
