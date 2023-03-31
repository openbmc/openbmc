SUMMARY = "The network configuration abstraction renderer"
DESCRIPTION = "Netplan is a utility for easily configuring networking on a \
linux system. You simply create a YAML description of the required network \
interfaces and what each should be configured to do. From this description \
Netplan will generate all the necessary configuration for your chosen renderer \
tool."
HOMEPAGE = "https://netplan.io"
SECTION = "net/misc"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

S = "${WORKDIR}/git"
SRCREV = "15ce044d1df27b5057556d84d0d14beef8dd4e4d"
PV = "0.106"

SRC_URI = "git://github.com/CanonicalLtd/netplan.git;branch=main;protocol=https \
           file://0001-Makefile-do-not-use-Werror.patch \
           "

SRC_URI:append:libc-musl = " file://0001-don-t-fail-if-GLOB_BRACE-is-not-defined.patch"

DEPENDS = "glib-2.0 libyaml ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

PACKAGECONFIG ?= ""

PACKAGECONFIG[tests] = ",,,python3-nose python3-coverage python3-netifaces python3-pycodestyle python3-pyflakes python3-pyyaml"

RDEPENDS:${PN} = "python3 python3-core python3-netifaces python3-pyyaml util-linux-libuuid libnetplan python3-dbus python3-rich"

inherit pkgconfig systemd

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OEMAKE = "generate netplan/_features.py"
EXTRA_OEMAKE =+ "${@bb.utils.contains('DISTRO_FEATURES','systemd','netplan-dbus dbus/io.netplan.Netplan.service','',d)}"

do_install() {
	install -d ${D}${sbindir} ${D}${libdir} ${D}${base_libdir}/netplan ${D}${datadir}/netplan/netplan/cli/commands ${D}${sysconfdir}/netplan
	install -m 755 ${S}/generate ${D}${base_libdir}/netplan/
	install -m 644 ${S}/netplan/*.py ${D}${datadir}/netplan/netplan
	install -m 644 ${S}/netplan/cli/*.py ${D}${datadir}/netplan/netplan/cli
	install -m 644 ${S}/netplan/cli/commands/*.py ${D}${datadir}/netplan/netplan/cli/commands
	install -m 755 ${S}/src/netplan.script ${D}${datadir}/netplan/
	ln -srf ${D}${datadir}/netplan/netplan.script ${D}${sbindir}/netplan
	sed -i -e "s#/lib/netplan/generate#${base_libdir}/netplan/generate#" ${D}${datadir}/netplan/netplan/cli/utils.py

	install -d ${D}/${systemd_unitdir}/system ${D}${systemd_unitdir}/system-generators
	ln -srf ${D}/${base_libdir}/netplan/generate ${D}${systemd_unitdir}/system-generators

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}${datadir}/dbus-1/system.d ${D}${datadir}/dbus-1/system-services
		install -m 755 ${S}/netplan-dbus ${D}${base_libdir}/netplan
		install -m 644 ${S}/dbus/io.netplan.Netplan.conf ${D}${datadir}/dbus-1/system.d
		install -m 644 ${S}/dbus/io.netplan.Netplan.service ${D}${datadir}/dbus-1/system-services
		sed -i -e "s#^Exec=/lib/#Exec=${base_libdir}/#" ${D}${datadir}/dbus-1/system-services/io.netplan.Netplan.service
	fi

	install -m 755 ${S}/libnetplan.so.0.0 ${D}${libdir}
        ln -rfs ${D}${libdir}/libnetplan.so.0.0 ${D}${libdir}/libnetplan.so
}

PACKAGES += "${PN}-dbus libnetplan"

FILES:libnetplan = "${libdir}/libnetplan.so.0.0"
FILES:${PN} = "${sbindir} ${base_libdir}/netplan/generate ${datadir}/netplan ${sysconfdir}/netplan ${systemd_unitdir}"
FILES:${PN}-dbus = "${base_libdir}/netplan/netplan-dbus ${datadir}/dbus-1"
