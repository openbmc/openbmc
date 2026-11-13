SUMMARY = "Image viewer for GNOME"
DESCRIPTION = "Loupe shows images with support for zooming, rotating and printing. \
Images are loaded via glycin, which decodes them in sandboxed subprocesses."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/loupe"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/loupe/-/issues"
SECTION = "graphics"

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=97a733ff40c50b4bfc74471e1f6ca88b"

inherit cargo_common cargo-update-recipe-crates meson pkgconfig gettext itstool gnome-help gsettings gtk-icon-cache mime-xdg features_check

REQUIRED_DISTRO_FEATURES = "opengl"

GTKIC_VERSION = "4"

SRC_URI = "git://gitlab.gnome.org/GNOME/loupe.git;protocol=https;branch=main;tag=${PV} \
           file://0001-meson-Support-cross-compilation-via-a-rust_target-cr.patch \
           "
SRCREV = "0ae0d797d11e18a045b2d383e47a097181b4062b"

UPSTREAM_CHECK_URI = "https://gitlab.gnome.org/GNOME/loupe/-/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

require ${BPN}-crates.inc

DEPENDS = " \
    cairo \
    desktop-file-utils \
    fontconfig \
    gdk-pixbuf \
    glib-2.0 \
    glib-2.0-native \
    graphene \
    gtk4 \
    hicolor-icon-theme \
    libadwaita \
    libgweather4 \
    libseccomp \
    pango \
"

do_write_config:append:class-target() {
	cat > ${WORKDIR}/meson-rust.cross <<-EOF
	[properties]
	rust_target = '${RUST_HOST_SYS}'
	EOF
}

EXTRA_OEMESON:append:class-target = " --cross-file ${WORKDIR}/meson-rust.cross"

do_configure:prepend() {
	cargo_common_do_configure
}

do_compile:prepend() {
	mkdir -p ${B}/cargo-home
	install -m 0644 ${CARGO_HOME}/config.toml ${B}/cargo-home/config.toml
}

EXTRA_OEMESON += "-Dprofile=release"

export CARGO_NET_OFFLINE = "1"
export CARGO_BUILD_JOBS = "${@oe.utils.cpu_count()}"
export RUSTFLAGS

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "-Dx11=enabled,-Dx11=disabled,"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"

RDEPENDS:${PN} += "glycin-loaders"
