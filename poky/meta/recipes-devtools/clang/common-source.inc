do_fetch() {
        :
}
do_fetch[noexec] = "1"
deltask do_unpack
deltask do_patch

SRC_URI = ""

do_configure[depends] += "llvm-project-source-${PV}:do_preconfigure"
do_populate_lic[depends] += "llvm-project-source-${PV}:do_unpack"
do_create_spdx[depends] += "llvm-project-source-${PV}:do_preconfigure"

# spdx shared workdir detection fails as not WORKDIR is altered but S and B
# return always true to fix that
def is_work_shared_spdx(d):
    return True
