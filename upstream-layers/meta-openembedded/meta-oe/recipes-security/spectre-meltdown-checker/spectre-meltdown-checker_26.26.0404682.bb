SUMMARY = "Hardware vulnerability and mitigation checker"
DESCRIPTION = "\
    Reptar, Downfall, Zenbleed, ZombieLoad, RIDL, Fallout, Foreshadow, Spectre, \
    Meltdown vulnerability/mitigation checker for Linux & BSD \
"
HOMEPAGE = "https://github.com/speed47/spectre-meltdown-checker"
BUGTRACKER = "https://github.com/speed47/spectre-meltdown-checker/issues"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://spectre-meltdown-checker.sh;beginline=2;endline=2;md5=3ae5399e70af7be9b93f72568069e2f4"

SRC_URI = "git://github.com/speed47/spectre-meltdown-checker;protocol=https;branch=master;tag=v${PV}"
SRCREV = "8a6f9d5d638c601d2a875669015a6cb217e40f49"

# binutils: readelf,objdump might be used for deeper analysis
# coreutils: dd with iflag=skip_bytes oflag=seek_bytes in some cases
# kernel-dev: /boot/config is used
# perl: sometimes used when other tools (dd, rdmsr/wdmsr) are missing
RRECOMMENDS:${PN} = "\
    binutils \
    coreutils \
    kernel-dev \
    perl \
"

INHIBIT_DEFAULT_DEPS = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_compile[noexec] = "1"

do_install() {
    install -m 755 -D ${S}/${BPN}.sh ${D}${bindir}/${BPN}
}
