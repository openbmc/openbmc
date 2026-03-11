LICENSE = "MIT"

inherit allarch systemd update-rc.d

do_install() {
	install -d ${D}${systemd_system_unitdir}
	touch ${D}${systemd_system_unitdir}/${BPN}.service

	install -d ${D}${INIT_D_DIR}
	touch ${D}${INIT_D_DIR}/${BPN}
}

INITSCRIPT_NAME = "${BPN}"

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

EXCLUDE_FROM_WORLD = "1"
