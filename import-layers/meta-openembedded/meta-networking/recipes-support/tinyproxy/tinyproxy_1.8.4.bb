SUMMARY = "Lightweight http(s) proxy daemon"
HOMEPAGE = "https://tinyproxy.github.io/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.gz \
		   file://disable-documentation.patch \
		   file://tinyproxy.service"

SRC_URI[md5sum] = "3b60f7d08e0821ed1a3e2cf1e5778cac"
SRC_URI[sha256sum] = "8234c879a129feee61efa98bac14a1a3e46e5cf08f01696a216940872aa70faf"

EXTRA_OECONF += " \
	--enable-filter \
	--enable-transparent \
	--disable-regexcheck \
	--enable-reverse \
	--enable-upstream \
	--enable-xtinyproxy \
	"

inherit autotools systemd useradd

#User specific
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "nobody"
GROUPADD_PARAM_${PN} = "--system tinyproxy"

SYSTEMD_PACKAGES += "${BPN}"
SYSTEMD_SERVICE_${PN} = "tinyproxy.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

do_install_append() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_system_unitdir}
		install -m 0644 ${WORKDIR}/tinyproxy.service ${D}${systemd_system_unitdir}
	fi
}
