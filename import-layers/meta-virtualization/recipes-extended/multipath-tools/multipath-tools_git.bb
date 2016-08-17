SUMMARY = "Tools to Manage Multipathed Devices with the device-mapper"
DESCRIPTION = "This package provides the tools to manage multipathed devices by \
instructing the device-mapper multipath module what to do"

HOMEPAGE = "http://christophe.varoqui.free.fr/"
DEPENDS = "readline libaio lvm2 udev"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=7be2873b6270e45abacc503abbe2aa3d"
S="${WORKDIR}/git"


SRC_URI = "git://git.opensvc.com/multipath-tools/.git;protocol=http"

SRCREV = "d3683ab18b386e9b3b54b59a122c689e9ebdf5cf"
PV = "0.4.9+gitr${SRCPV}"

inherit autotools-brokensep

EXTRA_OEMAKE="LIB=${libdir} exec_prefix=${exec_prefix} libdir=${libdir}"

PACKAGES =+ "libmpathpersist mpathpersist kpartx libmultipath multipath multipathd libmultipath-dev libmpathpersist-dev"


RDEPENDS_${PN} += "libmpathpersist mpathpersist kpartx libmultipath multipath multipathd udev"

do_install_append () {
	ln -sf libmpathpersist.so.0 ${D}${libdir}/libmpathpersist.so
	ln -sf libmultipath.so.0 ${D}${libdir}/libmultipath.so
}

ALLOW_EMPTY_${PN} = "1"
FILES_${PN}     = ""

FILES_libmpathpersist = "${libdir}/libmpathpersist*.so.0"
FILES_mpathpersist = "${sbindir}/mpathpersist"
FILES_kpartx = "${sbindir}/kpartx ${base_libdir}/udev/"
FILES_libmultipath = "${libdir}/libcheck*.so ${libdir}/libpri*.so ${libdir}/libmultipath*.so.0"
FILES_multipath = "${sbindir}/multipath ${sysconfdir}"
FILES_multipathd = "${sbindir}/multipathd ${base_libdir}"

#put the symbol link lib in -dev 
FILES_libmultipath-dev = "${libdir}/libmultipath*.so"
FILES_libmpathpersist-dev = "${libdir}/libmpathpersist*.so"


