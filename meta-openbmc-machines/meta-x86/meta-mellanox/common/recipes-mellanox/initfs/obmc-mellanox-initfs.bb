SUMMARY = "Phosphor OpenBMC pre-init scripts"
DESCRIPTION = "Phosphor OpenBMC filesytem mount reference implementation."
PR = "r1"

inherit obmc-phosphor-license

S = "${WORKDIR}"
SRC_URI += "file://obmc-init.sh"
SRC_URI += "file://obmc-shutdown.sh"
SRC_URI += "file://obmc-update.sh"
SRC_URI += "file://obmc-update_all.sh"
SRC_URI += "file://failsafe-boot.sh"
SRC_URI += "file://recovery.sh"
SRC_URI += "file://whitelist"

do_install() {
	for f in init-download-url init-options
	do
		if test -e $f
		then
			install -m 0755 ${WORKDIR}/$f ${D}/$f
		fi
	done
        install -m 0755 ${WORKDIR}/obmc-init.sh ${D}/init
        install -m 0755 ${WORKDIR}/obmc-shutdown.sh ${D}/shutdown
        install -m 0755 ${WORKDIR}/obmc-update.sh ${D}/update
	install -m 0755 ${WORKDIR}/failsafe-boot.sh ${D}/failsafe
        install -m 0755 ${WORKDIR}/obmc-update_all.sh ${D}/update_all
	install -m 0755 ${WORKDIR}/recovery.sh ${D}/recovery
        install -m 0644 ${WORKDIR}/whitelist ${D}/whitelist
        install -d ${D}/dev
        mknod -m 622 ${D}/dev/console c 5 1
}

FILES_${PN} += " /init /shutdown /update /update_all /whitelist /dev /failsafe /recovery"
FILES_${PN} += " /init-options /init-download-url "
