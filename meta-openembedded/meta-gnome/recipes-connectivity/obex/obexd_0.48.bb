SUMMARY = "OBEX Server and Client"
# obexd was integrated into bluez5
DEPENDS = "glib-2.0 dbus libical"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES','bluez5','bluez5','bluez4',d)}"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

SRC_URI = "http://www.kernel.org/pub/linux/bluetooth/obexd-${PV}.tar.gz \
           file://ssize_t_definition.patch \
"
SRC_URI[md5sum] = "d03cf9bad2983243837f4f6d76ef14a6"
SRC_URI[sha256sum] = "eaa9d8d9542700e6750918d72a3ce00f8cf3a2771d3e2516efd1be5a05f78582"

inherit autotools-brokensep pkgconfig

PACKAGES =+ "obex-client obex-plugins"

FILES_${PN} += "${datadir}/dbus-1/services/${PN}.service"
FILES_obex-client = "${libexecdir}/obex-client \
                     ${datadir}/dbus-1/services/obex-client.service"
# currently the plugins are empty
FILES_obex-plugins = "${libdir}/obex/plugins"
