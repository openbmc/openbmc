DESCRIPTION = "Thunar Archive Plugin allows you to create and extract archive files using file context menus in Thunar"
HOMEPAGE = "http://goodies.xfce.org/projects/thunar-plugins/thunar-archive-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=252890d9eee26aab7b432e8b8a616475"

inherit thunar-plugin

SRC_URI[md5sum] = "0a7738a3a5f84bf218ece3ffb5241c63"
SRC_URI[sha256sum] = "bf82fa86a388124eb3c4854249c30712b2922e61789607268ee14548549b3115"

# install tap files in ${libdir}/thunar-archive-plugin
EXTRA_OECONF += "--libexecdir=${libdir}"
