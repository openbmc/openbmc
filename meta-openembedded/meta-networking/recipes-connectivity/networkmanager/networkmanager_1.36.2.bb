SUMMARY = "NetworkManager"
HOMEPAGE = "https://wiki.gnome.org/Projects/NetworkManager"
SECTION = "net/misc"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
"

DEPENDS = " \
    coreutils-native \
    intltool-native \
    libxslt-native \
    libnl \
    udev \
    util-linux \
    libndp \
    libnewt \
    curl \
    dbus \
"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gettext update-rc.d systemd gobject-introspection gtk-doc update-alternatives upstream-version-is-even

SRC_URI = " \
    ${GNOME_MIRROR}/NetworkManager/${@gnome_verdir("${PV}")}/NetworkManager-${PV}.tar.xz \
    file://${BPN}.initd \
    file://enable-dhcpcd.conf \
    file://enable-iwd.conf \
    file://0001-do-not-ask-host-for-ifcfg-defaults.patch \
    file://0001-libnm-client-test-add-dependency-libnm_client_public.patch \
"
SRC_URI[sha256sum] = "ab855cbe3b41832e9a3b003810e7c7313dfe19e630d29806d14d87fdd1470cab"

S = "${WORKDIR}/NetworkManager-${PV}"

# ['auto', 'symlink', 'file', 'netconfig', 'resolvconf']
NETWORKMANAGER_DNS_RC_MANAGER_DEFAULT ??= "auto"

# ['dhcpcanon', 'dhclient', 'dhcpcd', 'internal', 'nettools']
NETWORKMANAGER_DHCP_DEFAULT ??= "internal"

EXTRA_OEMESON = "\
    -Difcfg_rh=false \
    -Dtests=yes \
    -Dnmtui=true \
    -Dudev_dir=${nonarch_base_libdir}/udev \
    -Dlibpsl=false \
    -Dqt=false \
    -Dconfig_dns_rc_manager_default=${NETWORKMANAGER_DNS_RC_MANAGER_DEFAULT} \
    -Dconfig_dhcp_default=${NETWORKMANAGER_DHCP_DEFAULT} \
    -Ddhcpcanon=false \
"

# stolen from https://github.com/void-linux/void-packages/blob/master/srcpkgs/NetworkManager/template
# avoids:
# | ../NetworkManager-1.16.0/libnm-core/nm-json.c:106:50: error: 'RTLD_DEEPBIND' undeclared (first use in this function); did you mean 'RTLD_DEFAULT'?
CFLAGS:append:libc-musl = " \
    -DRTLD_DEEPBIND=0 \
"

do_compile:prepend() {
    export GI_TYPELIB_PATH="${B}}/src/libnm-client-impl${GI_TYPELIB_PATH:+:$GI_TYPELIB_PATH}"
}

PACKAGECONFIG ??= "readline nss ifupdown dnsmasq nmcli vala \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', bb.utils.contains('DISTRO_FEATURES', 'x11', 'consolekit', '', d), d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez5', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'wifi polkit', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux audit', '', d)} \
"

inherit ${@bb.utils.contains('PACKAGECONFIG', 'nmcli', 'bash-completion', '', d)}
inherit ${@bb.utils.contains('PACKAGECONFIG', 'vala', 'vala', '', d)}

PACKAGECONFIG[systemd] = "\
    -Dsystemdsystemunitdir=${systemd_unitdir}/system -Dsession_tracking=systemd,\
    -Dsystemdsystemunitdir=no -Dsystemd_journal=false -Dsession_tracking=no\
"
PACKAGECONFIG[polkit] = "-Dpolkit=true,-Dpolkit=false,polkit"
PACKAGECONFIG[bluez5] = "-Dbluez5_dun=true,-Dbluez5_dun=false,bluez5"
# consolekit is not picked by shlibs, so add it to RDEPENDS too
PACKAGECONFIG[consolekit] = "-Dsession_tracking_consolekit=true,-Dsession_tracking_consolekit=false,consolekit,consolekit"
PACKAGECONFIG[modemmanager] = "-Dmodem_manager=true,-Dmodem_manager=false,modemmanager mobile-broadband-provider-info"
PACKAGECONFIG[ppp] = "-Dppp=true -Dpppd=/usr/sbin/pppd,-Dppp=false,ppp,ppp"
PACKAGECONFIG[dnsmasq] = "-Ddnsmasq=${bindir}/dnsmasq"
PACKAGECONFIG[nss] = "-Dcrypto=nss,,nss"
PACKAGECONFIG[resolvconf] = "-Dresolvconf=${base_sbindir}/resolvconf,-Dresolvconf=no,,resolvconf"
PACKAGECONFIG[gnutls] = "-Dcrypto=gnutls,,gnutls"
PACKAGECONFIG[wifi] = "-Dwext=true -Dwifi=true,-Dwext=false -Dwifi=false"
PACKAGECONFIG[iwd] = "-Diwd=true,-Diwd=false"
PACKAGECONFIG[ifupdown] = "-Difupdown=true,-Difupdown=false"
PACKAGECONFIG[cloud-setup] = "-Dnm_cloud_setup=true,-Dnm_cloud_setup=false"
PACKAGECONFIG[nmcli] = "-Dnmcli=true,-Dnmcli=false"
PACKAGECONFIG[readline] = "-Dreadline=libreadline,,readline"
PACKAGECONFIG[libedit] = "-Dreadline=libedit,,libedit"
PACKAGECONFIG[ovs] = "-Dovs=true,-Dovs=false,jansson"
PACKAGECONFIG[audit] = "-Dlibaudit=yes,-Dlibaudit=no"
PACKAGECONFIG[selinux] = "-Dselinux=true,-Dselinux=false,libselinux"
PACKAGECONFIG[vala] = "-Dvapi=true,-Dvapi=false"
PACKAGECONFIG[dhcpcd] = "-Ddhcpcd=yes,-Ddhcpcd=no,,dhcpcd"
PACKAGECONFIG[dhclient] = "-Ddhclient=yes,-Ddhclient=no,,dhcp"
PACKAGECONFIG[concheck] = "-Dconcheck=true,-Dconcheck=false"


PACKAGES =+ " \
    ${PN}-adsl \
    ${PN}-bluetooth \
    ${PN}-cloud-setup \
    ${PN}-nmcli ${PN}-nmcli-doc \
    ${PN}-nmtui ${PN}-nmtui-doc \
    ${PN}-wifi \
    ${PN}-wwan \
    ${PN}-ovs ${PN}-ovs-doc \
    ${PN}-ppp \
"

SYSTEMD_PACKAGES = "${PN} ${PN}-cloud-setup"

NETWORKMANAGER_PLUGINDIR = "${libdir}/NetworkManager/${PV}"

FILES:${PN}-adsl = "${NETWORKMANAGER_PLUGINDIR}/libnm-device-plugin-adsl.so"

FILES:${PN}-bluetooth = "${NETWORKMANAGER_PLUGINDIR}/libnm-device-plugin-bluetooth.so"

FILES:${PN}-cloud-setup = " \
    ${libexecdir}/nm-cloud-setup \
    ${systemd_system_unitdir}/nm-cloud-setup.service \
    ${systemd_system_unitdir}/nm-cloud-setup.timer \
    ${libdir}/NetworkManager/dispatcher.d/90-nm-cloud-setup.sh \
    ${libdir}/NetworkManager/dispatcher.d/no-wait.d/90-nm-cloud-setup.sh \
"
ALLOW_EMPTY:${PN}-cloud-setup = "1"
SYSTEMD_SERVICE:${PN}-cloud-setup = "${@bb.utils.contains('PACKAGECONFIG', 'cloud-setup', 'nm-cloud-setup.service nm-cloud-setup.timer', '', d)}"

FILES:${PN}-nmcli = " \
    ${bindir}/nmcli \
"

FILES:${PN}-nmcli-doc = " \
    ${mandir}/man1/nmcli* \
"

FILES:${PN}-nmtui = " \
    ${bindir}/nmtui \
    ${bindir}/nmtui-edit \
    ${bindir}/nmtui-connect \
    ${bindir}/nmtui-hostname \
"

FILES:${PN}-nmtui-doc = " \
    ${mandir}/man1/nmtui* \
"

FILES:${PN}-wifi = "${NETWORKMANAGER_PLUGINDIR}/libnm-device-plugin-wifi.so"

FILES:${PN}-wwan = "\
    ${NETWORKMANAGER_PLUGINDIR}/libnm-device-plugin-wwan.so \
    ${NETWORKMANAGER_PLUGINDIR}/libnm-wwan.so \
"

FILES:${PN}-ovs = "\
    ${NETWORKMANAGER_PLUGINDIR}/libnm-device-plugin-ovs.so \
    ${systemd_system_unitdir}/NetworkManager.service.d/NetworkManager-ovs.conf \
"

FILES:${PN}-ovs-doc = "\
    ${mandir}/man7/nm-openvswitch.7* \
"

FILES:${PN}-ppp = "\
    ${NETWORKMANAGER_PLUGINDIR}/libnm-ppp-plugin.so \
    ${libdir}/pppd/*/nm-pppd-plugin.so \
"

FILES:${PN}-dev += " \
    ${libdir}/pppd/*/*.la \
    ${libdir}/NetworkManager/*.la \
    ${NETWORKMANAGER_PLUGINDIR}/*.la \
"

FILES:${PN} += " \
    ${libexecdir} \
    ${libdir}/NetworkManager \
    ${nonarch_libdir}/firewalld/zones \
    ${nonarch_libdir}/NetworkManager/conf.d \
    ${nonarch_libdir}/NetworkManager/dispatcher.d \
    ${nonarch_libdir}/NetworkManager/dispatcher.d/pre-down.d \
    ${nonarch_libdir}/NetworkManager/dispatcher.d/pre-up.d \
    ${nonarch_libdir}/NetworkManager/dispatcher.d/no-wait.d \
    ${nonarch_libdir}/NetworkManager/VPN \
    ${nonarch_libdir}/NetworkManager/system-connections \
    ${datadir}/polkit-1 \
    ${datadir}/dbus-1 \
    ${nonarch_base_libdir}/udev/* \
    ${systemd_system_unitdir} \
"

RRECOMMENDS:${PN} += "\
    iptables \
    ${@bb.utils.filter('PACKAGECONFIG', 'dnsmasq', d)} \
    ${@bb.utils.contains('PACKAGECONFIG','adsl','${PN}-adsl','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','bluez5','${PN}-bluetooth','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','cloud-setup','${PN}-cloud-setup','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','nmcli','${PN}-nmcli','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','nmtui','${PN}-nmtui','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','wifi','${PN}-wifi','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','wwan','${PN}-wwan','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','ovs','${PN}-ovs','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','ppp','${PN}-ppp','',d)} \
"
RCONFLICTS:${PN} = "connman"


INITSCRIPT_NAME = "network-manager"
SYSTEMD_SERVICE:${PN} = "\
    NetworkManager.service \
    NetworkManager-dispatcher.service \
"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','resolv-conf','',d)}"
ALTERNATIVE_TARGET[resolv-conf] = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${sysconfdir}/resolv-conf.NetworkManager','',d)}"
ALTERNATIVE_LINK_NAME[resolv-conf] = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${sysconfdir}/resolv.conf','',d)}"

do_install:append() {
    install -Dm 0755 ${WORKDIR}/${BPN}.initd ${D}${sysconfdir}/init.d/network-manager

    rm -rf ${D}/run ${D}${localstatedir}/run

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        # For read-only filesystem, do not create links during bootup
        ln -sf ../run/NetworkManager/resolv.conf ${D}${sysconfdir}/resolv-conf.NetworkManager

        # systemd v210 and newer do not need this rule file
        rm ${D}/${nonarch_base_libdir}/udev/rules.d/84-nm-drivers.rules
    fi

    # Enable iwd if compiled
    if ${@bb.utils.contains('PACKAGECONFIG','iwd','true','false',d)}; then
        install -Dm 0644 ${WORKDIR}/enable-iwd.conf ${D}${libdir}/NetworkManager/conf.d/enable-iwd.conf
    fi

    # Enable dhcpd if compiled
    if ${@bb.utils.contains('PACKAGECONFIG','dhcpcd','true','false',d)}; then
        install -Dm 0644 ${WORKDIR}/enable-dhcpcd.conf ${D}${libdir}/NetworkManager/conf.d/enable-dhcpcd.conf
    fi
}
