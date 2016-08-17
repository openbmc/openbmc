DESCRIPTION = "Library containing NEON-optimized implementations for a common set of functions"
HOMEPAGE = "http://projectne10.github.io/Ne10/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=463ac0a7d64edc2b787c4206635ca2b1"
SECTION = "libs"

SRC_URI = "git://github.com/projectNe10/Ne10.git"
SRCREV = "a08b29d88e3c94d32b5b8f827e7fcf0bc2b34ac2"

S = "${WORKDIR}/git"

inherit cmake

NE10_TARGET_ARCH = ""
EXTRA_OECMAKE = '-DGNULINUX_PLATFORM=ON -DNE10_BUILD_SHARED=ON -DNE10_LINUX_TARGET_ARCH="${NE10_TARGET_ARCH}"'

COMPATIBLE_MACHINE_aarch64 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"

python () {
    if any(t.startswith('armv7') for t in d.getVar('TUNE_FEATURES', True).split()):
        d.setVar('NE10_TARGET_ARCH', 'armv7')
        bb.debug(2, 'Building Ne10 for armv7')
    elif any(t.startswith('aarch64') for t in d.getVar('TUNE_FEATURES', True).split()):
        d.setVar('NE10_TARGET_ARCH', 'aarch64')
        bb.debug(2, 'Building Ne10 for aarch64')
    else:
        raise bb.parse.SkipPackage("Incompatible with archs other than armv7 and aarch64")
}

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -m 0644 ${S}/inc/NE10*.h ${D}${includedir}/
    install -m 0644 ${B}/modules/libNE10.a ${D}${libdir}/
    install -m 0755 ${B}/modules/libNE10.so.* ${D}${libdir}/
    cp -a ${B}/modules/libNE10.so ${D}${libdir}/
}

# ERROR: QA Issue: ELF binary 'ne10/1.2.1-r0/packages-split/ne10/usr/lib/libNE10.so.10' has relocations in .text [textrel]
# ERROR: QA Issue: ELF binary 'ne10/1.2.1-r0/packages-split/ne10/usr/lib/libNE10.so.10' has relocations in .text [textrel]
INSANE_SKIP_${PN} += "textrel"
