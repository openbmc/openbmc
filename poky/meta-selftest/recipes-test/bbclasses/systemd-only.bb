LICENSE = "MIT"

inherit allarch systemd

do_install() {
	install -d ${D}${systemd_system_unitdir}
	touch ${D}${systemd_system_unitdir}/${BPN}.service
}

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

EXCLUDE_FROM_WORLD="1"
