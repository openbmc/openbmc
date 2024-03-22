SUMMARY = "gBMC Health Metrics Blob"
DESCRIPTION = "BMC health metrics IPMI blob handler."
GOOGLE_MISC_PROJ = "metrics-ipmi-blobs"

require ../google-misc/google-misc.inc

inherit pkgconfig

DEPENDS += " \
  nanopb-generator-native \
  nanopb-runtime \
  phosphor-ipmi-blobs \
  phosphor-logging \
  "

FILES:${PN} += "${libdir}/blob-ipmid"

EXTRA_OEMESON += "-Dtests=disabled"
