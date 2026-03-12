DESCRIPTION = "The FSGuard panel plugin checks free space on a chosen mount point frequently and displays a message when a limit is reached"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-fsguard-plugin/start"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=3434d79d62df09abf5f78bb76d6cd21b"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "9e40cf3ce7b34e1c27d6b442f3a067886c35154b5d0c4d644a239038611da64f"
