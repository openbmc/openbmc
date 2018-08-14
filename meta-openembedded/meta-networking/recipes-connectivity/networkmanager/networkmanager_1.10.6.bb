SUMMARY = "NetworkManager"
HOMEPAGE = "https://wiki.gnome.org/Projects/NetworkManager"
SECTION = "net/misc"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=cbbffd568227ada506640fe950a4823b \
                    file://libnm-util/COPYING;md5=1c4fa765d6eb3cd2fbd84344a1b816cd \
                    file://docs/api/html/license.html;md5=77b9e362690c149da196aefe7712db30 \
"

DEPENDS = " \
    intltool-native \
    libxslt-native \
    libnl \
    dbus \
    dbus-glib \
    dbus-glib-native \
    libgudev \
    util-linux \
    libndp \
    libnewt \
    polkit \
    jansson \
    curl \
"

inherit gnomebase gettext systemd bluetooth bash-completion vala gobject-introspection gtk-doc

SRC_URI = " \
    ${GNOME_MIRROR}/NetworkManager/${@gnome_verdir("${PV}")}/NetworkManager-${PV}.tar.xz \
    file://0001-sd-lldp.h-Remove-net-ethernet.h-seems-to-be-over-spe.patch \
    file://0002-Fixed-configure.ac-Fix-pkgconfig-sysroot-locations.patch \
    file://0003-Do-not-create-settings-settings-property-documentati.patch \
    file://musl/0001-musl-basic.patch \
    file://musl/0002-musl-dlopen-configure-ac.patch \
    file://musl/0003-musl-network-support.patch \
    file://musl/0004-musl-process-util.patch \
    file://musl/0005-musl-avoid-further-conflicts-by-including-net-ethern.patch \
    file://musl/0006-Add-a-strndupa-replacement-for-musl.patch \
"
SRC_URI[md5sum] = "de3c7147a693da6f80eb22f126086a14"
SRC_URI[sha256sum] = "6af0b1e856a3725f88791f55c4fbb04105dc0b20dbf182aaec8aad16481fac76"

S = "${WORKDIR}/NetworkManager-${PV}"

EXTRA_OECONF = " \
    --disable-ifcfg-rh \
    --disable-ifnet \
    --disable-ifcfg-suse \
    --disable-more-warnings \
    --with-iptables=${sbindir}/iptables \
    --with-tests \
    --with-nmtui=yes \
"

# gobject-introspection related
GI_DATA_ENABLED_libc-musl = "False"

# stolen from https://github.com/voidlinux/void-packages/blob/master/srcpkgs/NetworkManager/template
CFLAGS_libc-musl_append = " \
    -DHAVE_SECURE_GETENV -Dsecure_getenv=getenv \
    -D__USE_POSIX199309 -DRTLD_DEEPBIND=0 \
"

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/libnm/.libs:${B}/libnm-glib/.libs:${B}/libnm-util/.libs"
}

PACKAGECONFIG ??= "nss ifupdown netconfig dhclient dnsmasq \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', bb.utils.contains('DISTRO_FEATURES', 'x11', 'consolekit', '', d), d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', '${BLUEZ}', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'wifi', d)} \
"
PACKAGECONFIG[systemd] = " \
    --with-systemdsystemunitdir=${systemd_unitdir}/system --with-session-tracking=systemd --enable-polkit, \
    --without-systemdsystemunitdir, \
    polkit \
"
PACKAGECONFIG[bluez5] = "--enable-bluez5-dun,--disable-bluez5-dun,bluez5"
# consolekit is not picked by shlibs, so add it to RDEPENDS too
PACKAGECONFIG[consolekit] = "--with-session-tracking=consolekit,,consolekit,consolekit"
PACKAGECONFIG[modemmanager] = "--with-modem-manager-1=yes,--with-modem-manager-1=no,modemmanager"
PACKAGECONFIG[ppp] = "--enable-ppp,--disable-ppp,ppp,ppp"
# Use full featured dhcp client instead of internal one
PACKAGECONFIG[dhclient] = "--with-dhclient=${base_sbindir}/dhclient,,,dhcp-client"
PACKAGECONFIG[dnsmasq] = "--with-dnsmasq=${bindir}/dnsmasq"
PACKAGECONFIG[nss] = "--with-crypto=nss,,nss"
PACKAGECONFIG[gnutls] = "--with-crypto=gnutls,,gnutls"
PACKAGECONFIG[wifi] = "--enable-wifi=yes,--enable-wifi=no,wireless-tools,wpa-supplicant wireless-tools"
PACKAGECONFIG[ifupdown] = "--enable-ifupdown,--disable-ifupdown"
PACKAGECONFIG[netconfig] = "--with-netconfig=yes,--with-netconfig=no"
PACKAGECONFIG[qt4-x11-free] = "--enable-qt,--disable-qt,qt4-x11-free"

PACKAGES =+ "libnmutil libnmglib libnmglib-vpn \
  ${PN}-nmtui ${PN}-nmtui-doc \
  ${PN}-adsl \
"

FILES_libnmutil += "${libdir}/libnm-util.so.*"
FILES_libnmglib += "${libdir}/libnm-glib.so.*"
FILES_libnmglib-vpn += "${libdir}/libnm-glib-vpn.so.*"

FILES_${PN}-adsl = "${libdir}/NetworkManager/libnm-device-plugin-adsl.so"

FILES_${PN} += " \
    ${libexecdir} \
    ${libdir}/pppd/*/nm-pppd-plugin.so \
    ${libdir}/NetworkManager/*.so \
    ${nonarch_libdir}/NetworkManager/VPN \
    ${nonarch_libdir}/NetworkManager/conf.d \
    ${datadir}/polkit-1 \
    ${datadir}/dbus-1 \
    ${base_libdir}/udev/* \
    ${systemd_unitdir}/system \
"

RRECOMMENDS_${PN} += "iptables \
    ${@bb.utils.filter('PACKAGECONFIG', 'dnsmasq', d)} \
"
RCONFLICTS_${PN} = "connman"

FILES_${PN}-dev += " \
    ${datadir}/NetworkManager/gdb-cmd \
    ${libdir}/pppd/*/*.la \
    ${libdir}/NetworkManager/*.la \
"

FILES_${PN}-nmtui = " \
    ${bindir}/nmtui \
    ${bindir}/nmtui-edit \
    ${bindir}/nmtui-connect \
    ${bindir}/nmtui-hostname \
"

FILES_${PN}-nmtui-doc = " \
    ${mandir}/man1/nmtui* \
"

SYSTEMD_SERVICE_${PN} = "NetworkManager.service NetworkManager-dispatcher.service"

do_install_append() {
    rm -rf ${D}/run ${D}${localstatedir}/run
}
