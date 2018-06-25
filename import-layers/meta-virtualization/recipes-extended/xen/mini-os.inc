# Copyright (C) 2018 kebodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require stubdom.inc

# clear this out to break dependency circle
DEPENDS = ""

do_configure() {
    ${MAKE} -C ${WORKDIR}/mini-os links
}

# Nothing to configure or compile
do_compile[noexec] = "1"

# needed because this directory isn't typically part of a sysroot
SYSROOT_DIRS += "${prefix}/mini-os"
RDEPENDS_${PN}-dev = "perl"

FILES_${PN}-dev = "\
    ${prefix} \
"

do_install() {
    install -d ${D}${prefix}/mini-os
    cp -r -t ${D}${prefix}/mini-os ${S}/*
    rm -rf ${D}${prefix}/mini-os/scripts
}
