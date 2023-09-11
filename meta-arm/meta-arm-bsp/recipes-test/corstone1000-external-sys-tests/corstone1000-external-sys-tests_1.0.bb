SUMMARY = "CORSTONE1000 external systems communications tests"
DESCRIPTION = "This is a Linux userspace tool to test the communication between Corstone1000 cores"
HOMEPAGE = "https://git.linaro.org/landing-teams/working/arm/test-apps.git"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.md;md5=e44b2531cd6ffe9dece394dbe988d9a0"

SRC_URI = "git://git.gitlab.arm.com/arm-reference-solutions/corstone1000/applications.git;protocol=https;branch=master"
SRCREV = "2945cd92f7c6dbe4999ee72cd5cf1e2615eedba7"
PV .= "+git"

COMPATIBLE_MACHINE = "corstone1000"

S = "${WORKDIR}/git"

do_compile() {
    ${CC} ${S}/test-app.c ${CFLAGS} ${LDFLAGS} -o ${S}/systems-comms-tests
}

do_install() {
    install -D -p -m 0755 ${S}/systems-comms-tests ${D}${bindir}/systems-comms-tests
}
