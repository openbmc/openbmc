SUMMARY = "XFCE panel plugin to generate alarm messages"
DESCRIPTION = "This is a simple plugin that lets the user run an alarm at a specified time or at the end of a specified countdown period"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-timer-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1c52159bdaebd029cb11927cbe709e4"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "1d3ac3aa2c4345400c025642778e7643aab41047622baf9cdc00bbac78e89f99"
