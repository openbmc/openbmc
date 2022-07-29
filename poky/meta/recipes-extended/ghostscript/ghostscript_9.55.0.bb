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

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f98ffa763e50cded76f49bce73aade16"

DEPENDS = "ghostscript-native tiff jpeg fontconfig cups libpng"
DEPENDS:class-native = "libpng-native"

UPSTREAM_CHECK_URI = "https://github.com/ArtifexSoftware/ghostpdl-downloads/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)\.tar"

# As of ghostscript 9.54.0 the jpeg issue in the CVE is present in the gs jpeg sources
# however we use an external jpeg which doesn't have the issue.
CVE_CHECK_IGNORE += "CVE-2013-6629"

def gs_verdir(v):
    return "".join(v.split("."))


SRC_URI_BASE = "https://github.com/ArtifexSoftware/ghostpdl-downloads/releases/download/gs${@gs_verdir("${PV}")}/${BPN}-${PV}.tar.gz \
                file://ghostscript-9.15-parallel-make.patch \
                file://ghostscript-9.16-Werror-return-type.patch \
                file://do-not-check-local-libpng-source.patch \
                file://avoid-host-contamination.patch \
                file://mkdir-p.patch \
                file://CVE-2022-2085.patch \
"

SRC_URI = "${SRC_URI_BASE} \
           file://ghostscript-9.21-prevent_recompiling.patch \
           file://cups-no-gcrypt.patch \
           "

SRC_URI:class-native = "${SRC_URI_BASE} \
                        file://ghostscript-9.21-native-fix-disable-system-libtiff.patch \
                        file://base-genht.c-add-a-preprocessor-define-to-allow-fope.patch \
                        "

SRC_URI[sha256sum] = "31e2064be67e15b478a8da007d96d6cd4d2bee253e5be220703a225f7f79a70b"

# Put something like
#
#   PACKAGECONFIG:append:pn-ghostscript = " x11"
#
# in local.conf to enable building with X11.  Be careful.  The order
# of the overrides matters!
#
#PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG:class-native = ""

PACKAGECONFIG[x11] = "--with-x --x-includes=${STAGING_INCDIR} --x-libraries=${STAGING_LIBDIR}, \
                      --without-x, virtual/libx11 libxext libxt gtk+3\
                      "

EXTRA_OECONF = "--without-libpaper --with-system-libtiff --with-jbig2dec \
                --with-fontpath=${datadir}/fonts \
                --without-libidn --with-cups-serverbin=${exec_prefix}/lib/cups \
                --with-cups-datadir=${datadir}/cups \
                CUPSCONFIG="${STAGING_BINDIR_CROSS}/cups-config" \
                "

EXTRA_OECONF:append:mipsarcho32 = " --with-large_color_index=0"

# Explicity disable libtiff, fontconfig,
# freetype, cups for ghostscript-native
EXTRA_OECONF:class-native = "--without-x --with-system-libtiff=no \
                             --without-jbig2dec --without-libpaper \
                             --with-fontpath=${datadir}/fonts \
                             --without-libidn --disable-fontconfig \
                             --enable-freetype --disable-cups "

# This has been fixed upstream but for now we need to subvert the check for time.h
# http://bugs.ghostscript.com/show_bug.cgi?id=692443
# http://bugs.ghostscript.com/show_bug.cgi?id=692426
CFLAGS += "-DHAVE_SYS_TIME_H=1"
BUILD_CFLAGS += "-DHAVE_SYS_TIME_H=1"

inherit autotools-brokensep

do_configure:prepend:class-target () {
        rm -rf ${S}/jpeg/
}

do_configure:append () {
	# copy tools from the native ghostscript build
	if [ "${PN}" != "ghostscript-native" ]; then
		mkdir -p obj/aux soobj
		for i in genarch genconf mkromfs echogs gendev genht packps; do
			cp ${STAGING_BINDIR_NATIVE}/ghostscript-${PV}/$i obj/aux/$i
		done
	fi
}

do_install:append () {
    mkdir -p ${D}${datadir}/ghostscript/${PV}/
    cp -r ${S}/Resource ${D}${datadir}/ghostscript/${PV}/
    cp -r ${S}/iccprofiles ${D}${datadir}/ghostscript/${PV}/
}

do_compile:class-native () {
    mkdir -p obj
    for i in genarch genconf mkromfs echogs gendev genht packps; do
        oe_runmake obj/aux/$i
    done
}

do_install:class-native () {
    install -d ${D}${bindir}/ghostscript-${PV}
    for i in genarch genconf mkromfs echogs gendev genht packps; do
        install -m 755 obj/aux/$i ${D}${bindir}/ghostscript-${PV}/$i
    done
}

BBCLASSEXTEND = "native"

# ghostscript does not supports "arc"
COMPATIBLE_HOST = "^(?!arc).*"

# some entries in NVD uses gpl_ghostscript
CVE_PRODUCT = "ghostscript gpl_ghostscript"
