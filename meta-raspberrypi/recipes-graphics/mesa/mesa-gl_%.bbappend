PACKAGECONFIG_append_rpi = " gbm"
PROVIDES_append_rpi = " virtual/libgbm"

DRIDRIVERS_append_rpi = ",swrast"

do_install_append_rpi() {
    rm -rf ${D}${includedir}/KHR/khrplatform.h
}
