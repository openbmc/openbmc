DESCRIPTION = "eSpeak is a compact open source software speech synthesizer"
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://License.txt;md5=cb7a20edb4c9f5f478de6523dcd7362c"

SRC_URI = "${SOURCEFORGE_MIRROR}/espeak/espeak-${PV}-source.zip"

S = "${WORKDIR}/espeak-${PV}-source"

DEPENDS = "portaudio-v19"
