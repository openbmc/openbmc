SUMMARY = "Window and compositing manager based on Clutter"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    atk \
    cairo \
    colord \
    fribidi \
    gdk-pixbuf \
    glycin \
    graphene \
    gsettings-desktop-schemas \
    gtk4 \
    harfbuzz \
    lcms \
    libdisplay-info \
    libdrm \
    libei \
    libxkbcommon \
    pango \
    pixman \
    python3-argcomplete-native \
    python3-docutils-native \
    virtual/egl \
    virtual/libgles2 \
    wayland \
    wayland-native \
    wayland-protocols \
    "

inherit gnomebase gsettings gobject-introspection gettext features_check

SRC_URI += "file://0001-Dont-use-system-sysprof-dbus-folder.patch"
SRC_URI[archive.sha256sum] = "273d33c875abcb4b6cbea3f4ec045d18155fbc510c3521fc7e47926371310988"

REQUIRED_DISTRO_FEATURES = "wayland polkit"
ANY_OF_DISTRO_FEATURES = "opengl vulkan"

# systemd can be replaced by libelogind (not available atow - make systemd
# mandatory distro feature)
LOGIND ?= "systemd"
REQUIRED_DISTRO_FEATURES += "systemd"

PACKAGECONFIG ??= " \
    native-backend \
    egl \
    gles2 \
    opengl \
    fonts \
    bash-completion \
    gnome-desktop \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xwayland', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'logind udev', '', d)} \
"

PACKAGECONFIG[native-backend] = "-Dnative_backend=true -Dudev=true, -Dnative_backend=false -Dudev=false, libdrm virtual/libgbm libinput ${LOGIND} virtual/egl virtual/libgles2 udev libxcvt-native"
PACKAGECONFIG[opengl] = "-Dopengl=true, -Dopengl=false, virtual/libgl"
PACKAGECONFIG[gles2] = "-Dgles2=true, -Dgles2=false, virtual/libgles2"
PACKAGECONFIG[egl] = "-Degl=true, -Degl=false, virtual/egl"
PACKAGECONFIG[egl-device] = "-Degl_device=true, -Degl_device=false"
PACKAGECONFIG[wayland-eglstream] = "-Dwayland_eglstream=true, -Dwayland_eglstream=false, wayland-eglstream-protocols"
PACKAGECONFIG[udev] = "-Dudev=true, -Dudev=false, udev"
PACKAGECONFIG[logind] = "-Dlogind=true, -Dlogind=false, systemd"
PACKAGECONFIG[libwacom] = "-Dlibwacom=true, -Dlibwacom=false, libwacom"
PACKAGECONFIG[remote-desktop] = "-Dremote_desktop=true, -Dremote_desktop=false, pipewire"
PACKAGECONFIG[gnome-desktop] = "-Dlibgnome_desktop=true, -Dlibgnome_desktop=false, gnome-desktop gnome-settings-daemon"
PACKAGECONFIG[sound-player] = "-Dsound_player=true, -Dsound_player=false, libcanberra"
PACKAGECONFIG[profiler] = "-Dprofiler=true, -Dprofiler=false, sysprof"
PACKAGECONFIG[startup-notification] = "-Dstartup_notification=true, -Dstartup_notification=false, startup-notification, startup-notification"
PACKAGECONFIG[xwayland] = "-Dxwayland=true, -Dxwayland=false, libxcb libxi xcomposite libxcursor xdamage xext libxkbfile libxfixes xkeyboard-config virtual/libx11 xinerama xau xwayland"
# 'auto' would probe the xwayland pkg-config for -initfd support in the sysroot.
PACKAGECONFIG[xwayland-initfd] = "-Dxwayland_initfd=enabled, -Dxwayland_initfd=disabled"
PACKAGECONFIG[fonts] = "-Dfonts=true, -Dfonts=false, pango harfbuzz fribidi"
PACKAGECONFIG[bash-completion] = "-Dbash_completion=true, -Dbash_completion=false, bash-completion"
PACKAGECONFIG[docs] = "-Ddocs=true, -Ddocs=false, gi-docgen-native"
PACKAGECONFIG[devkit] = "-Ddevkit=enabled, -Ddevkit=disabled, gtk4"

EXTRA_OEMESON += " \
    -Dtests=disabled \
    -Dinstalled_tests=false \
    -Dcogl_tests=false \
    -Dclutter_tests=false \
    -Dmutter_tests=false \
    -Dkvm_tests=false \
    -Dtty_tests=false \
    -Dcatch=false \
    -Dverbose=true \
"

MUTTER_API_NAME = "mutter-18"

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
