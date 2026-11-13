SUMMARY = "A light and fast file manager"
LICENSE = "GPL-3.0-or-later AND GFDL-1.3-or-later AND CC0-1.0"
LIC_FILES_CHKSUM = " \
    file://LICENSES/GPL-3.0-or-later.txt;md5=75d892af193fd5a298f724c4377d8f62 \
    file://LICENSES/GFDL-1.3-or-later.txt;md5=e0771ae6a62dc8a2e50b1d450fea66b7 \
    file://LICENSES/CC0-1.0.txt;md5=65d3616852dbf7b1a6d4b53b00626032 \
"

DEPENDS += " \
    desktop-file-utils-native \
    gdk-pixbuf \
    glib-2.0 \
    glib-2.0-native \
    gtk4 \
    vte \
"

CXXFLAGS += "-D_LIBCPP_ENABLE_CXX17_REMOVED_AUTO_PTR"

inherit cargo_common cargo-update-recipe-crates gnomebase itstool gettext gnome-help features_check meson gtk-icon-cache mime-xdg gobject-introspection

REQUIRED_DISTRO_FEATURES = "opengl gobject-introspection-data"

GIR_MESON_OPTION = ""

SRC_URI[archive.sha256sum] = "e51178f3256de45324ffd928a4a144fa859a865662bf295129f0188d8d243b57"
SRC_URI += "file://0001-meson-Support-cross-compilation-via-a-rust_target-cr.patch \
            file://0002-weak_ref-Use-a-Mutex-for-the-object-id-counter.patch \
"

require ${BPN}-crates.inc

do_write_config:append:class-target() {
	cat > ${WORKDIR}/meson-rust.cross <<-EOF
	[properties]
	rust_target = '${RUST_HOST_SYS}'
	rust_flags = '${RUSTFLAGS}'
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

do_install:append() {
	chrpath --delete ${D}${bindir}/gnome-commander
}

export CARGO_NET_OFFLINE = "1"
export CARGO_BUILD_JOBS = "${@oe.utils.cpu_count()}"
export CARGO_PROFILE_RELEASE_STRIP = "false"

PACKAGECONFIG ??= "exiv2 taglib libgsf poppler"
PACKAGECONFIG[exiv2] = "-Dexiv2=enabled,-Dexiv2=disabled,exiv2"
PACKAGECONFIG[taglib] = "-Dtaglib=enabled,-Dtaglib=disabled,taglib"
PACKAGECONFIG[libgsf] = "-Dlibgsf=enabled,-Dlibgsf=disabled,libgsf"
PACKAGECONFIG[poppler] = "-Dpoppler=enabled,-Dpoppler=disabled,poppler"

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/libgcmd.so ${datadir}/metainfo"
INSANE_SKIP:${PN} += "dev-so"
