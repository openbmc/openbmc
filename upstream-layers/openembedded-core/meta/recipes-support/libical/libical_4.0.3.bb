SUMMARY = "iCal and scheduling (RFC 2445, 2446, 2447) library"
DESCRIPTION = "An Open Source implementation of the iCalendar protocols \
and protocol data units. The iCalendar specification describes how \
calendar clients can communicate with calendar servers so users can store \
their calendar data and arrange meetings with other users. "
HOMEPAGE = "https://github.com/libical/libical"
BUGTRACKER = "https://github.com/libical/libical/issues"
LICENSE = "LGPL-2.1-only | MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=6d9294493d031c817783b0400a126c89 \
                    "
SECTION = "libs"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz \
           file://flags.patch \
           "
SRC_URI[sha256sum] = "86f29029d0ec9fa30c9001de16c0859a3816ae154ff5b097392b014e21a3d254"

inherit cmake pkgconfig gobject-introspection vala github-releases

DEPENDS += "libical-native"

PACKAGECONFIG ??= "icu glib"
PACKAGECONFIG[bdb] = ",-DCMAKE_DISABLE_FIND_PACKAGE_BDB=True,db"
PACKAGECONFIG[glib] = "-DLIBICAL_GLIB=True,-DLIBICAL_GLIB=False,glib-2.0-native libxml2-native glib-2.0 libxml2"
# ICU is used for RSCALE (RFC7529) support
PACKAGECONFIG[icu] = ",-DCMAKE_DISABLE_FIND_PACKAGE_ICU=True,icu"

# No need to use perl-native, the host perl is sufficient.
EXTRA_OECMAKE += "-DPERL_EXECUTABLE=${HOSTTOOLS_DIR}/perl"
# Disable the test suite as we can't install it
EXTRA_OECMAKE += "-DLIBICAL_BUILD_TESTING=false"
# doc build fails with linker error (??) for libical-glib so disable it
EXTRA_OECMAKE += "-DLIBICAL_GLIB_BUILD_DOCS=false"
# gobject-introspection
EXTRA_OECMAKE:append:class-target = " -DGObjectIntrospection_COMPILER=${STAGING_BINDIR}/g-ir-compiler-wrapper"
EXTRA_OECMAKE:append:class-target = " -DGObjectIntrospection_SCANNER=${STAGING_BINDIR}/g-ir-scanner-wrapper"
EXTRA_OECMAKE += "-DVAPIGEN=${STAGING_BINDIR_NATIVE}/vapigen"
EXTRA_OECMAKE += "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DLIBICAL_GOBJECT_INTROSPECTION=ON -DLIBICAL_GLIB_VAPI=ON', '-DLIBICAL_GOBJECT_INTROSPECTION=OFF -DLIBICAL_GLIB_VAPI=OFF', d)}"
EXTRA_OECMAKE:append:class-native = " -DLIBICAL_GOBJECT_INTROSPECTION=OFF -DLIBICAL_GLIB_VAPI=OFF"
# no java
EXTRA_OECMAKE += "-DLIBICAL_JAVA_BINDINGS=False"

# Tell the cross-libical where the tool it needs to build is
EXTRA_OECMAKE:append:class-target = " -DIMPORT_ICAL_GLIB_SRC_GENERATOR=${STAGING_LIBDIR_NATIVE}/cmake/LibIcal/IcalGlibSrcGenerator.cmake"

do_install:append () {
    # Remove build host references (https://github.com/libical/libical/issues/532)
    sed -i -e 's,${STAGING_LIBDIR},${libdir},g' ${D}${libdir}/cmake/LibIcal/LibIcalTargets.cmake
}

# This tool is only needed to cross-compile, delete it from the target packages
do_install:append:class-target() {
    rm -f ${D}${libexecdir}/libical/ical-glib-src-generator
    rm -f ${D}${libdir}/cmake/LibIcal/IcalGlibSrcGenerator*.cmake
}

BBCLASSEXTEND = "native"
