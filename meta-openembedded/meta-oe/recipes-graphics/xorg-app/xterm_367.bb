require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "xterm is the standard terminal emulator for the X Window System"
DEPENDS = "libxaw xorgproto libxext libxau libxinerama libxpm ncurses desktop-file-utils-native"

LIC_FILES_CHKSUM = "file://xterm.h;beginline=3;endline=31;md5=987de9787175385203a1ea2482246a17"

SRC_URI = "http://invisible-mirror.net/archives/${BPN}/${BP}.tgz \
           file://0001-Add-configure-time-check-for-setsid.patch \
          "

SRC_URI[sha256sum] = "27f1a8b1c756e269fd5684e60802b545f0be9b36b8b5d6bdbc840c6b000dc51f"

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

do_configure() {
    gnu-configize --force
    sed -e "s%/usr/contrib/X11R6%${STAGING_LIBDIR}%g" -i configure
    oe_runconf
}

do_install_append() {
    oe_runmake install-desktop DESTDIR="${D}" DESKTOP_FLAGS="--dir=${D}${DESKTOPDIR}"
}

RPROVIDES_${PN} = "virtual/x-terminal-emulator"

# busybox can supply resize too
inherit update-alternatives

ALTERNATIVE_${PN} = "resize x-terminal-emulator"
ALTERNATIVE_TARGET[x-terminal-emulator] = "${bindir}/xterm"
# rxvt-unicode defaults to priority 10. Let's be one point lower to let it override xterm.
ALTERNATIVE_PRIORITY[x-terminal-emulator] = "9"
