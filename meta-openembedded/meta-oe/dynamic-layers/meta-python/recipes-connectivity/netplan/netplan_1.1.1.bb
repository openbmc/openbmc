SUMMARY = "The network configuration abstraction renderer"
DESCRIPTION = "Netplan is a utility for easily configuring networking on a \
linux system. You simply create a YAML description of the required network \
interfaces and what each should be configured to do. From this description \
Netplan will generate all the necessary configuration for your chosen renderer \
tool."
HOMEPAGE = "https://netplan.io"
SECTION = "net/misc"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit meson pkgconfig systemd python3targetconfig features_check

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = "git://github.com/CanonicalLtd/netplan.git;branch=stable/1.1;protocol=https \
           file://0001-meson.build-do-not-use-Werror.patch \
          "

SRC_URI:append:libc-musl = " file://0001-don-t-fail-if-GLOB_BRACE-is-not-defined.patch"

SRCREV = "731d3c9e7e88d3f27ed6cf77edba7a2f5890a778"

S = "${WORKDIR}/git"

DEPENDS = "glib-2.0 libyaml util-linux-libuuid \
           systemd python3-cffi-native \
          "

EXTRA_OEMESON = "-Dtesting=false -Dunit_testing=false"

RDEPENDS:${PN} = "python3-core python3-netifaces python3-pyyaml \
                  python3-dbus python3-rich python3-cffi \
                  python3-json python3-fcntl \
                  util-linux-libuuid libnetplan \
                 "

do_install:append() {
    install -d -m 755 ${D}${sysconfdir}/netplan
}

PACKAGES += "${PN}-dbus libnetplan"

FILES:libnetplan = "${libdir}/libnetplan.so.*"
FILES:${PN} = "${sbindir} ${libexecdir}/netplan/generate \
               ${datadir}/netplan ${datadir}/bash-completion \
               ${systemd_unitdir} ${PYTHON_SITEPACKAGES_DIR} \
               ${sysconfdir}/netplan \
              "
FILES:${PN}-dbus = "${libexecdir}/netplan/netplan-dbus ${datadir}/dbus-1"
