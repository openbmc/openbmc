SUMMARY = "Panel plugin to display frequency of all cpus"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-cpufreq-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=1f6f1c0be32491a0c8d2915607a28f36"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "ccd8f0f7aef51bc4caf1049986d9614f"
SRC_URI[sha256sum] = "c5e044c0dc401d2066f208a3df82a588b3e51ff01425f155d0a1d0f8fce8f5b5"
SRC_URI += "file://0001-Fix-memory-leak-and-reduce-cpu-load-slightly.patch"
