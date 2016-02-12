SUMMARY = "Phosphor OpenBMC pre-init scripts"
DESCRIPTION = "Phosphor OpenBMC filesytem mount reference implementation."
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-initfs

S = "${WORKDIR}"
SRC_URI += "file://obmc-init.sh"
SRC_URI += "file://obmc-shutdown.sh"
SRC_URI += "file://obmc-update.sh"
SRC_URI += "file://whitelist.d/dropbear"
SRC_URI += "file://whitelist.d/events"
SRC_URI += "file://whitelist.d/networkd"
SRC_URI += "file://whitelist.d/users"
SRC_URI += "file://whitelist.d/uuid"

do_install() {
        install -m 0755 ${S}/obmc-init.sh ${D}/init
        install -m 0755 ${S}/obmc-shutdown.sh ${D}/shutdown
        install -m 0755 ${S}/obmc-update.sh ${D}/update
        install -d ${D}/whitelist.d/
        install -m 0644 ${S}/whitelist.d/dropbear ${D}/whitelist.d
        install -m 0644 ${S}/whitelist.d/events ${D}/whitelist.d
        install -m 0644 ${S}/whitelist.d/networkd ${D}/whitelist.d
        install -m 0644 ${S}/whitelist.d/users ${D}/whitelist.d
        install -m 0644 ${S}/whitelist.d/uuid ${D}/whitelist.d
        install -d ${D}/dev
        mknod -m 622 ${D}/dev/console c 5 1
}

FILES_${PN} += " /init /shutdown /update /dev "
FILES_${PN} += " /whitelist.d/dropbear  "
FILES_${PN} += " /whitelist.d/events  "
FILES_${PN} += " /whitelist.d/networkd  "
FILES_${PN} += " /whitelist.d/users  "
FILES_${PN} += " /whitelist.d/uuid  "
