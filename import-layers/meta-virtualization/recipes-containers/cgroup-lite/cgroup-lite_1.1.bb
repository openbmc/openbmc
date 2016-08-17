SECTION = "devel"
SUMMARY = "Light-weight package to set up cgroups at system boot."
DESCRIPTION =  "Light-weight package to set up cgroups at system boot."
HOMEPAGE = "http://packages.ubuntu.com/source/precise/cgroup-lite"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=5d5da4e0867cf06014f87102154d0102"
SRC_URI = "http://archive.ubuntu.com/ubuntu/pool/main/c/cgroup-lite/cgroup-lite_1.1.tar.gz"
SRC_URI += "file://cgroups-init"
SRC_URI[md5sum] = "041a0d8ad2b192271a2e5507fdb6809f"
SRC_URI[sha256sum] = "e7f9992b90b5b4634f3b8fb42580ff28ff31093edb297ab872c37f61a94586bc"

inherit update-rc.d

INITSCRIPT_NAME = "cgroups-init"
INITSCRIPT_PARAMS = "start 8 2 3 4 5 . stop 20 0 1 6 ."
do_install() {
	install -d ${D}/bin
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/scripts/cgroups-mount ${D}/bin
	install -m 0755 ${S}/scripts/cgroups-umount ${D}/bin
	install -m 0755 ${WORKDIR}/cgroups-init ${D}${sysconfdir}/init.d/cgroups-init
}
