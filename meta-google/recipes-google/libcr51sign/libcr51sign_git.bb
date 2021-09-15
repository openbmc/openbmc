SUMMARY = "Google libcr51sign"
DESCRIPTION = "Google libcr51sign"
GOOGLE_MISC_PROJ = "libcr51sign"

require ../google-misc/google-misc.inc

inherit pkgconfig

DEPENDS += " \
  openssl \
"
