SUMMARY = "Window and compositing manager based on Clutter"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    xserver-xorg-cvt-native \
    wayland-native \
    virtual/libx11 \
    colord \
    graphene \
    gtk4 \
    gdk-pixbuf \
    cairo \
    pango \
    gsettings-desktop-schemas \
    json-glib \
    libei \
    libxtst \
    libxkbfile \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xinerama', '', d)} \
    xwayland \
"


inherit gnomebase gsettings gobject-introspection gettext features_check

SRC_URI[archive.sha256sum] = "009baa77f8362612caa2e18c338a1b3c8aad3b5fe2964c2fef7824d321228983"

# x11 is still manadatory - see meson.build
REQUIRED_DISTRO_FEATURES = "wayland x11 polkit"

# systemd can be replaced by libelogind (not available atow - make systemd
# mandatory distro feature)
LOGIND ?= "systemd"
REQUIRED_DISTRO_FEATURES += "systemd"

# profiler requires sysprof 3.34 which is not willing to build atow
PACKAGECONFIG ??= " \
    native-backend \
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl x11', 'opengl glx', '', d)} \
    sm \
    startup-notification \
    gnome-desktop \
"

EXTRA_OEMESON += " \
    -Dtests=false \
    -Dnative_tests=false \
    -Dxwayland_path=${bindir}/Xwayland \
"

# combi-config - see meson_options.txt for more details
PACKAGECONFIG[native-backend] = "-Dnative_backend=true -Dudev=true, -Dnative_backend=false -Dudev=false, libdrm virtual/libgbm libinput ${LOGIND} virtual/egl virtual/libgles2 udev"
PACKAGECONFIG[opengl] = "-Dopengl=true, -Dopengl=true, virtual/libgl"
PACKAGECONFIG[glx] = "-Dglx=true, -Dglx=false"
PACKAGECONFIG[libdisplay-info] = "-Dlibdisplay_info=true, -Dlibdisplay_info=false, libdisplay-info"
PACKAGECONFIG[libwacom] = "-Dlibwacom=true, -Dlibwacom=false, libwacom"
# Remove depending on pipewire-0.2 when mutter is upgraded to 3.36+
PACKAGECONFIG[remote-desktop] = "-Dremote_desktop=true, -Dremote_desktop=false, pipewire"
PACKAGECONFIG[gnome-desktop] = "-Dlibgnome_desktop=true, -Dlibgnome_desktop=false, gnome-desktop gnome-settings-daemon"
PACKAGECONFIG[sm] = "-Dsm=true, -Dsm=false, libsm"
PACKAGECONFIG[sound-player] = "-Dsound_player=true, -Dsound_player=false, libcanberra"
PACKAGECONFIG[profiler] = "-Dprofiler=true,-Dprofiler=false,sysprof"
PACKAGECONFIG[startup-notification] = "-Dstartup_notification=true, -Dstartup_notification=false, startup-notification, startup-notification"

MUTTER_API_NAME = "mutter-14"

do_install:prepend() {
    sed -i -e 's|${B}/||g' ${B}/cogl/cogl/cogl-enum-types.c
    sed -i -e 's|${B}/||g' ${B}/clutter/clutter/clutter-enum-types.c
    sed -i -e 's|${B}/||g' ${B}/src/meta-private-enum-types.c
    sed -i -e 's|${B}/||g' ${B}/src/meta/meta-enum-types.c
}

do_install:append() {
    # Add gir links in standard paths. That makes dependents life much easier
    # to find them
    install -d ${D}${datadir}/gir-1.0
    for gir_full in `find ${D}${libdir}/${MUTTER_API_NAME} -name '*.gir'`; do
        gir=`basename "$gir_full"`
        ln -sr "${D}${libdir}/${MUTTER_API_NAME}/$gir" "${D}${datadir}/gir-1.0/$gir"
    done
}

GSETTINGS_PACKAGE = "${PN}-gsettings"

PACKAGES =+ "${PN}-tests ${PN}-gsettings"

FILES:${PN} += " \
    ${datadir}/gnome-control-center \
    ${datadir}/gir-1.0 \
    ${libdir}/${MUTTER_API_NAME}/lib*${SOLIBS} \
    ${libdir}/${MUTTER_API_NAME}/*.typelib \
    ${libdir}/${MUTTER_API_NAME}/plugins \
"

FILES:${PN}-tests += " \
    ${datadir}/installed-tests \
    ${datadir}/${MUTTER_API_NAME}/tests \
    ${libexecdir}/installed-tests/${MUTTER_API_NAME} \
"

FILES:${PN}-dev += " \
    ${libdir}/${MUTTER_API_NAME}/*.gir \
    ${libdir}/${MUTTER_API_NAME}/lib*.so \
"

RDEPENDS:${PN} += "${PN}-gsettings gsettings-desktop-schemas"

