DESCRIPTION = "TinyALSA is a small library to interface with ALSA in \
the Linux kernel. It is a lightweight alternative to libasound."
HOMEPAGE = "https://github.com/tinyalsa/tinyalsa"
SECTION = "libs/multimedia"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://NOTICE;md5=d2918795d9185efcbf430b9ad5cda46d"

PV .= "+git"
SRCREV = "f78ed25aced2dfea743867b8205a787bfb091340"
SRC_URI = "git://github.com/tinyalsa/tinyalsa;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson

# tinyalsa is built as a static library. Enable PIC to avoid relocation
# errors like these:
#
#    unresolvable R_AARCH64_ADR_PREL_PG_HI21 relocation against symbol `stderr@@GLIBC_2.17'
CFLAGS += " -fPIC -DPIC "
