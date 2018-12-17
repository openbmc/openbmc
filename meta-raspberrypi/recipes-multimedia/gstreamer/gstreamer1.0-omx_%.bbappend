DEPENDS_append_rpi = " userland"
GSTREAMER_1_0_OMX_TARGET_rpi = "rpi"
GSTREAMER_1_0_OMX_CORE_NAME_rpi = "${libdir}/libopenmaxil.so"
# How to make this RPI specific?
EXTRA_OECONF_append_rpi  = " CFLAGS="$CFLAGS -I${STAGING_DIR_TARGET}/usr/include/IL -I${STAGING_DIR_TARGET}/usr/include/interface/vcos/pthreads -I${STAGING_DIR_TARGET}/usr/include/interface/vmcs_host/linux""
#examples only build with GL but not GLES, so disable it for RPI
EXTRA_OECONF_append_rpi = " --disable-examples"

RDEPENDS_${PN}_append_rpi = " userland"
