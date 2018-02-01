require quilt.inc
inherit gettext
RDEPENDS_${PN} += "patch diffstat bzip2 util-linux"
SRC_URI += "file://aclocal.patch \
            file://gnu_patch_test_fix_target.patch \
           "
