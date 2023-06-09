SUMMARY = "Pulls in pkgdata for world"
LICENSE = "MIT"

INHIBIT_DEFAULT_DEPS = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask do_allpackagedata before do_build
do_allpackagedata() {
	:
}
do_allpackagedata[recrdeptask] = "do_packagedata do_allpackagedata"
do_allpackagedata[noexec] = "1"

WORLD_PKGDATADIR = "${D}/world-pkgdata"

addtask do_collect_packagedata after do_allpackagedata
SSTATETASKS += "do_collect_packagedata"
do_collect_packagedata[sstate-inputdirs] = "${WORLD_PKGDATADIR}"
do_collect_packagedata[sstate-outputdirs] = "${STAGING_DIR_HOST}/world-pkgdata"

python do_collect_packagedata() {
    import oe.copy_buildsystem
    outdir = os.path.join(d.getVar('WORLD_PKGDATADIR'))
    bb.utils.mkdirhier(outdir)
    sigfile = os.path.join(outdir, 'locked-sigs-pkgdata.inc')
    oe.copy_buildsystem.generate_locked_sigs(sigfile, d)
}

deltask do_fetch
deltask do_unpack
deltask do_patch
deltask do_configure
deltask do_compile
deltask do_install
deltask do_create_spdx
deltask do_create_spdx_runtime

do_prepare_recipe_sysroot[deptask] = ""

WORLD_PKGDATA_EXCLUDE ?= ""

python calculate_extra_depends() {
    exclude = '${WORLD_PKGDATA_EXCLUDE}'.split()
    for p in world_target:
        if p == self_pn:
            continue

        if p in exclude:
            continue

        deps.append(p)
}

PACKAGES = ""
