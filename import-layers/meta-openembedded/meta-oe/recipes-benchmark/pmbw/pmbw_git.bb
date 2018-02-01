SUMMERY = "Parallel Memory Bandwidth Measurement / Benchmark"
DESCRIPTION = "\
The tool pmbw is a set of assembler routines to measure the parallel memory \
(cache and RAM) bandwidth of modern multi-core machines."
HOMEPAGE = "http://panthema.net/2013/pmbw/"
SECTION = "benchmark/tests"
AUTHOR = "Timo Bingmann"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "git://github.com/bingmann/pmbw;protocol=https"

PV = "0.6.2+git${SRCPV}"
SRCREV = "4a3b37728060a8aba06fc83f157a1965088d79d6"

S = "${WORKDIR}/git"

inherit autotools

# the hand coded asm uses r11, which therefore cannot be used for storing
# the frame pointer when debugging on arm
SELECTED_OPTIMIZATION_remove_arm = "-fno-omit-frame-pointer"

PACKAGES =+ "${PN}-stats2gnuplot"

FILES_${PN}-stats2gnuplot = "${bindir}/stats2gnuplot"

RRECOMMENDS_${PN} = "${PN}-stats2gnuplot"

PNBLACKLIST[pmbw] ?= "Fails to build with RSS http://errors.yoctoproject.org/Errors/Details/130568/ - the recipe will be removed on 2017-09-01 unless the issue is fixed"
