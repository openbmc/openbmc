SUMMARY = "Avahi IPv4LL network address configuration daemon"
DESCRIPTION = 'Avahi is a fully LGPL framework for Multicast DNS Service Discovery. It \
allows programs to publish and discover services and hosts running on a local network \
with no specific configuration. This tool implements IPv4LL, "Dynamic Configuration of \
IPv4 Link-Local Addresses" (IETF RFC3927), a protocol for automatic IP address \
configuration from the link-local 169.254.0.0/16 range without the need for a central \
server.'
HOMEPAGE = "http://avahi.org"
BUGTRACKER = "https://github.com/avahi/avahi/issues"
SECTION = "network"

# major part is under LGPL-2.1-or-later, but several .dtd, .xsl, initscripts and
# python scripts are under GPL-2.0-or-later
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://avahi-common/address.h;endline=25;md5=b1d1d2cda1c07eb848ea7d6215712d9d \
                    file://avahi-core/dns.h;endline=23;md5=6fe82590b81aa0ddea5095b548e2fdcb \
                    file://avahi-daemon/main.c;endline=21;md5=9ee77368c5407af77caaef1b07285969 \
                    file://avahi-client/client.h;endline=23;md5=f4ac741a25c4f434039ba3e18c8674cf"

SRC_URI = "git://github.com/avahi/avahi;protocol=https;branch=master;tag=v0.9-rc5 \
           file://00avahi-autoipd \
           file://99avahi-autoipd \
           file://avahi-daemon.in \
           file://avahi-dnsconfd.in \
           file://0001-Fix-opening-etc-resolv.conf-error.patch \
           "

PV = "0.9~rc5"
SRCREV = "71b640e686964efb27cb708f4457ffaed183c319"

GITHUB_BASE_URI = "https://github.com/avahi/avahi/releases/"

CVE_STATUS[CVE-2021-26720] = "not-applicable-platform: Issue only affects Debian/SUSE"

DEPENDS = "expat libcap libdaemon glib-2.0 glib-2.0-native"

# For gtk related PACKAGECONFIGs: gtk, gtk3
AVAHI_GTK ?= ""

PACKAGECONFIG ??= "dbus ${@bb.utils.contains_any('DISTRO_FEATURES','x11 wayland','${AVAHI_GTK}','',d)} ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"
PACKAGECONFIG[gtk] = "--enable-gtk,--disable-gtk,gtk+"
PACKAGECONFIG[gtk3] = "--enable-gtk3,--disable-gtk3,gtk+3"
PACKAGECONFIG[libdns_sd] = "--enable-compat-libdns_sd --enable-dbus,,dbus"
PACKAGECONFIG[libevent] = "--enable-libevent,--disable-libevent,libevent"
PACKAGECONFIG[systemd] = "--enable-libsystemd,--disable-libsystemd --without-systemdsystemunitdir,systemd"
PACKAGECONFIG[qt5] = "--enable-qt5,--disable-qt5,qtbase"

inherit autotools pkgconfig gettext gobject-introspection github-releases

EXTRA_OECONF = " \
             --runstatedir=${runtimedir} \
             --with-avahi-priv-access-group=adm \
             --disable-stack-protector \
             --disable-gdbm \
             --disable-dbm \
             --disable-mono \
             --disable-monodoc \
             --disable-qt3 \
             --disable-qt4 \
             --disable-python \
             --disable-doxygen-doc \
             --disable-manpages \
             ${EXTRA_OECONF_SYSVINIT} \
           "

# The distro choice determines what init scripts are installed
EXTRA_OECONF_SYSVINIT = "${@bb.utils.contains('DISTRO_FEATURES','sysvinit','--with-distro=debian','--with-distro=none',d)}"

do_configure:prepend() {
    # This m4 file will get in the way of our introspection.m4 with special cross-compilation fixes
    rm "${S}/common/introspection.m4" || true
}

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/avahi-gobject/.libs:${B}/avahi-common/.libs:${B}/avahi-client/.libs:${B}/avahi-glib/.libs"
}

RRECOMMENDS:${PN}:append:libc-glibc = " avahi-libnss-mdns"

do_install() {
	autotools_do_install

	rm -rf ${D}${runtimedir}
	test -d ${D}${datadir}/dbus-1 && rmdir --ignore-fail-on-non-empty ${D}${datadir}/dbus-1

	# Move example service files out of /etc/avahi/services so we don't
	# advertise ssh & sftp-ssh by default
	install -d ${D}${docdir}/avahi
	mv ${D}${sysconfdir}/avahi/services/* ${D}${docdir}/avahi
}

PACKAGES =+ "${@bb.utils.contains("PACKAGECONFIG", "libdns_sd", "libavahi-compat-libdnssd", "", d)}"

FILES:libavahi-compat-libdnssd = "${libdir}/libdns_sd.so.*"

RPROVIDES:libavahi-compat-libdnssd = "libdns-sd"

inherit update-rc.d systemd useradd

PACKAGES =+ "libavahi-gobject avahi-daemon libavahi-common libavahi-core libavahi-client avahi-dnsconfd libavahi-glib avahi-autoipd avahi-utils avahi-discover avahi-ui"

FILES:avahi-ui = "${libdir}/libavahi-ui*.so.*"
FILES:avahi-discover = "${datadir}/applications/avahi-discover.desktop \
                        ${datadir}/avahi/interfaces/avahi-discover.ui \
                        ${bindir}/avahi-discover-standalone \
                        "

LICENSE:libavahi-gobject = "LGPL-2.1-or-later"
LICENSE:avahi-daemon = "LGPL-2.1-or-later"
LICENSE:libavahi-common = "LGPL-2.1-or-later"
LICENSE:libavahi-core = "LGPL-2.1-or-later"
LICENSE:libavahi-client = "LGPL-2.1-or-later"
LICENSE:avahi-dnsconfd = "LGPL-2.1-or-later"
LICENSE:libavahi-glib = "LGPL-2.1-or-later"
LICENSE:avahi-autoipd = "LGPL-2.1-or-later"
LICENSE:avahi-utils = "LGPL-2.1-or-later"

# As avahi doesn't put any files into PN, clear the files list to avoid problems
# if extra libraries appear.
FILES:${PN} = ""
FILES:avahi-autoipd = "${sbindir}/avahi-autoipd \
                       ${sysconfdir}/avahi/avahi-autoipd.action \
                       ${sysconfdir}/dhcp/*/avahi-autoipd \
                       ${sysconfdir}/udhcpc.d/00avahi-autoipd \
                       ${sysconfdir}/udhcpc.d/99avahi-autoipd"
FILES:libavahi-common = "${libdir}/libavahi-common.so.*"
FILES:libavahi-core = "${libdir}/libavahi-core.so.* ${libdir}/girepository-1.0/AvahiCore*.typelib"
FILES:avahi-daemon = "${sbindir}/avahi-daemon \
                      ${sysconfdir}/avahi/avahi-daemon.conf \
                      ${sysconfdir}/avahi/hosts \
                      ${sysconfdir}/avahi/services \
                      ${sysconfdir}/init.d/avahi-daemon \
                      ${datadir}/dbus-1 \
                      ${datadir}/avahi/avahi-service.dtd \
                      ${datadir}/avahi/service-types \
                      ${datadir}/dbus-1/system-services"
FILES:libavahi-client = "${libdir}/libavahi-client.so.*"
FILES:avahi-dnsconfd = "${sbindir}/avahi-dnsconfd \
                        ${sysconfdir}/avahi/avahi-dnsconfd.action \
                        ${sysconfdir}/init.d/avahi-dnsconfd"
FILES:libavahi-glib = "${libdir}/libavahi-glib.so.*"
FILES:libavahi-gobject = "${libdir}/libavahi-gobject.so.*  ${libdir}/girepository-1.0/Avahi*.typelib"
FILES:avahi-utils = "${bindir}/avahi-* ${bindir}/b* ${datadir}/applications/b*"

DEV_PKG_DEPENDENCY = "avahi-daemon (= ${EXTENDPKGV}) libavahi-core (= ${EXTENDPKGV})"
DEV_PKG_DEPENDENCY += "${@["", " libavahi-client (= ${EXTENDPKGV})"][bb.utils.contains('PACKAGECONFIG', 'dbus', 1, 0, d)]}"
RDEPENDS:${PN}-dnsconfd = "${PN}-daemon"

RRECOMMENDS:avahi-daemon:append:libc-glibc = " avahi-libnss-mdns"

CONFFILES:avahi-daemon = "${sysconfdir}/avahi/avahi-daemon.conf"

USERADD_PACKAGES = "avahi-daemon avahi-autoipd"
USERADD_PARAM:avahi-daemon = "--system --home /run/avahi-daemon \
                              --no-create-home --shell /bin/false \
                              --user-group avahi"

USERADD_PARAM:avahi-autoipd = "--system --home /run/avahi-autoipd \
                              --no-create-home --shell /bin/false \
                              --user-group \
                              -c \"Avahi autoip daemon\" \
                              avahi-autoipd"

INITSCRIPT_PACKAGES = "avahi-daemon avahi-dnsconfd"
INITSCRIPT_NAME:avahi-daemon = "avahi-daemon"
INITSCRIPT_PARAMS:avahi-daemon = "defaults 21 19"
INITSCRIPT_NAME:avahi-dnsconfd = "avahi-dnsconfd"
INITSCRIPT_PARAMS:avahi-dnsconfd = "defaults 22 19"

SYSTEMD_PACKAGES = "${PN}-daemon ${PN}-dnsconfd"
SYSTEMD_SERVICE:${PN}-daemon = "avahi-daemon.service"
SYSTEMD_SERVICE:${PN}-dnsconfd = "avahi-dnsconfd.service"

do_install:append() {
	install -d ${D}${sysconfdir}/udhcpc.d
	install ${UNPACKDIR}/00avahi-autoipd ${D}${sysconfdir}/udhcpc.d
	install ${UNPACKDIR}/99avahi-autoipd ${D}${sysconfdir}/udhcpc.d

	install -d ${D}${sysconfdir}/init.d
	install ${UNPACKDIR}/avahi-daemon.in ${D}${sysconfdir}/init.d/avahi-daemon
	install ${UNPACKDIR}/avahi-dnsconfd.in ${D}${sysconfdir}/init.d/avahi-dnsconfd
	sed -i -e 's,@sbindir@,${sbindir},g' -e 's,@sysconfdir@,${sysconfdir},g' ${D}${sysconfdir}/init.d/avahi-*
}

# At the time the postinst runs, dbus might not be setup so only restart if running 
# Don't exit early, because update-rc.d needs to run subsequently.
pkg_postinst:avahi-daemon () {
if [ -z "$D" ]; then
	killall -q -HUP dbus-daemon || true
fi
}

