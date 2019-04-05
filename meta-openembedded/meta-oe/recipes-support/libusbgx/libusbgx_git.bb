SUMMARY = "USB Gadget neXt Configfs Library"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libconfig"

inherit autotools pkgconfig systemd update-rc.d

PV = "0.2.0+git${SRCPV}"
SRCREV = "45c14ef4d5d7ced0fbf984208de44ced6d5ed898"
SRCBRANCH = "master"
SRC_URI = " \
    git://github.com/libusbgx/libusbgx.git;branch=${SRCBRANCH} \
    file://gadget-start \
    file://usbgx.initd \
    file://usbgx.service \
"

S = "${WORKDIR}/git"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "usbgx.service"

INITSCRIPT_NAME = "usbgx"
INITSCRIPT_PARAMS = "defaults"

EXTRA_OECONF = "--includedir=${includedir}/usbgx"

do_install_append() {
    install -Dm 0755 ${WORKDIR}/gadget-start ${D}/${bindir}/gadget-start
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -Dm 0644 ${WORKDIR}/usbgx.service ${D}${systemd_system_unitdir}/usbgx.service
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		install -Dm 0755 ${WORKDIR}/usbgx.initd ${D}${sysconfdir}/init.d/usbgx
	fi
}

RDEPENDS_${PN} += "libusbgx-config"
