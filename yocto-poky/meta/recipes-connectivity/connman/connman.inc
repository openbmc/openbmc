SUMMARY = "A daemon for managing internet connections within embedded devices"
DESCRIPTION = "The ConnMan project provides a daemon for managing \
internet connections within embedded devices running the Linux \
operating system.  The Connection Manager is designed to be slim and \
to use as few resources as possible, so it can be easily integrated. \
It is a fully modular system that can be extended, through plug-ins, \
to support all kinds of wired or wireless technologies. Also, \
configuration methods, like DHCP and domain name resolving, are \
implemented using plug-ins."
HOMEPAGE = "http://connman.net/"
BUGTRACKER = "https://01.org/jira/browse/CM"
LICENSE  = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
                    file://src/main.c;beginline=1;endline=20;md5=486a279a6ab0c8d152bcda3a5b5edc36"

inherit autotools pkgconfig systemd update-rc.d bluetooth

DEPENDS  = "dbus glib-2.0 ppp iptables readline"

INC_PR = "r20"

EXTRA_OECONF += "\
    ac_cv_path_WPASUPPLICANT=${sbindir}/wpa_supplicant \
    ac_cv_path_PPPD=${sbindir}/pppd \
    --enable-debug \
    --enable-loopback \
    --enable-ethernet \
    --enable-tools \
    --enable-test \
    --disable-polkit \
    --enable-client \
"
CFLAGS += "-D_GNU_SOURCE"

PACKAGECONFIG ??= "wispr \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'systemd','systemd', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'wifi','wifi', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', '3g','3g', '', d)} \
"

# If you want ConnMan to support VPN, add following statement into
# local.conf or distro config
# PACKAGECONFIG_append_pn-connman = " openvpn vpnc l2tp pptp"

PACKAGECONFIG[systemd] = "--with-systemdunitdir=${systemd_unitdir}/system/ --with-tmpfilesdir=${sysconfdir}/tmpfiles.d/,--with-systemdunitdir='' --with-tmpfilesdir=''"
PACKAGECONFIG[wifi] = "--enable-wifi, --disable-wifi, wpa-supplicant, wpa-supplicant"
PACKAGECONFIG[bluez] = "--enable-bluetooth, --disable-bluetooth, ${BLUEZ}, ${BLUEZ}"
PACKAGECONFIG[3g] = "--enable-ofono, --disable-ofono, ofono, ofono"
PACKAGECONFIG[tist] = "--enable-tist,--disable-tist,"
PACKAGECONFIG[openvpn] = "--enable-openvpn --with-openvpn=${sbindir}/openvpn,--disable-openvpn,,openvpn"
PACKAGECONFIG[vpnc] = "--enable-vpnc --with-vpnc=${sbindir}/vpnc,--disable-vpnc,,vpnc"
PACKAGECONFIG[l2tp] = "--enable-l2tp --with-l2tp=${sbindir}/xl2tpd,--disable-l2tp,,xl2tpd"
PACKAGECONFIG[pptp] = "--enable-pptp --with-pptp=${sbindir}/pptp,--disable-pptp,,pptp-linux"
# WISPr support for logging into hotspots, requires TLS
PACKAGECONFIG[wispr] = "--enable-wispr,--disable-wispr,gnutls,"

INITSCRIPT_NAME = "connman"
INITSCRIPT_PARAMS = "start 05 5 2 3 . stop 22 0 1 6 ."

python __anonymous () {
    systemd_packages = "${PN}"
    pkgconfig = d.getVar('PACKAGECONFIG', True)
    if ('openvpn' or 'vpnc' or 'l2tp' or 'pptp') in pkgconfig.split():
        systemd_packages += " ${PN}-vpn"
    d.setVar('SYSTEMD_PACKAGES', systemd_packages)
}

SYSTEMD_SERVICE_${PN} = "connman.service"
SYSTEMD_SERVICE_${PN}-vpn = "connman-vpn.service"
SYSTEMD_SERVICE_${PN}-wait-online = "connman-wait-online.service"

do_install_append() {
	if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
		install -d ${D}${sysconfdir}/init.d
		install -m 0755 ${WORKDIR}/connman ${D}${sysconfdir}/init.d/connman
		sed -i s%@DATADIR@%${datadir}% ${D}${sysconfdir}/init.d/connman
	fi

	install -d ${D}${bindir}
	install -m 0755 ${B}/tools/*-test ${D}${bindir}
	if [ -e ${B}/tools/wispr ]; then
		install -m 0755 ${B}/tools/wispr ${D}${bindir}
	fi
	install -m 0755 ${B}/client/connmanctl ${D}${bindir}

	# We don't need to package an empty directory
	rmdir --ignore-fail-on-non-empty ${D}${libdir}/connman/scripts

	# Automake 1.12 won't install empty directories, but we need the
	# plugins directory to be present for ownership
	mkdir -p ${D}${libdir}/connman/plugins
}

# These used to be plugins, but now they are core
RPROVIDES_${PN} = "\
	connman-plugin-loopback \
	connman-plugin-ethernet \
	${@bb.utils.contains('PACKAGECONFIG', 'bluetooth','connman-plugin-bluetooth', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'wifi','connman-plugin-wifi', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', '3g','connman-plugin-ofono', '', d)} \
	"

RDEPENDS_${PN} = "\
	dbus \
	"

PACKAGES_DYNAMIC += "^${PN}-plugin-.*"

def add_rdepends(bb, d, file, pkg, depmap, multilib_prefix, add_insane_skip):
    plugintype = pkg.split( '-' )[-1]
    if plugintype in depmap:
        rdepends = map(lambda x: multilib_prefix + x, \
                       depmap[plugintype].split())
        d.setVar("RDEPENDS_%s" % pkg, " ".join(rdepends))
    if add_insane_skip:
        d.appendVar("INSANE_SKIP_%s" % pkg, "dev-so")

python populate_packages_prepend() {
    depmap = dict(pppd="ppp")
    multilib_prefix = (d.getVar("MLPREFIX", True) or "")

    hook = lambda file,pkg,x,y,z: \
        add_rdepends(bb, d, file, pkg, depmap, multilib_prefix, False)
    plugin_dir = d.expand('${libdir}/connman/plugins/')
    plugin_name = d.expand('${PN}-plugin-%s')
    do_split_packages(d, plugin_dir, '^(.*).so$', plugin_name, \
        '${PN} plugin for %s', extra_depends='', hook=hook, prepend=True )

    hook = lambda file,pkg,x,y,z: \
        add_rdepends(bb, d, file, pkg, depmap, multilib_prefix, True)
    plugin_dir = d.expand('${libdir}/connman/plugins-vpn/')
    plugin_name = d.expand('${PN}-plugin-vpn-%s')
    do_split_packages(d, plugin_dir, '^(.*).so$', plugin_name, \
        '${PN} VPN plugin for %s', extra_depends='', hook=hook, prepend=True )
}

PACKAGES =+ "${PN}-tools ${PN}-tests ${PN}-client"

FILES_${PN}-tools = "${bindir}/wispr"
RDEPENDS_${PN}-tools ="${PN}"

FILES_${PN}-tests = "${bindir}/*-test ${libdir}/${BPN}/test/*"
RDEPENDS_${PN}-tests = "python-dbus python-pygobject python-textutils python-subprocess python-fcntl python-netclient \
                        ${PN} \
"

FILES_${PN}-client = "${bindir}/connmanctl"
RDEPENDS_${PN}-client ="${PN}"

FILES_${PN} = "${bindir}/* ${sbindir}/* ${libexecdir}/* ${libdir}/lib*.so.* \
            ${libdir}/connman/plugins \
            ${sysconfdir} ${sharedstatedir} ${localstatedir} \
            ${base_bindir}/* ${base_sbindir}/* ${base_libdir}/*.so* ${datadir}/${PN} \
            ${datadir}/dbus-1/system-services/* \
            ${sysconfdir}/tmpfiles.d/connman_resolvconf.conf"

FILES_${PN}-dev += "${libdir}/connman/*/*.la"

PACKAGES =+ "${PN}-vpn ${PN}-wait-online"

SUMMARY_${PN}-vpn = "A daemon for managing VPN connections within embedded devices"
DESCRIPTION_${PN}-vpn = "The ConnMan VPN provides a daemon for \
managing VPN connections within embedded devices running the Linux \
operating system.  The connman-vpnd handles all the VPN connections \
and starts/stops VPN client processes when necessary. The connman-vpnd \
provides a DBus API for managing VPN connections. All the different \
VPN technogies are implemented using plug-ins."
FILES_${PN}-vpn += "${sbindir}/connman-vpnd \
                    ${sysconfdir}/dbus-1/system.d/connman-vpn-dbus.conf \
                    ${datadir}/dbus-1/system-services/net.connman.vpn.service \
                    ${systemd_unitdir}/system/connman-vpn.service"

SUMMARY_${PN}-wait-online = "A program that will return once ConnMan has connected to a network"
DESCRIPTION_${PN}-wait-online = "A service that can be enabled so that \
the system waits until a network connection is established."
FILES_${PN}-wait-online += "${sbindir}/connmand-wait-online \
                            ${systemd_unitdir}/system/connman-wait-online.service"

SUMMARY_${PN}-plugin-vpn-openvpn = "An OpenVPN plugin for ConnMan VPN"
DESCRIPTION_${PN}-plugin-vpn-openvpn = "The ConnMan OpenVPN plugin uses openvpn client \
to create a VPN connection to OpenVPN server."
FILES_${PN}-plugin-vpn-openvpn += "${libdir}/connman/scripts/openvpn-script \
                                   ${libdir}/connman/plugins-vpn/openvpn.so"
RDEPENDS_${PN}-plugin-vpn-openvpn += "${PN}-vpn"
RRECOMMENDS_${PN} += "${@bb.utils.contains('PACKAGECONFIG','openvpn','${PN}-plugin-vpn-openvpn', '', d)}"

SUMMARY_${PN}-plugin-vpn-vpnc = "A vpnc plugin for ConnMan VPN"
DESCRIPTION_${PN}-plugin-vpn-vpnc = "The ConnMan vpnc plugin uses vpnc client \
to create a VPN connection to Cisco3000 VPN Concentrator."
FILES_${PN}-plugin-vpn-vpnc += "${libdir}/connman/scripts/openconnect-script \
                                ${libdir}/connman/plugins-vpn/vpnc.so"
RDEPENDS_${PN}-plugin-vpn-vpnc += "${PN}-vpn"
RRECOMMENDS_${PN} += "${@bb.utils.contains('PACKAGECONFIG','vpnc','${PN}-plugin-vpn-vpnc', '', d)}"

SUMMARY_${PN}-plugin-vpn-l2tp = "A L2TP plugin for ConnMan VPN"
DESCRIPTION_${PN}-plugin-vpn-l2tp = "The ConnMan L2TP plugin uses xl2tpd daemon \
to create a VPN connection to L2TP server."
FILES_${PN}-plugin-vpn-l2tp += "${libdir}/connman/scripts/libppp-plugin.so* \
                                ${libdir}/connman/plugins-vpn/l2tp.so"
RDEPENDS_${PN}-plugin-vpn-l2tp += "${PN}-vpn"
RRECOMMENDS_${PN} += "${@bb.utils.contains('PACKAGECONFIG','l2tp','${PN}-plugin-vpn-l2tp', '', d)}"

SUMMARY_${PN}-plugin-vpn-pptp = "A PPTP plugin for ConnMan VPN"
DESCRIPTION_${PN}-plugin-vpn-pptp = "The ConnMan PPTP plugin uses pptp-linux client \
to create a VPN connection to PPTP server."
FILES_${PN}-plugin-vpn-pptp += "${libdir}/connman/scripts/libppp-plugin.so* \
                                ${libdir}/connman/plugins-vpn/pptp.so"
RDEPENDS_${PN}-plugin-vpn-pptp += "${PN}-vpn"
RRECOMMENDS_${PN} += "${@bb.utils.contains('PACKAGECONFIG','pptp','${PN}-plugin-vpn-pptp', '', d)}"
