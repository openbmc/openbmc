SUMMARY = "Qt-based Multimedia Player"
DESCRIPTION = "Qmmp is an audio player, written with the help of the Qt library. The user interface is similar to Winamp or XMMS."
HOMEPAGE = "http://qmmp.ylsoftware.com"
LICENSE = "GPLv2"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SECTION = "multimedia"

DEPENDS = "taglib libmad libvorbis libogg alsa-lib libsndfile1 libsamplerate0 curl"

SRC_URI = "http://qmmp.ylsoftware.com/files/${BP}.tar.bz2 \
           file://no-host-paths.patch \
          "

SRC_URI[md5sum] = "67e96e89c61b7432e5a12f555db702b1"
SRC_URI[sha256sum] = "0daea3f0a0df7b34089513c2c57413c82816ab8892bddabf49c18a7956255449"
inherit cmake qt4x11

QMMP_PLUGIN_OPTIONS ??= "\
                        -DUSE_MMS:BOOL=FALSE \
                        -DUSE_MPC:BOOL=FALSE \
                        -DUSE_MODPLUG:BOOL=FALSE \
                        -DUSE_WAVPACK:BOOL=FALSE \
                        -DUSE_FFMPEG:BOOL=FALSE \
                        -DUSE_AAC:BOOL=FALSE \
                        -DUSE_CDA:BOOL=FALSE \
                        -DUSE_MIDI:BOOL=FALSE \
                        -DUSE_GME:BOOL=FALSE \
                        -DUSE_OSS4:BOOL=FALSE \
                        -DUSE_JACK:BOOL=FALSE \
                        -DUSE_BS2B:BOOL=FALSE \
                        -DUSE_PROJECTM:BOOL=FALSE \
                        -DUSE_ENCA:BOOL=FALSE \
                        "

export EXTRA_OECMAKE = "-DQT_QMAKE_EXECUTABLE=${OE_QMAKE_QMAKE} \
                        -DQT_LRELEASE_EXECUTABLE=${OE_QMAKE_LRELEASE} \
                        -DQT_MOC_EXECUTABLE=${OE_QMAKE_MOC} \
                        -DQT_UIC_EXECUTABLE=${OE_QMAKE_UIC} \
                        -DQT_RCC_EXECUTABLE=${OE_QMAKE_RCC} \
                        -DQT_LIBRARY_DIR=${OE_QMAKE_LIBDIR_QT} \
                        -DQT_HEADERS_DIR=${OE_QMAKE_INCDIR_QT} \
                        -DQT_QTCORE_INCLUDE_DIR=${OE_QMAKE_INCDIR_QT}/QtCore \
                        ${QMMP_PLUGIN_OPTIONS} \
                        "

do_configure() {
	# Ensure we get the cmake configure and not qmake
	cmake_do_configure
}

PACKAGES_DYNAMIC += "^qmmp-plugin-.* "

python populate_packages_prepend () {
    qmmp_libdir = d.expand('${libdir}/qmmp')
    gd = d.expand('${D}/${libdir}/qmmp')
    plug_dirs = os.listdir(gd)

    for plug_dir in plug_dirs:
        g_plug_dir = os.path.join(qmmp_libdir,plug_dir)
        do_split_packages(d, g_plug_dir, '^lib(.*)\.so$', 'qmmp-plugin-' + plug_dir.lower() + '-%s', 'Qmmp ' + plug_dir  + ' plugin for %s')
}

FILES_${PN} = "\
                ${bindir}/qmmp \
                ${libdir}/lib*${SOLIBS} \
                ${datadir}/icons/* \
                ${datadir}/qmmp/images/* \
                ${datadir}/applications/* \
                "

FILES_${PN}-dbg += "\
                ${libdir}/qmmp/*/.debug/* \
               "

RDEPENDS_${PN} += "taglib alsa-lib libmad curl libicui18n"
