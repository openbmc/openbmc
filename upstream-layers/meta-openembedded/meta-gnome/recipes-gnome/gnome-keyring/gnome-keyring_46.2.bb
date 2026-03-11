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
    gcr3 \
    libgcrypt \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} \
"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gsettings features_check gettext

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "bf26c966b8a8b7f3285ecc8bb3e467b9c20f9535b94dc451c9c559ddcff61925"
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
    ${systemd_user_unitdir} \
"
# fix | gnome-keyring-daemon: insufficient process capabilities, unsecure memory might get used
pkg_postinst:${PN} () {
    setcap cap_ipc_lock+ep $D/${bindir}/gnome-keyring-daemon
}
PACKAGE_WRITE_DEPS += "libcap-native"
