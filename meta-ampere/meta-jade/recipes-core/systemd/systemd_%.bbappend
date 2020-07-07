FILESEXTRAPATHS_append_mtjade := "${THISDIR}/${PN}:"
SRC_URI += "file://systemd-timedated.service"

#Overwrite the default systemd-timedated.service with a version that qcquires
#    and releases the rtc_lock GPIO_Z3
do_install_append_mtjade() {
	cp ${WORKDIR}/systemd-timedated.service ${D}${systemd_system_unitdir}/systemd-timedated.service
}
