SUMMARY = "OpenPower Hostboot Boot settings tool"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd
inherit allarch

SRC_URI = "file://hb_settings"
SYSTEMD_SERVICE_${PN} += "hostboot-settings.service"

do_fetch[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/hb_settings ${D}${bindir}
}
