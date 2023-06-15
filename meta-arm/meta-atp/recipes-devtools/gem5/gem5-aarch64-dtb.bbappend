do_compile:prepend:gem5-atp-arm64() {
    # Export datadir paths for baremetal_atp.py script
    export GEM5_DATADIR="${STAGING_DATADIR_NATIVE}/gem5"
    export ATP_DATADIR="${STAGING_DATADIR_NATIVE}/gem5"
}
