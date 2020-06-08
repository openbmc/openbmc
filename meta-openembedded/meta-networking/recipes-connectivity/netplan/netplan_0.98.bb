SUMMARY = "The network configuration abstraction renderer"
DESCRIPTION = "Netplan is a utility for easily configuring networking on a \
linux system. You simply create a YAML description of the required network \
interfaces and what each should be configured to do. From this description \
Netplan will generate all the necessary configuration for your chosen renderer \
tool."
HOMEPAGE = "https://netplan.io"
SECTION = "net/misc"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

S = "${WORKDIR}/git"
SRCREV = "5d22e9d22c4a3724d27b80b0cd9b898ae8f59d2b"
PV = "0.98+git${SRCPV}"

SRC_URI = " \
        git://github.com/CanonicalLtd/netplan.git \
"

DEPENDS = "glib-2.0 libyaml ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

RDEPENDS_${PN} = "python3 python3-core python3-pyyaml python3-netifaces python3-nose python3-coverage python3-pycodestyle python3-pyflakes util-linux-libuuid"

inherit pkgconfig systemd

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OEMAKE = "generate"
EXTRA_OEMAKE =+ "${@bb.utils.contains('DISTRO_FEATURES','systemd','netplan-dbus dbus/io.netplan.Netplan.service','',d)}"

do_install() {
	install -d ${D}${sbindir} ${D}${base_libdir}/netplan ${D}${datadir}/netplan/netplan/cli/commands ${D}${sysconfdir}/netplan
	install -m 755 ${S}/generate ${D}${base_libdir}/netplan/
	install -m 644 ${S}/netplan/*.py ${D}${datadir}/netplan/netplan
	install -m 644 ${S}/netplan/cli/*.py ${D}${datadir}/netplan/netplan/cli
	install -m 644 ${S}/netplan/cli/commands/*.py ${D}${datadir}/netplan/netplan/cli/commands
	install -m 755 ${S}/src/netplan.script ${D}${datadir}/netplan/
	ln -srf ${D}${datadir}/netplan/netplan.script ${D}${sbindir}/netplan

	install -d ${D}/${systemd_unitdir}/system ${D}${systemd_unitdir}/system-generators
	install -m 644 ${S}/src/netplan-wpa@.service ${D}${systemd_unitdir}/system/
	ln -srf ${D}/${base_libdir}/netplan/generate ${D}${systemd_unitdir}/system-generators

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}${datadir}/dbus-1/system.d ${D}${datadir}/dbus-1/system-services
		install -m 755 ${S}/netplan-dbus ${D}${base_libdir}/netplan
		install -m 644 ${S}/dbus/io.netplan.Netplan.conf ${D}${datadir}/dbus-1/system.d
		install -m 644 ${S}/dbus/io.netplan.Netplan.service ${D}${datadir}/dbus-1/system-services
	fi
}

SYSTEMD_SERVICE_${PN} = "netplan-wpa@.service"
SYSTEMD_AUTO_ENABLE = "disable"

PACKAGES += "${PN}-dbus"

FILES_${PN} = "${sbindir} ${base_libdir}/netplan/generate ${datadir}/netplan ${sysconfdir}/netplan ${systemd_unitdir}"
FILES_${PN}-dbus = "${base_libdir}/netplan/netplan-dbus ${datadir}/dbus-1"
