SUMMARY = "Lightweight http(s) proxy daemon"
HOMEPAGE = "https://tinyproxy.github.io/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.gz \
           file://disable-documentation.patch \
           file://tinyproxy.service \
           file://tinyproxy.conf \
           file://CVE-2022-40468.patch \
           "

SRC_URI[sha256sum] = "1574acf7ba83c703a89e98bb2758a4ed9fda456f092624b33cfcf0ce2d3b2047"

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
		install -m 0644 ${UNPACKDIR}/tinyproxy.service ${D}${systemd_system_unitdir}
	fi
	install -m 0644 ${UNPACKDIR}/tinyproxy.conf ${D}${sysconfdir}/tinyproxy.conf
}
