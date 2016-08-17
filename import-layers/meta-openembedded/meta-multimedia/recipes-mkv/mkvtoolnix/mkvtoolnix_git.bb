SUMMARY = "MKVToolNix -- Cross-platform tools for Matroska"
HOMEPAGE = "http://www.bunkus.org/videotools/mkvtoolnix/source.html"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "curl boost expat zlib libebml libmatroska libogg libvorbis bzip2 lzo file ruby-native"

PV = "8.4.0+git${SRCPV}"
SRCREV_mkvtoolnix = "7f63ea48ee474754a95838f37aba8f6118c94a65"
SRCREV_libebml = "04b34b0dbded40e0cec93cafa6a4f4c8e90c3206"
SRCREV_libmatroska = "db5d627b5bf48516c9e0b540254c0d36595760c3"
SRCREV_FORMAT = "mkvtoolnix"
SRC_URI = " \
           git://github.com/mbunkus/mkvtoolnix.git;name=mkvtoolnix \
           git://github.com/Matroska-Org/libebml.git;name=libebml;destsuffix=git/lib/libebml \
           git://github.com/Matroska-Org/libmatroska.git;name=libmatroska;destsuffix=git/lib/libmatroska \
          "

S = "${WORKDIR}/git"

inherit autotools-brokensep gettext

# make sure rb files are used from sysroot, not from host
# ruby-1.9.3-always-use-i386.patch is doing target_cpu=`echo $target_cpu | sed s/i.86/i386/`
# we need to replace it too (a bit longer version without importing re)
RUBY_SYS = "${@ '${BUILD_SYS}'.replace('i486', 'i386').replace('i586', 'i386').replace('i686', 'i386') }"
export RUBYLIB="${STAGING_DATADIR_NATIVE}/rubygems:${STAGING_LIBDIR_NATIVE}/ruby:${STAGING_LIBDIR_NATIVE}/ruby/${RUBY_SYS}"

PACKAGECONFIG ??= "flac ${@bb.utils.contains('BBFILE_COLLECTIONS', 'qt5-layer', 'qt5', '', d)}"
PACKAGECONFIG[flac] = "--with-flac,--without-flac,flac"
PACKAGECONFIG[qt5] = "--enable-qt --with-moc=${STAGING_BINDIR_NATIVE}/qt5/moc --with-uic=${STAGING_BINDIR_NATIVE}/qt5/uic --with-rcc=${STAGING_BINDIR_NATIVE}/qt5/rcc,--disable-qt,qtbase"

EXTRA_OECONF = " --with-boost-libdir=${STAGING_LIBDIR} \
"

FILES_${PN} += "${datadir}"

# remove some hardcoded searchpaths
do_configure_prepend() {
    sed -i -e s:/usr/local/lib:${STAGING_LIBDIR}:g -e s:/usr/local/include:${STAGING_INCDIR}:g ${S}/ac/qt5.m4
}

# Yeah, no makefile
do_compile() {
    LC_ALL="en_US.UTF-8" ${S}/drake ${PARALLEL_MAKE}
}

do_install() {
    LC_ALL="en_US.UTF-8" ${S}/drake install DESTDIR=${D}
}

# | In file included from src/common/utf8_codecvt_facet.cpp:22:0:
# | src/common/../../lib/boost/utf8_codecvt_facet/utf8_codecvt_facet.cpp:174:5: error: 'int mtx::utf8_codecvt_facet::do_length' is not a static data member of 'struct mtx::utf8_codecvt_facet'
# |      BOOST_CODECVT_DO_LENGTH_CONST std::mbstate_t &,
# |      ^
PNBLACKLIST[mkvtoolnix] ?= "BROKEN: Failx to build with gcc-5"
