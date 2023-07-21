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

DEPENDS = "tiff jpeg fontconfig cups libpng freetype zlib"

UPSTREAM_CHECK_URI = "https://github.com/ArtifexSoftware/ghostpdl-downloads/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)\.tar"

def gs_verdir(v):
    return "".join(v.split("."))


SRC_URI = "https://github.com/ArtifexSoftware/ghostpdl-downloads/releases/download/gs${@gs_verdir("${PV}")}/${BPN}-${PV}.tar.gz \
           file://ghostscript-9.16-Werror-return-type.patch \
           file://avoid-host-contamination.patch \
"

SRC_URI[sha256sum] = "a4cd61a07fec161bee35da0211a5e5cde8ff8a0aaf942fc0176715e499d21661"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gtk] = "--enable-gtk,--disable-gtk,gtk+3"
PACKAGECONFIG[libidn] = "--with-libidn,--without-libidn,libidn"
PACKAGECONFIG[libpaper] = "--with-libpaper,--without-libpaper,libpaper"
PACKAGECONFIG[x11] = "--with-x --x-includes=${STAGING_INCDIR} --x-libraries=${STAGING_LIBDIR}, \
                      --without-x, virtual/libx11 libxext libxt"

EXTRA_OECONF = "--with-jbig2dec \
                --with-fontpath=${datadir}/fonts \
                CUPSCONFIG="${STAGING_BINDIR_CROSS}/cups-config" \
                PKGCONFIG=pkg-config \
                "

EXTRA_OECONF:append:mipsarcho32 = " --with-large_color_index=0"

# Uses autoconf but not automake, can't do out-of-tree
inherit autotools-brokensep pkgconfig

# Prune the source tree of libraries that we're using our packaging of, so that
# ghostscript can't link to them. Can't prune zlib as that's needed for the
# native tools.
prune_sources() {
    rm -rf ${S}/jpeg/ ${S}/libpng/ ${S}/tiff/ ${S}/expat/ ${S}/freetype/ ${S}/cups/lib
}
do_unpack[postfuncs] += "prune_sources"

do_install:append () {
    mkdir -p ${D}${datadir}/ghostscript/${PV}/
    cp -r ${S}/Resource ${D}${datadir}/ghostscript/${PV}/
    cp -r ${S}/iccprofiles ${D}${datadir}/ghostscript/${PV}/
}

# ghostscript does not supports "arc"
COMPATIBLE_HOST = "^(?!arc).*"

# some entries in NVD uses gpl_ghostscript
CVE_PRODUCT = "ghostscript gpl_ghostscript"
