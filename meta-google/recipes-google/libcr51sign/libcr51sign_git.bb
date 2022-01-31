SUMMARY = "Google libcr51sign"
DESCRIPTION = "Google libcr51sign"
GOOGLE_MISC_PROJ = "libcr51sign"

require ../google-misc/google-misc.inc

inherit pkgconfig

# We need to suppress these warnings in OpenSSL 3.0+ until a fix is available
CFLAGS += " \
  -Wno-deprecated-declarations \
"

DEPENDS += " \
  openssl \
"
