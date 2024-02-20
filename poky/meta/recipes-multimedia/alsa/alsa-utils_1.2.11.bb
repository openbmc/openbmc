SUMMARY = "ALSA sound utilities"
DESCRIPTION = "collection of small and often extremely powerful applications \
designed to allow users to control the various parts of the ALSA system."
HOMEPAGE = "http://www.alsa-project.org"
BUGTRACKER = "http://alsa-project.org/main/index.php/Bug_Tracking"
SECTION = "console/utils"
# Some parts are GPL-2.0-or-later, some are GPL-2.0-only (e.g. axfer, alsactl)
# so result is GPL-2.0-only
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://alsactl/utils.c;beginline=3;endline=18;md5=96cc06a4cebe5eb7975688ffb0e65642"
DEPENDS = "alsa-lib ncurses libsamplerate0"

PACKAGECONFIG ??= "udev"

# alsabat can be built also without fftw support (with reduced functionality).
# It would be better to always enable alsabat, but provide an option for
# enabling/disabling fftw. The configure script doesn't support that, however
# (at least in any obvious way), so for now we only support alsabat with fftw
# or no alsabat at all.
PACKAGECONFIG[bat] = "--enable-bat,--disable-bat,fftwf"

PACKAGECONFIG[udev] = "--with-udev-rules-dir=`pkg-config --variable=udevdir udev`/rules.d,--with-udev-rules-dir=/unwanted/rules.d,udev"
PACKAGECONFIG[manpages] = "--enable-xmlto, --disable-xmlto, xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"

# alsa-utils specified in SRC_URI due to alsa-utils-scripts recipe
SRC_URI = "https://www.alsa-project.org/files/pub/utils/alsa-utils-${PV}.tar.bz2 \
           "
SRC_URI[sha256sum] = "9ac6ca3a883f151e568dcf979b8d2e5cbecc51b819bb0e6bb8a2e9b34cc428a7"

# On build machines with python-docutils (not python3-docutils !!) installed
# rst2man (not rst2man.py) is detected and compile fails with
# | make[1]: *** No rule to make target 'alsaucm.1', needed by 'all-am'.  Stop.
# Avoid this by disabling expicitly
EXTRA_OECONF = "--disable-rst2man"

inherit autotools gettext pkgconfig manpages

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
             alsa-utils-scripts \
             alsa-utils-nhltdmicinfo \
            "

PACKAGES += "${ALSA_UTILS_PKGS}"
RDEPENDS:${PN} += "${ALSA_UTILS_PKGS}"

FILES:${PN} = ""
ALLOW_EMPTY:alsa-utils = "1"
FILES:alsa-utils-alsabat     = "${bindir}/alsabat"
FILES:alsa-utils-alsatplg    = "${bindir}/alsatplg ${libdir}/alsa-topology"
FILES:alsa-utils-aplay       = "${bindir}/aplay ${bindir}/arecord ${bindir}/axfer"
FILES:alsa-utils-amixer      = "${bindir}/amixer"
FILES:alsa-utils-alsamixer   = "${bindir}/alsamixer"
FILES:alsa-utils-speakertest = "${bindir}/speaker-test ${datadir}/sounds/alsa/ ${datadir}/alsa/speaker-test/"
FILES:alsa-utils-midi        = "${bindir}/aplaymidi ${bindir}/arecordmidi ${bindir}/amidi"
FILES:alsa-utils-aconnect    = "${bindir}/aconnect"
FILES:alsa-utils-aseqnet     = "${bindir}/aseqnet"
FILES:alsa-utils-iecset      = "${bindir}/iecset"
FILES:alsa-utils-alsactl     = "${sbindir}/alsactl */udev/rules.d/90-alsa-restore.rules */*/udev/rules.d/90-alsa-restore.rules ${systemd_unitdir} ${localstatedir}/lib/alsa ${datadir}/alsa/init/"
FILES:alsa-utils-aseqdump    = "${bindir}/aseqdump"
FILES:alsa-utils-alsaloop    = "${bindir}/alsaloop"
FILES:alsa-utils-alsaucm     = "${bindir}/alsaucm */udev/rules.d/89-alsa-ucm.rules */*/udev/rules.d/89-alsa-ucm.rules"
FILES:alsa-utils-scripts     = "${sbindir}/alsaconf \
               ${sbindir}/alsa-info.sh \
               ${sbindir}/alsabat-test.sh \
              "
FILES:alsa-utils-nhltdmicinfo = "${bindir}/nhlt-dmic-info"

SUMMARY:alsa-utils-alsabat      = "Command-line sound tester for ALSA sound card driver"
SUMMARY:alsa-utils-alsatplg     = "Converts topology text files into binary format for kernel"
SUMMARY:alsa-utils-aplay        = "Play (and record) sound files using ALSA"
SUMMARY:alsa-utils-amixer       = "Command-line control for ALSA mixer and settings"
SUMMARY:alsa-utils-alsamixer    = "ncurses-based control for ALSA mixer and settings"
SUMMARY:alsa-utils-speakertest  = "ALSA surround speaker test utility"
SUMMARY:alsa-utils-midi         = "Miscellaneous MIDI utilities for ALSA"
SUMMARY:alsa-utils-aconnect     = "ALSA sequencer connection manager"
SUMMARY:alsa-utils-aseqnet      = "Network client/server for ALSA sequencer"
SUMMARY:alsa-utils-iecset       = "ALSA utility for setting/showing IEC958 (S/PDIF) status bits"
SUMMARY:alsa-utils-alsactl      = "Saves/restores ALSA-settings in /etc/asound.state"
SUMMARY:alsa-utils-aseqdump     = "Shows the events received at an ALSA sequencer port"
SUMMARY:alsa-utils-alsaloop     = "ALSA PCM loopback utility"
SUMMARY:alsa-utils-alsaucm      = "ALSA Use Case Manager"
SUMMARY:alsa-utils-scripts      = "Shell scripts that show help info and create ALSA configuration files"
SUMMARY:alsa-utils-nhltdmicinfo = "Dumps microphone array information from ACPI NHLT table"

RRECOMMENDS:alsa-utils-alsactl = "alsa-states"

do_install() {
	autotools_do_install

	install -d ${D}${sbindir}
	install -m 0755 ${B}/alsaconf/alsaconf ${D}${sbindir}/
	install -m 0755 ${S}/alsa-info/alsa-info.sh ${D}${sbindir}/
	if ${@bb.utils.contains('PACKAGECONFIG', 'bat', 'true', 'false', d)}; then
		install -m 0755 ${S}/bat/alsabat-test.sh ${D}${sbindir}/
	fi

	# If udev is disabled, we told configure to install the rules
	# in /unwanted, so we can remove them now. If udev is enabled,
	# then /unwanted won't exist and this will have no effect.
	rm -rf ${D}/unwanted
}


PROVIDES = "alsa-utils-alsaconf alsa-utils-scripts"

RDEPENDS:${PN}-scripts += "bash"
