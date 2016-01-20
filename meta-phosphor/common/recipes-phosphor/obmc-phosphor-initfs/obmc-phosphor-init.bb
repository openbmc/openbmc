SUMMARY = "Phosphor OpenBMC pre-init scripts"
DESCRIPTION = "Phosphor OpenBMC filesytem mount reference implementation."
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-initfs

S = "${WORKDIR}"
SRC_URI += "file://obmc-init.sh"
SRC_URI += "file://obmc-shutdown.sh"
SRC_URI += "file://obmc-update.sh"
SRC_URI += "file://whitelist"

do_install() {
	if ${OVERLAY_BASETYPE} != ext4 -o ${IMAGE_BASETYPE} != squashfs-xz
	then
		bbfatal only ext4 overlayed  squashfs-xz is currently supported
	fi

        install -m 0755 ${WORKDIR}/obmc-init.sh ${D}/init
        install -m 0755 ${WORKDIR}/obmc-shutdown.sh ${D}/shutdown
        install -m 0755 ${WORKDIR}/obmc-update.sh ${D}/update
        install -m 0644 ${WORKDIR}/whitelist ${D}/whitelist
        install -d ${D}/dev
        mknod -m 622 ${D}/dev/console c 5 1
}

FILES_${PN} += " /init /shutdown /update /whitelist /dev "
