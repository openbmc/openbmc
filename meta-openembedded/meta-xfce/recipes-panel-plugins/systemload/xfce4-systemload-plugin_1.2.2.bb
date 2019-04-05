DESCRIPTION = "Panel plugin displaying current CPU load, the memory in use, the swap space and the system uptime"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-systemload-plugin"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=9acb172a93ff6c43cce2aff790a8aef8"

inherit xfce-panel-plugin

DEPENDS += "upower"

SRC_URI[md5sum] = "c7b9237ec4a421de5dac76449b4b5a78"
SRC_URI[sha256sum] = "3c67dfeb042eaea5aca440de9c6b3ecf99be0fbaa1cf7fdf9e6528233b46c99e"
