SUMMARY = "PolicyKit-gnome provides an Authentication Agent for PolicyKit"
HOMEPAGE = "https://gitlab.gnome.org/Archive/policykit-gnome"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=74579fab173e4c5e12aac0cd83ee98ec"

DEPENDS = "glib-2.0-native glib-2.0 gtk+3 polkit intltool-native"

inherit autotools pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI = " \
    git://gitlab.gnome.org/Archive/policykit-gnome.git;protocol=https;branch=master \
    file://0001-Select-the-current-user-to-authenticate-with-by-defa.patch \
    file://0002-Auth-dialog-Make-the-label-wrap-at-70-chars.patch \
    file://0003-Get-user-icon-from-accountsservice-instead-of-lookin.patch \
    file://0004-Use-fresh-X11-timestamps-when-displaying-authenticat.patch \
    file://0005-configure.ac-disable-gnome-tools-that-are-not-provid.patch \
    file://polkit-gnome-authentication-agent-1.desktop \
"
SRCREV = "a0763a246a81188f60b0f9810143e49224dc752f"
S = "${WORKDIR}/git"


do_install:append() {
  install -d ${D}${datadir}/applications
  install -m644 ${WORKDIR}/polkit-gnome-authentication-agent-1.desktop \
      ${D}${datadir}/applications
}
