SUMMARY = "SMS Gateway software"
DESCRIPTION = "The SMS Server Tools 3 is a SMS Gateway software which can send and receive short messages through GSM modems and mobile phones."
SECTION = "console/network"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4d21efa1bb2a186360dde4035f860682"
HOMEPAGE = "http://smstools3.kekekasvi.com"

SRC_URI = "http://smstools3.kekekasvi.com/packages/${BP}.tar.gz \
           file://sms_binpath.patch \
           file://scripts_no_bash.patch \
           file://0001-Make-extern-declarations-to-avoid-duplicate-var-defi.patch \
           "

SRC_URI[md5sum] = "6a9f038fb38a49cc3a4f8f14a88fb8af"
SRC_URI[sha256sum] = "a26ba4c02b16f6cf13177bffca6c9230dc5fefaeba8e3030cd4e4905f6a92084"


S = "${WORKDIR}/${BPN}"

EXTRA_OEMAKE += "LFLAGS='${LDFLAGS}'"

INITSCRIPT_NAME = "sms3"
INITSCRIPT_PARAMS = "defaults"

inherit update-rc.d

do_install () {

    install -d ${D}${bindir}
    install -m 755 ${S}/src/smsd "${D}${bindir}/smsd"

    install -m 755 ${S}/scripts/sendsms "${D}${bindir}/sendsms"
    install -m 755 ${S}/scripts/sms2html "${D}${bindir}/sms2html"
    install -m 755 ${S}/scripts/sms2unicode "${D}${bindir}/sms2unicode"
    install -m 755 ${S}/scripts/unicode2sms "${D}${bindir}/unicode2sms"

    install -d ${D}${sysconfdir}
    install -m 644 ${S}/examples/smsd.conf.easy "${D}${sysconfdir}/smsd.conf"

    install -d "${D}${localstatedir}/spool"
    install -d "${D}${localstatedir}/spool/sms"
    install -d "${D}${localstatedir}/spool/sms/incoming"
    install -d "${D}${localstatedir}/spool/sms/outgoing"
    install -d "${D}${localstatedir}/spool/sms/checked"

    install -d ${D}${sysconfdir}/init.d
    install -m 755 ${S}/scripts/sms3 "${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}"

}
