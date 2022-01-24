SUMMARY = "Basic GNOME desktop"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# gnome-menus & gmime get debian renamed
PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup features_check

REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam gobject-introspection-data"

RDEPENDS:${PN} = " \
    adwaita-icon-theme \
    adwaita-icon-theme-cursors \
    evolution-data-server \
    gnome-backgrounds \
    gnome-bluetooth \
    gnome-control-center \
    gnome-desktop \
    gnome-flashback \
    gnome-keyring \
    gnome-menus \
    gnome-session \
    gnome-settings-daemon \
    gnome-shell \
    gnome-shell-extensions \
    gnome-tweaks \
    gmime \
    gvfs gvfsd-ftp gvfsd-sftp gvfsd-trash \
"
