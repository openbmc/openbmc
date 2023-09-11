SUMMARY = "Implementation of XDG Sound Theme and Name Specifications"
DESCRIPTION = "Libcanberra is an implementation of the XDG Sound Theme and Name Specifications, for generating event sounds on free desktops."
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/canberra.h;beginline=7;endline=24;md5=c616c687cf8da540a14f917e0d23ab03"

DEPENDS = "libtool libvorbis"

inherit autotools gtk-doc

SRC_URI = " \
    http://0pointer.de/lennart/projects/${BPN}/${BPN}-${PV}.tar.xz \
    file://0001-build-gtk-and-gtk3-version-for-canberra_gtk_play.patch \
    file://0001-gtk-Don-t-assume-all-GdkDisplays-are-GdkX11Displays-.patch \
    file://0001-remove-dropped-templates.patch \
"
SRC_URI[md5sum] = "34cb7e4430afaf6f447c4ebdb9b42072"
SRC_URI[sha256sum] = "c2b671e67e0c288a69fc33dc1b6f1b534d07882c2aceed37004bf48c601afa72"

EXTRA_OECONF = "\
    --enable-null \
    --disable-oss \
    --disable-tdb \
    --disable-lynx \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa pulseaudio', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk gtk3', '', d)} \
"
PACKAGECONFIG[alsa] = "--enable-alsa, --disable-alsa, alsa-lib"
PACKAGECONFIG[pulseaudio] = "--enable-pulse, --disable-pulse, pulseaudio"
PACKAGECONFIG[gstreamer] = "--enable-gstreamer, --disable-gstreamer, gstreamer1.0"
PACKAGECONFIG[gtk] = "--enable-gtk, --disable-gtk, gtk+"
PACKAGECONFIG[gtk3] = "--enable-gtk3, --disable-gtk3, gtk+3"

python populate_packages:prepend() {
    plugindir = d.expand('${libdir}/${BPN}-${PV}/')
    do_split_packages(d, plugindir, r'^libcanberra-(.*)\.so$', 'libcanberra-%s', '%s support library', extra_depends='' )
    do_split_packages(d, plugindir, r'^libcanberra-(.*)\.la$', 'libcanberra-%s', '%s support library', extra_depends='' )
}

PACKAGES =+ "${PN}-gnome ${PN}-gtk2 ${PN}-gtk3 ${PN}-systemd"
PACKAGES_DYNAMIC += "^libcanberra-.*"

FILES:${PN} = "${bindir}/ ${libdir}/${BPN}.so.*"

FILES:${PN}-dev += "${datadir}/vala/vapi ${libdir}/*/modules/*.la ${libdir}/*/*.la"

FILES:${PN}-dbg += "${libdir}/${BPN}-${PV}/.debug ${libdir}/gtk-*/modules/.debug"

FILES:${PN}-gtk2 = "${libdir}/${BPN}-gtk.so.* \
                    ${libdir}/gtk-2.0/modules/*.so \
                    ${bindir}/canberra-gtk-play"

# -gtk3 ships a symlink to a .so
INSANE_SKIP:${PN}-gtk3 = "dev-so"
FILES:${PN}-gtk3 = "${libdir}/${BPN}-gtk3.so.* \
                    ${libdir}/gtk-3.0/modules/*.so \
                    ${bindir}/canberra-gtk3-play"

FILES:${PN}-gnome = "${libdir}/gnome-settings-daemon-3.0/ \
                     ${datadir}/gdm/ ${datadir}/gnome/"

FILES:${PN}-systemd = "${systemd_unitdir}/system/*.service"
