#
# musl specific configuration
#

LIBCEXTENSION = "-musl"

LIBCOVERRIDE = ":libc-musl"

PREFERRED_PROVIDER_virtual/libc ?= "musl"
PREFERRED_PROVIDER_virtual/libiconv ?= "musl"
PREFERRED_PROVIDER_virtual/libintl ?= "musl"
PREFERRED_PROVIDER_virtual/crypt ?= "musl"
PREFERRED_PROVIDER_virtual/libc-locale ?= "musl-locales"
PREFERRED_PROVIDER_virtual/nativesdk-libintl ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/nativesdk-libiconv ?= "nativesdk-glibc"

DISTRO_FEATURES_OPTED_OUT += "ldconfig"

#USE_NLS ?= "no"

CXXFLAGS += "-fvisibility-inlines-hidden"

LIBC_DEPENDENCIES = "\
    musl \
    musl-dbg \
    musl-dev \
    musl-utils \
    musl-utils-iconv \
    bsd-headers-dev \
    "
