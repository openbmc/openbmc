SUMMARY = "ms-tpm20-ref for Trusted Services"

SRC_URI_MS_TPM20_REF = "git://github.com/microsoft/ms-tpm-20-ref;protocol=https;branch=v1.83"

SRCREV_ms-tpm-20-ref = "e9fc7b89d865536c46deb63f9c7d0121a3ded49c"

LIC_FILES_CHKSUM += "file://ts-external/mstpm/LICENSE;md5=5a3925ece0806073ae9ebbb08ff6f11e"

EXTRA_OECMAKE += "-DMS_TPM_SOURCE_DIR=${S}/ts-external/mstpm"

SRC_URI += "${SRC_URI_MS_TPM20_REF};name=ms-tpm-20-ref;destsuffix=ts/ts-external/mstpm \
"

do_apply_local_src_patches:append() {
     apply_local_src_patches ${S}/external/ms_tpm ${S}/ts-external/mstpm
}
