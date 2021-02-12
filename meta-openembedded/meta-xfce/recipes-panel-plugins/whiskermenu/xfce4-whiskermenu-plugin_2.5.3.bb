SUMMARY = "An alternate menu for the Xfce desktop environment"
HOMEPAGE = "http://gottcode.org/xfce4-whiskermenu-plugin/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin cmake

SRC_URI[sha256sum] = "39cc34c8a83381997c6faaacb6bf792339234303438a1fccd15c9a1770b87daf"

RRECOMMENDS_${PN} += "menulibre"
