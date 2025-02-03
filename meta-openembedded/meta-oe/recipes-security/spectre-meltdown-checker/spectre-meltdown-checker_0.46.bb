SUMMARY = "Hardware vulnerability and mitigation checker"
DESCRIPTION = "\
    Reptar, Downfall, Zenbleed, ZombieLoad, RIDL, Fallout, Foreshadow, Spectre, \
    Meltdown vulnerability/mitigation checker for Linux & BSD \
"
HOMEPAGE = "https://github.com/speed47/spectre-meltdown-checker"
BUGTRACKER = "https://github.com/speed47/spectre-meltdown-checker/issues"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://spectre-meltdown-checker.sh;beginline=2;endline=2;md5=3ae5399e70af7be9b93f72568069e2f4"

SRC_URI = "git://github.com/speed47/spectre-meltdown-checker;protocol=https;branch=master"
SRCREV = "b8f8c81d519435c0871b13f02b5c4a72c5bffd5e"

S = "${UNPACKDIR}/git"

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

inherit allarch

do_compile[noexec] = "1"

do_install() {
    install -m 755 -D ${S}/${PN}.sh ${D}${bindir}/${PN}
}
