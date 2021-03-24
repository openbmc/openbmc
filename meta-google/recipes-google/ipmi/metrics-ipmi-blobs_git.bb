SUMMARY = "gBMC Health Metrics Blob"
DESCRIPTION = "BMC health metrics IPMI blob handler."
GOOGLE_MISC_PROJ = "metrics-ipmi-blobs"

require ../google-misc/google-misc.inc

inherit pkgconfig

DEPENDS += " \
  phosphor-ipmi-blobs \
  phosphor-logging \
  protobuf-native \
  protobuf \
  "

FILES_${PN} += " \
  ${libdir}/ipmid-providers/libmetricsblob.so* \
  ${libdir}/blob-ipmid/libmetricsblob.so* \
  "
BLOBIPMI_PROVIDER_LIBRARY += "libmetricsblob.so"

INSANE_SKIP_${PN} += "dev-so"

do_install_append() {
  install -d ${D}/${libdir}/blob-ipmid
  ln -s ../ipmid-providers/libmetricsblob.so ${D}/${libdir}/blob-ipmid/libmetricsblob.so.0
}
