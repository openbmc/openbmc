SUMMARY = "Password and keyring managing daemon"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/gnome"

LICENSE = "GPLv2+ & LGPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

inherit distro_features_check gnomebase remove-libtool

DEPENDS = " \
    intltool-native \
    glib-2.0-native \
    gtk+3 \
    gcr \
    libgcrypt \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} \
"

SRC_URI[archive.md5sum] = "e09efe8021944dae404736b5a2adb98e"
SRC_URI[archive.sha256sum] = "bc17cecd748a0e46e302171d11c3ae3d76bba5258c441fabec3786f418e7ec99"
SRC_URI += "file://musl.patch"

REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS_${PN} = "libgnome-keyring glib-2.0-utils"

EXTRA_OECONF = " \
    --disable-doc \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam --with-pam-dir=${base_libdir}/security', '--disable-pam', d)} \
"


FILES_${PN} += " \
    ${datadir}/dbus-1/services \
    ${datadir}/p11-kit \
    ${base_libdir}/security/*${SOLIBSDEV} \
    ${libdir}/pkcs11/gnome-keyring-pkcs11.so \
"
