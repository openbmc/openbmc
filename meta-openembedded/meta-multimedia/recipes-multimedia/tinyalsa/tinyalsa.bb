DESCRIPTION = "TinyALSA is a small library to interface with ALSA in \
the Linux kernel. It is a lightweight alternative to libasound."
HOMEPAGE = "https://github.com/tinyalsa/tinyalsa"
SECTION = "libs/multimedia"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://NOTICE;md5=dbdefe400d894b510a9de14813181d0b"

SRCREV = "8449529c7e50f432091539ba7b438e79b04059b5"
SRC_URI = "git://github.com/tinyalsa/tinyalsa;branch=master;protocol=https \
           file://0001-Use-CMAKE_INSTALL_-path-instead-of-hardcoding-bin-li.patch \
          "
PV = "1.1.1+git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake

# tinyalsa is built as a static library. Enable PIC to avoid relocation
# errors like these:
#
#    unresolvable R_AARCH64_ADR_PREL_PG_HI21 relocation against symbol `stderr@@GLIBC_2.17'
CFLAGS += " -fPIC -DPIC "
