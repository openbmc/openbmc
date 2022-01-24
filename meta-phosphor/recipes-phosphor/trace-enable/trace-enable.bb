SUMMARY = "Enable Linux trace events"
DESCRIPTION = "Enable Linux trace events based on a configuration file"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS:${PN} = " \
    ${@d.getVar('PREFERRED_PROVIDER_u-boot-fw-utils', True) or 'u-boot-fw-utils'} \
    bash \
"

S = "${WORKDIR}"

SRC_URI += " file://trace-enable"

TRACE_EVENTS = ""

FILES:${PN} += "${sysconfdir}/trace-events.conf"
FILES:${PN} += "${libexecdir}/trace-enable"

do_install:append() {
    install -d ${D}${sysconfdir}
    for event in ${TRACE_EVENTS}
    do
        echo ${event} >> ${D}${sysconfdir}/trace-events.conf
    done
    echo >> ${D}${sysconfdir}/trace-events.conf
    chmod 0644 ${D}${sysconfdir}/trace-events.conf

    install -d ${D}${libexecdir}
    install -m 0755 ${WORKDIR}/trace-enable ${D}${libexecdir}
}

SYSTEMD_SERVICE:${PN} = "trace-enable.service"

inherit obmc-phosphor-systemd
