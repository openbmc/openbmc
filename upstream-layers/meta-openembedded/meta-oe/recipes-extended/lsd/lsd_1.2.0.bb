SUMMARY = "An ls command with a lot of pretty colors and some other stuff."
DESCRIPTION = "\
    This project is a rewrite of GNU ls with lots of added features like \
    colors, icons, tree-view, more formatting options etc. \
"
HOMEPAGE = "https://github.com/lsd-rs/lsd"
BUGTRACKER = "https://github.com/lsd-rs/lsd/issues"
SECTION = "console/tools"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=153d2db1c329326a2d9f881317ea942e"

SRC_URI = "git://github.com/lsd-rs/lsd.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "d5a4e1cb80626d5ec94b237f6b77f7280d0f2fc9"

inherit cargo cargo-update-recipe-crates

require ${BPN}-crates.inc

INSANE_SKIP:${PN} = "already-stripped"
