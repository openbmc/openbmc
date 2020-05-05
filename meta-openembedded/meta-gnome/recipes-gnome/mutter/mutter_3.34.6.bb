SUMMARY = "Window and compositing manager based on Clutter"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    xserver-xorg-cvt-native \
    virtual/libx11 \
    gtk+3 \
    gdk-pixbuf \
    cairo \
    pango \
    gsettings-desktop-schemas \
    json-glib \
    gnome-desktop3 \
    gnome-settings-daemon \
    libxtst \
    libxkbfile \
    xinerama \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext upstream-version-is-even features_check

SRC_URI[archive.md5sum] = "20913c458406e6efa3df005a3ce48c8e"
SRC_URI[archive.sha256sum] = "23bde87d33b8981358831cec8915bb5ff1eaf9c1de74c90cd1660b1b95883526"
SRC_URI += "file://0001-EGL-Include-EGL-eglmesaext.h.patch"

# x11 is still manadatory - see meson.build
REQUIRED_DISTRO_FEATURES = "x11"

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
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl wayland', 'wayland', '', d)} \
"

EXTRA_OEMESON += " \
    -Dxwayland_path=${bindir}/Xwayland \
"

# combi-config - see meson_options.txt for more details
PACKAGECONFIG[native-backend] = "-Dnative_backend=true -Dudev=true, -Dnative_backend=false -Dudev=false, libdrm virtual/libgbm libinput ${LOGIND} virtual/egl virtual/libgles2 udev"
PACKAGECONFIG[opengl] = "-Dopengl=true, -Dopengl=true, virtual/libgl"
PACKAGECONFIG[glx] = "-Dglx=true, -Dglx=false"
PACKAGECONFIG[libwacom] = "-Dlibwacom=true, -Dlibwacom=false, libwacom"
PACKAGECONFIG[remote-desktop] = "-Dremote_desktop=true, -Dremote_desktop=false, pipewire-0.2"
PACKAGECONFIG[sm] = "-Dsm=true, -Dsm=false, libsm"
PACKAGECONFIG[profiler] = "-Dprofiler=true,-Dprofiler=false,sysprof"
PACKAGECONFIG[startup-notification] = "-Dstartup_notification=true, -Dstartup_notification=false, startup-notification, startup-notification"
PACKAGECONFIG[wayland] = "-Dwayland=true,-Dwayland=false,wayland wayland-native, xserver-xorg-xwayland"
PACKAGECONFIG[wayland-eglstream] = "-Dwayland_eglstream=true,-Dwayland_eglstream=false"

# yes they changed from mutter-4 -> mutter-5 recently so be perpared
MUTTER_API_NAME = "mutter-5"

do_install_append() {
    # Add gir links in standard paths. That makes dependents life much easier
    # to find them
    install -d ${D}${datadir}/gir-1.0
    for gir_full in `find ${D}${libdir}/${MUTTER_API_NAME} -name '*.gir'`; do
        gir=`basename "$gir_full"`
        ln -sr "${D}${libdir}/${MUTTER_API_NAME}/$gir" "${D}${datadir}/gir-1.0/$gir"
    done
}

PACKAGES =+ "${PN}-tests"

FILES_${PN} += " \
    ${datadir}/gnome-control-center \
    ${libdir}/${MUTTER_API_NAME}/lib*${SOLIBS} \
    ${libdir}/${MUTTER_API_NAME}/*.typelib \
    ${libdir}/${MUTTER_API_NAME}/plugins \
"

FILES_${PN}-tests += " \
    ${datadir}/installed-tests \
    ${datadir}/${MUTTER_API_NAME}/tests \
    ${libexecdir}/installed-tests/${MUTTER_API_NAME} \
"

FILES_${PN}-dev += " \
    ${libdir}/${MUTTER_API_NAME}/*.gir \
    ${libdir}/${MUTTER_API_NAME}/lib*.so \
"

RDEPENDS_${PN} += "zenity"

