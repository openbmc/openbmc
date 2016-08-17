SUMMARY        = "Client for the Trivial File Transfer Protocol"
DESCRIPTION    = \
"The Trivial File Transfer Protocol (TFTP) is normally used only for \
booting diskless workstations.  The tftp package provides the user   \
interface for TFTP, which allows users to transfer files to and from a \
remote machine.  This program and TFTP provide very little security, \
and should not be enabled unless it is expressly needed."
DEPENDS = "tcp-wrappers readline"
SECTION = "net"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://MCONFIG.in;beginline=1;endline=9;md5=c28ba5adb43041fae4629db05c83cbdd \
                    file://tftp/tftp.c;beginline=1;endline=32;md5=988c1cba99d70858a26cd877209857f4"


SRC_URI = "http://kernel.org/pub/software/network/tftp/tftp-hpa/tftp-hpa-${PV}.tar.bz2 \
           file://tftp-0.40-remap.patch \
           file://tftp-0.42-tftpboot.patch \
           file://tftp-0.49-chk_retcodes.patch \
           file://tftp-0.49-cmd_arg.patch \
           file://tftp-hpa-0.39-tzfix.patch \
           file://tftp-hpa-0.49-fortify-strcpy-crash.patch \
           file://tftp-hpa-0.49-stats.patch \
           file://tftp-hpa-5.2-pktinfo.patch \
           file://default \
           file://init \
           file://add-error-check-for-disk-filled-up.patch \
           file://tftp-hpa-bug-fix-on-separated-CR-and-LF.patch \
           file://fix-writing-emtpy-file.patch \
"

SRC_URI[md5sum] = "46c9bd20bbffa62f79c958c7b99aac21"
SRC_URI[sha256sum] = "0a9f88d4c1c02687b4853b02ab5dd8779d4de4ffdb9b2e5c9332841304d1a269"

inherit autotools-brokensep update-rc.d update-alternatives

export AR = "${HOST_PREFIX}ar cq"

EXTRA_OECONF += "--disable-option-checking"

# configure.in has errors
do_configure() {
    oe_runconf
}

do_install() {
    oe_runmake install INSTALLROOT=${D}
    mv ${D}${bindir}/tftp ${D}${bindir}/tftp-hpa
    mv ${D}${sbindir}/in.tftpd ${D}${sbindir}/in.tftpd-hpa

    install -m 755 -d ${D}${localstatedir}/lib/tftpboot/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/tftpd-hpa
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/tftpd-hpa
    sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sysconfdir}/init.d/tftpd-hpa
    sed -i 's!/var/!${localstatedir}/!g' ${D}${sysconfdir}/init.d/tftpd-hpa
    sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${sysconfdir}/init.d/tftpd-hpa

    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/tftpd-hpa
}

FILES_${PN} = "${bindir}"

PACKAGES += "tftp-hpa-server"
SUMMARY_tftp-hpa-server = "Server for the Trivial File Transfer Protocol"
FILES_tftp-hpa-server = "${sbindir} ${sysconfdir} ${localstatedir}"
CONFFILES_tftp-hpa-server = "${sysconfdir}/default/tftpd-hpa"

INITSCRIPT_PACKAGES = "tftp-hpa-server"
INITSCRIPT_NAME = "tftpd-hpa"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 20 1 ."

ALTERNATIVE_${PN} = "tftp"
ALTERNATIVE_TARGET[tftp] = "${bindir}/tftp-hpa"
ALTERNATIVE_PRIORITY = "50"

