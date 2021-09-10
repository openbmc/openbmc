SUMMARY = "Bluetooth Audio ALSA Backend"
HOMEPAGE = "https://github.com/Arkq/bluez-alsa"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=72d868d66bdd5bf51fe67734431de057"

DEPENDS += "alsa-lib bluez5 glib-2.0 sbc"

SRCREV = "aac8742a9e7dd12a1fead9cbce7d2dc8b961999c"

SRC_URI = " \
    git://github.com/Arkq/bluez-alsa.git;protocol=https;branch=master \
    file://bluealsa.service \
"

S  = "${WORKDIR}/git"

PACKAGECONFIG[aac]  = "--enable-aac, --disable-aac,"
PACKAGECONFIG[aptx] = "--enable-aptx,--disable-aptx,"
PACKAGECONFIG[hcitop]   = "--enable-hcitop, --disable-hcitop, libbsd ncurses"
PACKAGECONFIG[systemd]   = "--enable-systemd, --disable-systemd, systemd"

PACKAGECONFIG += "hcitop ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

inherit autotools pkgconfig systemd

FILES:${PN} += "\
    ${datadir}/alsa/alsa.conf.d/20-bluealsa.conf\
    ${libdir}/alsa-lib/libasound_module_ctl_bluealsa.so\
    ${libdir}/alsa-lib/libasound_module_pcm_bluealsa.so\
"

FILES:${PN}-staticdev += "\
    ${libdir}/alsa-lib/libasound_module_ctl_bluealsa.a\
    ${libdir}/alsa-lib/libasound_module_pcm_bluealsa.a\
"

SYSTEMD_SERVICE:${PN} = "bluealsa.service bluealsa-aplay.service"
