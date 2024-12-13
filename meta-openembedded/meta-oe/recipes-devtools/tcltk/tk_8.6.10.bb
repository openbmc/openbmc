SUMMARY = "Tool Command Language ToolKit Extension"
HOMEPAGE = "http://tcl.sourceforge.net"
SECTION = "devel/tcltk"

# http://www.tcl.tk/software/tcltk/license.html
LICENSE = "TCL"
LIC_FILES_CHKSUM = "file://${S}/../license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://${S}/../compat/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://${S}/../doc/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://${S}/../library/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://${S}/../macosx/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://${S}/../tests/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://${S}/../unix/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://${S}/../win/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://${S}/../xlib/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
"

DEPENDS = "tcl virtual/libx11 libxt"

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/tcl/${BPN}${PV}-src.tar.gz \
    file://confsearch.diff;striplevel=2 \
    file://tkprivate.diff;striplevel=2 \
    file://fix-xft.diff \
"
SRC_URI[md5sum] = "602a47ad9ecac7bf655ada729d140a94"
SRC_URI[sha256sum] = "63df418a859d0a463347f95ded5cd88a3dd3aaa1ceecaeee362194bc30f3e386"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/tcl/files/Tcl/"
UPSTREAM_CHECK_REGEX = "Tcl/(?P<pver>\d+(\.\d+)+)/"

S = "${WORKDIR}/${BPN}${PV}/unix"

DEBUG_PREFIX_MAP += "-fdebug-prefix-map=${S}/../=${TARGET_DBGSRC_DIR}/.."

PSEUDO_IGNORE_PATHS .= ",${WORKDIR}/${BPN}${PV}"

# Short version format: "8.6"
VER = "${@os.path.splitext(d.getVar('PV'))[0]}"

LDFLAGS += "-Wl,-rpath,${libdir}/tcltk/${PV}/lib"
inherit autotools features_check pkgconfig
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "\
    --enable-threads \
    --with-x \
    --with-tcl=${STAGING_BINDIR}/crossscripts \
    --libdir=${libdir} \
"
export TK_LIBRARY='${libdir}/tk${VER}'
do_install:append() {
    ln -sf libtk${VER}.so ${D}${libdir}/libtk${VER}.so.0
    oe_libinstall -so libtk${VER} ${D}${libdir}
    ln -sf wish${VER} ${D}${bindir}/wish

    sed -i "s;-L${B};-L${STAGING_LIBDIR};g" tkConfig.sh
    sed -i "s;'${WORKDIR};'${STAGING_INCDIR};g" tkConfig.sh
    install -d ${D}${bindir_crossscripts}
    install -m 0755 tkConfig.sh ${D}${bindir_crossscripts}
}

PACKAGECONFIG ??= "xft"
PACKAGECONFIG[xft] = "--enable-xft,--disable-xft,xft"
PACKAGECONFIG[xss] = "--enable-xss,--disable-xss,libxscrnsaver libxext"

PACKAGES =+ "${PN}-lib"

FILES:${PN}-lib = "${libdir}/libtk${VER}.so*"
FILES:${PN} += "${libdir}/tk*"

# isn't getting picked up by shlibs code
RDEPENDS:${PN} += "tk-lib"
RDEPENDS:${PN}:class-native = ""

BBCLASSEXTEND = "native nativesdk"

# Fix the path in sstate
SSTATE_SCAN_FILES += "*Config.sh"

inherit binconfig

SYSROOT_DIRS += "${bindir_crossscripts}"

# Fix some paths that might be used by Tcl extensions
BINCONFIG_GLOB = "*Config.sh"

# Cleanup host path from ${libdir}/tclConfig.sh and remove the
# ${bindir_crossscripts}/tclConfig.sh from target
PACKAGE_PREPROCESS_FUNCS += "tcl_package_preprocess"
tcl_package_preprocess() {
        sed -i -e "s;${DEBUG_PREFIX_MAP};;g" \
               -e "s;-L${STAGING_LIBDIR};-L${libdir};g" \
               -e "s;${STAGING_INCDIR};${includedir};g" \
               -e "s;--sysroot=${RECIPE_SYSROOT};;g" \
               ${PKGD}${libdir}/tkConfig.sh

        rm -f ${PKGD}${bindir_crossscripts}/tkConfig.sh
}
