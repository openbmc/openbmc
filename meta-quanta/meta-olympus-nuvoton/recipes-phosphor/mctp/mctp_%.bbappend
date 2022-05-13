FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://default \
             file://0001-support-smbus-in-mctp-mux.patch "

TARGET_CFLAGS:append:olympus-nuvoton = " -DMCTP_HAVE_FILEIO"
