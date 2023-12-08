SUMMARY = "Enable and disable eSPI bus on demand"
DESCRIPTION = "Enable and disable eSPI bus on demand"
GOOGLE_MISC_PROJ = "espi-control"

require ../google-misc/google-misc.inc

inherit pkgconfig

DEPENDS += " \
  stdplus \
"
