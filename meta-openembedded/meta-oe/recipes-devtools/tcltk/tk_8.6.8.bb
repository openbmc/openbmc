SUMMARY = "Tool Command Language ToolKit Extension"
HOMEPAGE = "http://tcl.sourceforge.net"
SECTION = "devel/tcltk"

# http://www.tcl.tk/software/tcltk/license.html
LICENSE = "tcl"
LIC_FILES_CHKSUM = "file://../license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://../compat/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://../doc/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://../library/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://../macosx/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://../tests/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://../unix/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://../win/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
    file://../xlib/license.terms;md5=c88f99decec11afa967ad33d314f87fe \
"

DEPENDS = "tcl virtual/libx11 libxt"

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/tcl/${BPN}${PV}-src.tar.gz \
    file://confsearch.diff;striplevel=2 \
    file://non-linux.diff;striplevel=2 \
    file://tklibrary.diff;striplevel=2 \
    file://tkprivate.diff;striplevel=2 \
    file://fix-xft.diff \
    file://configure.use.fontconfig.with.xft.patch \
"
SRC_URI[md5sum] = "5e0faecba458ee1386078fb228d008ba"
SRC_URI[sha256sum] = "49e7bca08dde95195a27f594f7c850b088be357a7c7096e44e1158c7a5fd7b33"

S = "${WORKDIR}/${BPN}${PV}/unix"

# Short version format: "8.6"
VER = "${@os.path.splitext(d.getVar('PV'))[0]}"

LDFLAGS += "-Wl,-rpath,${libdir}/tcltk/${PV}/lib"
inherit autotools distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "\
    --enable-threads \
    --with-x \
    --with-tcl=${STAGING_BINDIR}/crossscripts \
    --libdir=${libdir} \
"
export TK_LIBRARY='${libdir}/tk${VER}'
do_install_append() {
    ln -sf libtk${VER}.so ${D}${libdir}/libtk${VER}.so.0
    oe_libinstall -so libtk${VER} ${D}${libdir}
    ln -sf wish${VER} ${D}${bindir}/wish
}

PACKAGECONFIG ??= "xft"
PACKAGECONFIG[xft] = "--enable-xft,--disable-xft,xft"
PACKAGECONFIG[xss] = "--enable-xss,--disable-xss,libxscrnsaver libxext"

PACKAGES =+ "${PN}-lib"

FILES_${PN}-lib = "${libdir}/libtk${VER}.so*"
FILES_${PN} += "${libdir}/tk*"

# isn't getting picked up by shlibs code
RDEPENDS_${PN} += "tk-lib"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"

# Fix the path in sstate
SSTATE_SCAN_FILES += "*Config.sh"
