SUMMARY = "Multimedia processing server for Linux"
AUTHOR = "Wim Taymans <wtaymans@redhat.com>"
HOMEPAGE = "https://pipewire.org"
SECTION = "multimedia"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=e2c0b7d86d04e716a3c4c9ab34260e69 \
    file://COPYING;md5=97be96ca4fab23e9657ffa590b931c1a \
"
DEPENDS = "alsa-lib dbus udev"
SRCREV = "74a1632f0720886d5b3b6c23ee8fcd6c03ca7aac"
PV = "0.3.1"

SRC_URI = "git://github.com/PipeWire/pipewire"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd manpages

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio systemd vulkan', d)} \
    jack gstreamer \
"

PACKAGECONFIG[bluez] = "-Dbluez5=true,-Dbluez5=false,bluez5 sbc"
PACKAGECONFIG[jack] = "-Djack=true,-Djack=false,jack"
PACKAGECONFIG[gstreamer] = "-Dgstreamer=true,-Dgstreamer=false,glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxml-parser-perl-native"
PACKAGECONFIG[pulseaudio] = "-Dpipewire-pulseaudio=true,-Dpipewire-pulseaudio=false,pulseaudio"
PACKAGECONFIG[systemd] = "-Dsystemd=true,-Dsystemd=false,systemd"
PACKAGECONFIG[vulkan] = "-Dvulkan=true,-Dvulkan=false,vulkan-loader"

LDFLAGS_append_mipsarch = " -latomic"
LDFLAGS_append_x86 = " -latomic"
LDFLAGS_append_riscv32 = " -latomic"

PACKAGES =+ "\
    ${PN}-spa-plugins \
    ${PN}-alsa \
    ${PN}-config \
    gstreamer1.0-${PN} \
    lib${PN} \
    lib${PN}-modules \
    lib${PN}-jack \
"

RDEPENDS_lib${PN} += "lib${PN}-modules ${PN}-spa-plugins"

FILES_${PN} = "\
    ${sysconfdir}/pipewire/pipewire.conf \
    ${bindir}/pw-* \
    ${bindir}/pipewire* \
    ${systemd_user_unitdir}/* \
"
FILES_lib${PN} = "\
    ${libdir}/libpipewire-*.so.* \
    ${libdir}/libjack-*.so.* \
    ${libdir}/libpulse-*.so.* \
"
FILES_lib${PN}-modules = "\
    ${libdir}/pipewire-*/* \
"
FILES_${PN}-spa-plugins = "\
    ${bindir}/spa-* \
    ${libdir}/spa-*/* \
"
FILES_${PN}-alsa = "\
    ${libdir}/alsa-lib/* \
"
FILES_gstreamer1.0-${PN} = "\
    ${libdir}/gstreamer-1.0/* \
"

CONFFILES_${PN} = "\
    ${sysconfdir}/pipewire/pipewire.conf \
"
