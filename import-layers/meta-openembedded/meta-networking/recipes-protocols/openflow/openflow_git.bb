include ${BPN}.inc

SRCREV = "c84f33f09d5dbcfc9b489f64cb30475bf36f653a"
PV = "1.0+git${SRCPV}"

SRC_URI += "\
           file://0001-Check-and-use-strlcpy-from-libc-before-defining-own.patch \
           file://0002-lib-netdev-Adjust-header-include-sequence.patch \
           "
