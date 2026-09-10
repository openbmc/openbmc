FILESEXTRAPATHS:prepend := "${THISDIR}/systemd:"

require systemd.inc

SUMMARY = "native tools from systemd"

# TODO: Remove STATX_MNT_ID patch once minimum supported build host kernel is >= 5.8 (RHEL 8 EOL: 2029)
SRC_URI += "file://Handle-missing-STATX_MNT_ID-on-older-kernels.patch"

# We don't actually need jinja to generate code, but it's checked for at configure time
DEPENDS = "gperf-native python3-jinja2-native"

inherit pkgconfig meson native

# Disable everything that is auto-detected by default
EXTRA_OEMESON += "--auto-features disabled"

# Link the binaries statically as we don't install libsystemd-shared.so
EXTRA_OEMESON += "-Dlink-systemctl-shared=false -Dlink-udev-shared=false"

# Ensure unused build paths are not in the binary
EXTRA_OEMESON += "-Dsysvinit-path= -Dsysvrcnd-path="

# Target-absolute paths that satisfy both tools from one meson configure:
#  - systemd-hwdb needs prefix=/usr so the compiled-in UDEVLIBEXECDIR
#    (/usr/lib/udev) matches the target rootfs layout, letting
#    "update --root $D --usr" find hwdb.d sources and write hwdb.bin there.
#  - systemctl needs sysconfdir=/etc; it operates on the target rootfs but the
#    sysroot is fixed at configure time rather than run time.
#    See https://github.com/systemd/systemd/issues/35897#issuecomment-2665405887
EXTRA_OEMESON += "--prefix /usr --sysconfdir /etc"

MESON_TARGET = "systemctl systemd-hwdb"
MESON_INSTALL_TAGS = "systemctl"

do_install:append() {
    # Can't install this with a tag "hwdb" also tries to install the hwdb
    # itself, and there's no separate tag for systemd-hwdb.
    install ${B}/systemd-hwdb ${D}${bindir}/systemd-hwdb
}
