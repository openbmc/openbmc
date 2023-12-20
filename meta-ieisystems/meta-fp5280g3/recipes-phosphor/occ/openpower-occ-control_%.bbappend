DEPENDS:append = " \
        pdbg \
        ipl \
        libpldm \
        "

EXTRA_OEMESON:append = " \
        -Dmax-cpus=4 \
        -Dwith-host-communication-protocol=pldm \
        -Dpower10-support=enabled \
        -Dread-occ-sensors=enabled \
        "
