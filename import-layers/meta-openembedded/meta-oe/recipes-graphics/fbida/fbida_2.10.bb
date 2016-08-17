SUMMARY = "Framebuffer image and doc viewer tools"
DESCRIPTION = "The fbida project contains a few applications for viewing and editing images, \
               with the main focus being photos."
HOMEPAGE = "http://linux.bytesex.org/fbida/"
AUTHOR = "Gerd Hoffmann"
SECTION = "utils"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

DEPENDS = "virtual/libiconv jpeg fontconfig freetype libexif"

SRC_URI = "https://www.kraxel.org/releases/fbida/fbida-${PV}.tar.gz"
SRC_URI[md5sum] = "09460b964b58c2e39b665498eca29018"
SRC_URI[sha256sum] = "7a5a3aac61b40a6a2bbf716d270a46e2f8e8d5c97e314e927d41398a4d0b6cb6"

B = "${WORKDIR}/build"

EXTRA_OEMAKE = "STRIP= 'srcdir=${S}' -f ${S}/GNUmakefile"

PACKAGECONFIG ??= "gif png curl"
PACKAGECONFIG[curl] = ",,curl"
PACKAGECONFIG[gif] = ",,giflib"
PACKAGECONFIG[png] = ",,libpng"
PACKAGECONFIG[tiff] = ",,tiff"
PACKAGECONFIG[motif] = ",,libx11 libxext libxpm libxt openmotif"
PACKAGECONFIG[webp] = ",,libwebp"
PACKAGECONFIG[lirc] = ",,lirc"

do_compile() {
    sed -i -e 's# fbgs# \$(srcdir)/fbgs#; s#-Ijpeg#-I\$(srcdir)/jpeg#; s# jpeg/# \$(srcdir)/jpeg/#' ${S}/GNUmakefile
    sed -i -e 's:/sbin/ldconfig:echo x:' ${S}/mk/Autoconf.mk
    sed -i -e 's: cpp: ${TARGET_PREFIX}cpp -I${STAGING_INCDIR}:' ${S}/GNUmakefile

    # Be sure to respect preferences (force to "no")
    # Also avoid issues when ${BUILD_ARCH} == ${HOST_ARCH}
    if [ -z "${@bb.utils.contains('PACKAGECONFIG', 'curl', 'curl', '', d)}" ]; then
        sed -i -e '/^HAVE_LIBCURL/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.contains('PACKAGECONFIG', 'gif', 'gif', '', d)}" ]; then
        sed -i -e '/^HAVE_LIBGIF/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.contains('PACKAGECONFIG', 'png', 'png', '', d)}" ]; then
        sed -i -e '/^HAVE_LIBPNG/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.contains('PACKAGECONFIG', 'tiff', 'tiff', '', d)}" ]; then
        sed -i -e '/^HAVE_LIBTIFF/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.contains('PACKAGECONFIG', 'motif', 'motif', '', d)}" ]; then
        sed -i -e '/^HAVE_MOTIF/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.contains('PACKAGECONFIG', 'webp', 'webp', '', d)}" ]; then
        sed -i -e '/^HAVE_LIBWEBP/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi
    if [ -z "${@bb.utils.contains('PACKAGECONFIG', 'lirc', 'lirc', '', d)}" ]; then
        sed -i -e '/^HAVE_LIBLIRC/s/:=.*$/:= no/' ${S}/GNUmakefile
    fi

    oe_runmake
}

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

RDEPENDS_${PN} = "ttf-dejavu-sans-mono bash"
