require oprofileui.inc

SRCREV = "389e1875af4721d52c7e65cf9cfffb69b0ed6a59"
PV = "0.0+git${SRCPV}"

S = "${WORKDIR}/git"

SRC_URI = "git://git.yoctoproject.org/oprofileui \
           file://init \
           file://oprofileui-server.service "

DEPENDS += "intltool-native"

EXTRA_OECONF += "--disable-client --enable-server"

RDEPENDS_${PN} = "oprofile avahi-daemon"

do_install_append() {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/oprofileui-server

	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/oprofileui-server.service ${D}${systemd_unitdir}/system/
	sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' \
		-e 's,@BINDIR@,${bindir},g' ${D}${systemd_unitdir}/system/oprofileui-server.service
}

inherit update-rc.d systemd

INITSCRIPT_NAME = "oprofileui-server"
INITSCRIPT_PARAMS = "start 99 5 2 . stop 20 0 1 6 ."

SYSTEMD_SERVICE_${PN} = "oprofileui-server.service"
SYSTEMD_AUTO_ENABLE = "disable"
