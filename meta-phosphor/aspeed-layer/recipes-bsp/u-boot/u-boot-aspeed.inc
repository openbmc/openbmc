inherit image_version

SRC_URI_append_aspeed = " file://0001-configs-ast-Add-redundnant-env.patch"

SRC_URI_append_aspeed_df-obmc-ubi-fs = " \
    file://0002-config-ast-common-hack-bootopts.patch \
    file://0003-config-ast-common-Add-bootopts-to-support-ubi-and-mt.patch \
    file://0004-config-ast-common-Add-conditional-factory-reset-comm.patch \
    file://0005-config-ast-common-Fall-back-to-secondary-flash-on-fa.patch \
    "

SRC_URI_append_aspeed_df-isolate-bmc = " \
    file://0001-aspeed-Disable-unnecessary-features.patch \
    "
SRC_URI_append_aspeed_df-aspeednic-use-mac2 = " \
    file://0002-board-aspeed-aspeednic-Use-MAC2-for-networking.patch \
    "

do_patch[depends] += "os-release:do_populate_sysroot"

python do_patch_append_aspeed_df-obmc-ubi-fs () {
    version_id=do_get_versionID(d)
    d.setVar('VERSION_ID', version_id)
    bb.build.exec_func("patch_kernelname", d)
}

patch_kernelname () {
    sed -ri "s/kernel-(0|[a-fA-F0-9]{8})/kernel-${VERSION_ID}/g" \
    ${S}/include/configs/ast-common.h &> /dev/null
}
