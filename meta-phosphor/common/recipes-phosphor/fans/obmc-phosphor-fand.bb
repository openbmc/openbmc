SUMMARY = "Phosphor OpenBMC Fan Management."
DESCRIPTION = "Phosphor OpenBMC fan management reference implementation."
PR = "r1"

inherit obmc-phosphor-fan-mgmt
inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-c-daemon

S = "${WORKDIR}"
SRC_URI += "file://Makefile \
           file://obmc-phosphor-fand.c \
           "
