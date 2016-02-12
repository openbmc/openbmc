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
        install -m 0755 ${S}/obmc-init.sh ${D}/init
        install -m 0755 ${S}/obmc-shutdown.sh ${D}/shutdown
        install -m 0755 ${S}/obmc-update.sh ${D}/update
        install -m 0644 ${S}/whitelist ${D}/whitelist
        install -d ${D}/dev
        mknod -m 622 ${D}/dev/console c 5 1
}

FILES_${PN} += " /init /shutdown /update /whitelist /dev "
