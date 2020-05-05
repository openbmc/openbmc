SUMMARY = "Abseil is a cpp library like STL"
DESCRIPTION = "Abseil provides pieces missing from the C++ standard. Contains \
additional useful libraries like algorithm, container, debugging, hash, memory, \
meta, numeric, strings, synchronization, time, types and utility"
HOMEPAGE = "https://abseil.io/"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=df52c6edb7adc22e533b2bacc3bd3915"

PV = "20190808+git${SRCPV}"
SRCREV = "aa844899c937bde5d2b24f276b59997e5b668bde"
BRANCH = "lts_2019_08_08"
SRC_URI = "git://github.com/abseil/abseil-cpp;branch=${BRANCH}                \
           file://0001-Remove-maes-option-from-cross-compilation.patch        \
           file://0002-Add-forgotten-ABSL_HAVE_VDSO_SUPPORT-conditional.patch \
           file://0003-Add-fPIC-option.patch                                  \
           file://0001-Add-RISCV-support-to-GetProgramCounter.patch \
           file://0001-absl-always-use-asm-sgidefs.h.patch \
           file://0001-Fix-build-on-riscv32.patch \
          "

S = "${WORKDIR}/git"

DEPENDS_append_libc-musl = " libexecinfo "

ASNEEDED_class-native = ""
ASNEEDED_class-nativesdk = ""

inherit cmake

BBCLASSEXTEND = "native nativesdk"
ALLOW_EMPTY_${PN} = "1"

python () {
    arch = d.getVar("TARGET_ARCH")

    if arch == "aarch64":
        tunes = d.getVar("TUNE_FEATURES")
        if not tunes:
            raise bb.parse.SkipPackage("%s-%s Needs support for crypto on armv8" % (pkgn, pkgv))
            return
        pkgn = d.getVar("PN")
        pkgv = d.getVar("PV")
        if "crypto" not in tunes:
            raise bb.parse.SkipPackage("%s-%s Needs support for crypto on armv8" % (pkgn, pkgv))

    if arch == "x86_64":
        tunes = d.getVar("TUNE_FEATURES")
        if not tunes:
           raise bb.parse.SkipPackage("%s-%s Needs support for corei7 on x86_64" % (pkgn, pkgv))
           return
        pkgn = d.getVar("PN")
        pkgv = d.getVar("PV")
        if "corei7" not in tunes:
            raise bb.parse.SkipPackage("%s-%s Needs support for corei7 on x86_64" % (pkgn, pkgv))

}

