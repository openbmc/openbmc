SUMMARY = "Panel plugin to display frequency of all cpus"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-cpufreq-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=1f6f1c0be32491a0c8d2915607a28f36"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "500f04b8d857c96da8c8c7a4eecba30a903d0fce6e35a05e674529e43b47e498"

SRC_URI += "file://0001-Fix-memory-leak-and-reduce-cpu-load-slightly.patch \
            file://xfce4-cpufreq-plugin-1.2.1-gcc10-common.patch \
           "
