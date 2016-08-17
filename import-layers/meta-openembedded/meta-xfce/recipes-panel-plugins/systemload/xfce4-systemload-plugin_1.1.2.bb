DESCRIPTION = "Panel plugin displaying current CPU load, the memory in use, the swap space and the system uptime"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-systemload-plugin"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=9acb172a93ff6c43cce2aff790a8aef8"

inherit xfce-panel-plugin

DEPENDS += "upower"

SRC_URI[md5sum] = "68c9d20b352c13f3eb6b39a0d9fe2ba2"
SRC_URI[sha256sum] = "b469b6b3a08ec29b9061151950d876d36bf25a3106ec77256923fdd6b5d18a7c"
