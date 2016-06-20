SUMMARY = "ALSA sound utilities"
HOMEPAGE = "http://www.alsa-project.org"
BUGTRACKER = "https://bugtrack.alsa-project.org/alsa-bug/login_page.php"
SECTION = "console/utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://alsactl/utils.c;beginline=1;endline=20;md5=fe9526b055e246b5558809a5ae25c0b9"
DEPENDS = "alsa-lib ncurses libsamplerate0"

PACKAGECONFIG ??= "udev"
PACKAGECONFIG[bat] = "--enable-bat,--disable-bat,fftw"
PACKAGECONFIG[udev] = "--with-udev-rules-dir=`pkg-config --variable=udevdir udev`/rules.d,,udev"
PACKAGECONFIG[xmlto] = "--enable-xmlto, --disable-xmlto, xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"

SRC_URI = "ftp://ftp.alsa-project.org/pub/utils/alsa-utils-${PV}.tar.bz2 \
           file://0001-alsactl-don-t-let-systemd-unit-restore-the-volume-wh.patch \
           file://0001-alsabat-rename-to-avoid-naming-conflict.patch \
          "

SRC_URI[md5sum] = "b9d6102fbbd0b68040bb77023ed30c0c"
SRC_URI[sha256sum] = "3b1c3135b76e14532d3dd23fb15759ddd7daf9ffbc183f7a9a0a3a86374748f1"

# lazy hack. needs proper fixing in gettext.m4, see
# http://bugs.openembedded.org/show_bug.cgi?id=2348
# please close bug and remove this comment when properly fixed
#
EXTRA_OECONF_append_libc-uclibc = " --disable-nls"

inherit autotools gettext pkgconfig

# This are all packages that we need to make. Also, the now empty alsa-utils
# ipk depends on them.

ALSA_UTILS_PKGS = "\
             ${@bb.utils.contains('PACKAGECONFIG', 'bat', 'alsa-utils-alsabat', '', d)} \
             alsa-utils-alsamixer \
             alsa-utils-alsatplg \
             alsa-utils-midi \
             alsa-utils-aplay \
             alsa-utils-amixer \
             alsa-utils-aconnect \
             alsa-utils-iecset \
             alsa-utils-speakertest \
             alsa-utils-aseqnet \
             alsa-utils-aseqdump \
             alsa-utils-alsactl \
             alsa-utils-alsaloop \
             alsa-utils-alsaucm \
            "

PACKAGES += "${ALSA_UTILS_PKGS}"
RDEPENDS_${PN} += "${ALSA_UTILS_PKGS}"

FILES_${PN} = ""
FILES_alsa-utils-alsabat     = "${bindir}/alsabat"
FILES_alsa-utils-alsatplg    = "${bindir}/alsatplg"
FILES_alsa-utils-aplay       = "${bindir}/aplay ${bindir}/arecord"
FILES_alsa-utils-amixer      = "${bindir}/amixer"
FILES_alsa-utils-alsamixer   = "${bindir}/alsamixer"
FILES_alsa-utils-speakertest = "${bindir}/speaker-test ${datadir}/sounds/alsa/ ${datadir}/alsa/speaker-test/"
FILES_alsa-utils-midi        = "${bindir}/aplaymidi ${bindir}/arecordmidi ${bindir}/amidi"
FILES_alsa-utils-aconnect    = "${bindir}/aconnect"
FILES_alsa-utils-aseqnet     = "${bindir}/aseqnet"
FILES_alsa-utils-iecset      = "${bindir}/iecset"
FILES_alsa-utils-alsactl     = "${sbindir}/alsactl */udev/rules.d */*/udev/rules.d ${systemd_unitdir} ${localstatedir}/lib/alsa ${datadir}/alsa/init/"
FILES_alsa-utils-aseqdump    = "${bindir}/aseqdump"
FILES_alsa-utils-alsaloop    = "${bindir}/alsaloop"
FILES_alsa-utils-alsaucm     = "${bindir}/alsaucm"

SUMMARY_alsa-utils-alsabat      = "Command-line sound tester for ALSA sound card driver"
SUMMARY_alsa-utils-alsatplg     = "Converts topology text files into binary format for kernel"
SUMMARY_alsa-utils-aplay        = "Play (and record) sound files using ALSA"
SUMMARY_alsa-utils-amixer       = "Command-line control for ALSA mixer and settings"
SUMMARY_alsa-utils-alsamixer    = "ncurses-based control for ALSA mixer and settings"
SUMMARY_alsa-utils-speakertest  = "ALSA surround speaker test utility"
SUMMARY_alsa-utils-midi         = "Miscellaneous MIDI utilities for ALSA"
SUMMARY_alsa-utils-aconnect     = "ALSA sequencer connection manager"
SUMMARY_alsa-utils-aseqnet      = "Network client/server for ALSA sequencer"
SUMMARY_alsa-utils-iecset       = "ALSA utility for setting/showing IEC958 (S/PDIF) status bits"
SUMMARY_alsa-utils-alsactl      = "Saves/restores ALSA-settings in /etc/asound.state"
SUMMARY_alsa-utils-aseqdump     = "Shows the events received at an ALSA sequencer port"
SUMMARY_alsa-utils-alsaloop     = "ALSA PCM loopback utility"
SUMMARY_alsa-utils-alsaucm      = "ALSA Use Case Manager"

RRECOMMENDS_alsa-utils-alsactl = "alsa-states"

ALLOW_EMPTY_alsa-utils = "1"

do_install() {
	autotools_do_install

	# We don't ship this here because it requires a dependency on bash.
	# See alsa-utils-scripts_${PV}.bb
	rm ${D}${sbindir}/alsaconf
	rm ${D}${sbindir}/alsa-info.sh

	if ${@bb.utils.contains('PACKAGECONFIG', 'udev', 'false', 'true', d)}; then
	   # This is where alsa-utils will install its rules if we don't tell it anything else.
	   rm -rf ${D}/lib/udev
	   rmdir --ignore-fail-on-non-empty ${D}/lib
	fi
}
