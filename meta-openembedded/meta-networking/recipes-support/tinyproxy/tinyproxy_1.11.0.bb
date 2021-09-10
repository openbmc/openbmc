SUMMARY = "Lightweight http(s) proxy daemon"
HOMEPAGE = "https://tinyproxy.github.io/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.gz \
           file://disable-documentation.patch \
           file://tinyproxy.service \
           file://tinyproxy.conf \
           "

SRC_URI[md5sum] = "658db5558ffb849414341b756a546a99"
SRC_URI[sha256sum] = "20f74769e40144e4d251d2977cc4c40d2d428a2bec8c1b8709cd07315454baef"

UPSTREAM_CHECK_URI = "https://github.com/tinyproxy/tinyproxy/releases"

EXTRA_OECONF += " \
	--enable-filter \
	--enable-transparent \
	--enable-reverse \
	--enable-upstream \
	--enable-xtinyproxy \
	"

inherit autotools systemd useradd

#User specific
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --home /dev/null \
                       --no-user-group --gid nogroup tinyproxy"

SYSTEMD_PACKAGES += "${BPN}"
SYSTEMD_SERVICE:${PN} = "tinyproxy.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install:append() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_system_unitdir}
		install -m 0644 ${WORKDIR}/tinyproxy.service ${D}${systemd_system_unitdir}
	fi
	install -m 0644 ${WORKDIR}/tinyproxy.conf ${D}${sysconfdir}/tinyproxy.conf
}
