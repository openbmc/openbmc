SUMMARY = "Multi-protocol, multi-mailbox mail watcher for the Xfce4 panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-mailwatch-plugin"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

DEPENDS += "gnutls"

SRC_URI[sha256sum] = "054964e9fe4ca668486400991ce1ea01d07aac7ba235f4b14d4a8f7d9800046a"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
