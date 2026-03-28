SUMMARY = "Tools of dm-thin device-mapper"
DESCRIPTION = "A suite of tools for manipulating the metadata of the dm-thin device-mapper target."
HOMEPAGE = "https://github.com/device-mapper-utils/thin-provisioning-tools"
LICENSE = "GPL-3.0-only"
SECTION = "devel"

LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = " \
    git://github.com/device-mapper-utils/thin-provisioning-tools;branch=main;protocol=https;tag=v${PV} \
    file://0001-Use-portable-atomics-crate.patch \
    file://disable-cargo-metadata.patch \
    ${@bb.utils.contains('TUNE_FEATURES', '32', \
        'file://dms-no-layout-check.patch;patchdir=${CARGO_VENDORING_DIRECTORY}/devicemapper-sys-0.3.3', \
        '', d)} \
    "

SRCREV = "8b663fb4c6fb8e52ca06cea57b986c5ba45f668d"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

inherit cargo cargo-update-recipe-crates
inherit pkgconfig

DEPENDS += "udev libdevmapper libdevmapper-native clang-native"

export LIBCLANG_PATH = "${STAGING_LIBDIR_NATIVE}"
# Remove octeontx2 specific CPU flags that may cause issues with bindgen
BINDGEN_EXTRA_CLANG_ARGS = "${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS} --target=${TARGET_SYS}"
BINDGEN_EXTRA_CLANG_ARGS:remove = "-mcpu=octeontx2+crypto"
export BINDGEN_EXTRA_CLANG_ARGS

require ${BPN}-crates.inc

do_install:append() {
	install -d ${D}${sbindir}
	mv ${D}${bindir}/pdata_tools ${D}${sbindir}/pdata_tools
	rmdir --ignore-fail-on-non-empty ${D}${bindir}

	for tool in cache_check \
	    cache_dump \
	    cache_metadata_size \
	    cache_repair \
	    cache_restore \
	    cache_writeback \
	    thin_check \
	    thin_delta \
	    thin_dump \
	    thin_ls \
	    thin_metadata_size \
	    thin_metadata_pack \
	    thin_metadata_unpack \
	    thin_repair \
	    thin_restore \
	    thin_rmap \
	    thin_shrink \
	    thin_trim \
	    era_check \
	    era_dump \
	    era_invalidate \
	    era_repair \
	    era_restore; do
                ln -sf pdata_tools ${D}${sbindir}/$tool
            done
}
