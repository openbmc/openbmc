SUMMARY = "The GPL Ghostscript PostScript/PDF interpreter"
DESCRIPTION = "Ghostscript is used for PostScript/PDF preview and printing.  Usually as \
a back-end to a program such as ghostview, it can display PostScript and PDF \
documents in an X11 environment. \
\
Furthermore, it can render PostScript and PDF files as graphics to be printed \
on non-PostScript printers. Supported printers include common \
dot-matrix, inkjet and laser models. \
"
HOMEPAGE = "http://www.ghostscript.com"
SECTION = "console/utils"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b17cea54743435ab2a581c237bea294a"

DEPENDS = "ghostscript-native tiff jpeg fontconfig cups libpng"
DEPENDS_class-native = "libpng-native"

UPSTREAM_CHECK_URI = "https://github.com/ArtifexSoftware/ghostpdl-downloads/releases"

SRC_URI_BASE = "https://github.com/ArtifexSoftware/ghostpdl-downloads/releases/download/gs919/${BPN}-${PV}.tar.gz \
                file://ghostscript-9.15-parallel-make.patch \
                file://ghostscript-9.16-Werror-return-type.patch \
                file://png_mak.patch \
                file://do-not-check-local-libpng-source.patch \
"

SRC_URI = "${SRC_URI_BASE} \
           file://ghostscript-9.02-prevent_recompiling.patch \
           file://ghostscript-9.02-genarch.patch \
           file://objarch.h \
           file://cups-no-gcrypt.patch \
           "

SRC_URI_class-native = "${SRC_URI_BASE} \
                        file://ghostscript-native-fix-disable-system-libtiff.patch \
                        file://base-genht.c-add-a-preprocessor-define-to-allow-fope.patch \
                        "

SRC_URI[md5sum] = "c9682ce6b852f9197c69905a43928907"
SRC_URI[sha256sum] = "cf3c0dce67db1557a87366969945f9c5235887989c0b585e037af366dc035989"

EXTRA_OECONF = "--without-x --with-system-libtiff --without-jbig2dec \
                --with-fontpath=${datadir}/fonts \
                --without-libidn --with-cups-serverbin=${exec_prefix}/lib/cups \
                --with-cups-datadir=${datadir}/cups \
                "

EXTRA_OECONF_append_mips = " --with-large_color_index=0"
EXTRA_OECONF_append_mipsel = " --with-large_color_index=0"

# Explicity disable libtiff, fontconfig,
# freetype, cups for ghostscript-native
EXTRA_OECONF_class-native = "--without-x --with-system-libtiff=no \
                             --without-jbig2dec \
                             --with-fontpath=${datadir}/fonts \
                             --without-libidn --disable-fontconfig \
                             --disable-freetype --disable-cups"

# This has been fixed upstream but for now we need to subvert the check for time.h
# http://bugs.ghostscript.com/show_bug.cgi?id=692443
# http://bugs.ghostscript.com/show_bug.cgi?id=692426
CFLAGS += "-DHAVE_SYS_TIME_H=1"
BUILD_CFLAGS += "-DHAVE_SYS_TIME_H=1"

inherit autotools

do_configure_prepend () {
	mkdir -p obj
	mkdir -p soobj
	if [ -e ${WORKDIR}/objarch.h ]; then
		cp ${WORKDIR}/objarch.h obj/arch.h
	fi
}

do_configure_append () {
	# copy tools from the native ghostscript build
	if [ "${PN}" != "ghostscript-native" ]; then
		mkdir -p obj/aux soobj
		for i in genarch genconf mkromfs echogs gendev genht; do
			cp ${STAGING_BINDIR_NATIVE}/ghostscript-${PV}/$i obj/aux/$i
		done
	fi
}

do_install_append () {
    mkdir -p ${D}${datadir}/ghostscript/${PV}/
    cp -r ${S}/Resource ${D}${datadir}/ghostscript/${PV}/
    cp -r ${S}/iccprofiles ${D}${datadir}/ghostscript/${PV}/
}

do_compile_class-native () {
    mkdir -p obj
    for i in genarch genconf mkromfs echogs gendev genht; do
        oe_runmake obj/aux/$i
    done
}

do_install_class-native () {
    install -d ${D}${bindir}/ghostscript-${PV}
    for i in genarch genconf mkromfs echogs gendev genht; do
        install -m 755 obj/aux/$i ${D}${bindir}/ghostscript-${PV}/$i
    done
}

BBCLASSEXTEND = "native"
