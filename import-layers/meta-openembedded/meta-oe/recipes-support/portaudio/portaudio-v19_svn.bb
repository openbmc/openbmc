SUMMARY = "A portable audio library"
SECTION = "libs/multimedia"
LICENSE = "PortAudio"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=26107732c2ab637c5710446fcfaf02df"

PV = "v19+svnr${SRCPV}"

SRCREV = "1387"
SRC_URI = "svn://subversion.assembla.com/svn/portaudio/portaudio;module=trunk;protocol=http"
S = "${WORKDIR}/trunk"

inherit autotools pkgconfig

PACKAGECONFIG ??= "alsa jack"
PACKAGECONFIG[alsa] = "--with-alsa, --without-alsa, alsa-lib,"
PACKAGECONFIG[jack] = "--with-jack, --without-jack, jack,"

EXTRA_OECONF = "--without-oss"

TESTS = "  pa_devs patest1      patest_hang patest_many                   patest_prime patest_sine patest_stop     patest_write_sine        \
pa_fuzz    patest_buffer        patest_in_overflow  patest_maxsines       patest_read_record  patest_sine8         patest_sync              \
pa_minlat  patest_callbackstop  patest_latency      patest_multi_sine     patest_record       patest_sine_formats  patest_toomanysines      \
paqa_devs  patest_clip          patest_leftright    patest_out_underflow  patest_ringmix      patest_sine_time     patest_underflow         \
paqa_errs  patest_dither        patest_longsine     patest_pink           patest_saw          patest_start_stop    patest_wire"

# DEFINES = PA_USE_OSS=1 HAVE_LIBPTHREAD=1
# DEFINES += PA_LITTLE_ENDIAN

# INCLUDEPATH = ../pa_common

PACKAGES += "portaudio-examples"
FILES_portaudio-examples = "${bindir}"
