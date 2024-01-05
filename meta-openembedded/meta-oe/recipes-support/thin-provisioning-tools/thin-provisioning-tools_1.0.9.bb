SUMMARY = "Tools of dm-thin device-mapper"
DESCRIPTION = "A suite of tools for manipulating the metadata of the dm-thin device-mapper target."
HOMEPAGE = "https://github.com/jthornber/thin-provisioning-tools"
LICENSE = "GPL-3.0-only"
SECTION = "devel"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
S = "${WORKDIR}/git"

SRC_URI = " \
    git://github.com/jthornber/thin-provisioning-tools;branch=main;protocol=https \
    "
SRC_URI:append:libc-musl = " file://0001-Replace-LFS-functions.patch"

SRCREV = "1d60839b0a920df6476712b80f933854fb32e160"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

inherit cargo cargo-update-recipe-crates

require ${BPN}-crates.inc
require ${BPN}-git-crates.inc

do_install:append() {
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
	    thin_repair \
	    thin_restore \
	    thin_rmap \
	    thin_metadata_size \
	    thin_metadata_pack \
	    thin_metadata_unpack \
	    thin_trim \
	    era_check \
	    era_dump \
	    era_invalidate \
	    era_restore; do
                ln -sf pdata_tools ${D}${bindir}/$tool
            done
}
