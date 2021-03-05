SUMMARY = "Sets up distcc for compilation on the target device"
DESCRIPTION = "${SUMMARY}"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://distcc.sh"

S = "${WORKDIR}"

# Default to the host machine for a running qemu session
DISTCC_HOSTS ?= "192.168.7.1"

do_configure() {
	sed -i "s%@DISTCC_HOSTS@%${DISTCC_HOSTS}%" distcc.sh
}

do_install() {
	install -d ${D}${sysconfdir}/profile.d
	install -m 0755 distcc.sh ${D}${sysconfdir}/profile.d/
}

RDEPENDS_${PN} = "distcc"
