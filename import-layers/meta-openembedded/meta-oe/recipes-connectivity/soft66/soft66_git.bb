SUMMARY = "Library and tools for Soft66ADD and related SDR radio receivers"
LICENSE = "GPLv3 & LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
                    file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02 "

PNBLACKLIST[soft66] ?= "BROKEN: depends on broken libftdi"

DEPENDS = "libftdi"

PV = "0.1.3+gitr${SRCPV}"
PR = "r1"

SRCREV = "a1dab25e73896c90c98227ac8055f227b830d512"
SRC_URI = "git://home.horsten.com/soft66 \
file://fix-ar.patch"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

