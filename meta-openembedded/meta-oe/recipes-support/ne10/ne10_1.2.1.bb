DESCRIPTION = "Library containing NEON-optimized implementations for a common set of functions"
HOMEPAGE = "http://projectne10.github.io/Ne10/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e7fe20c9be97be5579e3ab5d92d3a218"
SECTION = "libs"

SRC_URI = "git://github.com/projectNe10/Ne10.git;branch=master;protocol=https \
           file://0001-CMakeLists.txt-Remove-mthumb-interwork.patch \
           file://0001-Dont-specify-march-explicitly.patch \
           "
SRCREV = "18c4c982a595dad069cd8df4932aefb1d257591f"

S = "${WORKDIR}/git"
PV .= "+git"

inherit cmake

# Incompatible with archs other than armv7, armv7ve and aarch64
COMPATIBLE_MACHINE = "(^$)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:armv7a = "${@bb.utils.contains("TUNE_FEATURES","neon","(.*)","(^$)",d)}"
COMPATIBLE_MACHINE:armv7ve = "${@bb.utils.contains("TUNE_FEATURES","neon","(.*)","(^$)",d)}"
NE10_TARGET_ARCH = ""
NE10_TARGET_ARCH:aarch64 = "aarch64"
NE10_TARGET_ARCH:armv7a = "${@bb.utils.contains("TUNE_FEATURES","neon","armv7","",d)}"
NE10_TARGET_ARCH:armv7ve = "${@bb.utils.contains("TUNE_FEATURES","neon","armv7","",d)}"

EXTRA_OECMAKE = '-DGNULINUX_PLATFORM=ON -DNE10_BUILD_SHARED=ON -DNE10_LINUX_TARGET_ARCH="${NE10_TARGET_ARCH}"'

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -m 0644 ${S}/inc/NE10*.h ${D}${includedir}/
    install -m 0644 ${B}/modules/libNE10.a ${D}${libdir}/
    install -m 0755 ${B}/modules/libNE10.so.* ${D}${libdir}/
    cp -d ${B}/modules/libNE10.so ${D}${libdir}/
}

# ERROR: QA Issue: ELF binary 'ne10/1.2.1-r0/packages-split/ne10/usr/lib/libNE10.so.10' has relocations in .text [textrel]
# ERROR: QA Issue: ELF binary 'ne10/1.2.1-r0/packages-split/ne10/usr/lib/libNE10.so.10' has relocations in .text [textrel]
INSANE_SKIP:${PN} += "textrel"
