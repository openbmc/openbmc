SUMMARY = "iCal and scheduling (RFC 2445, 2446, 2447) library"
DESCRIPTION = "An Open Source implementation of the iCalendar protocols \
and protocol data units. The iCalendar specification describes how \
calendar clients can communicate with calendar servers so users can store \
their calendar data and arrange meetings with other users. "
HOMEPAGE = "https://github.com/libical/libical"
BUGTRACKER = "https://github.com/libical/libical/issues"
LICENSE = "LGPL-2.1-only | MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1910a2a76ddf6a9ba369182494170d87 \
                    file://LICENSE.LGPL21.txt;md5=8f690bb538f4b301d931374a6eb864d0 \
                    file://LICENSE.MPL2.txt;md5=f75d2927d3c1ed2414ef72048f5ad640 \
                    "
SECTION = "libs"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz \
           file://0001-cmake-Do-not-export-CC-into-gir-compiler.patch \
          "
SRC_URI[sha256sum] = "72b7dc1a5937533aee5a2baefc990983b66b141dd80d43b51f80aced4aae219c"

inherit cmake pkgconfig gobject-introspection vala github-releases

DEPENDS += "libical-native"

PACKAGECONFIG ??= "icu glib"
PACKAGECONFIG[bdb] = ",-DCMAKE_DISABLE_FIND_PACKAGE_BDB=True,db"
PACKAGECONFIG[glib] = "-DICAL_GLIB=True,-DICAL_GLIB=False,glib-2.0-native libxml2-native glib-2.0 libxml2"
# ICU is used for RSCALE (RFC7529) support
PACKAGECONFIG[icu] = ",-DCMAKE_DISABLE_FIND_PACKAGE_ICU=True,icu"

# No need to use perl-native, the host perl is sufficient.
EXTRA_OECMAKE += "-DPERL_EXECUTABLE=${HOSTTOOLS_DIR}/perl"
# Disable the test suite as we can't install it
EXTRA_OECMAKE += "-DLIBICAL_BUILD_TESTING=false"
# doc build fails with linker error (??) for libical-glib so disable it
EXTRA_OECMAKE += "-DICAL_BUILD_DOCS=false"
# gobject-introspection
EXTRA_OECMAKE:append:class-target = " -DGObjectIntrospection_COMPILER=${STAGING_BINDIR}/g-ir-compiler-wrapper"
EXTRA_OECMAKE:append:class-target = " -DGObjectIntrospection_SCANNER=${STAGING_BINDIR}/g-ir-scanner-wrapper"
EXTRA_OECMAKE += "-DVAPIGEN=${STAGING_BINDIR_NATIVE}/vapigen"
EXTRA_OECMAKE += "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DGOBJECT_INTROSPECTION=ON -DICAL_GLIB_VAPI=ON', '-DGOBJECT_INTROSPECTION=OFF', d)}"

# Tell the cross-libical where the tool it needs to build is
EXTRA_OECMAKE:append:class-target = " -DIMPORT_ICAL_GLIB_SRC_GENERATOR=${STAGING_LIBDIR_NATIVE}/cmake/LibIcal/IcalGlibSrcGenerator.cmake"

do_install:append () {
    # Remove build host references (https://github.com/libical/libical/issues/532)
    sed -i \
       -e 's,${STAGING_LIBDIR},${libdir},g' \
       -e 's,${STAGING_INCDIR},${includedir},g' \
       ${D}${libdir}/cmake/LibIcal/LibIcal*.cmake \
       ${D}${libdir}/cmake/LibIcal/Ical*.cmake
}

BBCLASSEXTEND = "native"
