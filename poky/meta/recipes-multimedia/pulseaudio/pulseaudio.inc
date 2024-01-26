SUMMARY = "Sound server for Linux and Unix-like operating systems"
DESCRIPTION = "A general purpose sound server intended to run as a middleware \
between your applications and your hardware devices, either using ALSA or OSS."
HOMEPAGE = "http://www.pulseaudio.org"
SECTION = "libs/multimedia"

# Most of PulseAudio code is under LGPL-2.1-or-later. There are a few
# exceptions:
#
# The "adrian" echo canceller variant has code under a non-standard permissive
# license. See src/modules/echo-cancel/adrian-license.txt for details. This
# recipe disables the adrian echo canceller to avoid hassle with the unusual
# license.
#
# The src/modules/reserve* and src/pulsecore/rtkit* files are under the MIT
# license.
#
# The src/pulsecore/filter/ directory contains code under the 3-clause BSD
# license.
#
# People who distribute PulseAudio binaries need to also consider that there
# are some dependencies to GPL libraries. LGPL code that depends on GPL
# libraries probably becomes effectively GPL-licensed (at compile-time? or at
# at link-time?). I'm not a lawyer, though, so I'm not sure of the exact
# implications. The GPL dependencies only affect the server, not the client
# library, with the exception of libdbus that affects both. These are the GPL
# library dependencies:
#
# One of the resampler implementations uses libsamplerate. This recipe doesn't
# enable that resampler, however.
#
# One of the database implementations uses gdbm. This recipe doesn't enable
# that database implementation, however.
#
# module-lirc (enabled by PACKAGECONFIG[lirc]) uses LIRC.
#
# module-equalizer-sink uses FFTW. This recipe disables that, however.
#
# The dependency with the most complicated licensing considerations is libdbus.
# When PACKAGECONFIG[dbus] is enabled (like it is by default), libdbus will be
# used by both the server and the client library (libpulse). Does this affect
# applications that use libpulse? It should also be noted that libdbus is
# dual-licensed: either GPL-2.0-or-later or AFL-2.0 terms apply. Whose decision
# is it which of the licenses apply? What a mess. Some people hold the view that
# libdbus is a system library that is covered by the "special exception" in
# GPLv2's section 3, and therefore libdbus's GPL license doesn't affect
# PulseAudio.
LICENSE = "LGPL-2.1-or-later & MIT & BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0e5cd938de1a7a53ea5adac38cc10c39 \
                    file://GPL;md5=4325afd396febcb659c36b49533135d4 \
                    file://LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/modules/echo-cancel/adrian-license.txt;md5=abbab006a561fbffccedf1c3531f34ab \
                    file://src/pulsecore/filter/LICENSE.WEBKIT;md5=49defbaffddf8c51faf606ff7fc3b1f7 \
                    file://src/pulsecore/resampler.h;beginline=4;endline=21;md5=09794012ae16912c0270f3280cc8ff84 \
                    file://src/modules/reserve.h;beginline=6;endline=28;md5=0e23094760367d51b6609750e9b31fbb \
                    file://src/pulsecore/rtkit.h;beginline=6;endline=29;md5=3f00ff966716ae0817c31576d1473528 \
                    file://src/modules/echo-cancel/adrian-aec.h;beginline=3;endline=12;md5=d3ed4fad1c073f8b06f37495dc5d1026 \
                    file://src/pulsecore/filter/biquad.h;beginline=1;endline=4;md5=6d46d1365206528a20137355924233c1 \
"

# libtool is needed for libltdl, used in module loading.
DEPENDS = "m4-native libatomic-ops libsndfile1 libtool"
# optional
DEPENDS += "udev alsa-lib glib-2.0"
DEPENDS += "speexdsp libxml-parser-perl-native libcap"

inherit bash-completion meson pkgconfig useradd gettext perlnative systemd manpages gsettings

# *.desktop rules wont be generated during configure and build will fail
# if using --disable-nls
USE_NLS = "yes"

EXTRA_OEMESON = "\
		-Dhal-compat=false \
		-Dorc=disabled \
		-Daccess_group=audio \
		-Dopenssl=disabled \
		-Ddatabase=simple \
		-Dzshcompletiondir=no \
		-Dudevrulesdir=`pkg-config --variable=udevdir udev`/rules.d \
		-Dvalgrind=disabled \
		-Dtests=false \
		-Drunning-from-build-tree=false \
"

# soxr (the SoX Resampler library) doesn't seem to be currently packaged in
# oe-core nor meta-oe, so let's not add a PACKAGECONFIG entry for it for now.
EXTRA_OEMESON += "-Dsoxr=disabled"

# The FFTW dependency (for module-equalizer-sink) was removed in commit
# ddbd713293 without explaining why it was not made a PACKAGECONFIG item
# instead. Oh well, let's keep it disabled until someone expresses some
# interest in having it enabled.
EXTRA_OEMESON += "-Dfftw=disabled"

# The "adrian" echo canceller implementation has a non-standard license
# (src/modules/echo-cancel/adrian-license.txt). It's a permissive license, so
# the licensing terms are probably not problematic, but it would be an extra
# hassle to add the license to OE-Core's set of licenses. The canceller isn't
# very good anyway, better alternatives exist (such as the webrtc canceller).
EXTRA_OEMESON += "-Dadrian-aec=false"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez5', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', '3g', 'ofono', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 systemd x11', d)} \
                   dbus gsettings \
                   "

PACKAGECONFIG[dbus] = "-Ddbus=enabled,-Ddbus=disabled,dbus"
PACKAGECONFIG[bluez5] = "-Dbluez5=enabled,-Dbluez5=disabled,bluez5 sbc"
PACKAGECONFIG[gsettings] = "-Dgsettings=enabled,-Dgsettings=disabled,glib-2.0-native glib-2.0"
PACKAGECONFIG[ofono] = "-Dbluez5-ofono-headset=true,-Dbluez5-ofono-headset=false,ofono"
PACKAGECONFIG[gtk] = "-Dgtk=enabled,-Dgtk=disabled,gtk+3"
PACKAGECONFIG[systemd] = "-Dsystemd=enabled -Dsystemduserunitdir=${systemd_user_unitdir},-Dsystemd=disabled,systemd"
PACKAGECONFIG[x11] = "-Dx11=enabled,-Dx11=disabled,virtual/libx11 libxtst libice libsm libxcb"
PACKAGECONFIG[avahi] = "-Davahi=enabled,-Davahi=disabled,avahi"
PACKAGECONFIG[jack] = "-Djack=enabled,-Djack=disabled,jack"
# Since many embedded systems don't have non-root users, it's useful to be
# able to use pulseaudio autospawn for root as well.
PACKAGECONFIG[autospawn-for-root] = ",,,"
PACKAGECONFIG[lirc] = "-Dlirc=enabled,-Dlirc=disabled,lirc"
PACKAGECONFIG[webrtc] = "-Dwebrtc-aec=enabled,-Dwebrtc-aec=disabled,webrtc-audio-processing"
PACKAGECONFIG[ipv6] = "-Dipv6=true,-Dipv6=false,"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,"

export TARGET_PFPU = "${TARGET_FPU}"

set_cfg_value () {
	sed -i -e "s/\(; *\)\?$2 =.*/$2 = $3/" "$1"
	if ! grep -q "^$2 = $3\$" "$1"; then
		die "Use of sed to set '$2' to '$3' in '$1' failed"
	fi
}

do_compile:append () {
	if ${@bb.utils.contains('PACKAGECONFIG', 'autospawn-for-root', 'true', 'false', d)}; then
		set_cfg_value src/pulse/client.conf allow-autospawn-for-root yes
	fi
}

do_install:append() {
	install -d ${D}${sysconfdir}/default/volatiles
	install -m 0644 ${WORKDIR}/volatiles.04_pulse  ${D}${sysconfdir}/default/volatiles/04_pulse
}

USERADD_PACKAGES = "pulseaudio-server"
GROUPADD_PARAM:pulseaudio-server = "--system pulse"
USERADD_PARAM:pulseaudio-server = "--system --home /var/run/pulse \
                              --no-create-home --shell /bin/false \
                              --groups audio,pulse --gid pulse pulse"

PACKAGES =+ "\
             libpulse \
             libpulse-mainloop-glib \
             libpulse-simple \
             libpulsecommon \
             libpulsecore \
             ${PN}-pa-info \
             ${PN}-server \
             ${PN}-misc \
             "

# The console-kit module is included here explicitly so bitbake can map to the
# RDEPENDS we define for it in this recipe, and thereby ensure that when
# adding the console-kit module to an image, we also get the necessary
# consolekit package produced.
PACKAGES =+ "${@bb.utils.contains('PACKAGECONFIG', 'dbus', 'pulseaudio-module-console-kit', '', d)}"

#upgrade path:
RREPLACES:pulseaudio-server = "libpulse-bin libpulse-conf"

PACKAGES_DYNAMIC += "^pulseaudio-lib-.* ^pulseaudio-module-.*"

FILES:libpulsecore = "${libdir}/pulseaudio/libpulsecore*.so"
FILES:libpulsecommon = "${libdir}/pulseaudio/libpulsecommon*.so"

# client.conf configures the behaviour of libpulse, so it belongs in the same
# package.
FILES:libpulse = "${libdir}/libpulse.so.* ${sysconfdir}/pulse/client.conf"

FILES:libpulse-simple = "${libdir}/libpulse-simple.so.*"
FILES:libpulse-mainloop-glib = "${libdir}/libpulse-mainloop-glib.so.*"

FILES:${PN}-dev += "${libdir}/pulse-${PV}/modules/*.la ${datadir}/vala"
FILES:${PN}-bin += "${sysconfdir}/default/volatiles/04_pulse"
FILES:${PN}-pa-info = "${bindir}/pa-info"
FILES:${PN}-server = "${bindir}/pulseaudio ${bindir}/start-* ${sysconfdir} ${bindir}/pactl */udev/rules.d/*.rules */*/udev/rules.d/*.rules ${systemd_user_unitdir}/*"
FILES:${PN}-server += "${datadir}/dbus-1/system.d/pulseaudio-system.conf"

#SYSTEMD_PACKAGES = "${PN}-server"
SYSTEMD_SERVICE:${PN}-server = "pulseaudio.service"

FILES:${PN}-misc = "${bindir}/* ${libdir}/pulseaudio/libpulsedsp.so"

# Allow the pulseaudio package to be created empty as a placeholder (-dbg and -dev depend on it)
FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"

CONFFILES:libpulse = "${sysconfdir}/pulse/client.conf"

CONFFILES:pulseaudio-server = "\ 
  ${sysconfdir}/pulse/default.pa \
  ${sysconfdir}/pulse/daemon.conf \
  ${sysconfdir}/pulse/system.pa \
  "

pkg_postinst:${PN}-server() {
        if [ -z "$D" ] && [ -e ${sysconfdir}/init.d/populate-volatile.sh ] ; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
}

python populate_packages:prepend() {
    plugindir = d.expand('${libdir}/pulseaudio/modules/')
    do_split_packages(d, plugindir, r'^module-(.*)\.so$', '${PN}-module-%s', 'PulseAudio module for %s', extra_depends='', prepend=True)
    do_split_packages(d, plugindir, r'^lib(.*)\.so$', '${PN}-lib-%s', 'PulseAudio library for %s', extra_depends='', prepend=True)
}

# pa-info is a bash script that collects information about the audio setup.
# It's primarily useful for attaching an information dump when reporting bugs.
RDEPENDS:${PN}-pa-info = "\
                          alsa-utils-amixer \
                          alsa-utils-aplay \
                          alsa-utils-scripts \
                          bash \
                          ${PN}-server \
                          "

RDEPENDS:pulseaudio-server = " \
    pulseaudio-module-filter-apply \
    pulseaudio-module-filter-heuristics \
    pulseaudio-module-udev-detect \
    pulseaudio-module-null-sink \
    pulseaudio-module-device-restore \
    pulseaudio-module-stream-restore \
    pulseaudio-module-card-restore \
    pulseaudio-module-augment-properties \
    pulseaudio-module-detect \
    pulseaudio-module-alsa-sink \
    pulseaudio-module-alsa-source \
    pulseaudio-module-alsa-card \
    pulseaudio-module-native-protocol-unix \
    pulseaudio-module-default-device-restore \
    pulseaudio-module-intended-roles \
    pulseaudio-module-rescue-streams \
    pulseaudio-module-always-sink \
    pulseaudio-module-suspend-on-idle \
    pulseaudio-module-position-event-sounds \
    pulseaudio-module-role-cork \
    pulseaudio-module-switch-on-port-available"

# If the server is installed, it's usually desirable to make ALSA applications
# use PulseAudio. alsa-plugins-pulseaudio-conf will install the configuration
# that makes the PulseAudio plugin the default ALSA device.
RDEPENDS:pulseaudio-server += "${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'alsa-plugins-pulseaudio-conf', '', d)}"

# pulseaudio-module-console-kit is built whenever dbus is enabled by PACKAGECONFIG
# but consolekit depends on libx11 and is available only for DISTRO with x11 in DISTRO_FEATURES
RDEPENDS:pulseaudio-module-console-kit =+ "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit x11', 'consolekit', '', d)}"
RDEPENDS:pulseaudio-misc += "pulseaudio-module-cli-protocol-unix"

FILES:${PN}-module-alsa-card += "${datadir}/pulseaudio/alsa-mixer"

GSETTINGS_PACKAGE = "${@bb.utils.contains('PACKAGECONFIG', 'gsettings', '${PN}-module-gsettings', '', d)}"
FILES:${PN}-module-gsettings += "${libexecdir}/pulse/gsettings-helper ${datadir}/GConf/gsettings ${datadir}/glib-2.0/schemas"

# The console-kit module is good to have on X11 systems (it keeps PulseAudio
# running for the duration of the user login session). The device-manager and
# x11-* modules are referenced from the start-pulseaudio-x11 script, so those
# modules must be installed when X11 is enabled.
RDEPENDS:pulseaudio-server += "\
        ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '\
                pulseaudio-module-device-manager \
                pulseaudio-module-x11-cork-request \
                pulseaudio-module-x11-publish \
                pulseaudio-module-x11-xsmp \
        ', '', d)}"

RDEPENDS:pulseaudio-server += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', \
                                  bb.utils.contains('DISTRO_FEATURES', 'systemd', 'pulseaudio-module-systemd-login', 'pulseaudio-module-console-kit', d), \
                                  '', d)}"
