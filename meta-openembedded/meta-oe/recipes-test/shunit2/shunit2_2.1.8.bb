SUMMARY = "shUnit2 is a xUnit based unit test framework for Bourne based shell scripts"
HOMEPAGE = "https://github.com/kward/shunit2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI = " git://github.com/kward/shunit2.git;branch=master;protocol=https"
SRCREV = "080159b303537888c5d41910b4d47a5002638e30"

S = "${WORKDIR}/git"

do_install() {
    install -D -m 0755 ${S}/shunit2 ${D}${bindir}/shunit2
}
