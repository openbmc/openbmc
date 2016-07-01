SUMMARY = "Phosphor OpenBMC pre-init scripts"
DESCRIPTION = "Phosphor OpenBMC filesytem mount reference implementation."
PR = "r1"

inherit obmc-phosphor-license

S = "${WORKDIR}"
SRC_URI += "file://obmc-init.sh"
SRC_URI += "file://obmc-shutdown.sh"
SRC_URI += "file://obmc-update.sh"
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
        install -d ${D}/whitelist.d
        install -m 0644 ${WORKDIR}/whitelist ${D}/whitelist.d/whitelist
        install -d ${D}/dev
        mknod -m 622 ${D}/dev/console c 5 1
}

FILES_${PN} += " /init /shutdown /update /whitelist.d/whitelist /dev "
FILES_${PN} += " /init-options /init-download-url "
