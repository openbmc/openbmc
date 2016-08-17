DESCRIPTION = "Gnuplot is a portable command-line driven interactive datafile \
(text or binary) and function plotting utility."
HOMEPAGE = "http://www.gnuplot.info/"
SECTION = "console/scientific"
LICENSE = "gnuplot"
LIC_FILES_CHKSUM = "file://Copyright;md5=243a186fc2fd3b992125d60d5b1bab8f"
DEPENDS = "virtual/libx11 gd readline"

inherit autotools distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

acpaths = ""

PACKAGECONFIG ??= "cairo"
PACKAGECONFIG[cairo] = "--with-cairo,--without-cairo,cairo pango"
PACKAGECONFIG[lua] = "--with-lua,--without-lua,lua"

EXTRA_OECONF = "--with-readline=${STAGING_LIBDIR}/.. \
                --without-lisp-files \
                --without-tutorial \
                --disable-wxwidgets"

do_compile_prepend() {
    install -m 0644 ${WORKDIR}/qtplot-0.2/qtopia.trm ${S}/term/
}

do_install_append() {
    install -d ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/gnuplot.desktop ${D}${datadir}/applications/
    install -d ${D}${datadir}/pixmaps/
    install -m 0644 ${WORKDIR}/gnuplot.png ${D}${datadir}/pixmaps/
}

PACKAGES =+ "${PN}-x11-dbg ${PN}-x11"
DESCRIPTION_${PN}-x11 = "X11 display terminal for Gnuplot."
SECTION_${PN}-x11 = "x11/scientific"
FILES_${PN}-x11 = "${libexecdir} ${datadir}/applications ${datadir}/pixmaps ${libdir}/X11 "

FILES_${PN} += "${datadir}/texmf"
FILES_${PN}-x11-dbg += "${libexecdir}/gnuplot/*/.debug"
