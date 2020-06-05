require alsa-utils.inc

SUMMARY = "Shell scripts that show help info and create ALSA configuration files"
PROVIDES = "alsa-utils-alsaconf"

FILESEXTRAPATHS_prepend := "${THISDIR}/alsa-utils:"

PACKAGES = "${PN}"
RDEPENDS_${PN} += "bash"

FILES_${PN} = "${sbindir}/alsaconf \
               ${sbindir}/alsa-info.sh \
               ${sbindir}/alsabat-test.sh \
              "

S = "${WORKDIR}/alsa-utils-${PV}"

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${B}/alsaconf/alsaconf ${D}${sbindir}/
	install -m 0755 ${S}/alsa-info/alsa-info.sh ${D}${sbindir}/
	if ${@bb.utils.contains('PACKAGECONFIG', 'bat', 'true', 'false', d)}; then
		install -m 0755 ${S}/bat/alsabat-test.sh ${D}${sbindir}/
	fi
}
