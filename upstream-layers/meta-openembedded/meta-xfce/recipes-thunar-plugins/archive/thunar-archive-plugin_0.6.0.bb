DESCRIPTION = "Thunar Archive Plugin allows you to create and extract archive files using file context menus in Thunar"
HOMEPAGE = "https://docs.xfce.org/xfce/thunar/archive"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cf66a4984120007c9881cc871cf49db"

inherit thunar-plugin

SRC_URI[sha256sum] = "692708cd047c7a552f2f85fe2ee32f19c7d5be5bf695d0288e8cadf50289db06"

# install tap files in ${libdir}/thunar-archive-plugin
EXTRA_OECONF += "--libexecdir=${libdir}"
