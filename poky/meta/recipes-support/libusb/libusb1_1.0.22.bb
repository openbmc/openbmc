SUMMARY = "Userspace library to access USB (version 1.0)"
HOMEPAGE = "http://libusb.sf.net"
BUGTRACKER = "http://www.libusb.org/report"
SECTION = "libs"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

BBCLASSEXTEND = "native nativesdk"

SRC_URI = "${SOURCEFORGE_MIRROR}/libusb/libusb-${PV}.tar.bz2 \
           file://no-dll.patch \
           file://run-ptest \
          "

SRC_URI[md5sum] = "466267889daead47674df933cea9cacb"
SRC_URI[sha256sum] = "75aeb9d59a4fdb800d329a545c2e6799f732362193b465ea198f2aa275518157"

S = "${WORKDIR}/libusb-${PV}"

inherit autotools pkgconfig ptest

PACKAGECONFIG_class-target ??= "udev"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,udev"

EXTRA_OECONF = "--libdir=${base_libdir}"

do_install_append() {
	install -d ${D}${libdir}
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		mv ${D}${base_libdir}/pkgconfig ${D}${libdir}
	fi
}

do_compile_ptest() {                                                             
    oe_runmake -C tests stress                                                   
}                                                                                
                                                                                 
do_install_ptest() {                                                             
    install -m 755 ${B}/tests/.libs/stress ${D}${PTEST_PATH}         
}

FILES_${PN} += "${base_libdir}/*.so.*"

FILES_${PN}-dev += "${base_libdir}/*.so ${base_libdir}/*.la"
