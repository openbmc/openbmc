# Create a subset of the package feed that just contain the
# packages depended on by this recipe.

LOCALPKGFEED_DIR = "${WORKDIR}/localpkgfeed"

addtask localpkgfeed after do_build
do_localpkgfeed[cleandirs] = "${LOCALPKGFEED_DIR}"
do_localpkgfeed[nostamp] = "1"

def get_packaging_class(d):
    package_class = d.getVar("PACKAGE_CLASSES").split()[0]
    return package_class.replace("package_", "")

python () {
    packaging = get_packaging_class(d)
    d.setVarFlag("do_localpkgfeed", "rdeptask", "do_package_write_" + packaging)
}

python do_localpkgfeed() {
    import oe.package_manager

    packaging = get_packaging_class(d)
    deploydir = d.getVar("DEPLOY_DIR_" + packaging.upper())
    task = "package_write_" + packaging

    oe.package_manager.create_packages_dir(d, d.getVar("LOCALPKGFEED_DIR"), deploydir, task, True, True)
}
