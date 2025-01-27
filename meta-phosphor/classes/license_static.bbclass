####
# Copyright 2021 Intel Corporation
#
# Add a class to support serving license info through bmcweb.
#
# bmcweb serves static content from the /usr/share/www folder, so this class
# copies the license info from /usr/share/common-licenses to
# /usr/share/www/common-licenses so it will be statically served by bmcweb.
#
# Requires 'COPY_LIC_DIRS' to be enabled to create /usr/share/common-licenses.
#
# Class can be inherited in a project bbclass to copy the license info.
#
# Example:
# inherit license_static
####

STATIC_LICENSE_DIR = "${IMAGE_ROOTFS}${datadir}/www/common-licenses"


def add_index_html_header(f):
    f.write("<!DOCTYPE html>")
    f.write("<html>")
    f.write("<body>")
    f.write("<p>")


def add_index_html_footer(f):
    f.write("</p>")
    f.write("</body>")
    f.write("</html>")


def create_index_files(d):
    import os

    static_license_dir = d.getVar('STATIC_LICENSE_DIR')
    for dirpath, dirnames, filenames in os.walk(static_license_dir):
        with open(os.path.join(dirpath, "index.html"), "w") as f:
            add_index_html_header(f)
            full_list = filenames+dirnames
            full_list.sort()
            f.write("<br>".join(full_list))
            add_index_html_footer(f)


def copy_license_files(d):
    import shutil

    rootfs_license_dir = d.getVar('ROOTFS_LICENSE_DIR')
    static_license_dir = d.getVar('STATIC_LICENSE_DIR')
    shutil.copytree(rootfs_license_dir, static_license_dir)


python do_populate_static_lic() {
    copy_lic_dirs = d.getVar('COPY_LIC_DIRS')
    if copy_lic_dirs == "1":
        copy_license_files(d)
        create_index_files(d)
    else:
        bb.warn("Static licenses not copied because 'COPY_LIC_DIRS' is disabled.")
}

ROOTFS_POSTPROCESS_COMMAND:append = " do_populate_static_lic"
