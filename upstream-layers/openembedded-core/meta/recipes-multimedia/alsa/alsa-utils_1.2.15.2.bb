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
DEPENDS = "alsa-lib ncurses libsamplerate0 bash"

# Only needed as the dynamic packaging was altered, remove on upgrade
PR = "r2"

# alsa-utils specified in SRC_URI due to alsa-utils-scripts recipe
SRC_URI = "https://www.alsa-project.org/files/pub/utils/alsa-utils-${PV}.tar.bz2"
SRC_URI[sha256sum] = "7aaaafbfb01942113ec0c31e51f705910e81079205088ca2f8f137a3869e1a3a"

inherit autotools gettext pkgconfig manpages

PACKAGECONFIG ??= "udev"
# alsabat can be built also without fftw support (with reduced functionality).
# It would be better to always enable alsabat, but provide an option for
# enabling/disabling fftw. The configure script doesn't support that, however
# (at least in any obvious way), so for now we only support alsabat with fftw
# or no alsabat at all.
PACKAGECONFIG[bat] = "--enable-bat,--disable-bat,fftwf"
PACKAGECONFIG[udev] = ",--with-udev-rules-dir=/unwanted/rules.d,udev"
PACKAGECONFIG[manpages] = "--enable-rst2man --enable-xmlto, --disable-rst2man --disable-xmlto, python3-docutils-native xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"

# alsa-utils is an empty meta-package
FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"

FILES:${PN}-alsabat      = "${sbindir}/alsabat-test.sh"
FILES:${PN}-alsactl      = "*/udev/rules.d/90-alsa-restore.rules */*/udev/rules.d/90-alsa-restore.rules ${systemd_unitdir} ${localstatedir}/lib/alsa ${datadir}/alsa/init/"
FILES:${PN}-alsatplg     = "${libdir}/alsa-topology"
FILES:${PN}-amidi        = "${bindir}/amidi ${bindir}/aplaymidi* ${bindir}/arecordmidi*"
FILES:${PN}-aplay        = "${bindir}/aplay ${bindir}/arecord"
FILES:${PN}-speaker-test = "${datadir}/sounds/alsa/"

SUMMARY:${PN}-aconnect       = "ALSA sequencer connection manager"
SUMMARY:${PN}-alsabat        = "Command-line sound tester for ALSA sound card driver"
SUMMARY:${PN}-alsaconf       = "ALSA driver configurator script"
SUMMARY:${PN}-alsactl        = "Saves/restores ALSA-settings in /etc/asound.state"
SUMMARY:${PN}-alsa-info      = "Gather information about ALSA subsystem"
SUMMARY:${PN}-alsaloop       = "ALSA PCM loopback utility"
SUMMARY:${PN}-alsamixer      = "ncurses-based control for ALSA mixer and settings"
SUMMARY:${PN}-alsatplg       = "Converts topology text files into binary format for kernel"
SUMMARY:${PN}-alsaucm        = "ALSA Use Case Manager"
SUMMARY:${PN}-amidi          = "Miscellaneous MIDI utilities for ALSA"
SUMMARY:${PN}-amixer         = "Command-line control for ALSA mixer and settings"
SUMMARY:${PN}-aplay          = "Play (and record) sound files using ALSA"
SUMMARY:${PN}-aseqdump       = "Shows the events received at an ALSA sequencer port"
SUMMARY:${PN}-aseqnet        = "Network client/server for ALSA sequencer"
SUMMARY:${PN}-aseqsend       = "Send arbitrary messages to ALSA seqencer port"
SUMMARY:${PN}-axfer          = "Transfer audio data frames"
SUMMARY:${PN}-iecset         = "ALSA utility for setting/showing IEC958 (S/PDIF) status bits"
SUMMARY:${PN}-nhlt-dmic-info = "Dumps microphone array information from ACPI NHLT table"
SUMMARY:${PN}-speaker-test   = "ALSA surround speaker test utility"

RRECOMMENDS:${PN}-alsactl = "alsa-states"

RPROVIDES:${PN}-alsabat += "${PN}-alsabat-test"
RPROVIDES:${PN}-aplay += "${PN}-arecord"
RPROVIDES:${PN}-amidi += "${PN}-aplaymidi ${PN}-aplaymidi2 ${PN}-arecordmidi ${PN}-arecordmidi2"

do_install:append() {
	# If udev is disabled, we told configure to install the rules
	# in /unwanted, so we can remove them now. If udev is enabled,
	# then /unwanted won't exist and this will have no effect.
	rm -rf ${D}/unwanted
}

python populate_packages:prepend() {
    pn = d.getVar("PN")
    packages = do_split_packages(d, d.getVar("bindir"), r"^([^.]+).*$", pn + "-%s", "alsa-utils tool %s", extra_depends="")
    packages += do_split_packages(d, d.getVar("sbindir"), r"^([^.]+).*$", pn + "-%s", "alsa-utils tool %s", extra_depends="")
    d.setVar("RDEPENDS:" + pn, " ".join(packages))
}

PACKAGES_DYNAMIC = "^${PN}-.*"

RDEPENDS:${PN}-alsa-info += "bash"
RDEPENDS:${PN}-alsabat += "bash"
RDEPENDS:${PN}-alsaconf += "bash"
