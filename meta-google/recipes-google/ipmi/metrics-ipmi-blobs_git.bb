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

FILES:${PN} += "${libdir}/blob-ipmid"

EXTRA_OEMESON += "-Dtests=disabled"
