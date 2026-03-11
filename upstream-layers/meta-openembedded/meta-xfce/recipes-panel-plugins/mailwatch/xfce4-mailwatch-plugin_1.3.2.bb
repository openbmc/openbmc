SUMMARY = "Multi-protocol, multi-mailbox mail watcher for the Xfce4 panel"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-mailwatch-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "gnutls"

SRC_URI[sha256sum] = "c4783f1533891cd2e0c34066da859864dce45a23caa6015b58cb9fa9d65a7e44"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
