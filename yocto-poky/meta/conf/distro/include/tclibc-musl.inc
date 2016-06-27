#
# musl specific configuration
#

LIBCEXTENSION = "-musl"

# Add uclibc overrides to the overrides.
LIBCOVERRIDE = ":libc-musl"
OVERRIDES .= "${LIBCOVERRIDE}"

PREFERRED_PROVIDER_virtual/libc ?= "musl"
PREFERRED_PROVIDER_virtual/libiconv ?= "musl"
PREFERRED_PROVIDER_virtual/libintl ?= "musl"
PREFERRED_PROVIDER_virtual/nativesdk-libintl ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/nativesdk-libiconv ?= "nativesdk-glibc"

#USE_NLS ?= "no"

CXXFLAGS += "-fvisibility-inlines-hidden"

IMAGE_LINGUAS = ""

LIBC_DEPENDENCIES = "\
    musl \
    musl-dbg \
    musl-dev \
    "

# GLib binaries trigger assertion failures and crash under qemu
# when musl is in use
DISTRO_FEATURES_BACKFILL_CONSIDERED += "gobject-introspection-data"
