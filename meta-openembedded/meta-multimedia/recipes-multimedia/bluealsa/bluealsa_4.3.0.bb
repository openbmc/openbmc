SUMMARY = "Bluetooth Audio ALSA Backend"
DESCRIPTION = "\
    BlueALSA is a replacement for BlueZ >= 5, with which one can achieve the \
    same Bluetooth audio profile support as with PulseAudio, but with fewer \
    dependencies and at a lower level in the software stack. It is designed \
    specifically for use on small, low-powered, dedicated audio or audio/visual \
    systems where the high-level audio management features of PulseAudio or \
    PipeWire are not required. \
"
HOMEPAGE = "https://github.com/Arkq/bluez-alsa"
BUGTRACKER = "https://github.com/arkq/bluez-alsa/issues"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=143bc4e73f39cc5e89d6e096ac0315ba"

DEPENDS += "alsa-lib bluez5 dbus glib-2.0-native python3-packaging-native sbc"

SRC_URI = "git://github.com/Arkq/bluez-alsa.git;protocol=https;branch=master \
           file://0001-Use-basename-implementation-from-glib-2.0.patch"

SRCREV = "959573c2cccef5cf074f5b2fa7941abbd699c5f4"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "aplay cli hcitop ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[a2dpconf] = "--enable-a2dpconf,--disable-a2dpconf"
PACKAGECONFIG[aac] = "--enable-aac,--disable-aac,fdk-aac"
PACKAGECONFIG[aplay] = "--enable-aplay,--disable-aplay"
PACKAGECONFIG[cli] = "--enable-cli,--disable-cli"
PACKAGECONFIG[coverage] = "--with-coverage,--without-coverage,lcov-native"
PACKAGECONFIG[debug] = "--enable-debug,--disable-debug"
PACKAGECONFIG[debug-time] = "--enable-debug-time,--disable-debug-time"
PACKAGECONFIG[faststream] = "--enable-faststream,--disable-faststream"
PACKAGECONFIG[hcitop] = "--enable-hcitop,--disable-hcitop,libbsd ncurses"
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind"
PACKAGECONFIG[midi] = "--enable-midi,--disable-midi"
PACKAGECONFIG[mp3lame] = "--enable-mp3lame,--disable-mp3lame,lame"
PACKAGECONFIG[mpg123] = "--enable-mpg123,--disable-mpg123,mpg123,mpg123"
PACKAGECONFIG[msbc] = "--enable-msbc,--disable-msbc,spandsp"
PACKAGECONFIG[ofono] = "--enable-ofono,--disable-ofono,ofono"
PACKAGECONFIG[payloadcheck] = "--enable-payloadcheck,--disable-payloadcheck"
PACKAGECONFIG[rfcomm] = "--enable-rfcomm,--disable-rfcomm"
PACKAGECONFIG[systemd] = "--enable-systemd --with-systemdsystemunitdir=${systemd_system_unitdir} \
                          --with-systemdbluealsaargs='${SYSTEMD_BLUEALSA_ARGS}' --with-systemdbluealsaaplayargs='${SYSTEMD_BLUEALSA_APLAY_ARGS}',--disable-systemd,systemd"
PACKAGECONFIG[test] = "--enable-test,--disable-test,libcheck libsndfile1"
PACKAGECONFIG[upower] = "--enable-upower,--disable-upower,,upower"

inherit autotools pkgconfig python3native systemd

# These proprietary codecs are not available in Yocto
EXTRA_OECONF = "\
    --disable-aptx \
    --disable-lc3plus \
    --disable-ldac \
    --disable-manpages \
"

PACKAGE_BEFORE_PN = "${PN}-aplay"

FILES:${PN}-aplay += "${bindir}/bluealsa-aplay"
FILES:${PN} += "${libdir}/alsa-lib/* ${datadir}/dbus-1/system.d"

RRECOMMENDS:${PN} = "${PN}-aplay"

SYSTEMD_PACKAGES += "${PN}-aplay"
SYSTEMD_SERVICE:${PN} = "bluealsa.service"
SYSTEMD_SERVICE:${PN}-aplay = "bluealsa-aplay.service"

SYSTEMD_AUTO_ENABLE:${PN}-aplay = "disable"

# Choose bluez-alsa arguments to be used in bluealsa systemd service
# Usually could choose profiles with it: a2dp-source a2dp-sink hfp-hf hfp-ag hsp-hs hsp-ag hfp-ofono
# Enable bluez-alsa arguments by default:
SYSTEMD_BLUEALSA_ARGS ?= "-p a2dp-source -p a2dp-sink"

# Choose bluealsa-aplay arguments to be used in bluealsa-aplay systemd service
# Defaults to be empty:
SYSTEMD_BLUEALSA_APLAY_ARGS ?= ""
