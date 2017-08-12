inherit obmc-phosphor-license

SYSTEMD_SERVICE_${PN} = "mount-overlay.service"

inherit obmc-phosphor-systemd

SRC_URI = "file://fstab"

FILES_${PN} = "${sysconfdir}/fstab"

do_install() {
	install -m 0644 -D ${WORKDIR}/fstab ${D}${sysconfdir}/fstab
}
