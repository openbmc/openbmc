SUMMARY = "Runs postinstall scripts on first boot of the target device"
DESCRIPTION = "${SUMMARY}"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://run-postinsts \
           file://run-postinsts.init \
           file://run-postinsts.service"

S = "${WORKDIR}"

inherit allarch systemd update-rc.d

RDEPENDS:${PN} = "util-linux-fcntl-lock"

INITSCRIPT_NAME = "run-postinsts"
INITSCRIPT_PARAMS = "start 99 S ."

SYSTEMD_SERVICE:${PN} = "run-postinsts.service"

do_configure() {
	:
}

do_compile () {
	:
}

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${WORKDIR}/run-postinsts ${D}${sbindir}/

	install -d ${D}${sysconfdir}/init.d/
	install -m 0755 ${WORKDIR}/run-postinsts.init ${D}${sysconfdir}/init.d/run-postinsts

	install -d ${D}${systemd_system_unitdir}/
	install -m 0644 ${WORKDIR}/run-postinsts.service ${D}${systemd_system_unitdir}/

	sed -i -e 's:#SYSCONFDIR#:${sysconfdir}:g' \
               -e 's:#SBINDIR#:${sbindir}:g' \
               -e 's:#BASE_BINDIR#:${base_bindir}:g' \
               -e 's:#LOCALSTATEDIR#:${localstatedir}:g' \
               ${D}${sbindir}/run-postinsts \
               ${D}${systemd_system_unitdir}/run-postinsts.service
}
