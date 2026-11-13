SUMMARY = "Document viewer for GNOME"
HOMEPAGE = "https://apps.gnome.org/Papers/"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/papers/-/issues"
SECTION = "x11/office"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=96f2f8d5ee576a2163977938ea36fa7b"

inherit cargo_common cargo-update-recipe-crates meson pkgconfig gettext gnome-help gsettings gtk-icon-cache mime-xdg gobject-introspection features_check

REQUIRED_DISTRO_FEATURES = "opengl"

GTKIC_VERSION = "4"

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"

SRC_URI = "git://gitlab.gnome.org/GNOME/papers.git;protocol=https;branch=main;tag=${PV} \
           file://0001-meson-Support-cross-compilation-via-a-rust_target-cr.patch \
           "
SRCREV = "6ca6e7ec685343cd4664f6253ab0f42d31e51bb3"

UPSTREAM_CHECK_URI = "https://gitlab.gnome.org/GNOME/papers/-/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

require ${BPN}-crates.inc

DEPENDS = " \
    appstream-native \
    blueprint-compiler-native \
    cairo \
    desktop-file-utils \
    exempi \
    gdk-pixbuf \
    glib-2.0 \
    glib-2.0-native \
    gtk4 \
    libadwaita \
    libarchive \
    poppler \
    tiff \
    zlib \
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

EXTRA_OEMESON += " \
    -Ddocumentation=false \
    -Dtests=false \
    -Dfile_tests=false \
    -Dsysprof=disabled \
    -Ddjvu=disabled \
"

export CARGO_NET_OFFLINE = "1"
export CARGO_BUILD_JOBS = "${@oe.utils.cpu_count()}"
export RUSTFLAGS
export GI_TYPELIB_PATH = "${STAGING_LIBDIR}/girepository-1.0/"

PACKAGECONFIG ??= "spell_check"
PACKAGECONFIG[nautilus] = "-Dnautilus=true,-Dnautilus=false,nautilus"
PACKAGECONFIG[spell_check] = "-Dspell_check=enabled,-Dspell_check=disabled,libspelling"

PACKAGES =+ "${PN}-nautilus-extension"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/thumbnailers \
"
FILES:${PN}-nautilus-extension = "${libdir}/nautilus/*/*.so"

RRECOMMENDS:${PN} += "adwaita-icon-theme"
