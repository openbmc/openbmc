SUMMARY = "Sandboxed and extendable image decoding"
DESCRIPTION = "Glycin decodes images in sandboxed subprocesses. It ships a set of \
image loaders, the libglycin C library with GObject introspection bindings, and a \
thumbnailer implementing the freedesktop thumbnail specification."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/glycin"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/glycin/-/issues"
SECTION = "graphics"

LICENSE = "MPL-2.0 OR LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE-MPL-2.0;md5=f75d2927d3c1ed2414ef72048f5ad640 \
                    file://LICENSE-LGPL-2.1;md5=e5dba73410bf27bc90d66e56336c7e2b"


inherit cargo_common cargo-update-recipe-crates meson pkgconfig gettext gobject-introspection

SRC_URI = "git://gitlab.gnome.org/GNOME/glycin.git;protocol=https;branch=2.1;tag=${PV} \
           file://0001-meson-Support-cross-compilation-via-a-rust_target-cr.patch \
           file://0002-libglycin-Read-cargo-artifacts-from-the-per-target-s.patch \
           "
SRCREV = "62c5096eaac2c92dab84c2eb896936d8da3dd9c1"

UPSTREAM_CHECK_URI = "https://gitlab.gnome.org/GNOME/glycin/-/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

require ${BPN}-crates.inc

DEPENDS = "glib-2.0 glib-2.0-native libseccomp lcms fontconfig"

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


EXTRA_OEMESON += "-Dprofile=release \
                  -Dlto=false \
                  -Dtests=false \
                  -Dtest_tokio=false \
                  -Dpython_tests=false \
                  -Dcapi_docs=false \
                  "

export CARGO_NET_OFFLINE = "1"
export CARGO_BUILD_JOBS = "${@oe.utils.cpu_count()}"
export RUSTFLAGS
export SYSTEM_DEPS_GLYCIN_2_NO_PKG_CONFIG = "1"
export SYSTEM_DEPS_GLYCIN_2_LIB = "glycin-2"
export SYSTEM_DEPS_GLYCIN_2_SEARCH_NATIVE = "${B}/libglycin"

PACKAGECONFIG ??= "libglycin thumbnailer image-rs jxl svg \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gtk4', '', d)}"

PACKAGECONFIG[libglycin] = "-Dlibglycin=true,-Dlibglycin=false"
PACKAGECONFIG[gtk4] = "-Dlibglycin-gtk4=true,-Dlibglycin-gtk4=false,gtk4"
PACKAGECONFIG[thumbnailer] = "-Dglycin-thumbnailer=true,-Dglycin-thumbnailer=false"
PACKAGECONFIG[vapi] = "-Dvapi=true,-Dvapi=false,vala-native"
PACKAGECONFIG[image-rs] = ",,"
PACKAGECONFIG[heif] = ",,libheif"
PACKAGECONFIG[jxl] = ",,libjxl"
PACKAGECONFIG[raw] = ",,"
PACKAGECONFIG[svg] = ",,librsvg cairo"

def glycin_loaders(d):
    known = (("heif", "glycin-heif"), ("image-rs", "glycin-image-rs"),
             ("jxl", "glycin-jxl"), ("raw", "glycin-raw"), ("svg", "glycin-svg"))
    enabled = d.getVar("PACKAGECONFIG").split()
    loaders = [name for flag, name in known if flag in enabled]
    if not loaders:
        return "-Dglycin-loaders=false"
    return "-Dglycin-loaders=true -Dloaders=" + ",".join(loaders)

EXTRA_OEMESON += "${@glycin_loaders(d)}"
EXTRA_OEMESON[vardeps] += "PACKAGECONFIG"

GLYCIN_COMPAT_VERSION = "2+"

PACKAGES =+ "${PN}-loaders ${PN}-thumbnailer ${PN}-gtk4"

FILES:${PN}-loaders = "${libexecdir}/glycin-loaders \
                       ${datadir}/glycin-loaders \
                       "

FILES:${PN}-gtk4 = "${libdir}/libglycin-gtk4-2.so.* \
                    ${libdir}/girepository-1.0/GlyGtk4-2.typelib \
                    "
FILES:${PN}-thumbnailer = "${bindir}/glycin-thumbnailer \
                           ${datadir}/thumbnailers \
                           "

RDEPENDS:${PN}-loaders += "${@bb.utils.contains('PACKAGECONFIG', 'thumbnailer', '${PN}-thumbnailer', '', d)}"
RDEPENDS:${PN}-thumbnailer += "${PN}"

