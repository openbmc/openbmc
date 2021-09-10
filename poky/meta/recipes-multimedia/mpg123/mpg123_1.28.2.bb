SUMMARY = "Audio decoder for MPEG-1 Layer 1/2/3"
DESCRIPTION = "The core of mpg123 is an MPEG-1 Layer 1/2/3 decoding library, which can be used by other programs. \
mpg123 also comes with a command-line tool which can playback using ALSA, PulseAudio, OSS, and several other APIs, \
and also can write the decoded audio to WAV."
HOMEPAGE = "http://mpg123.de/"
BUGTRACKER = "http://sourceforge.net/p/mpg123/bugs/"
SECTION = "multimedia"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=e7b9c15fcfb986abb4cc5e8400a24169"

SRC_URI = "https://www.mpg123.de/download/${BP}.tar.bz2"
SRC_URI[sha256sum] = "7eefd4b68fdac7e138d04c37efe12155a8ebf25a5bccf0fb7e775af22d21db00"

UPSTREAM_CHECK_REGEX = "mpg123-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools pkgconfig

# The options should be mutually exclusive for configuration script.
# If both alsa and pulseaudio are specified (as in the default distro features)
# pulseaudio takes precedence.
PACKAGECONFIG_ALSA = "${@bb.utils.filter('DISTRO_FEATURES', 'alsa', d)}"
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '${PACKAGECONFIG_ALSA}', d)}"

PACKAGECONFIG[alsa] = "--with-default-audio=alsa,,alsa-lib"
PACKAGECONFIG[esd] = ",,esound"
PACKAGECONFIG[jack] = ",,jack"
PACKAGECONFIG[openal] = ",,openal-soft"
PACKAGECONFIG[portaudio] = ",,portaudio-v19"
PACKAGECONFIG[pulseaudio] = "--with-default-audio=pulse,,pulseaudio"
PACKAGECONFIG[sdl] = ",,libsdl2"

# Following are possible sound output modules:
# alsa arts coreaudio dummy esd jack nas openal os2 oss portaudio pulse sdl sndio sun tinyalsa win32 win32_wasapi
AUDIOMODS += "${@bb.utils.filter('PACKAGECONFIG', 'alsa esd jack openal portaudio sdl', d)}"
AUDIOMODS += "${@bb.utils.contains('PACKAGECONFIG', 'pulseaudio', 'pulse', '', d)}"

EXTRA_OECONF = " \
    --enable-shared \
    --with-audio='${AUDIOMODS}' \
    ${@bb.utils.contains('TUNE_FEATURES', 'neon', '--with-cpu=neon', '', d)} \
    ${@bb.utils.contains('TUNE_FEATURES', 'altivec', '--with-cpu=altivec', '', d)} \
    ${@bb.utils.contains('TARGET_FPU', 'soft', '--with-cpu=generic_nofpu', '', d)} \
"
# Fails to build with thumb-1 (qemuarm)
#| {standard input}: Assembler messages:
#| {standard input}:47: Error: selected processor does not support Thumb mode `smull r5,r6,r7,r4'
#| {standard input}:48: Error: shifts in CMP/MOV instructions are only supported in unified syntax -- `mov r5,r5,lsr#24'
#...
#| make[3]: *** [equalizer.lo] Error 1
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
