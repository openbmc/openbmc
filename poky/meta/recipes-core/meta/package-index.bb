SUMMARY = "Rebuilds the package index"
LICENSE = "MIT"

INHIBIT_DEFAULT_DEPS = "1"
PACKAGES = ""

inherit nopackages

deltask do_fetch
deltask do_unpack
deltask do_patch
deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_sysroot

do_package_index[nostamp] = "1"
do_package_index[depends] += "${PACKAGEINDEXDEPS}"

python do_package_index() {
    from oe.rootfs import generate_index_files
    generate_index_files(d)
}
addtask do_package_index before do_build
EXCLUDE_FROM_WORLD = "1"
