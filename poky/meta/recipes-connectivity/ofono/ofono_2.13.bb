SUMMARY = "open source telephony"
DESCRIPTION = "oFono is a stack for mobile telephony devices on Linux. oFono supports speaking to telephony devices through specific drivers, or with generic AT commands."
HOMEPAGE = "http://www.ofono.org"
BUGTRACKER = "https://01.org/jira/browse/OF"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://src/ofono.h;beginline=1;endline=6;md5=13e42133935ceecfc9bcb547f256e277"
DEPENDS = "dbus glib-2.0 udev mobile-broadband-provider-info ell"

SRC_URI = "\
    ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
    file://ofono \
"
SRC_URI[sha256sum] = "fa813ed74a4658e5702f707033497bf891504ffdc0f1b45d85bc3f092010744f"

inherit autotools pkgconfig update-rc.d systemd gobject-introspection-data

INITSCRIPT_NAME = "ofono"
INITSCRIPT_PARAMS = "defaults 22"
SYSTEMD_SERVICE:${PN} = "ofono.service"

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
"
PACKAGECONFIG[systemd] = "--with-systemdunitdir=${systemd_system_unitdir}/,--with-systemdunitdir="
PACKAGECONFIG[bluez] = "--enable-bluetooth, --disable-bluetooth, bluez5"

EXTRA_OECONF += "--enable-test --enable-external-ell"

do_install:append() {
    install -d ${D}${sysconfdir}/init.d/
    install -m 0755 ${UNPACKDIR}/ofono ${D}${sysconfdir}/init.d/ofono
}

PACKAGES =+ "${PN}-tests"

FILES:${PN} += "${systemd_unitdir}"
FILES:${PN}-tests = "${libdir}/${BPN}/test"

RDEPENDS:${PN} += "dbus"
RDEPENDS:${PN}-tests = "\
    python3-core \
    python3-dbus \
    ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'python3-pygobject', '', d)} \
"

RRECOMMENDS:${PN} += "kernel-module-tun mobile-broadband-provider-info"
