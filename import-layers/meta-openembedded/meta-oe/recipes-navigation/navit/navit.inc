DESCRIPTION = "Navit is a car navigation system with routing engine."
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ed539d099d6ce08de6ea0dfed9ecb333 \
                    file://LGPL-2;md5=3214f080875748938ba060314b4f727d \
                    file://GPL-2;md5=751419260aa954499f7abaabaa882bbe \
"

SECTION = "x11/applications"
DEPENDS = "glib-2.0 gd gypsy librsvg-native"
RDEPENDS_${PN} = " ${PN}-icons ${PN}-config "
RRECOMMENDS_${PN} = "gpsd ${PN}-dbus ${PN}-speech-cmdline ${PN}-gui-internal ${PN}-graphics-gtk"
RSUGGESTS_${PN} = "flite-alsa espeak ${PN}-speech-dbus ${PN}-gui-gtk ${PN}-gui-qml ${PN}-graphics-sdl ${PN}-graphics-qt ${PN}-maptool"

PE = "1"
INC_PR = "r11"

# Depends on gypsy
PNBLACKLIST[navit] ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5 conflicts with bluez4 and bluez5 is selected in DISTRO_FEATURES', '', d)}"

inherit autotools gettext pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[fribidi] = "--enable-fribidi,--disable-fribidi,fribidi"
PACKAGECONFIG[imlib2] = "--enable-imlib2,--disable-imlib2,imlib2"
PACKAGECONFIG[sdl] = "--enable-graphics-sdl,--disable-graphics-sdl,libsdl"
PACKAGECONFIG[gtk] = "--enable-graphics-gtk-drawing-area --enable-gui-gtk,--disable-graphics-gtk-drawing-area --disable-gui-gtk,gtk+"
PACKAGECONFIG[qt] = "--enable-graphics-qt-qpainter --enable-gui-qml,--disable-graphics-qt-qpainter --disable-gui-qml,qt4-x11-free,qt4-plugin-imageformat-svg"

EXTRA_OECONF += " \
    --disable-binding-python \
    --disable-samplemap \
    --enable-avoid-unaligned \
    --enable-svg2png-scaling-flag=32 \
    --disable-speech-speech-dispatcher \
    --enable-cache-size=20971520 \
"

#  --enable-avoid-float \
#check for TARGET_FPU=soft and inform configure of the result so it can disable some floating points
require navit-fpu.inc
EXTRA_OECONF += "${@get_navit_fpu_setting(bb, d)}"

PACKAGES =+ "${PN}-maptool ${PN}-config ${PN}-dbus ${PN}-speech-cmdline ${PN}-speech-dbus ${PN}-gui-gtk ${PN}-gui-internal ${PN}-gui-qml ${PN}-graphics-sdl ${PN}-graphics-gtk ${PN}-graphics-qt ${PN}-icons"

EXTRA_AUTORECONF = " -I m4"

CONFFILES_${PN}-config += "${datadir}/navit/navit.default.xml \
    ${datadir}/navit/navit.xml \
    ${datadir}/navit/maps.xml \
    ${datadir}/navit/osd.xml \
    ${datadir}/navit/speech.xml \
    ${datadir}/navit/plugins.xml \
"

SRC_URI += "file://navit.xml \
            file://maps.xml \
            file://osd.xml \
            file://speech.xml \
            file://plugins.xml \
"

FILES_${PN} += "${libdir}/${BPN}/*/*.so ${datadir}/icons"
FILES_${PN}-dbg += "${libdir}/${BPN}/*/.debug"
FILES_${PN}-maptool = " ${bindir}/maptool "
FILES_${PN}-config = " ${datadir}/navit/*.xml "
FILES_${PN}-dbus = " ${datadir}/dbus-1/services/ ${libdir}/${BPN}/binding/libbinding_dbus.so "
FILES_${PN}-speech-cmdline = " ${libdir}/${BPN}/speech/libspeech_cmdline.so "
FILES_${PN}-speech-dbus = " ${libdir}/${BPN}/speech/libspeech_dbus.so "
FILES_${PN}-gui-gtk = " ${libdir}/${BPN}/gui/libgui_gtk.so "
FILES_${PN}-gui-qml = " ${libdir}/${BPN}/gui/libgui_qml.so ${datadir}/navit/skins/ "
FILES_${PN}-gui-internal = " ${libdir}/${BPN}/gui/libgui_internal.so "
FILES_${PN}-graphics-sdl = " ${libdir}/${BPN}/graphics/libgraphics_sdl.so "
FILES_${PN}-graphics-qt = " ${libdir}/${BPN}/graphics/libgraphics_qt_qpainter.so "
FILES_${PN}-graphics-gtk = " ${libdir}/${BPN}/graphics/libgraphics_gtk_drawing_area.so "
FILES_${PN}-icons = "/usr/share/navit/xpm/"

do_configure_prepend() {
    # MOC=`$PKG_CONFIG QtGui --variable=moc_location` returns only /usr/bin/moc4 which is not available on hosts without Qt
    sed -i "s#MOC=#MOC=${STAGING_DIR_NATIVE}#g" ${S}/configure.in
}

do_install_append() {
    # fix QA issue
    sed -i 's#Categories=GTK;Utility;Geography;#Categories=Graphics;#g' ${D}${datadir}/applications/navit.desktop

    rm ${D}${libdir}/${BPN}/*/*.la

    #Use split config
    mv ${D}${datadir}/navit/navit.xml ${D}${datadir}/navit/navit.default.xml
    install -m 0644 ${WORKDIR}/navit.xml ${D}${datadir}/navit/navit.xml
    install -m 0644 ${WORKDIR}/maps.xml ${D}${datadir}/navit/maps.xml
    install -m 0644 ${WORKDIR}/osd.xml ${D}${datadir}/navit/osd.xml
    install -m 0644 ${WORKDIR}/speech.xml ${D}${datadir}/navit/speech.xml
    install -m 0644 ${WORKDIR}/plugins.xml ${D}${datadir}/navit/plugins.xml
}
