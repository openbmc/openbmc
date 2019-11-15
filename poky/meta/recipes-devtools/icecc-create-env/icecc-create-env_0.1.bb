SUMMARY = "icecc environment setup script"
DESCRIPTION = "This is a version of the icecc-create-env script that has \
been modified in order to make it work with OE."
SECTION = "base"
# source file has just a "GPL" word, but upstream is GPLv2+.
# most probably just GPL would be a mistake
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://icecc-create-env;beginline=2;endline=5;md5=ae1df3d6a058bfda40b66094c5f6065f"

PR = "r2"

DEPENDS = ""
INHIBIT_DEFAULT_DEPS = "1"

# This is needed, because otherwise there is dependency loop from quilt-native
# Dependency loop #1 found:
#  Task 10907 (meta/recipes-devtools/quilt/quilt-native_0.60.bb, do_install) (dependent Tasks ['quilt-native, do_compile'])
#  Task 10908 (meta/recipes-devtools/quilt/quilt-native_0.60.bb, do_populate_sysroot) (dependent Tasks ['quilt-native, do_install'])
#  Task 10997 (meta/recipes-devtools/icecc-create-env/icecc-create-env-native_0.1.bb, do_patch) (dependent Tasks ['icecc-create-env-native, do_unpack', 'quilt-native, do_populate_sysroot'])
#  Task 11001 (meta/recipes-devtools/icecc-create-env/icecc-create-env-native_0.1.bb, do_configure) (dependent Tasks ['icecc-create-env-native, do_patch'])
#  Task 11002 (meta/recipes-devtools/icecc-create-env/icecc-create-env-native_0.1.bb, do_compile) (dependent Tasks ['icecc-create-env-native, do_configure'])
#  Task 10998 (meta/recipes-devtools/icecc-create-env/icecc-create-env-native_0.1.bb, do_install) (dependent Tasks ['icecc-create-env-native, do_compile'])
#  Task 10999 (meta/recipes-devtools/icecc-create-env/icecc-create-env-native_0.1.bb, do_populate_sysroot) (dependent Tasks ['icecc-create-env-native, do_install'])
#  Task 10910 (meta/recipes-devtools/quilt/quilt-native_0.60.bb, do_configure) (dependent Tasks ['quilt-native, do_patch', 'icecc-create-env-native, do_populate_sysroot'])
#  Task 10911 (meta/recipes-devtools/quilt/quilt-native_0.60.bb, do_compile) (dependent Tasks ['quilt-native, do_configure'])
PATCHTOOL = "patch"
SRC_URI = "file://icecc-create-env"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/icecc-create-env ${D}/${bindir}
}

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN}_class-nativesdk = "patchelf"
