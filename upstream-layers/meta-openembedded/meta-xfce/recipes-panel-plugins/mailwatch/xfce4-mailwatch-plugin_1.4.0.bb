SUMMARY = "Multi-protocol, multi-mailbox mail watcher for the Xfce4 panel"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-mailwatch-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

DEPENDS += "gnutls"

SRC_URI[sha256sum] = "5c211025db1096663fa6b8cc41213464a6d71f24e76326499d857ff81ea3861f"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
