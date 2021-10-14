DESCRIPTION = "The GNU inetutils are a collection of common \
networking utilities and servers including ftp, ftpd, rcp, \
rexec, rlogin, rlogind, rsh, rshd, syslog, syslogd, talk, \
talkd, telnet, telnetd, tftp, tftpd, and uucpd."
HOMEPAGE = "http://www.gnu.org/software/inetutils"
SECTION = "net"
DEPENDS = "ncurses netbase readline virtual/crypt"

LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "file://COPYING;md5=0c7051aef9219dc7237f206c5c4179a7"

SRC_URI[sha256sum] = "01b9a4bc73a47e63f6e8a07b76122d9ad2a2e46ebf14870e9c91d660b5647a22"
SRC_URI = "${GNU_MIRROR}/inetutils/inetutils-${PV}.tar.xz \
           file://inetutils-1.8-0001-printf-parse-pull-in-features.h-for-__GLIBC__.patch \
           file://inetutils-1.8-0003-wchar.patch \
           file://rexec.xinetd.inetutils  \
           file://rlogin.xinetd.inetutils \
           file://rsh.xinetd.inetutils \
           file://telnet.xinetd.inetutils \
           file://tftpd.xinetd.inetutils \
           file://inetutils-1.9-PATH_PROCNET_DEV.patch \
           file://inetutils-only-check-pam_appl.h-when-pam-enabled.patch \
           file://CVE-2021-40491.patch \
"

inherit autotools gettext update-alternatives texinfo

acpaths = "-I ./m4"

SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', '', 'file://fix-disable-ipv6.patch', d)}"

PACKAGECONFIG ??= "ftp uucpd \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6 ping6', '', d)} \
                  "
PACKAGECONFIG[ftp] = "--enable-ftp,--disable-ftp,readline"
PACKAGECONFIG[uucpd] = "--enable-uucpd,--disable-uucpd,readline"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6 gl_cv_socket_ipv6=no,"
PACKAGECONFIG[ping6] = "--enable-ping6,--disable-ping6,"

EXTRA_OECONF = "--with-ncurses-include-dir=${STAGING_INCDIR} \
        inetutils_cv_path_login=${base_bindir}/login \
        --with-libreadline-prefix=${STAGING_LIBDIR} \
        --enable-rpath=no \
"

# These are horrible for security, disable them
EXTRA_OECONF:append = " --disable-rsh --disable-rshd --disable-rcp \
        --disable-rlogin --disable-rlogind --disable-rexec --disable-rexecd"

do_configure:prepend () {
    export HELP2MAN='true'
    cp ${STAGING_DATADIR_NATIVE}/gettext/config.rpath ${S}/build-aux/config.rpath
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
    rm -f ${S}/glob/configure*
}

do_install:append () {
    install -m 0755 -d ${D}${base_sbindir}
    install -m 0755 -d ${D}${sbindir}
    install -m 0755 -d ${D}${sysconfdir}/xinetd.d
    if [ "${base_bindir}" != "${bindir}" ] ; then
         install -m 0755 -d ${D}${base_bindir}
         mv ${D}${bindir}/ping* ${D}${base_bindir}/
         mv ${D}${bindir}/hostname ${D}${base_bindir}/
         mv ${D}${bindir}/dnsdomainname ${D}${base_bindir}/
    fi
    mv ${D}${bindir}/ifconfig ${D}${base_sbindir}/
    mv ${D}${libexecdir}/syslogd ${D}${base_sbindir}/
    mv ${D}${libexecdir}/tftpd ${D}${sbindir}/in.tftpd
    mv ${D}${libexecdir}/telnetd ${D}${sbindir}/in.telnetd
    if [ -e ${D}${libexecdir}/rexecd ]; then
        mv ${D}${libexecdir}/rexecd ${D}${sbindir}/in.rexecd
        cp ${WORKDIR}/rexec.xinetd.inetutils ${D}/${sysconfdir}/xinetd.d/rexec
    fi
    if [ -e ${D}${libexecdir}/rlogind ]; then
        mv ${D}${libexecdir}/rlogind ${D}${sbindir}/in.rlogind
        cp ${WORKDIR}/rlogin.xinetd.inetutils ${D}/${sysconfdir}/xinetd.d/rlogin
    fi
    if [ -e ${D}${libexecdir}/rshd ]; then
        mv ${D}${libexecdir}/rshd ${D}${sbindir}/in.rshd
        cp ${WORKDIR}/rsh.xinetd.inetutils ${D}/${sysconfdir}/xinetd.d/rsh
    fi
    if [ -e ${D}${libexecdir}/talkd ]; then
        mv ${D}${libexecdir}/talkd ${D}${sbindir}/in.talkd
    fi
    mv ${D}${libexecdir}/uucpd ${D}${sbindir}/in.uucpd
    mv ${D}${libexecdir}/* ${D}${bindir}/
    cp ${WORKDIR}/telnet.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/telnet
    cp ${WORKDIR}/tftpd.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/tftpd

    sed -e 's,@SBINDIR@,${sbindir},g' -i ${D}/${sysconfdir}/xinetd.d/*
    if [ -e ${D}${libdir}/charset.alias ]; then
        rm -rf ${D}${libdir}/charset.alias
    fi
    rm -rf ${D}${libexecdir}/
    # remove usr/lib if empty
    rmdir ${D}${libdir} || true
}

PACKAGES =+ "${PN}-ping ${PN}-ping6 ${PN}-hostname ${PN}-ifconfig \
${PN}-tftp ${PN}-logger ${PN}-traceroute ${PN}-syslogd \
${PN}-ftp ${PN}-ftpd ${PN}-tftpd ${PN}-telnet ${PN}-telnetd ${PN}-inetd \
${PN}-rsh ${PN}-rshd"

# The packages tftpd, telnetd and rshd conflict with the ones
# provided by netkit, so add the corresponding -dbg packages
# for them to avoid the confliction between the dbg package
# of inetutils and netkit.
PACKAGES =+ "${PN}-tftpd-dbg ${PN}-telnetd-dbg ${PN}-rshd-dbg"
NOAUTOPACKAGEDEBUG = "1"

ALTERNATIVE_PRIORITY = "79"
ALTERNATIVE:${PN} = "whois dnsdomainname"
ALTERNATIVE_LINK_NAME[uucpd]  = "${sbindir}/in.uucpd"
ALTERNATIVE_LINK_NAME[dnsdomainname]  = "${base_bindir}/dnsdomainname"

ALTERNATIVE_PRIORITY_${PN}-logger = "60"
ALTERNATIVE:${PN}-logger = "logger"
ALTERNATIVE:${PN}-syslogd = "syslogd"
ALTERNATIVE_LINK_NAME[syslogd]  = "${base_sbindir}/syslogd"

ALTERNATIVE:${PN}-ftp = "ftp"
ALTERNATIVE:${PN}-ftpd = "ftpd"
ALTERNATIVE:${PN}-tftp = "tftp"
ALTERNATIVE:${PN}-tftpd = "tftpd"
ALTERNATIVE_LINK_NAME[tftpd] = "${sbindir}/tftpd"
ALTERNATIVE_TARGET[tftpd]  = "${sbindir}/in.tftpd"

ALTERNATIVE:${PN}-telnet = "telnet"
ALTERNATIVE:${PN}-telnetd = "telnetd"
ALTERNATIVE_LINK_NAME[telnetd] = "${sbindir}/telnetd"
ALTERNATIVE_TARGET[telnetd] = "${sbindir}/in.telnetd"

ALTERNATIVE:${PN}-inetd= "inetd"
ALTERNATIVE:${PN}-traceroute = "traceroute"

ALTERNATIVE:${PN}-hostname = "hostname"
ALTERNATIVE_LINK_NAME[hostname]  = "${base_bindir}/hostname"

ALTERNATIVE:${PN}-doc = "hostname.1 dnsdomainname.1 logger.1 syslogd.8 \
                         tftpd.8 tftp.1 telnetd.8"
ALTERNATIVE_LINK_NAME[hostname.1] = "${mandir}/man1/hostname.1"
ALTERNATIVE_LINK_NAME[dnsdomainname.1] = "${mandir}/man1/dnsdomainname.1"
ALTERNATIVE_LINK_NAME[logger.1] = "${mandir}/man1/logger.1"
ALTERNATIVE_LINK_NAME[syslogd.8] = "${mandir}/man8/syslogd.8"
ALTERNATIVE_LINK_NAME[telnetd.8] = "${mandir}/man8/telnetd.8"
ALTERNATIVE_LINK_NAME[tftpd.8] = "${mandir}/man8/tftpd.8"
ALTERNATIVE_LINK_NAME[tftp.1] = "${mandir}/man1/tftp.1"

ALTERNATIVE:${PN}-ifconfig = "ifconfig"
ALTERNATIVE_LINK_NAME[ifconfig]  = "${base_sbindir}/ifconfig"

ALTERNATIVE:${PN}-ping = "ping"
ALTERNATIVE_LINK_NAME[ping]   = "${base_bindir}/ping"

ALTERNATIVE:${PN}-ping6 = "${@bb.utils.filter('PACKAGECONFIG', 'ping6', d)}"
ALTERNATIVE_LINK_NAME[ping6]  = "${base_bindir}/ping6"


FILES:${PN}-dbg += "${base_bindir}/.debug ${base_sbindir}/.debug ${bindir}/.debug ${sbindir}/.debug"
FILES:${PN}-ping = "${base_bindir}/ping.${BPN}"
FILES:${PN}-ping6 = "${base_bindir}/ping6.${BPN}"
FILES:${PN}-hostname = "${base_bindir}/hostname.${BPN}"
FILES:${PN}-ifconfig = "${base_sbindir}/ifconfig.${BPN}"
FILES:${PN}-traceroute = "${bindir}/traceroute.${BPN}"
FILES:${PN}-logger = "${bindir}/logger.${BPN}"

FILES:${PN}-syslogd = "${base_sbindir}/syslogd.${BPN}"
RCONFLICTS:${PN}-syslogd = "rsyslog busybox-syslog sysklogd syslog-ng"

FILES:${PN}-ftp = "${bindir}/ftp.${BPN}"

FILES:${PN}-tftp = "${bindir}/tftp.${BPN}"
FILES:${PN}-telnet = "${bindir}/telnet.${BPN}"

# We make us of RCONFLICTS / RPROVIDES here rather than using the normal
# alternatives method as this leads to packaging QA issues when using
# musl as that library does not provide what these applications need to
# build.
FILES:${PN}-rsh = "${bindir}/rsh ${bindir}/rlogin ${bindir}/rexec ${bindir}/rcp"
RCONFLICTS:${PN}-rsh += "netkit-rsh-client"
RPROVIDES:${PN}-rsh = "rsh"

FILES:${PN}-rshd = "${sbindir}/in.rshd ${sbindir}/in.rlogind ${sbindir}/in.rexecd \
                    ${sysconfdir}/xinetd.d/rsh ${sysconfdir}/xinetd.d/rlogin ${sysconfdir}/xinetd.d/rexec"
FILES:${PN}-rshd-dbg = "${sbindir}/.debug/in.rshd ${sbindir}/.debug/in.rlogind ${sbindir}/.debug/in.rexecd"
RDEPENDS:${PN}-rshd += "xinetd tcp-wrappers"
RCONFLICTS:${PN}-rshd += "netkit-rshd-server"
RPROVIDES:${PN}-rshd = "rshd"

FILES:${PN}-ftpd = "${bindir}/ftpd.${BPN}"
FILES:${PN}-ftpd-dbg = "${bindir}/.debug/ftpd.${BPN}"
RDEPENDS:${PN}-ftpd += "xinetd"

FILES:${PN}-tftpd = "${sbindir}/in.tftpd ${sysconfdir}/xinetd.d/tftpd"
FILES:${PN}-tftpd-dbg = "${sbindir}/.debug/in.tftpd"
RCONFLICTS:${PN}-tftpd += "netkit-tftpd"
RDEPENDS:${PN}-tftpd += "xinetd"

FILES:${PN}-telnetd = "${sbindir}/in.telnetd ${sysconfdir}/xinetd.d/telnet"
FILES:${PN}-telnetd-dbg = "${sbindir}/.debug/in.telnetd"
RCONFLICTS:${PN}-telnetd += "netkit-telnet"
RPROVIDES:${PN}-telnetd = "telnetd"
RDEPENDS:${PN}-telnetd += "xinetd"

FILES:${PN}-inetd = "${bindir}/inetd.${BPN}"

RDEPENDS:${PN} = "xinetd"
