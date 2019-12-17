DESCRIPTION = "Panel plugin displaying current CPU load, the memory in use, the swap space and the system uptime"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-systemload-plugin"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=9acb172a93ff6c43cce2aff790a8aef8"

inherit xfce-panel-plugin

DEPENDS += "upower"

SRC_URI[md5sum] = "5d75865be699d0b3d36a5fc17ed02d44"
SRC_URI[sha256sum] = "052407c575203da4de2db6f4a5e997220d95ec655d393dc3875a0d5a20520775"
