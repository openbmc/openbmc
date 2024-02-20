SUMMARY = "Receive a forwarded serial from serial-forward and provide a PTY"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07"
SECTION = "console/network"
SRCREV = "00dbec2636ae0385ad028587e20e446272ff97ec"
PV = "1.1+git"

SRC_URI = "git://github.com/freesmartphone/cornucopia.git;protocol=https;branch=master"
S = "${WORKDIR}/git/tools/serial_forward"

inherit autotools native

do_deploy[sstate-outputdirs] = "${DEPLOY_DIR_IMAGE}"
do_deploy() {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0755 ${B}/src/pty_forward ${DEPLOY_DIR_IMAGE}/pty-forward
}

addtask deploy before do_package after do_install
