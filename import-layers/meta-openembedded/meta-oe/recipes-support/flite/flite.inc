DESCRIPTION = "flite - the festival light speech synthesizer"
HOMEPAGE = "http://www.speech.cs.cmu.edu/flite/"
SECTION = "console/utils"
LICENSE = "flite"
LIC_FILES_CHKSUM = "file://COPYING;md5=68a16b1ee49c96c36f232ccdcd287650"
DEPENDS = "alsa-oss"

PARALLEL_MAKE = ""

S = "${WORKDIR}/flite-${PV}-release"

CFLAGS += " -lasound "

inherit autotools-brokensep

PACKAGES += "lib${PN} lib${PN}-vox8 lib${PN}-vox16"

FILES_${PN} = "${bindir}"

FILES_lib${PN} ="${libdir}/libflite.so.* \
    ${libdir}/libflite_cmu_time_awb.so.* \
    ${libdir}/libflite_cmulex.so.* \
    ${libdir}/libflite_usenglish.so.*"

FILES_lib${PN}-vox8 = "${libdir}/libflite_cmu_us_kal.so.*"

FILES_lib${PN}-vox16 = "${libdir}/libflite_cmu_us_kal16.so.*"

LEAD_SONAME = "libflite.so"

do_install() {
    oe_runmake INSTALLBINDIR="${D}${bindir}" INSTALLLIBDIR="${D}${libdir}" INSTALLINCDIR="${D}${includedir}" install
}
