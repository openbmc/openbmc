SUMMARY = "Password and keyring managing daemon"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/gnome"

LICENSE = "GPLv2+ & LGPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

inherit distro_features_check gnomebase remove-libtool gettext upstream-version-is-even

DEPENDS = " \
    intltool-native \
    glib-2.0-native \
    gtk+3 \
    gcr \
    libgcrypt \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} \
"

SRC_URI[archive.md5sum] = "284580f954f762caf62aed2ae7358177"
SRC_URI[archive.sha256sum] = "81171b7d07211b216b4c9bb79bf2deb3deca18fe8d56d46dda1c4549b4a2646a"
SRC_URI += "file://musl.patch"

REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS_${PN} = "libgnome-keyring glib-2.0-utils"

EXTRA_OECONF = "--disable-doc \
                ac_cv_path_SSH_AGENT=${bindir}/ssh-agent \
                ac_cv_path_SSH_ADD=${bindir}/ssh-add"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG[pam] = "--enable-pam --with-pam-dir=${base_libdir}/security, --disable-pam"
PACKAGECONFIG[ssh-agent] = "--enable-ssh-agent,--disable-ssh-agent,,openssh-misc"

FILES_${PN} += " \
    ${datadir}/dbus-1/services \
    ${datadir}/p11-kit \
    ${base_libdir}/security/*${SOLIBSDEV} \
    ${libdir}/pkcs11/gnome-keyring-pkcs11.so \
"
