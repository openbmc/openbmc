FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://avahi.conf"
SRC_URI += "file://check-avahi-pre-cond.service"
SRC_URI += "file://create-user"
SRC_URI += "file://check-local-domain"

RRECOMMENDS_${PN}-daemon += "bind-utils"

AVAHI_SVC = "avahi-daemon.service"

AVAHI_DROPIN_DIR = "${AVAHI_SVC}.d"

FILES_${PN}-daemon_append += "${systemd_system_unitdir}/${AVAHI_DROPIN_DIR}/avahi.conf"

PACKAGE_BEFORE_PN += "${PN}-daemon-preconditions"

FILES_${PN}-daemon-preconditions += "${systemd_system_unitdir}/check-avahi-pre-cond.service"
FILES_${PN}-daemon-preconditions += "${sbindir}/create-user"
FILES_${PN}-daemon-preconditions += "${sbindir}/check-local-domain"

RRECOMMENDS_${PN}-daemon += "${PN}-daemon-preconditions"

do_install_append() {

    mkdir -p ${D}/${systemd_system_unitdir}/${AVAHI_DROPIN_DIR}
    install -m 0755 ${WORKDIR}/avahi.conf ${D}/${systemd_system_unitdir}/${AVAHI_DROPIN_DIR}/avahi.conf
    install -m 0755 ${WORKDIR}/check-avahi-pre-cond.service ${D}/${systemd_system_unitdir}/check-avahi-pre-cond.service
    install -m 0755 ${WORKDIR}/check-local-domain ${D}/${sbindir}/check-local-domain
    install -m 0755 ${WORKDIR}/create-user ${D}/${sbindir}/create-user
}
