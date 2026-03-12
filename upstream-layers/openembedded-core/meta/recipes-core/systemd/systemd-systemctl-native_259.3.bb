FILESEXTRAPATHS:prepend := "${THISDIR}/systemd:"

SUMMARY = "Systemctl executable from systemd"

require systemd.inc

DEPENDS = "gperf-native libcap-native util-linux-native python3-jinja2-native"

inherit pkgconfig meson native

MESON_TARGET = "systemctl:executable"
MESON_INSTALL_TAGS = "systemctl"
EXTRA_OEMESON += "-Dlink-systemctl-shared=false"
EXTRA_OEMESON += "-Dsysvinit-path= -Dsysvrcnd-path="

# Systemctl is supposed to operate on target, but the target sysroot is not
# determined at run-time, but rather set during configure
# More details are here https://github.com/systemd/systemd/issues/35897#issuecomment-2665405887
EXTRA_OEMESON += "--sysconfdir ${sysconfdir_native}"

do_install:append() {
	# Install systemd-sysv-install in /usr/bin rather than /usr/lib/systemd
	# (where it is normally installed) so systemctl can find it in $PATH.
	# It is expected that the use of systemd-sysv-install will be removed
	# with version 259 of systemd and then this, and everything that was
	# added along with it, should be reverted.
	install -Dm 0755 ${S}/src/systemctl/systemd-sysv-install.SKELETON ${D}${bindir}/systemd-sysv-install
}
