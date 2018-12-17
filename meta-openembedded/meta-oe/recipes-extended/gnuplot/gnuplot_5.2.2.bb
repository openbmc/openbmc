SUMMARY = "Gnuplot is a portable command-line driven graphing utility"
DESCRIPTION = "Gnuplot is a portable command-line driven interactive datafile \
(text or binary) and function plotting utility."
HOMEPAGE = "http://www.gnuplot.info/"
SECTION = "console/scientific"
LICENSE = "gnuplot"
LIC_FILES_CHKSUM = "file://Copyright;md5=243a186fc2fd3b992125d60d5b1bab8f"
DEPENDS = "${BPN}-native virtual/libx11 gd readline"

inherit autotools distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "${SOURCEFORGE_MIRROR}/gnuplot/${BP}.tar.gz;name=archive \
    http://www.mneuroth.de/privat/zaurus/qtplot-0.2.tar.gz;name=qtplot \
    file://gnuplot.desktop \
    file://gnuplot.png \
"
SRC_URI_append_class-target = " \
    file://0002-do-not-build-demos.patch \
    file://0003-Use-native-tools-to-build-docs.patch \
    file://0004-Add-configure-option-to-find-qt5-native-tools.patch \
"

SRC_URI[archive.md5sum] = "60aedd08998160593199459dea8467fe"
SRC_URI[archive.sha256sum] = "a416d22f02bdf3873ef82c5eb7f8e94146795811ef808e12b035ada88ef7b1a1"
SRC_URI[qtplot.md5sum] = "0a481885a496092c77eb4017540b5cf6"
SRC_URI[qtplot.sha256sum] = "6df317183ff62cc82f3dcf88207a267cd6478cb5147f55d7530c94f1ad5f4132"

# for building docs (they deserve it) we need *doc2* tools native
BBCLASSEXTEND = "native"
DEPENDS_class-native = "readline-native"
PACKAGECONFIG_class-native = ""

SRC_URI_append_class-native = "file://0001-reduce-build-to-conversion-tools-for-native-build.patch"

do_install_class-native() {
    install -d ${D}${bindir}
	install ${B}/docs/*doc* ${D}${bindir}
    rm ${D}${bindir}/*.o
}

PACKAGECONFIG ??= "cairo"
PACKAGECONFIG[cairo] = "--with-cairo,--without-cairo,cairo pango"
PACKAGECONFIG[lua] = "--with-lua,--without-lua,lua"
PACKAGECONFIG[qt5] = "--with-qt --with-qt5nativesysroot=${STAGING_DIR_NATIVE},--without-qt,qtbase-native qtbase qtsvg qttools-native"

EXTRA_OECONF = "--with-readline=${STAGING_LIBDIR}/.. \
                --without-tutorial \
                --disable-wxwidgets"

do_compile_prepend() {
    install -m 0644 ${WORKDIR}/qtplot-0.2/qtopia.trm ${S}/term/
}

do_install_append_class-target() {
    install -d ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/gnuplot.desktop ${D}${datadir}/applications/
    install -d ${D}${datadir}/pixmaps/
    install -m 0644 ${WORKDIR}/gnuplot.png ${D}${datadir}/pixmaps/
}

PACKAGES =+ "${PN}-x11"

RPROVIDES_${PN}-dbg += "${PN}-x11-dbg"

DESCRIPTION_${PN}-x11 = "X11 display terminal for Gnuplot."
SECTION_${PN}-x11 = "x11/scientific"
FILES_${PN}-x11 = "${libexecdir} ${datadir}/applications ${datadir}/pixmaps ${libdir}/X11 "

FILES_${PN} += "${datadir}/texmf"
