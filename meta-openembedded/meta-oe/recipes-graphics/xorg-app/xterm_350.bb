require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "xterm is the standard terminal emulator for the X Window System"
DEPENDS = "libxaw xorgproto libxext libxau libxinerama libxpm ncurses"

LIC_FILES_CHKSUM = "file://xterm.h;beginline=3;endline=31;md5=c7faceb872d90115e7c0ad90e90c390d"

SRC_URI = "http://invisible-mirror.net/archives/${BPN}/${BP}.tgz"

SRC_URI[md5sum] = "189b593841189da66bc1cea92df602a5"
SRC_URI[sha256sum] = "aefb59eefd310268080d1a90a447368fb97a9a6737bfecfc3800bf6cc304104d"
PACKAGECONFIG ?= ""
PACKAGECONFIG[xft] = "--enable-freetype,--disable-freetype,libxft fontconfig freetype-native"

EXTRA_OECONF = " --x-includes=${STAGING_INCDIR} \
                 --x-libraries=${STAGING_LIBDIR} \
                 FREETYPE_CONFIG=${STAGING_BINDIR_CROSS}/freetype-config \
                 --disable-imake \
                 --disable-rpath-hack \
                 --disable-setuid \
                 --with-app-defaults=${datadir}/X11/app-defaults \
                 "

B = "${S}"

do_configure() {
    gnu-configize --force
    sed -e "s%/usr/contrib/X11R6%${STAGING_LIBDIR}%g" -i configure
    oe_runconf
}

# busybox can supply resize too
inherit update-alternatives

ALTERNATIVE_${PN} = "resize"
