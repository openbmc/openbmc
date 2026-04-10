SUMMARY        = "Client for the Trivial File Transfer Protocol"
DESCRIPTION    = \
"The Trivial File Transfer Protocol (TFTP) is normally used only for \
booting diskless workstations.  The tftp package provides the user   \
interface for TFTP, which allows users to transfer files to and from a \
remote machine.  This program and TFTP provide very little security, \
and should not be enabled unless it is expressly needed."
HOMEPAGE = "https://git.kernel.org/pub/scm/network/tftp/tftp-hpa.git/"
SECTION = "net"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://MCONFIG.in;beginline=1;endline=9;md5=c28ba5adb43041fae4629db05c83cbdd \
                    file://tftp/tftp.c;beginline=1;endline=32;md5=988c1cba99d70858a26cd877209857f4"


SRC_URI = "git://git.kernel.org/pub/scm/network/tftp/tftp-hpa.git;protocol=https;branch=master;tag=tftp-hpa-${PV} \
           file://tftp-0.40-remap.patch \
           file://tftp-0.42-tftpboot.patch \
           file://tftp-0.49-chk_retcodes.patch \
           file://tftp-hpa-5.3-cmd_arg.patch \
           file://tftp-hpa-0.39-tzfix.patch \
           file://tftp-hpa-0.49-fortify-strcpy-crash.patch \
           file://tftp-hpa-0.49-stats.patch \
           file://tftp-hpa-5.3-pktinfo.patch \
           file://add-error-check-for-disk-filled-up.patch \
           file://tftp-hpa-bug-fix-on-separated-CR-and-LF.patch \
           file://fix-writing-emtpy-file.patch \
           file://default \
           file://init \
           file://tftpd-hpa.socket \
           file://tftpd-hpa.service \
          "

SRCREV = "15c4f369ee741e9205dc28ce631aaf6799193b04"

inherit autotools-brokensep update-rc.d update-alternatives systemd

DEPENDS = "readline"

EXTRA_OECONF = "--enable-largefile \
                --enable-year2038 \
                --with-readline \
                --without-tcpwrappers \
               "

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--with-ipv6,--without-ipv6,"

CACHED_CONFIGUREVARS:libc-musl = "ac_cv_type_socklen_t=yes"

do_install() {
    oe_runmake install INSTALLROOT=${D}
    mv ${D}${bindir}/tftp ${D}${bindir}/tftp-hpa
    mv ${D}${sbindir}/in.tftpd ${D}${sbindir}/in.tftpd-hpa

    install -m 755 -d ${D}${localstatedir}/lib/tftpboot/

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/default
        install -m 0644 ${UNPACKDIR}/default ${D}${sysconfdir}/default/tftpd-hpa

        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${UNPACKDIR}/init ${D}${sysconfdir}/init.d/tftpd-hpa
        sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/tftpd-hpa
        sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sysconfdir}/init.d/tftpd-hpa
        sed -i 's!/var/!${localstatedir}/!g' ${D}${sysconfdir}/init.d/tftpd-hpa
        sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${sysconfdir}/init.d/tftpd-hpa
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/tftpd-hpa.socket ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/tftpd-hpa.service ${D}${systemd_unitdir}/system
        sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/tftpd-hpa.service
    fi
}

FILES:${PN} = "${bindir}"

PACKAGES += "tftp-hpa-server"
SUMMARY:tftp-hpa-server = "Server for the Trivial File Transfer Protocol"
FILES:tftp-hpa-server = "${sbindir} ${sysconfdir} ${localstatedir}"
CONFFILES:tftp-hpa-server = "${sysconfdir}/default/tftpd-hpa"

INITSCRIPT_PACKAGES = "tftp-hpa-server"
INITSCRIPT_NAME = "tftpd-hpa"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 20 1 ."

ALTERNATIVE:${PN}-doc = "tftpd.8 tftp.1"
ALTERNATIVE_LINK_NAME[tftpd.8] = "${mandir}/man8/tftpd.8"
ALTERNATIVE_LINK_NAME[tftp.1] = "${mandir}/man1/tftp.1"

ALTERNATIVE:${PN} = "tftp"
ALTERNATIVE_TARGET[tftp] = "${bindir}/tftp-hpa"
ALTERNATIVE_PRIORITY = "100"

SYSTEMD_PACKAGES = "tftp-hpa-server"
SYSTEMD_SERVICE:tftp-hpa-server = "tftpd-hpa.socket tftpd-hpa.service"
SYSTEMD_AUTO_ENABLE:tftp-hpa-server = "enable"
