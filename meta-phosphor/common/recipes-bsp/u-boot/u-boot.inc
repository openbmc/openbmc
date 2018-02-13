LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRCREV = "7fb88a88d08c19b0e6f88c0a484ab001a4476a27"
UBRANCH = "v2016.07-aspeed-openbmc"
SRC_URI = "git://git@github.com/openbmc/u-boot.git;branch=${UBRANCH};protocol=https"

inherit image_version

SRC_URI += "file://0001-configs-ast-Add-redundnant-env.patch"

SRC_URI += "${@bb.utils.contains('MACHINE_FEATURES', 'obmc-ubi-fs', \
    'file://0001-config-ast-common-hack-bootopts.patch \
     file://0004-config-ast-common-ubi-bootops.patch \
     file://0001-config-ast-common-Add-conditional-factory-reset-comm.patch', '', d)}"

python do_configure () {
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

PV = "v2016.07+git${SRCPV}"
