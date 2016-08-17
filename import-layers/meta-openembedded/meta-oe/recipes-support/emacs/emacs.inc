DESCRIPTION = "Emacs"
HOMEPAGE = "http://www.gnu.org/software/emacs/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
SECTION = "editor"
# and it needs to run some generated binaries..
DEPENDS += "qemu-native"

DEPENDS +=" liblockfile"

X11DEPENDS = " \
    libice libsm atk cairo dbus expat libffi fontconfig freetype gconf giflib \
    jpeg xz pixman libpng librsvg tiff libcroco \
    libx11 libxau libxcomposite libxcursor libxdamage libxdmcp libxext libxfixes \
    libxft libxpm libxrandr libxrender \
    gdk-pixbuf glib-2.0 gtk+ pango libxcb harfbuzz icu \
" 

EXTRA_OECONF =  "--without-sound --with-crt-dir=${STAGING_LIBDIR} "

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "--with-x-toolkit=gtk,--without-x --without-dbus,${X11DEPENDS}"


inherit autotools

PACKAGES =+ "${PN}-el"

FILES_${PN}-el = "${datadir}/emacs/*/*/*.el.gz \
                  ${datadir}/emacs/*/*/*/*.el.gz"

FILES_${PN} += "${datadir}/emacs ${datadir}/icons"

FILES_${PN}-dbg += "${libexecdir}/emacs/*/*/.debug ${datadir}/emacs/*/*/.debug"

TREEDIR = "${WORKDIR}/qemu-treedir"

QEMUARCH= "${TARGET_ARCH}"
QEMUARCH_x86 = "i386"

# Large stack is required at least on x86_64 host, otherwise random segfaults appear:
QEMU = "qemu-${QEMUARCH} ${QEMU_OPTIONS} -s 1048576 -L ${TREEDIR} "

export LOGNAME = "$(whoami)"

do_compile_prepend() {
    sed -i ':1;s:\(START.* \|LIB_STANDARD.* \|LIBES.* \)/usr/lib:\1${STAGING_LIBDIR}:;t1' ${S}/src/s/gnu-linux.h `find "${S}" -name Makefile`
    export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${TOOLCHAIN_PATH}/${TARGET_SYS}/lib"
# Ugly hack, see files/use-qemu.patch for more:
    ln -sf ../src/emacs lisp/emacs
    ln -sf ../src/emacs lib-src/emacs
    ln -sf ../src/emacs leim/emacs
#copy STAGING_DIR_TARGET and libgcc_s.so* to WORKDIR/qemu-treedir #copied code from glibc-package.bbclass
    treedir=${TREEDIR}
    if [ -e $treedir ];then
        rm -rf $treedir
    fi
    
    mkdir -p $treedir/${base_libdir}

    if [ -f ${TOOLCHAIN_PATH}/${TARGET_SYS}/${base_libdir}/libgcc_s.so ]; then
        cp -pPR ${TOOLCHAIN_PATH}/${TARGET_SYS}/${base_libdir}/libgcc_s.so $treedir/${base_libdir}
    fi
    if [ -f ${TOOLCHAIN_PATH}/${TARGET_SYS}/${base_libdir}/libgcc_s.so.* ]; then
        cp -pPR ${TOOLCHAIN_PATH}/${TARGET_SYS}/${base_libdir}/libgcc_s.so.* $treedir/${base_libdir}
    fi
    # stupid hack, but without it, it tries to use /usr/lib/libc.so from host and fails
    # temacs: error while loading shared libraries: /usr/lib/libc.so: ELF file version does not match current one
    if [ ! -f $treedir/${base_libdir}/libc.so ]; then
        ln -s libc.so.6 $treedir/${base_libdir}/libc.so
    fi

    # copy only as much stuff as we need
    # before: 5.5G   ../../qemu-treedir/
    # after:   17M   ../../qemu-treedir/
    cp -pPR \
    ${STAGING_DIR_TARGET}/${base_libdir}/ld-* \
    ${STAGING_DIR_TARGET}/${base_libdir}/libc.* \
    ${STAGING_DIR_TARGET}/${base_libdir}/libc-* \
    ${STAGING_DIR_TARGET}/${base_libdir}/libm-* \
    ${STAGING_DIR_TARGET}/${base_libdir}/libm.* \
    ${STAGING_DIR_TARGET}/${base_libdir}/libtinfo.so.* \
    ${STAGING_DIR_TARGET}/${base_libdir}/libresolv* \
    ${STAGING_DIR_TARGET}/${base_libdir}/libdl* \
    ${STAGING_DIR_TARGET}/${base_libdir}/librt* \
    ${STAGING_DIR_TARGET}/${base_libdir}/libgcc_s.so.* \
    ${STAGING_DIR_TARGET}/${base_libdir}/libpthread* $treedir/${base_libdir}/

    if [ ! -z "${@bb.utils.contains('PACKAGECONFIG', 'x11', 'x11', '', d)}" ] ; then
        echo "X11 build enabled"
        # copy only as much stuff as we need
        # before: 5.5G   ../../qemu-treedir/
        # after:   86M   ../../qemu-treedir/
        cp -pPR \
        ${STAGING_DIR_TARGET}/${base_libdir}/libuuid.so.* \
        ${STAGING_DIR_TARGET}/${base_libdir}/libz.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libICE.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libSM.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libatk-1.0.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libcairo.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libdbus-1.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libdbus-glib-1.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libexpat.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libffi.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libfontconfig.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libfreetype.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libgconf-2.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libgif.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libjpeg.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/liblzma.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libpixman-1.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libpng1*.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/librsvg-2.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libstdc++.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libtiff.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libxml2.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libcroco-0.6.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libharfbuzz.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libicule.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libicuuc.so.* \
        ${STAGING_DIR_TARGET}/${libdir}/libicudata.so.* $treedir/${base_libdir}/

        # X11 Xau Xcomposite Xcursor Xdamage Xdmcp Xext Xfixes Xft Xpm Xrandr Xrender
        cp -pPR ${STAGING_DIR_TARGET}/${libdir}/libX*.so.* $treedir/${base_libdir}/

        # gdk-x11 gdk_pixbuf gio glib gmodule gobject gthread gtk-x11
        cp -pPR ${STAGING_DIR_TARGET}/${libdir}/libg*-2.0.so.* $treedir/${base_libdir}/

        # pango pangocairo pangoft2
        cp -pPR ${STAGING_DIR_TARGET}/${libdir}/libpango*-1.0.so.* $treedir/${base_libdir}/

        # xcb-render xcb-shm xcb
        cp -pPR ${STAGING_DIR_TARGET}/${libdir}/libxcb*.so.* $treedir/${base_libdir}/
    fi
}

do_install_append() {
    # freaks package.bbclass and whole bitbake call returns error code
    # ERROR: runstrip: ''arm-oe-linux-gnueabi-strip'  '/var/lib/jenkins/jobs/shr-core-branches/workspace/shr-core/tmp-eglibc/work/armv4t-oe-linux-gnueabi/emacs-23.4-r0/package/usr/share/emacs/23.4/etc/tutorials/TUTORIAL.ko'' strip command failed
    mv ${D}${datadir}/${BPN}/${PV}/etc/tutorials/TUTORIAL.ko ${D}${datadir}/${BPN}/${PV}/etc/tutorials/TUTORIAL.KO
    # Info dir listing isn't interesting at this point so remove it if it exists.
    if [ -e "${D}${infodir}/dir" ]; then
        rm -f ${D}${infodir}/dir
    fi
}

EXTRA_OEMAKE += 'QEMU="${QEMU}"'
