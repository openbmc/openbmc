DESCRIPTION = "A feature rich Remote Desktop Application written in GTK+"
HOMEPAGE = "https://remmina.org"
SECTION = "Support"
LICENSE = "GPLv2 & openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab7215512044d49037272ce1ac4ea8f file://LICENSE.OpenSSL;md5=c1eb3cee0a4dea27503c531267a69769"
DEPENDS += "openssl freerdp gtk+3 gdk-pixbuf atk libgcrypt avahi-ui libsodium libssh vte json-glib libsoup-2.4 libvncserver libsecret libxkbfile"


DEPENDS_append_libc-musl = " libexecinfo"
LDFLAGS_append_libc-musl = " -lexecinfo"

SRC_URI = "https://gitlab.com/Remmina/Remmina/-/archive/v${PV}/Remmina-v${PV}.tar.bz2 \
"
SRC_URI[md5sum] = "6da599c3a5cab2df37a70f8fba2f5438"
SRC_URI[sha256sum] = "fbed745438bb0c21467b60cbd67c8148a9289b5ebc7482d06db443bea556af1a"

S = "${WORKDIR}/Remmina-v${PV}"

inherit cmake features_check mime-xdg
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECMAKE += "-DWITH_APPINDICATOR=OFF -DWITH_GETTEXT=OFF -DWITH_TRANSLATIONS=OFF"

do_install_append(){
    # We dont need the extra stuff form other desktop environments
    rm -rf ${D}/${datadir}/xsessions
    rm -rf ${D}/${datadir}/metainfo
    rm -rf ${D}/${datadir}/gnome-session
}

PACKAGECONFIG[spice] = "-DWITH_SPICE=ON, -DWITH_SPICE=OFF, spice spice-protocol"

RDEPENDS_${PN} = "bash"

FILES_${PN}_append = " ${datadir}/icons/hicolor/*"
