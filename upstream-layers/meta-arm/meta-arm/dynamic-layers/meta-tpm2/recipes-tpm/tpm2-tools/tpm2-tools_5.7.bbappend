# Include tpm2-tools dependencies. diffutils is needed for cmp tool, coreutils needed for dd and head (GNU instead of the busybox variant)
# need perl-misc for shasum

PACKAGES:prepend = "${PN}-tests "

RDEPENDS:${PN}-tests += " ${PN} xxd bash bash-completion dbus python3-yamllint python3 perl-misc coreutils diffutils curl"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
            file://0001-Remove-simulator-and-abrmd-startup.patch \
            file://0003-abrmd_policysigned_sh-fix-error-clear-lockout.patch \
            file://tpm2-test     \
            file://tpm2-test-all \
        "

do_install:append() {
    install -d ${D}${datadir}/tpm2-tools/test
    install -m 0755 ${UNPACKDIR}/tpm2-test ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/tpm2-test-all ${D}${bindir}

    cd ${S}/test
    find . -type d -exec install -d -m 0755 ${D}${datadir}/tpm2-tools/test/{} \;
    find . -type f -exec install -m 0755 {} ${D}${datadir}/tpm2-tools/test/{} \;
}

FILES:${PN}-tests += "${datadir}/tpm2-tools/test ${bindir}/tpm2-test ${bindir}/tpm2-test-all"
