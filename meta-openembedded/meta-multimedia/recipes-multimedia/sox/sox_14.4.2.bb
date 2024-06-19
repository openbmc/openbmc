DESCRIPTION="SoX is the Swiss Army knife of sound processing tools. \
It converts audio files among various standard audio file formats \
and can apply different effects and filters to the audio data."
HOMEPAGE = "http://sox.sourceforge.net"
SECTION = "audio"

DEPENDS = "libpng libsndfile1 libtool"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa pulseaudio', d)} \
                   magic \
"
PACKAGECONFIG[pulseaudio] = "--with-pulseaudio=dyn,--with-pulseaudio=no,pulseaudio,"
PACKAGECONFIG[alsa] = "--with-alsa=dyn,--with-alsa=no,alsa-lib,"
PACKAGECONFIG[wavpack] = "--with-wavpack=dyn,--with-wavpack=no,wavpack,"
PACKAGECONFIG[flac] = "--with-flac=dyn,--with-flac=no,flac,"
PACKAGECONFIG[amrwb] = "--with-amrwb=dyn,--with-amrwb=no,opencore-amr,"
PACKAGECONFIG[amrnb] = "--with-amrnb=dyn,--with-amrnb=no,opencore-amr,"
PACKAGECONFIG[oggvorbis] = "--with-oggvorbis=dyn,--with-oggvorbis=no,libvorbis"
PACKAGECONFIG[opus] = "--with-opus=dyn,--with-opus=no,opusfile"
PACKAGECONFIG[magic] = "--with-magic,--without-magic,file,"
PACKAGECONFIG[mad] = "--with-mad,--without-mad,libmad,"
PACKAGECONFIG[id3tag] = "--with-id3tag,--without-id3tag,libid3tag,"
PACKAGECONFIG[lame] = "--with-lame,--without-lame,lame,"
PACKAGECONFIG[ao] = "--with-ao,--without-ao,libao,"

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;md5=751419260aa954499f7abaabaa882bbe \
                    file://LICENSE.LGPL;md5=fbc093901857fcd118f065f900982c24"

SRC_URI = "${SOURCEFORGE_MIRROR}/sox/sox-${PV}.tar.gz \
           file://0001-remove-the-error-line-and-live-without-file-type-det.patch \
           file://0001-Update-exported-symbol-list.patch \
           file://0001-tests-Include-math.h-for-fabs-definition.patch \
           "
SRC_URI[md5sum] = "d04fba2d9245e661f245de0577f48a33"
SRC_URI[sha256sum] = "b45f598643ffbd8e363ff24d61166ccec4836fea6d3888881b8df53e3bb55f6c"

inherit autotools pkgconfig

# Enable largefile support
CFLAGS += "-D_FILE_OFFSET_BITS=64"

EXCLUDE_FROM_WORLD = "${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "0", "1", d)}"
