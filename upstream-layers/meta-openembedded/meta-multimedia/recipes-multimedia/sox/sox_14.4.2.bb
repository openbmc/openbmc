DESCRIPTION = "SoX is the Swiss Army knife of sound processing tools. \
It converts audio files among various standard audio file formats \
and can apply different effects and filters to the audio data."
HOMEPAGE = "http://sox.sourceforge.net"
SECTION = "audio"

DEPENDS = "autoconf-archive-native libpng libsndfile1 libtool"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa pulseaudio', d)} \
                   magic \
"
PACKAGECONFIG[pulseaudio] = "--enable-pulseaudio=dyn,--disable-pulseaudio,pulseaudio,"
PACKAGECONFIG[alsa] = "--enable-alsa=dyn,--disable-alsa,alsa-lib,"
PACKAGECONFIG[wavpack] = "--enable-wavpack=dyn,--disable-wavpack,wavpack,"
PACKAGECONFIG[flac] = "--enable-flac=dyn,--disable-flac,flac,"
PACKAGECONFIG[amrwb] = "--enable-amrwb=dyn,--disable-amrwb,opencore-amr,"
PACKAGECONFIG[amrnb] = "--enable-amrnb=dyn,--disable-amrnb,opencore-amr,"
PACKAGECONFIG[oggvorbis] = "--enable-oggvorbis=dyn,--disable-oggvorbis,libvorbis"
PACKAGECONFIG[opus] = "--enable-opus=dyn,--disable-opus,opusfile"
PACKAGECONFIG[magic] = "--with-magic,--without-magic,file,"
PACKAGECONFIG[mad] = "--with-mad,--without-mad,libmad,"
PACKAGECONFIG[id3tag] = "--with-id3tag,--without-id3tag,libid3tag,"
PACKAGECONFIG[lame] = "--with-lame,--without-lame,lame,"
PACKAGECONFIG[ao] = "--enable-ao,--disable-ao,libao,"

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;md5=751419260aa954499f7abaabaa882bbe \
                    file://LICENSE.LGPL;md5=fbc093901857fcd118f065f900982c24"

SRC_URI = "git://git.code.sf.net/p/sox/code;protocol=https;branch=master \
           file://0001-remove-the-error-line-and-live-without-file-type-det.patch \
           file://CVE-2021-3643_CVE-2021-23210.patch \
           file://CVE-2021-23159_CVE-2021-2317.patch \
           file://CVE-2021-33844.patch \
           file://CVE-2021-40426.patch \
           file://CVE-2022-31650.patch \
           file://CVE-2022-31651.patch \
           file://CVE-2023-32627.patch \
           "

# last release was in 2015, use latest hash from 2024-05-30
PV .= "+git"
SRCREV = "f3094754a7c2a7e55c35621d20fa9945736e72df"

CVE_PRODUCT:append = " libsox_project:libsox sound_exchange_project:sound_exchange"

CVE_STATUS_GROUPS += "CVE_STATUS_HASH_UPDATE"
CVE_STATUS_HASH_UPDATE = " \
    CVE-2017-11332 CVE-2017-11358 CVE-2017-11359 CVE-2017-15370 CVE-2017-15371 \
    CVE-2017-15372 CVE-2017-15642 CVE-2017-18189 CVE-2019-13590 CVE-2019-8354 \
    CVE-2019-8355 CVE-2019-8356 CVE-2019-8357 CVE-2019-1010004 \
"
CVE_STATUS_HASH_UPDATE[status] = "fixed-version: patched in current git hash"

inherit autotools pkgconfig

# Enable largefile support
CFLAGS += "-D_FILE_OFFSET_BITS=64"

EXCLUDE_FROM_WORLD = "${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "0", "1", d)}"
