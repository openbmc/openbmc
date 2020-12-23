SUMMARY = "iCal and scheduling (RFC 2445, 2446, 2447) library"
HOMEPAGE = "https://github.com/libical/libical"
BUGTRACKER = "https://github.com/libical/libical/issues"
LICENSE = "LGPLv2.1 | MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1910a2a76ddf6a9ba369182494170d87 \
                    file://LICENSE.LGPL21.txt;md5=933adb561f159e7c3da079536f0ed871 \
                    file://LICENSE.MPL2.txt;md5=f75d2927d3c1ed2414ef72048f5ad640 \
                    "
SECTION = "libs"

SRC_URI = " \
    https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
    file://0001-Use-our-hand-build-native-src-generator.patch \
    file://0001-Fix-build-with-icu-68.1.patch \
"
SRC_URI[md5sum] = "41bd1f1fcdcb4779cea478bb55cf07bf"
SRC_URI[sha256sum] = "09fecacaf75ba5a242159e3a9758a5446b5ce4d0ab684f98a7040864e1d1286f"
UPSTREAM_CHECK_URI = "https://github.com/libical/libical/releases"

inherit cmake pkgconfig

do_compile_prepend() {
	# As long as https://github.com/libical/libical/issues/394 is open build native src-generator manually
	NATIVE_CFLAGS="${BUILD_CFLAGS} `pkg-config-native --cflags glib-2.0` `pkg-config-native --cflags libxml-2.0`"
	NATIVE_LDFLAGS="${BUILD_LDFLAGS} `pkg-config-native --libs glib-2.0` `pkg-config-native --libs libxml-2.0`"
	${BUILD_CC} $NATIVE_CFLAGS ${S}/src/libical-glib/tools/generator.c ${S}/src/libical-glib/tools/xml-parser.c -o ${B}/src-generator $NATIVE_LDFLAGS
}

PACKAGECONFIG ??= "icu glib"
PACKAGECONFIG[bdb] = ",-DCMAKE_DISABLE_FIND_PACKAGE_BDB=True,db"
PACKAGECONFIG[glib] = "-DICAL_GLIB=True,-DICAL_GLIB=False,glib-2.0-native libxml2-native glib-2.0 libxml2"
# ICU is used for RSCALE (RFC7529) support
PACKAGECONFIG[icu] = ",-DCMAKE_DISABLE_FIND_PACKAGE_ICU=True,icu"

# No need to use perl-native, the host perl is sufficient.
EXTRA_OECMAKE += "-DPERL_EXECUTABLE=${HOSTTOOLS_DIR}/perl"
# doc build fails with linker error (??) for libical-glib so disable it
EXTRA_OECMAKE += "-DICAL_BUILD_DOCS=false"

do_install_append () {
    # Remove build host references
    sed -i \
       -e 's,${STAGING_LIBDIR},${libdir},g' \
       -e 's,${STAGING_INCDIR},${includedir},g' \
       ${D}${libdir}/cmake/LibIcal/LibIcal*.cmake
}
