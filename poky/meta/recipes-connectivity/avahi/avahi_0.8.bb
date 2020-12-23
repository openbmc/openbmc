SUMMARY = "Avahi IPv4LL network address configuration daemon"
DESCRIPTION = 'Avahi is a fully LGPL framework for Multicast DNS Service Discovery. It \
allows programs to publish and discover services and hosts running on a local network \
with no specific configuration. This tool implements IPv4LL, "Dynamic Configuration of \
IPv4 Link-Local Addresses" (IETF RFC3927), a protocol for automatic IP address \
configuration from the link-local 169.254.0.0/16 range without the need for a central \
server.'
AUTHOR = "Lennart Poettering <lennart@poettering.net>"
HOMEPAGE = "http://avahi.org"
BUGTRACKER = "https://github.com/lathiat/avahi/issues"
SECTION = "network"

# major part is under LGPLv2.1+, but several .dtd, .xsl, initscripts and
# python scripts are under GPLv2+
LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://avahi-common/address.h;endline=25;md5=b1d1d2cda1c07eb848ea7d6215712d9d \
                    file://avahi-core/dns.h;endline=23;md5=6fe82590b81aa0ddea5095b548e2fdcb \
                    file://avahi-daemon/main.c;endline=21;md5=9ee77368c5407af77caaef1b07285969 \
                    file://avahi-client/client.h;endline=23;md5=f4ac741a25c4f434039ba3e18c8674cf"

SRC_URI = "https://github.com/lathiat/avahi/releases/download/v${PV}/avahi-${PV}.tar.gz \
           file://00avahi-autoipd \
           file://99avahi-autoipd \
           file://initscript.patch \
           file://0001-Fix-opening-etc-resolv.conf-error.patch \
           "

UPSTREAM_CHECK_URI = "https://github.com/lathiat/avahi/releases/"
SRC_URI[md5sum] = "229c6aa30674fc43c202b22c5f8c2be7"
SRC_URI[sha256sum] = "060309d7a333d38d951bc27598c677af1796934dbd98e1024e7ad8de798fedda"

DEPENDS = "expat libcap libdaemon glib-2.0 intltool-native"

# For gtk related PACKAGECONFIGs: gtk, gtk3
AVAHI_GTK ?= "gtk3"

PACKAGECONFIG ??= "dbus ${@bb.utils.contains_any('DISTRO_FEATURES','x11 wayland','${AVAHI_GTK}','',d)}"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"
PACKAGECONFIG[gtk] = "--enable-gtk,--disable-gtk,gtk+"
PACKAGECONFIG[gtk3] = "--enable-gtk3,--disable-gtk3,gtk+3"
PACKAGECONFIG[libdns_sd] = "--enable-compat-libdns_sd --enable-dbus,,dbus"
PACKAGECONFIG[libevent] = "--enable-libevent,--disable-libevent,libevent"
PACKAGECONFIG[qt5] = "--enable-qt5,--disable-qt5,qtbase"

inherit autotools pkgconfig gettext gobject-introspection

EXTRA_OECONF = "--with-avahi-priv-access-group=adm \
             --disable-stack-protector \
             --disable-gdbm \
             --disable-dbm \
             --disable-mono \
             --disable-monodoc \
             --disable-qt3 \
             --disable-qt4 \
             --disable-python \
             --disable-doxygen-doc \
             --enable-manpages \
             ${EXTRA_OECONF_SYSVINIT} \
             ${EXTRA_OECONF_SYSTEMD} \
           "

# The distro choice determines what init scripts are installed
EXTRA_OECONF_SYSVINIT = "${@bb.utils.contains('DISTRO_FEATURES','sysvinit','--with-distro=debian','--with-distro=none',d)}"
EXTRA_OECONF_SYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES','systemd','--with-systemdsystemunitdir=${systemd_unitdir}/system/','--without-systemdsystemunitdir',d)}"

do_configure_prepend() {
    # This m4 file will get in the way of our introspection.m4 with special cross-compilation fixes
    rm "${S}/common/introspection.m4" || true
}

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/avahi-gobject/.libs:${B}/avahi-common/.libs:${B}/avahi-client/.libs:${B}/avahi-glib/.libs"
}

RRECOMMENDS_${PN}_append_libc-glibc = " libnss-mdns"

do_install() {
	autotools_do_install
	rm -rf ${D}/run
	rm -rf ${D}${datadir}/dbus-1/interfaces
	test -d ${D}${datadir}/dbus-1 && rmdir --ignore-fail-on-non-empty ${D}${datadir}/dbus-1
	rm -rf ${D}${libdir}/avahi

	# Move example service files out of /etc/avahi/services so we don't
	# advertise ssh & sftp-ssh by default
	install -d ${D}${docdir}/avahi
	mv ${D}${sysconfdir}/avahi/services/* ${D}${docdir}/avahi
}

PACKAGES =+ "${@bb.utils.contains("PACKAGECONFIG", "libdns_sd", "libavahi-compat-libdnssd", "", d)}"

FILES_libavahi-compat-libdnssd = "${libdir}/libdns_sd.so.*"

RPROVIDES_libavahi-compat-libdnssd = "libdns-sd"

inherit update-rc.d systemd useradd

PACKAGES =+ "libavahi-gobject avahi-daemon libavahi-common libavahi-core libavahi-client avahi-dnsconfd libavahi-glib avahi-autoipd avahi-utils avahi-discover avahi-ui"

FILES_avahi-ui = "${libdir}/libavahi-ui*.so.*"
FILES_avahi-discover = "${datadir}/applications/avahi-discover.desktop \
                        ${datadir}/avahi/interfaces/avahi-discover.ui \
                        ${bindir}/avahi-discover-standalone \
                        "

LICENSE_libavahi-gobject = "LGPLv2.1+"
LICENSE_avahi-daemon = "LGPLv2.1+"
LICENSE_libavahi-common = "LGPLv2.1+"
LICENSE_libavahi-core = "LGPLv2.1+"
LICENSE_libavahi-client = "LGPLv2.1+"
LICENSE_avahi-dnsconfd = "LGPLv2.1+"
LICENSE_libavahi-glib = "LGPLv2.1+"
LICENSE_avahi-autoipd = "LGPLv2.1+"
LICENSE_avahi-utils = "LGPLv2.1+"

# As avahi doesn't put any files into PN, clear the files list to avoid problems
# if extra libraries appear.
FILES_${PN} = ""
FILES_avahi-autoipd = "${sbindir}/avahi-autoipd \
                       ${sysconfdir}/avahi/avahi-autoipd.action \
                       ${sysconfdir}/dhcp/*/avahi-autoipd \
                       ${sysconfdir}/udhcpc.d/00avahi-autoipd \
                       ${sysconfdir}/udhcpc.d/99avahi-autoipd"
FILES_libavahi-common = "${libdir}/libavahi-common.so.*"
FILES_libavahi-core = "${libdir}/libavahi-core.so.* ${libdir}/girepository-1.0/AvahiCore*.typelib"
FILES_avahi-daemon = "${sbindir}/avahi-daemon \
                      ${sysconfdir}/avahi/avahi-daemon.conf \
                      ${sysconfdir}/avahi/hosts \
                      ${sysconfdir}/avahi/services \
                      ${sysconfdir}/dbus-1 \
                      ${sysconfdir}/init.d/avahi-daemon \
                      ${datadir}/avahi/introspection/*.introspect \
                      ${datadir}/avahi/avahi-service.dtd \
                      ${datadir}/avahi/service-types \
                      ${datadir}/dbus-1/system-services"
FILES_libavahi-client = "${libdir}/libavahi-client.so.*"
FILES_avahi-dnsconfd = "${sbindir}/avahi-dnsconfd \
                        ${sysconfdir}/avahi/avahi-dnsconfd.action \
                        ${sysconfdir}/init.d/avahi-dnsconfd"
FILES_libavahi-glib = "${libdir}/libavahi-glib.so.*"
FILES_libavahi-gobject = "${libdir}/libavahi-gobject.so.*  ${libdir}/girepository-1.0/Avahi*.typelib"
FILES_avahi-utils = "${bindir}/avahi-* ${bindir}/b* ${datadir}/applications/b*"

RDEPENDS_${PN}-dev = "avahi-daemon (= ${EXTENDPKGV}) libavahi-core (= ${EXTENDPKGV})"
RDEPENDS_${PN}-dev += "${@["", " libavahi-client (= ${EXTENDPKGV})"][bb.utils.contains('PACKAGECONFIG', 'dbus', 1, 0, d)]}"
RDEPENDS_${PN}-dnsconfd = "${PN}-daemon"

RRECOMMENDS_avahi-daemon_append_libc-glibc = " libnss-mdns"

CONFFILES_avahi-daemon = "${sysconfdir}/avahi/avahi-daemon.conf"

USERADD_PACKAGES = "avahi-daemon avahi-autoipd"
USERADD_PARAM_avahi-daemon = "--system --home /run/avahi-daemon \
                              --no-create-home --shell /bin/false \
                              --user-group avahi"

USERADD_PARAM_avahi-autoipd = "--system --home /run/avahi-autoipd \
                              --no-create-home --shell /bin/false \
                              --user-group \
                              -c \"Avahi autoip daemon\" \
                              avahi-autoipd"

INITSCRIPT_PACKAGES = "avahi-daemon avahi-dnsconfd"
INITSCRIPT_NAME_avahi-daemon = "avahi-daemon"
INITSCRIPT_PARAMS_avahi-daemon = "defaults 21 19"
INITSCRIPT_NAME_avahi-dnsconfd = "avahi-dnsconfd"
INITSCRIPT_PARAMS_avahi-dnsconfd = "defaults 22 19"

SYSTEMD_PACKAGES = "${PN}-daemon ${PN}-dnsconfd"
SYSTEMD_SERVICE_${PN}-daemon = "avahi-daemon.service"
SYSTEMD_SERVICE_${PN}-dnsconfd = "avahi-dnsconfd.service"

do_install_append() {
	install -d ${D}${sysconfdir}/udhcpc.d
	install ${WORKDIR}/00avahi-autoipd ${D}${sysconfdir}/udhcpc.d
	install ${WORKDIR}/99avahi-autoipd ${D}${sysconfdir}/udhcpc.d
}

# At the time the postinst runs, dbus might not be setup so only restart if running 
# Don't exit early, because update-rc.d needs to run subsequently.
pkg_postinst_avahi-daemon () {
if [ -z "$D" ]; then
	killall -q -HUP dbus-daemon || true
fi
}

