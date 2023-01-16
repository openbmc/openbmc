DESCRIPTION = "Thunar Archive Plugin allows you to create and extract archive files using file context menus in Thunar"
HOMEPAGE = "http://goodies.xfce.org/projects/thunar-plugins/thunar-archive-plugin"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cf66a4984120007c9881cc871cf49db"

inherit thunar-plugin

SRC_URI[sha256sum] = "8eca88a358282a5acdea72984db0d930efdf658b4bc5b82ef7bcd06224366ffa"

# install tap files in ${libdir}/thunar-archive-plugin
EXTRA_OECONF += "--libexecdir=${libdir}"
