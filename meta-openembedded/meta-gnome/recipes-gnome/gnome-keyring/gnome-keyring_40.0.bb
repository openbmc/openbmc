SUMMARY = "Password and keyring managing daemon"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/gnome"

LICENSE = "GPL-2.0-or-later & LGPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
    gcr \
    libgcrypt \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} \
"

inherit gnomebase gsettings features_check remove-libtool gettext

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "a3d24db08ee2fdf240fbbf0971a98c8ee295aa0e1a774537f4ea938038a3b931"
SRC_URI += " \
    file://0001-Set-paths-to-ssh-agent-and-ssh-add-by-configure-opti.patch \
    file://musl.patch \
"

PACKAGECONFIG ??= "ssh-agent"
PACKAGECONFIG[ssh-agent] = "--enable-ssh-agent --with-ssh-agent-path=${bindir}/ssh-agent --with-ssh-add-path=${bindir}/ssh-add,--disable-ssh-agent,,openssh-misc"

EXTRA_OECONF = " \
    --disable-doc \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam --with-pam-dir=${base_libdir}/security', '--disable-pam', d)} \
"

FILES:${PN} += " \
    ${datadir}/dbus-1/services \
    ${datadir}/p11-kit \
    ${datadir}/xdg-desktop-portal \
    ${base_libdir}/security/*${SOLIBSDEV} \
    ${libdir}/pkcs11/gnome-keyring-pkcs11.so \
"
# fix | gnome-keyring-daemon: insufficient process capabilities, unsecure memory might get used
pkg_postinst:${PN} () {
    setcap cap_ipc_lock+ep $D/${bindir}/gnome-keyring-daemon
}
PACKAGE_WRITE_DEPS += "libcap-native"
