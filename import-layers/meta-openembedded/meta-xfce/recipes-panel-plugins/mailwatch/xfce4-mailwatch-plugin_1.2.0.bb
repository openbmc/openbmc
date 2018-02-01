SUMMARY = "Multi-protocol, multi-mailbox mail watcher for the Xfce4 panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-mailwatch-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-panel-plugin

DEPENDS += "gnutls libgcrypt"

SRC_URI[md5sum] = "7263114ec0f2987a3aff15afeeb45577"
SRC_URI[sha256sum] = "624acc8229a8593c0dfeb28f883f4958119a715cc81cecdbaf29efc8ab1edcad"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
