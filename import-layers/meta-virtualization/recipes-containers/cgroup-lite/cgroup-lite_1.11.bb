SECTION = "devel"
SUMMARY = "Light-weight package to set up cgroups at system boot."
DESCRIPTION =  "Light-weight package to set up cgroups at system boot."
HOMEPAGE = "http://packages.ubuntu.com/source/precise/cgroup-lite"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=5d5da4e0867cf06014f87102154d0102"
SRC_URI = "https://launchpad.net/ubuntu/+archive/primary/+files/cgroup-lite_1.11.tar.xz"
SRC_URI += "file://cgroups-init"
SRC_URI[md5sum] = "b20976194ee8fdb61e6b55281fb6ead4"
SRC_URI[sha256sum] = "a79ab9ae6fb3ff3ce0aa5539b055c0379eaffdc6c5f003af4010fcea683c1a45"

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
