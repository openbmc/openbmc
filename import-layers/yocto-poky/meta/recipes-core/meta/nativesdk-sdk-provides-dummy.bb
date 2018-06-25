DUMMYARCH = "sdk-provides-dummy-${SDKPKGSUFFIX}"

# Add /bin/sh?
DUMMYPROVIDES = "\
    /bin/bash \
    /usr/bin/env \
    /usr/bin/perl \
    pkgconfig \
    libGL.so()(64bit) \
    libGL.so \
"

require dummy-sdk-package.inc

inherit nativesdk
