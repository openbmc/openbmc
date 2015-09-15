SUMMARY = "A simple shell script to run all installed ptests"
DESCRIPTION = "The ptest-runner package installs a ptest-runner \
shell script which loops through all installed ptest test suites and \
runs them in sequence."
HOMEPAGE = "https://wiki.yoctoproject.org/wiki/Ptest"
SRC_URI += "file://ptest-runner"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

INHIBIT_DEFAULT_DEPS = "1"

S = "${WORKDIR}"

do_install () {
    mkdir -p ${D}${bindir}
    install -m 0755 ${WORKDIR}/ptest-runner ${D}${bindir}
}

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_build[noexec] = "1"
