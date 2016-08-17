SUMMARY = "Tool Command Language"
HOMEPAGE = "http://tcl.sourceforge.net"
SECTION = "devel/tcltk"

# http://www.tcl.tk/software/tcltk/license.html
LICENSE = "tcl & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://../license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://../compat/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://../library/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://../macosx/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://../tests/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://../win/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
"

DEPENDS = "tcl-native zlib"

BASE_SRC_URI = "${SOURCEFORGE_MIRROR}/tcl/${BPN}${PV}-src.tar.gz \
                file://tcl-add-soname.patch"
SRC_URI = "${BASE_SRC_URI} \
           file://fix_non_native_build_issue.patch \
           file://fix_issue_with_old_distro_glibc.patch \
           file://no_packages.patch \
           file://tcl-remove-hardcoded-install-path.patch \
           file://alter-includedir.patch \
           file://run-ptest \
"
SRC_URI[md5sum] = "d7cbb91f1ded1919370a30edd1534304"
SRC_URI[sha256sum] = "9e6ed94c981c1d0c5f5fefb8112d06c6bf4d050a7327e95e71d417c416519c8d"

SRC_URI_class-native = "${BASE_SRC_URI}"

S = "${WORKDIR}/${BPN}${PV}/unix"

VER = "${PV}"

inherit autotools ptest binconfig

DEPENDS_class-native = "zlib-native"

EXTRA_OECONF = "--enable-threads --disable-rpath --libdir=${libdir}"

do_configure() {
	cd ${S}
	gnu-configize
	cd ${B}
	oe_runconf
}

do_compile_prepend() {
	echo > ${S}/../compat/fixstrtod.c
}

do_install() {
	autotools_do_install install-private-headers
	ln -sf ./tclsh${VER} ${D}${bindir}/tclsh
	ln -sf tclsh8.6 ${D}${bindir}/tclsh${VER}
	sed -i "s+-L${B}+-L${STAGING_LIBDIR}+g" tclConfig.sh
	sed -i "s+${WORKDIR}+${STAGING_INCDIR}+g" tclConfig.sh
	sed -i "s,-L${libdir},-L=${libdir},g" tclConfig.sh
	sed -i "s,-I${includedir},-I=${includedir},g" tclConfig.sh
	install -d ${D}${bindir_crossscripts}
	install -m 0755 tclConfig.sh ${D}${bindir_crossscripts}
	install -m 0755 tclConfig.sh ${D}${libdir}
	cd ..
	for dir in compat generic unix; do
		install -d ${D}${includedir}/${BPN}${VER}/$dir
		install -m 0644 ${S}/../$dir/*.h ${D}${includedir}/${BPN}${VER}/$dir/
	done
}

SYSROOT_PREPROCESS_FUNCS += "tcl_sysroot_preprocess"
tcl_sysroot_preprocess () {
	sysroot_stage_dir ${D}${bindir_crossscripts} ${SYSROOT_DESTDIR}${bindir_crossscripts}
}

PACKAGES =+ "tcl-lib"
FILES_tcl-lib = "${libdir}/libtcl8.6.so.*"
FILES_${PN} += "${libdir}/tcl${VER} ${libdir}/tcl8.6 ${libdir}/tcl8"
FILES_${PN}-dev += "${libdir}/tclConfig.sh ${libdir}/tclooConfig.sh"

# isn't getting picked up by shlibs code
RDEPENDS_${PN} += "tcl-lib"
RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN}-ptest += "libgcc"

BBCLASSEXTEND = "native"

do_compile_ptest() {
	oe_runmake tcltest
}

do_install_ptest() {
	cp ${B}/tcltest ${D}${PTEST_PATH}
	cp -r ${S}/../library ${D}${PTEST_PATH}
	cp -r ${S}/../tests ${D}${PTEST_PATH}
}

# Fix some paths that might be used by Tcl extensions
BINCONFIG_GLOB = "*Config.sh"

# Fix the path in sstate
SSTATE_SCAN_FILES += "*Config.sh"
