SUMMARY = "Lightweight http(s) proxy daemon"
HOMEPAGE = "https://tinyproxy.github.io/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.gz \
           file://disable-documentation.patch \
           file://tinyproxy.service \
           file://tinyproxy.conf \
           "

SRC_URI[md5sum] = "423047c8dc53a15e19f78e238198549c"
SRC_URI[sha256sum] = "6020955e6a0ef0ef898ad5bb17a448c47f9e4c003c464b4ae7c4dba063272055"

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
USERADD_PARAM_${PN} = "--system --home /dev/null \
                       --no-user-group --gid nogroup tinyproxy"

SYSTEMD_PACKAGES += "${BPN}"
SYSTEMD_SERVICE_${PN} = "tinyproxy.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

do_install_append() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_system_unitdir}
		install -m 0644 ${WORKDIR}/tinyproxy.service ${D}${systemd_system_unitdir}
	fi
	install -m 0644 ${WORKDIR}/tinyproxy.conf ${D}${sysconfdir}/tinyproxy.conf
}
