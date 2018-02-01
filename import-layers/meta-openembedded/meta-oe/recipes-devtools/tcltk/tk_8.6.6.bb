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
SRC_URI[md5sum] = "dd7dbb3a6523c42d05f6ab6e86096e99"
SRC_URI[sha256sum] = "d62c371a71b4744ed830e3c21d27968c31dba74dd2c45f36b9b071e6d88eb19d"

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
    --with-tcl=${STAGING_BINDIR_CROSS} \
    --libdir=${libdir} \
"

do_install_append() {
    ln -sf libtk${VER}.so ${D}${libdir}/libtk${VER}.so.0
    oe_libinstall -so libtk${VER} ${D}${libdir}
    ln -sf wish${VER} ${D}${bindir}/wish

    # Even after passing libdir=${libdir} at config, some incorrect dirs are still generated for the multilib build
    if [ "$libdir" != "/usr/lib" ]; then
        # Move files to correct library directory
        mv ${D}/usr/lib/tk${VER}/* ${D}/${libdir}/tk${VER}/
        # Remove unneeded/incorrect dir ('usr/lib/')
        rm -rf ${D}/usr/lib
    fi
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

BBCLASSEXTEND = "native"

# Fix the path in sstate
SSTATE_SCAN_FILES += "*Config.sh"
