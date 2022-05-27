SUMMARY = "An adapter to Linux kernel support for inotify directory-watching."
AUTHOR = "Dustin Oprea"
HOMEPAGE = "https://pypi.org/project/inotify/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://setup.py;md5=79b7ec72aa0d446a552d3cceb5799e41;beginline=28;endline=28"

SRC_URI[sha256sum] = "974a623a338482b62e16d4eb705fb863ed33ec178680fc3e96ccdf0df6c02a07"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-logging \
"
