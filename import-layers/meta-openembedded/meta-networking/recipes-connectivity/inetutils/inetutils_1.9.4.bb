DESCRIPTION = "The GNU inetutils are a collection of common \
networking utilities and servers including ftp, ftpd, rcp, \
rexec, rlogin, rlogind, rsh, rshd, syslog, syslogd, talk, \
talkd, telnet, telnetd, tftp, tftpd, and uucpd."
HOMEPAGE = "http://www.gnu.org/software/inetutils"
SECTION = "net"
DEPENDS = "ncurses netbase readline"
LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "file://COPYING;md5=0c7051aef9219dc7237f206c5c4179a7"

SRC_URI = "${GNU_MIRROR}/inetutils/inetutils-${PV}.tar.gz \
           file://version.patch \
           file://inetutils-1.8-0001-printf-parse-pull-in-features.h-for-__GLIBC__.patch \
           file://inetutils-1.8-0003-wchar.patch \
           file://rexec.xinetd.inetutils  \
           file://rlogin.xinetd.inetutils \
           file://rsh.xinetd.inetutils \
           file://telnet.xinetd.inetutils \
           file://tftpd.xinetd.inetutils \
           file://inetutils-1.9-PATH_PROCNET_DEV.patch \
           file://inetutils-only-check-pam_appl.h-when-pam-enabled.patch \
           file://0001-rcp-fix-to-work-with-large-files.patch \
"

SRC_URI[md5sum] = "04852c26c47cc8c6b825f2b74f191f52"
SRC_URI[sha256sum] = "be8f75eff936b8e41b112462db51adf689715658a1b09e0d6b05d11ec92cc616"

inherit autotools gettext update-alternatives texinfo

SRC_URI += "${@base_contains('DISTRO_FEATURES', 'ipv6', '', 'file://fix-disable-ipv6.patch', d)}"
noipv6="${@base_contains('DISTRO_FEATURES', 'ipv6', '', '--disable-ipv6 gl_cv_socket_ipv6=no', d)}"

PACKAGECONFIG ??= "ftp uucpd \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
                  "
PACKAGECONFIG[ftp] = "--enable-ftp,--disable-ftp,readline"
PACKAGECONFIG[uucpd] = "--enable-uucpd,--disable-uucpd,readline"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam"

EXTRA_OECONF = "--with-ncurses-include-dir=${STAGING_INCDIR} \
        ${noipv6} \
        inetutils_cv_path_login=${base_bindir}/login \
        --with-libreadline-prefix=${STAGING_LIBDIR} \
        --enable-rpath=no \
"

do_configure_prepend () {
    export HELP2MAN='true'
    cp ${STAGING_DATADIR_NATIVE}/gettext/config.rpath ${S}/build-aux/config.rpath
    rm -f ${S}/glob/configure*
}

do_install_append () {
    install -m 0755 -d ${D}${base_bindir}
    install -m 0755 -d ${D}${base_sbindir}
    install -m 0755 -d ${D}${sbindir}
    install -m 0755 -d ${D}${sysconfdir}/xinetd.d
    mv ${D}${bindir}/ping ${D}${base_bindir}/
    mv ${D}${bindir}/ping6 ${D}${base_bindir}/
    mv ${D}${bindir}/ifconfig ${D}${base_sbindir}/
    mv ${D}${libexecdir}/syslogd ${D}${base_sbindir}/
    mv ${D}${bindir}/hostname ${D}${base_bindir}/
    mv ${D}${libexecdir}/tftpd ${D}${sbindir}/in.tftpd
    mv ${D}${libexecdir}/telnetd ${D}${sbindir}/in.telnetd
    mv ${D}${libexecdir}/rexecd ${D}${sbindir}/in.rexecd
    mv ${D}${libexecdir}/rlogind ${D}${sbindir}/in.rlogind
    mv ${D}${libexecdir}/rshd ${D}${sbindir}/in.rshd
    mv ${D}${libexecdir}/talkd ${D}${sbindir}/in.talkd
    mv ${D}${libexecdir}/uucpd ${D}${sbindir}/in.uucpd
    mv ${D}${libexecdir}/* ${D}${bindir}/
    cp ${WORKDIR}/rexec.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/rexec
    cp ${WORKDIR}/rlogin.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/rlogin
    cp ${WORKDIR}/rsh.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/rsh
    cp ${WORKDIR}/telnet.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/telnet
    cp ${WORKDIR}/tftpd.xinetd.inetutils  ${D}/${sysconfdir}/xinetd.d/tftpd

    sed -e 's,@SBINDIR@,${sbindir},g' -i ${D}/${sysconfdir}/xinetd.d/*

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
PACKAGES += "${PN}-tftpd-dbg ${PN}-telnetd-dbg ${PN}-rshd-dbg"

ALTERNATIVE_PRIORITY = "80"
ALTERNATIVE_${PN} = "talk whois"
ALTERNATIVE_LINK_NAME[talkd]  = "${sbindir}/in.talkd"
ALTERNATIVE_LINK_NAME[uucpd]  = "${sbindir}/in.uucpd"

ALTERNATIVE_${PN}-logger = "logger"
ALTERNATIVE_${PN}-syslogd = "syslogd"
ALTERNATIVE_LINK_NAME[syslogd]  = "${base_sbindir}/syslogd"

ALTERNATIVE_${PN}-ftp = "ftp"
ALTERNATIVE_${PN}-ftpd = "ftpd"
ALTERNATIVE_${PN}-tftp = "tftp"
ALTERNATIVE_${PN}-tftpd = "tftpd"
ALTERNATIVE_LINK_NAME[tftpd] = "${sbindir}/tftpd"
ALTERNATIVE_TARGET[tftpd]  = "${sbindir}/in.tftpd"

ALTERNATIVE_${PN}-telnet = "telnet"
ALTERNATIVE_${PN}-telnetd = "telnetd"
ALTERNATIVE_LINK_NAME[telnetd] = "${sbindir}/telnetd"
ALTERNATIVE_TARGET[telnetd] = "${sbindir}/in.telnetd"

ALTERNATIVE_${PN}-rsh = "rcp rexec rlogin rsh"
ALTERNATIVE_${PN}-rshd = "rshd rexecd rlogind"
ALTERNATIVE_LINK_NAME[rshd] = "${sbindir}/rshd"
ALTERNATIVE_TARGET[rshd] = "${sbindir}/in.rshd"
ALTERNATIVE_LINK_NAME[rexecd] = "${sbindir}/rexecd"
ALTERNATIVE_TARGET[rexecd] = "${sbindir}/in.rexecd"
ALTERNATIVE_LINK_NAME[rlogind] = "${sbindir}/rlogind"
ALTERNATIVE_TARGET[rlogind] = "${sbindir}/in.rlogind"

ALTERNATIVE_${PN}-inetd= "inetd"
ALTERNATIVE_${PN}-traceroute = "traceroute"

ALTERNATIVE_${PN}-hostname = "hostname"
ALTERNATIVE_LINK_NAME[hostname]  = "${base_bindir}/hostname"

ALTERNATIVE_${PN}-ifconfig = "ifconfig"
ALTERNATIVE_LINK_NAME[ifconfig]  = "${base_sbindir}/ifconfig"

ALTERNATIVE_${PN}-ping = "ping"
ALTERNATIVE_LINK_NAME[ping]   = "${base_bindir}/ping"

ALTERNATIVE_${PN}-ping6 = "ping6"
ALTERNATIVE_LINK_NAME[ping6]  = "${base_bindir}/ping6"


FILES_${PN}-ping = "${base_bindir}/ping.${BPN}"
FILES_${PN}-ping6 = "${base_bindir}/ping6.${BPN}"
FILES_${PN}-hostname = "${base_bindir}/hostname.${BPN}"
FILES_${PN}-ifconfig = "${base_sbindir}/ifconfig.${BPN}"
FILES_${PN}-traceroute = "${bindir}/traceroute.${BPN}"
FILES_${PN}-logger = "${bindir}/logger.${BPN}"
FILES_${PN}-syslogd = "${base_sbindir}/syslogd.${BPN}"
FILES_${PN}-ftp = "${bindir}/ftp.${BPN}"

FILES_${PN}-tftp = "${bindir}/tftp.${BPN}"
FILES_${PN}-telnet = "${bindir}/telnet.${BPN}"
FILES_${PN}-rsh = "${bindir}/rsh.${BPN} ${bindir}/rlogin.${BPN} ${bindir}/rexec.${BPN} ${bindir}/rcp.${BPN}"

FILES_${PN}-rshd = "${sbindir}/in.rshd ${sbindir}/in.rlogind ${sbindir}/in.rexecd \
                    ${sysconfdir}/xinetd.d/rsh ${sysconfdir}/xinetd.d/rlogin ${sysconfdir}/xinetd.d/rexec"
FILES_${PN}-rshd-dbg = "${sbindir}/.debug/in.rshd ${sbindir}/.debug/in.rlogind ${sbindir}/.debug/in.rexecd"
RDEPENDS_${PN}-rshd += "xinetd tcp-wrappers"
RCONFLICTS_${PN}-rshd += "netkit-rshd"
RPROVIDES_${PN}-rshd = "rshd"

FILES_${PN}-ftpd = "${bindir}/ftpd.${BPN}"
FILES_${PN}-ftpd-dbg = "${bindir}/.debug/ftpd.${BPN}"
RDEPENDS_${PN}-ftpd += "xinetd"

FILES_${PN}-tftpd = "${sbindir}/in.tftpd ${sysconfdir}/xinetd.d/tftpd"
FILES_${PN}-tftpd-dbg = "${sbindir}/.debug/in.tftpd"
RCONFLICTS_${PN}-tftpd += "netkit-tftpd"
RDEPENDS_${PN}-tftpd += "xinetd"

FILES_${PN}-telnetd = "${sbindir}/in.telnetd ${sysconfdir}/xinetd.d/telnet"
FILES_${PN}-telnetd-dbg = "${sbindir}/.debug/in.telnetd"
RCONFLICTS_${PN}-telnetd += "netkit-telnetd"
RPROVIDES_${PN}-telnetd = "telnetd"
RDEPENDS_${PN}-telnetd += "xinetd"

FILES_${PN}-inetd = "${bindir}/inetd.${BPN}"

RDEPENDS_${PN} = "xinetd"
