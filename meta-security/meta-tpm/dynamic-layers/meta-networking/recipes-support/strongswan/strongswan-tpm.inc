FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

DEPENDS = "libtspi"

SRC_URI_append = " file://0001-xfrmi-Only-build-if-libcharon-is-built.patch"

PACKAGECONFIG += "aikgen tpm"

PACKAGECONFIG[tpm] = "--enable-tpm,--disable-tpm,,"
PACKAGECONFIG[aikgen] = "--enable-aikgen,--disable-aikgen,,"

EXTRA_OECONF += "--with-linux-headers=${STAGING_KERNEL_DIR}"
