SUMMARY = "XFCE panel plugin displaying status of keyboard LEDs"
DESCRIPTION = "This plugin shows the state of your keyboard LEDs: Caps, Scroll and Num Lock in Xfce panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-kbdleds-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
    file://COPYING.LIB;md5=252890d9eee26aab7b432e8b8a616475 \
"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "db6ad8e3502f3373f087ba2034141552"
SRC_URI[sha256sum] = "6d280ad7207bcb9cc87c279dc3ab9084fd93325e87f67858e8917729b50201cd"

FILES_${PN} += "${libdir}/xfce4/panel-plugins/xfce4-kbdleds-plugin"
