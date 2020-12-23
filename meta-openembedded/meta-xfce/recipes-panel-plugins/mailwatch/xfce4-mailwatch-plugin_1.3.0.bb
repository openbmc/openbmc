SUMMARY = "Multi-protocol, multi-mailbox mail watcher for the Xfce4 panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-mailwatch-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-panel-plugin

DEPENDS += "gnutls"

SRC_URI[sha256sum] = "20f91ebefd2880b27f421f773115b3740f67de2bf60feace3841bfd1a09cbe2e"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
