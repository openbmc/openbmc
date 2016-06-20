require alsa-utils_${PV}.bb

SUMMARY = "Shell scripts that show help info and create ALSA configuration files"
PROVIDES = "alsa-utils-alsaconf"

FILESEXTRAPATHS_prepend := "${THISDIR}/alsa-utils:"

PACKAGES = "${PN}"
RDEPENDS_${PN} += "bash"

FILES_${PN} = "${sbindir}/alsaconf \
               ${sbindir}/alsa-info.sh \
              "

S = "${WORKDIR}/alsa-utils-${PV}"

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${B}/alsaconf/alsaconf ${D}${sbindir}/
	install -m 0755 ${S}/alsa-info/alsa-info.sh ${D}${sbindir}/
}
