require avahi.inc

SRC_URI += "file://00avahi-autoipd \
           file://99avahi-autoipd \
           file://initscript.patch \
           file://0001-Fix-opening-etc-resolv.conf-error.patch \
           "

inherit update-rc.d systemd useradd

PACKAGES =+ "libavahi-gobject avahi-daemon libavahi-common libavahi-core libavahi-client avahi-dnsconfd libavahi-glib avahi-autoipd avahi-utils"

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
FILES_avahi-utils = "${bindir}/avahi-*"

RDEPENDS_${PN}-dev = "avahi-daemon (= ${EXTENDPKGV}) libavahi-core (= ${EXTENDPKGV})"
RDEPENDS_${PN}-dev += "${@["", " libavahi-client (= ${EXTENDPKGV})"][bb.utils.contains('PACKAGECONFIG', 'dbus', 1, 0, d)]}"

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
