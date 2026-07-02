SUMMARY = "A modern, colorful tail replacement with split-pane log monitoring"
DESCRIPTION = "\
    Fuzzytail is a modern, fast replacement for tail with split-pane log \
    monitoring, syntax highlighting, and powerful filtering. \
"
HOMEPAGE = "https://github.com/yodabytz/fuzzytail"
BUGTRACKER = "https://github.com/yodabytz/fuzzytail/issues"
SECTION = "console/tools"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c603190f79e3e5272a870f1a0dd680ef"

SRC_URI = "git://github.com/yodabytz/fuzzytail.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "f08c8d3e2c9501c0d3eb278278ac95d8cc211547"

inherit cargo cargo-update-recipe-crates

require ${BPN}-crates.inc

do_install:append() {
    install -m 644 -Dt ${D}/etc/fuzzytail/themes ${S}/themes/ft.conf.*
}
