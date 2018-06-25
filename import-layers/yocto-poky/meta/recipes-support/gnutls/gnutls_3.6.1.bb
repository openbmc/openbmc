require gnutls.inc

SRC_URI += "file://0001-configure.ac-fix-sed-command.patch \
            file://arm_eabi.patch \
           "
SRC_URI[md5sum] = "4b65ae3ffef59f3eeed51a6166ff12b3"
SRC_URI[sha256sum] = "20b10d2c9994bc032824314714d0e84c0f19bdb3d715d8ed55beb7364a8ebaed"

BBCLASSEXTEND = "native nativesdk"
