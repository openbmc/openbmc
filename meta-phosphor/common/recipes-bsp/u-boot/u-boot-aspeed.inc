inherit image_version

SRC_URI_append_aspeed = " file://0001-configs-ast-Add-redundnant-env.patch"

SRC_URI_append_aspeed = " ${@bb.utils.contains('MACHINE_FEATURES', 'obmc-ubi-fs', \
    'file://0001-config-ast-common-hack-bootopts.patch \
     file://0004-config-ast-common-ubi-bootops.patch \
     file://0001-config-ast-common-Add-conditional-factory-reset-comm.patch', '', d)}"


python do_configure_aspeed () {
    if "obmc-ubi-fs" in d.getVar("MACHINE_FEATURES", True):
        version_id=do_get_versionID(d)
        d.setVar('VERSION_ID', version_id)
        bb.build.exec_func("patch_uboot", d)
}

patch_uboot () {
	sed -i "s/kernel-0/kernel-${VERSION_ID}/g" \
	${S}/patches/0004-config-ast-common-ubi-bootops.patch &> /dev/null
	sed -i "s/kernel-0/kernel-${VERSION_ID}/g" \
	${S}/include/configs/ast-common.h &> /dev/null
}
