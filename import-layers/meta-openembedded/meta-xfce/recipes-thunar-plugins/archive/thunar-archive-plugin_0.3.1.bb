DESCRIPTION = "Thunar Archive Plugin allows you to create and extract archive files using file context menus in Thunar"
HOMEPAGE = "http://goodies.xfce.org/projects/thunar-plugins/thunar-archive-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=252890d9eee26aab7b432e8b8a616475"

inherit thunar-plugin

SRC_URI[md5sum] = "425f4faaace6dc7a4716a35b7795463a"
SRC_URI[sha256sum] = "9ad559b0c11308f6897ad56604e5a06dc8f369f649eb20120b2d3018ef5da54c"

# install tap files in ${libdir}/thunar-archive-plugin
EXTRA_OECONF += "--libexecdir=${libdir}"
