SUMMARY = "Window and compositing manager based on Clutter"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    colord \
    graphene \
    gtk4 \
    gdk-pixbuf \
    cairo \
    pango \
    gsettings-desktop-schemas \
    json-glib \
    libdisplay-info \
    libei \
    python3-argcomplete-native \
    python3-docutils-native \
    virtual/egl \
    virtual/libgles2 \
    wayland \
    wayland-native \
    wayland-protocols \
    libxcvt-native \
    "


inherit gnomebase gsettings gobject-introspection gettext features_check

SRC_URI += "file://0001-Dont-use-system-sysprof-dbus-folder.patch"
SRC_URI[archive.sha256sum] = "ec102aa3cbb0e39001206627aca3055314555f70609de5e6c2b7efcd1fa90f20"

REQUIRED_DISTRO_FEATURES = "wayland polkit"
ANY_OF_DISTRO_FEATURES = "opengl vulkan"

# systemd can be replaced by libelogind (not available atow - make systemd
# mandatory distro feature)
LOGIND ?= "systemd"
REQUIRED_DISTRO_FEATURES += "systemd"

# profiler requires sysprof 3.34 which is not willing to build atow
PACKAGECONFIG ??= " \
    native-backend \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 opengl glx sm xwayland startup-notification', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'logind udev', '', d)} \
    gnome-desktop \
    egl \
"

EXTRA_OEMESON += " \
    -Dtests=disabled \
"

# combi-config - see meson_options.txt for more details
PACKAGECONFIG[native-backend] = "-Dnative_backend=true -Dudev=true, -Dnative_backend=false -Dudev=false, libdrm virtual/libgbm libinput ${LOGIND} virtual/egl virtual/libgles2 udev"
PACKAGECONFIG[glx] = "-Dglx=true, -Dglx=false"
PACKAGECONFIG[opengl] = "-Dopengl=true, -Dopengl=false,virtual/libgl"
PACKAGECONFIG[egl] = "-Degl=true,-Degl=false,virtual/egl"
PACKAGECONFIG[libwacom] = "-Dlibwacom=true, -Dlibwacom=false, libwacom"
# Remove depending on pipewire-0.2 when mutter is upgraded to 3.36+
PACKAGECONFIG[remote-desktop] = "-Dremote_desktop=true, -Dremote_desktop=false, pipewire"
PACKAGECONFIG[gnome-desktop] = "-Dlibgnome_desktop=true, -Dlibgnome_desktop=false, gnome-desktop gnome-settings-daemon"
PACKAGECONFIG[sm] = "-Dsm=true, -Dsm=false, libsm"
PACKAGECONFIG[udev] = "-Dudev=true, -Dudev=false"
PACKAGECONFIG[logind] = "-Dlogind=true, -Dlogind=false, systemd"
PACKAGECONFIG[sound-player] = "-Dsound_player=true, -Dsound_player=false, libcanberra"
PACKAGECONFIG[profiler] = "-Dprofiler=true,-Dprofiler=false,sysprof"
PACKAGECONFIG[startup-notification] = "-Dstartup_notification=true, -Dstartup_notification=false, startup-notification, startup-notification"
PACKAGECONFIG[x11] = "-Dx11=true, -Dx11=false, virtual/libx11"
PACKAGECONFIG[xwayland] = "-Dxwayland=true, -Dxwayland=false, libxcb libxi xcomposite libxcursor xdamage xext libxkbfile libxfixes xkeyboard-config virtual/libx11 xinerama xau xwayland"

MUTTER_API_NAME = "mutter-16"

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
    ${datadir}/bash-completion \
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

