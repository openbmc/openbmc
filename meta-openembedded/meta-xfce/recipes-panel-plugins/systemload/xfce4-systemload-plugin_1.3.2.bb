DESCRIPTION = "Panel plugin displaying current CPU load, the memory in use, the swap space and the system uptime"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-systemload-plugin"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=9acb172a93ff6c43cce2aff790a8aef8"

inherit xfce-panel-plugin

PACKAGECONFIG ?= "network power"
PACKAGECONFIG[power] = ",,upower"
PACKAGECONFIG[network] = ",,libgtop"

SRC_URI += "file://convert-gulong.patch"
SRC_URI[sha256sum] = "bb303fc3020e053ad1fa0b8fcbf0d7681c5563bb8f649357d6a95a577802b072"
