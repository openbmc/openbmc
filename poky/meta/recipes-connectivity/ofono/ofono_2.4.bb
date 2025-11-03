SUMMARY = "open source telephony"
DESCRIPTION = "oFono is a stack for mobile telephony devices on Linux. oFono supports speaking to telephony devices through specific drivers, or with generic AT commands."
HOMEPAGE = "http://www.ofono.org"
BUGTRACKER = "https://01.org/jira/browse/OF"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://src/ofono.h;beginline=1;endline=20;md5=3ce17d5978ef3445def265b98899c2ee"
DEPENDS = "dbus glib-2.0 udev mobile-broadband-provider-info ell"

SRC_URI = "\
    ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
    file://ofono \
    file://0001-mbim-add-an-optional-TEMP_FAILURE_RETRY-macro-copy.patch \
    file://0002-mbim-Fix-build-with-ell-0.39-by-restoring-unlikely-m.patch \
    file://CVE-2023-2794-0001.patch \
    file://CVE-2023-2794-0002.patch \
    file://CVE-2023-2794-0003.patch \
    file://CVE-2023-2794-0004.patch \
    file://CVE-2024-7539.patch \
    file://CVE-2024-7543.patch \
    file://CVE-2024-7544.patch \
    file://CVE-2024-7545.patch \
    file://CVE-2024-7546.patch \
    file://CVE-2024-7547.patch \
    file://CVE-2024-7540_CVE-2024-7541_CVE-2024-7542.patch \
    file://CVE-2023-4232.patch \
    file://CVE-2023-4235.patch \
    file://CVE-2024-7537.patch \
"
SRC_URI[sha256sum] = "93580adc1afd1890dc516efb069de0c5cdfef014415256ddfb28ab172df2d11d"

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

do_configure:prepend() {
    bbnote "Removing bundled ell from ${S}/ell to prevent including it"
    rm -rf ${S}/ell
}

do_install:append() {
    install -d ${D}${sysconfdir}/init.d/
    install -m 0755 ${WORKDIR}/ofono ${D}${sysconfdir}/init.d/ofono
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
