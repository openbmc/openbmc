SUMMARY = "CORSTONE1000 external systems communications tests"
DESCRIPTION = "This is a Linux userspace tool to test the communication between Corstone1000 cores"
HOMEPAGE = "https://git.linaro.org/landing-teams/working/arm/test-apps.git"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e44b2531cd6ffe9dece394dbe988d9a0"

SRC_URI = "git://gitlab.arm.com/arm-reference-solutions/corstone1000/applications.git;protocol=https;branch=master"
SRCREV = "ab79333697875666f397a3a70ee5da15a6eba4b5"

UPSTREAM_CHECK_COMMITS = "1"

COMPATIBLE_MACHINE = "corstone1000"

do_compile() {
    ${CC} ${S}/test-app.c ${CFLAGS} ${LDFLAGS} -o ${S}/systems-comms-tests
}

do_install() {
    install -D -p -m 0755 ${S}/systems-comms-tests ${D}${bindir}/systems-comms-tests
}
