SUMMARY = "This is gnome-user-share 43.0, a system for easy sharing of user files."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = " \
    glib-2.0-native \
    glib-2.0 \
    systemd \
"

inherit cargo_common cargo-update-recipe-crates gnomebase gsettings features_check

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = "git://gitlab.gnome.org/GNOME/gnome-user-share.git;protocol=https;branch=gnome-48;tag=${PV}"
SRC_URI += "file://0001-meson-Support-cross-compilation-via-a-rust_target-cr.patch"
SRCREV = "e58291de6018667a1ed75382625d21c759066935"

require ${BPN}-crates.inc

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

MESON_BUILDTYPE = "release"

export CARGO_NET_OFFLINE = "1"
export CARGO_BUILD_JOBS = "${@oe.utils.cpu_count()}"
export RUSTFLAGS

PACKAGECONFIG ??= "modules"
PACKAGECONFIG[httpd] = "-Dhttpd=${sbindir}/httpd,,,apache2 mod-dnssd"
PACKAGECONFIG[modules] = "-Dmodules_path=${libexecdir}/apache2/modules"

FILES:${PN} += "${systemd_user_unitdir}"
