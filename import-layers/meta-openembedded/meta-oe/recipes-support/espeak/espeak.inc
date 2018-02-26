DESCRIPTION = "eSpeak is a compact open source software speech synthesizer"
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://License.txt;md5=cb7a20edb4c9f5f478de6523dcd7362c"

SRC_URI = "http://downloads.sourceforge.net/espeak/espeak-1.48.04-source.zip \
           file://gcc-narrowing-warning-fix.patch \
"
SRC_URI[md5sum] = "cadd7482eaafe9239546bdc09fa244c3"
SRC_URI[sha256sum] = "bf9a17673adffcc28ff7ea18764f06136547e97bbd9edf2ec612f09b207f0659"

S = "${WORKDIR}/espeak-${PV}-source"

DEPENDS = "portaudio-v19"
