require bluez5.inc

SRC_URI[md5sum] = "8e35c67c81a55d3ad4c9f22280dae178"
SRC_URI[sha256sum] = "5ffcaae18bbb6155f1591be8c24898dc12f062075a40b538b745bfd477481911"

# noinst programs in Makefile.tools that are conditional on READLINE
# support
NOINST_TOOLS_READLINE ?= " \
    ${@bb.utils.contains('PACKAGECONFIG', 'deprecated', 'attrib/gatttool', '', d)} \
    tools/obex-client-tool \
    tools/obex-server-tool \
    tools/bluetooth-player \
    tools/obexctl \
    tools/btmgmt \
"

# noinst programs in Makefile.tools that are conditional on TESTING
# support
NOINST_TOOLS_TESTING ?= " \
    emulator/btvirt \
    emulator/b1ee \
    emulator/hfp \
    peripheral/btsensor \
    tools/3dsp \
    tools/mgmt-tester \
    tools/gap-tester \
    tools/l2cap-tester \
    tools/sco-tester \
    tools/smp-tester \
    tools/hci-tester \
    tools/rfcomm-tester \
    tools/bnep-tester \
    tools/userchan-tester \
"

# noinst programs in Makefile.tools that are conditional on TOOLS
# support
NOINST_TOOLS_BT ?= " \
    tools/bdaddr \
    tools/avinfo \
    tools/avtest \
    tools/scotest \
    tools/amptest \
    tools/hwdb \
    tools/hcieventmask \
    tools/hcisecfilter \
    tools/btinfo \
    tools/btsnoop \
    tools/btproxy \
    tools/btiotest \
    tools/bneptest \
    tools/mcaptest \
    tools/cltest \
    tools/oobtest \
    tools/advtest \
    tools/seq2bseq \
    tools/nokfw \
    tools/create-image \
    tools/eddystone \
    tools/ibeacon \
    tools/btgatt-client \
    tools/btgatt-server \
    tools/test-runner \
    tools/check-selftest \
    tools/gatt-service \
    profiles/iap/iapd \
    ${@bb.utils.contains('PACKAGECONFIG', 'btpclient', 'tools/btpclient', '', d)} \
"
