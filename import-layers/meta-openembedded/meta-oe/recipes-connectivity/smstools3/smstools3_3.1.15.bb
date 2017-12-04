SUMMARY = "SMS Gateway software"
DESCRIPTION = "The SMS Server Tools 3 is a SMS Gateway software which can send and receive short messages through GSM modems and mobile phones."
SECTION = "console/network"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4d21efa1bb2a186360dde4035f860682"
HOMEPAGE = "http://smstools3.kekekasvi.com"

SRC_URI = "http://smstools3.kekekasvi.com/packages/${BP}.tar.gz \
           file://sms_binpath_and_psops.patch \
           file://fix-makefile-override.patch"

SRC_URI[md5sum] = "0241ef60e646fac1a06254a848e61ed7"
SRC_URI[sha256sum] = "ed00ffaeaa312a5b4f969f4e97a64603a866bbe16e393ea02f5bf05234814d59"


S = "${WORKDIR}/${BPN}"

EXTRA_OEMAKE += "LFLAGS='${LDFLAGS}'"

RDEPENDS_${PN} = "bash"
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
