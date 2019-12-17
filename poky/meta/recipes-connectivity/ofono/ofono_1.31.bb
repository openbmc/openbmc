SUMMARY = "open source telephony"
DESCRIPTION = "oFono is a stack for mobile telephony devices on Linux. oFono supports speaking to telephony devices through specific drivers, or with generic AT commands."
HOMEPAGE = "http://www.ofono.org"
BUGTRACKER = "https://01.org/jira/browse/OF"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://src/ofono.h;beginline=1;endline=20;md5=3ce17d5978ef3445def265b98899c2ee"
DEPENDS = "dbus glib-2.0 udev mobile-broadband-provider-info ell"

SRC_URI = "\
    ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
    file://ofono \
    file://0001-mbim-add-an-optional-TEMP_FAILURE_RETRY-macro-copy.patch \
"
SRC_URI[md5sum] = "1c26340e3c6ed132cc812595081bb3dc"
SRC_URI[sha256sum] = "a15c5d28096c10eb30e47a68b6dc2e7c4a5a99d7f4cfedf0b69624f33d859e9b"

inherit autotools pkgconfig update-rc.d systemd gobject-introspection-data

INITSCRIPT_NAME = "ofono"
INITSCRIPT_PARAMS = "defaults 22"
SYSTEMD_SERVICE_${PN} = "ofono.service"

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
"
PACKAGECONFIG[systemd] = "--with-systemdunitdir=${systemd_unitdir}/system/,--with-systemdunitdir="
PACKAGECONFIG[bluez] = "--enable-bluetooth, --disable-bluetooth, bluez5"

EXTRA_OECONF += "--enable-test --enable-external-ell"

do_install_append() {
  install -d ${D}${sysconfdir}/init.d/
  install -m 0755 ${WORKDIR}/ofono ${D}${sysconfdir}/init.d/ofono
}

PACKAGES =+ "${PN}-tests"

FILES_${PN} += "${systemd_unitdir}"
FILES_${PN}-tests = "${libdir}/${BPN}/test"

RDEPENDS_${PN} += "dbus"
RDEPENDS_${PN}-tests = "\
    python3-core \
    python3-dbus \
    ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'python3-pygobject', '', d)} \
"

RRECOMMENDS_${PN} += "kernel-module-tun mobile-broadband-provider-info"
