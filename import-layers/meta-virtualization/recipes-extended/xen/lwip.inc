# Copyright (C) 2018 kebodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require stubdom.inc

# clear this out to break dependency circle
DEPENDS = ""

# Nothing to configure or compile
# For stubdoms, lwip is basically a source package with a couple of patches applied.
do_configure[noexec] = "1"
do_compile[noexec] = "1"

# needed because this directory isn't typically part of a sysroot
SYSROOT_DIRS += "${prefix}/lwip"

FILES_${PN} = "\
    ${prefix} \
"

do_install() {
    install -d ${D}${prefix}/lwip
    cp -r -t ${D}${prefix}/lwip ${S}/src/*
}
