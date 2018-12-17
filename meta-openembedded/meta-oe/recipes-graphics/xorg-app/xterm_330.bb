require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "xterm is the standard terminal emulator for the X Window System"
DEPENDS = "libxaw xorgproto libxext libxau libxinerama libxpm ncurses"

LIC_FILES_CHKSUM = "file://xterm.h;beginline=3;endline=31;md5=f02ab9ebda87b770ecf711dbb3784a33"

SRC_URI = "http://invisible-mirror.net/archives/${BPN}/${BP}.tgz"

SRC_URI[md5sum] = "9a6db7974aa4ccbb50eabebf72618467"
SRC_URI[sha256sum] = "7aeef9f29f6b95e09f481173c8c3053357bf5ffe162585647f690fd1707556df"
PACKAGECONFIG ?= ""
PACKAGECONFIG[xft] = "--enable-freetype,--disable-freetype,libxft fontconfig freetype-native"

EXTRA_OECONF = " --x-includes=${STAGING_INCDIR} \
                 --x-libraries=${STAGING_LIBDIR} \
                 FREETYPE_CONFIG=${STAGING_BINDIR_CROSS}/freetype-config \
                 --disable-imake \
                 --disable-rpath-hack \
                 --disable-setuid"

B = "${S}"

do_configure() {
    gnu-configize --force
    sed -e "s%/usr/contrib/X11R6%${STAGING_LIBDIR}%g" -i configure
    oe_runconf
}

FILES_${PN} += "/usr/lib/X11"

# busybox can supply resize too
inherit update-alternatives

ALTERNATIVE_${PN} = "resize"
