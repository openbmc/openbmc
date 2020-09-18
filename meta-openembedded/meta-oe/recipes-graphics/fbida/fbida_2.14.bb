SUMMARY = "Framebuffer image and doc viewer tools"
DESCRIPTION = "The fbida project contains a few applications for viewing and editing images, \
               with the main focus being photos."
HOMEPAGE = "http://linux.bytesex.org/fbida/"
AUTHOR = "Gerd Hoffmann"
SECTION = "utils"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=e8feb78a32950a909621bbb51f634b39"

DEPENDS = "virtual/libiconv jpeg fontconfig freetype libexif libdrm pixman poppler libepoxy cairo"

SRC_URI = "https://www.kraxel.org/releases/fbida/fbida-${PV}.tar.gz \
	   file://0001-Avoid-using-host-path.patch \
	   file://fix-preprocessor.patch \
           file://support-jpeg-turbo.patch \
           file://cairo-weak-detect.patch \
           file://fbida-gcc10.patch \
	   "
SRC_URI[sha256sum] = "95b7c01556cb6ef9819f358b314ddfeb8a4cbe862b521a3ed62f03d163154438"

inherit pkgconfig features_check

# Depends on libepoxy
REQUIRED_DISTRO_FEATURES = "opengl"

EXTRA_OEMAKE = "STRIP= 'srcdir=${S}' -f ${S}/GNUmakefile"

PACKAGECONFIG ??= "gif png curl"
PACKAGECONFIG[curl] = ",,curl"
PACKAGECONFIG[gif] = ",,giflib"
PACKAGECONFIG[png] = ",,libpng"
PACKAGECONFIG[tiff] = ",,tiff"
PACKAGECONFIG[motif] = ",,libx11 libxext libxpm libxt openmotif"
PACKAGECONFIG[webp] = ",,libwebp"
PACKAGECONFIG[lirc] = ",,lirc"
# This can only be enabled when cairo has egl enabled in its packageconfig support too
PACKAGECONFIG[egl] = ",,"

EXTRA_OEMAKE += ""${@bb.utils.contains('PACKAGECONFIG', 'egl', 'HAVE_CAIRO_GL=yes', 'HAVE_CAIRO_GL=no', d)}""

do_compile() {
    sed -i -e 's# fbgs# \$(srcdir)/fbgs#; s#-Ijpeg#-I\$(srcdir)/jpeg#; s# jpeg/# \$(srcdir)/jpeg/#' ${S}/GNUmakefile
    sed -i -e 's:/sbin/ldconfig:echo x:' ${S}/mk/Autoconf.mk
    sed -i -e 's: cpp: ${CPP}:' ${S}/GNUmakefile

    # Be sure to respect preferences (force to "no")
    # Also avoid issues when ${BUILD_ARCH} == ${HOST_ARCH}
    if [ -z "${@bb.utils.filter('PACKAGECONFIG', 'curl', d)}" ]; then
        sed -i -e '/^HAVE_LIBCURL/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.filter('PACKAGECONFIG', 'gif', d)}" ]; then
        sed -i -e '/^HAVE_LIBGIF/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.filter('PACKAGECONFIG', 'png', d)}" ]; then
        sed -i -e '/^HAVE_LIBPNG/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.filter('PACKAGECONFIG', 'tiff', d)}" ]; then
        sed -i -e '/^HAVE_LIBTIFF/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.filter('PACKAGECONFIG', 'motif', d)}" ]; then
        sed -i -e '/^HAVE_MOTIF/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.filter('PACKAGECONFIG', 'webp', d)}" ]; then
        sed -i -e '/^HAVE_LIBWEBP/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.filter('PACKAGECONFIG', 'lirc', d)}" ]; then
        sed -i -e '/^HAVE_LIBLIRC/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi

    oe_runmake
}

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

RDEPENDS_${PN} = "ttf-dejavu-sans-mono bash"
