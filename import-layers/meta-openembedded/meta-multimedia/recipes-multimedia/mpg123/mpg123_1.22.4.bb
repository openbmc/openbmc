SUMMARY = "Audio decoder for MPEG-1 Layer 1/2/3"
DESCRIPTION = "The core of mpg123 is an MPEG-1 Layer 1/2/3 decoding library, which can be used by other programs. \
mpg123 also comes with a command-line tool which can playback using ALSA, PulseAudio, OSS, and several other APIs, \
and also can write the decoded audio to WAV."
HOMEPAGE = "http://mpg123.de/"
BUGTRACKER = "http://sourceforge.net/p/mpg123/bugs/"
SECTION = "multimedia"

LICENSE = "LGPLv2.1"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=1e86753638d3cf2512528b99079bc4f3"

DEPENDS = "audiofile"

SRC_URI = "${SOURCEFORGE_MIRROR}/mpg123/mpg123/${PV}/${BP}.tar.bz2"

SRC_URI[md5sum] = "2dfafae3bbc532b4c8b04a77c6a6de89"
SRC_URI[sha256sum] = "5069e02e50138600f10cc5f7674e44e9bf6f1930af81d0e1d2f869b3c0ee40d2"

inherit autotools pkgconfig

# The options should be mutually exclusive for configuration script.
# If both alsa and pulseaudio are specified (as in the default distro features)
# pulseaudio takes precedence.
PACKAGECONFIG_ALSA = "${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa', '', d)}"
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '${PACKAGECONFIG_ALSA}', d)}"

PACKAGECONFIG[alsa] = "--with-default-audio=alsa,,alsa-lib"
PACKAGECONFIG[esd] = ",,esound"
PACKAGECONFIG[jack] = ",,jack"
PACKAGECONFIG[openal] = ",,openal-soft"
PACKAGECONFIG[portaudio] = ",,portaudio-v19"
PACKAGECONFIG[pulseaudio] = "--with-default-audio=pulse,,pulseaudio"
PACKAGECONFIG[sdl] = ",,libsdl"

# Following are possible sound output modules:
# alsa arts coreaudio dummy esd jack nas openal os2 oss portaudio pulse sdl sndio sun tinyalsa win32 win32_wasapi
AUDIOMODS += "${@bb.utils.contains('PACKAGECONFIG', 'alsa', 'alsa', '', d)}"
AUDIOMODS += "${@bb.utils.contains('PACKAGECONFIG', 'esd', 'esd', '', d)}"
AUDIOMODS += "${@bb.utils.contains('PACKAGECONFIG', 'jack', 'jack', '', d)}"
AUDIOMODS += "${@bb.utils.contains('PACKAGECONFIG', 'openal', 'openal', '', d)}"
AUDIOMODS += "${@bb.utils.contains('PACKAGECONFIG', 'portaudio', 'portaudio', '', d)}"
AUDIOMODS += "${@bb.utils.contains('PACKAGECONFIG', 'pulseaudio', 'pulse', '', d)}"
AUDIOMODS += "${@bb.utils.contains('PACKAGECONFIG', 'sdl', 'sdl', '', d)}"

EXTRA_OECONF = " \
    --enable-shared \
    --with-audio='${AUDIOMODS}' \
    --with-module-suffix=.so \
    ${@bb.utils.contains('TUNE_FEATURES', 'neon', '--with-cpu=neon', '', d)} \
    ${@bb.utils.contains('TUNE_FEATURES', 'altivec', '--with-cpu=altivec', '', d)} \
"

# The x86 assembler optimisations contains text relocations and there are no
# upstream plans to fix them: http://sourceforge.net/p/mpg123/bugs/168/
INSANE_SKIP_${PN}_append_x86 = " textrel"

# Fails to build with thumb-1 (qemuarm)
#| {standard input}: Assembler messages:
#| {standard input}:47: Error: selected processor does not support Thumb mode `smull r5,r6,r7,r4'
#| {standard input}:48: Error: shifts in CMP/MOV instructions are only supported in unified syntax -- `mov r5,r5,lsr#24'
#...
#| make[3]: *** [equalizer.lo] Error 1
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
