# Use gem5 executable to produce a dtb

LICENSE = "MIT"

inherit deploy

DEPENDS = "gem5-aarch64-native"

do_configure[noexec] = "1"

do_compile() {
    # generate a dtb using gem5
    gem5.opt \
        ${STAGING_DATADIR_NATIVE}/gem5/${GEM5_RUN_PROFILE} \
        --dtb-gen

    if [ ! -f m5out/system.dtb ]; then
        echo "No dtb generated !!!"
        exit 1
    fi
}

do_install[noexec] = "1"

do_deploy() {
    install --d ${DEPLOYDIR}
    cp m5out/system.dtb ${DEPLOYDIR}/gem5-aarch64.dtb
}
addtask deploy before do_build after do_compile

