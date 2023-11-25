DESCRIPTION = "Thunar Archive Plugin allows you to create and extract archive files using file context menus in Thunar"
HOMEPAGE = "http://goodies.xfce.org/projects/thunar-plugins/thunar-archive-plugin"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cf66a4984120007c9881cc871cf49db"

inherit thunar-plugin

SRC_URI[sha256sum] = "6379f877bcfc0ea85db9f43723b6fb317893050c712bd03c2ae3232fb9d5ade3"

# install tap files in ${libdir}/thunar-archive-plugin
EXTRA_OECONF += "--libexecdir=${libdir}"
