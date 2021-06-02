DESCRIPTION = "Panel plugin displaying current CPU load, the memory in use, the swap space and the system uptime"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-systemload-plugin"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=9acb172a93ff6c43cce2aff790a8aef8"

inherit xfce-panel-plugin

PACKAGECONFIG ?= "network power"
PACKAGECONFIG[power] = ",,upower"
PACKAGECONFIG[network] = ",,libgtop"

SRC_URI[sha256sum] = "56d1007801d52d7c2b5a13bb54745f6d7f06fda28b49ce936145633068817652"
