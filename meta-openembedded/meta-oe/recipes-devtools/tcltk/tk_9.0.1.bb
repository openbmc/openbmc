SUMMARY = "Tool Command Language ToolKit Extension"
HOMEPAGE = "http://tcl.sourceforge.net"
SECTION = "devel/tcltk"

# http://www.tcl.tk/software/tcltk/license.html
LICENSE = "TCL"
LIC_FILES_CHKSUM = "file://license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://compat/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://doc/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://library/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://macosx/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://tests/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://unix/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://win/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://xlib/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
"

DEPENDS = "tcl virtual/libx11 libxt zip-native"

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/tcl/${BPN}${PV}-src.tar.gz \
"
SRC_URI[sha256sum] = "d6f01a4d598bfc6398be9584e1bab828c907b0758db4bbb351a1429106aec527"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/tcl/files/Tcl/"
UPSTREAM_CHECK_REGEX = "Tcl/(?P<pver>\d+(\.\d+)+)/"

S = "${WORKDIR}/${BPN}${PV}"

# Short version format: "8.6"
VER = "${@os.path.splitext(d.getVar('PV'))[0]}"

LDFLAGS += "-Wl,-rpath,${libdir}/tcltk/${PV}/lib"

inherit autotools features_check pkgconfig

AUTOTOOLS_SCRIPT_PATH = "${S}/unix"

# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "\
    --with-x \
    --with-tcl=${STAGING_BINDIR}/crossscripts \
    --libdir=${libdir} \
"

export TK_LIBRARY='${libdir}/tk${VER}'

do_install:append() {
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

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

FILES:${PN}-lib = "${libdir}/libtcl9tk${VER}.so*"
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
               -e "s;${B};${libdir};g" \
               -e "s;${WORKDIR};${TARGET_DBGSRC_DIR};g" \
               ${PKGD}${libdir}/tkConfig.sh

        rm -f ${PKGD}${bindir_crossscripts}/tkConfig.sh
}
