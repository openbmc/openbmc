require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "xterm is the standard terminal emulator for the X Window System"
DEPENDS = "libxaw xorgproto libxext libxau libxinerama libxpm ncurses desktop-file-utils-native"

LIC_FILES_CHKSUM = "file://xterm.h;beginline=3;endline=31;md5=1a3032aa728f8fda9d686948940ca58d"

SRC_URI = "http://invisible-mirror.net/archives/${BPN}/${BP}.tgz \
           file://0001-include-missing-pty.h-header-for-openpty.patch \
          "
SRC_URI[sha256sum] = "a2a0cb206eb0423dedc34794f5c2d38c83390d2dd1106b66aba0960c3a976c7a"

PACKAGECONFIG ?= ""
PACKAGECONFIG[xft] = "--enable-freetype,--disable-freetype,libxft fontconfig freetype-native"

# Let xterm install .desktop files
inherit mime-xdg

EXTRA_OECONF = " --x-includes=${STAGING_INCDIR} \
                 --x-libraries=${STAGING_LIBDIR} \
                 FREETYPE_CONFIG=${STAGING_BINDIR_CROSS}/freetype-config \
                 --disable-imake \
                 --disable-rpath-hack \
                 --disable-setuid \
                 --with-app-defaults=${datadir}/X11/app-defaults \
                 "

B = "${S}"

CFLAGS += "-D_GNU_SOURCE"

do_configure() {
    gnu-configize --force
    sed -e "s%/usr/contrib/X11R6%${STAGING_LIBDIR}%g" -i configure
    oe_runconf
}

do_install:append() {
    oe_runmake install-desktop DESTDIR="${D}" DESKTOP_FLAGS="--dir=${D}${DESKTOPDIR}"
}

RPROVIDES:${PN} = "virtual-x-terminal-emulator"

# busybox can supply resize too
inherit update-alternatives

ALTERNATIVE:${PN} = "resize x-terminal-emulator"
ALTERNATIVE_TARGET[x-terminal-emulator] = "${bindir}/xterm"
# rxvt-unicode defaults to priority 10. Let's be one point lower to let it override xterm.
ALTERNATIVE_PRIORITY[x-terminal-emulator] = "9"

CVE_STATUS[CVE-1999-0965] = "cpe-incorrect: Current version (392) not affected. This was fixed in version X11R5-fix-26 (R11R6 from 1994)"
