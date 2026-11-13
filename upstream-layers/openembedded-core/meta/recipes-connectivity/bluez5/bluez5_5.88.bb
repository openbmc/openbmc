require bluez5.inc

LDFLAGS += " ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', '-Wl,-z,nostart-stop-gc', '', d)}"

SRC_URI[sha256sum] = "6952c39eb5f8b0ee712cc50ab3bcff03be17c410dd97ca640f4849d9a1653ba3"

CVE_STATUS[CVE-2020-24490] = "cpe-incorrect: This issue has kernel fixes rather than bluez fixes"
CVE_STATUS[CVE-2020-12351] = "cpe-incorrect: This issue has kernel fixes rather than bluez fixes"
CVE_STATUS[CVE-2020-12352] = "cpe-incorrect: This issue has kernel fixes rather than bluez fixes"
CVE_STATUS[CVE-2026-19774] = "fixed-version: Fixed from version 5.87"

# noinst programs in Makefile.tools that are conditional on READLINE
# support
NOINST_TOOLS_READLINE ?= " \
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
    tools/iso-tester \
    tools/mesh-tester \
    tools/ioctl-tester \
    tools/6lowpan-tester \
"

# noinst programs in Makefile.tools that are conditional on TOOLS
# support
NOINST_TOOLS_BT ?= " \
    tools/bdaddr \
    tools/avinfo \
    tools/avtest \
    tools/scotest \
    tools/hwdb \
    tools/hcieventmask \
    tools/hcisecfilter \
    tools/btinfo \
    tools/btconfig \
    tools/btsnoop \
    tools/btproxy \
    tools/btiotest \
    tools/bneptest \
    tools/cltest \
    tools/oobtest \
    tools/advtest \
    tools/seq2bseq \
    tools/nokfw \
    tools/rtlfw \
    tools/bcmfw \
    tools/create-image \
    tools/eddystone \
    tools/ibeacon \
    tools/btgatt-client \
    tools/btgatt-server \
    tools/test-runner \
    tools/check-selftest \
    tools/gatt-service \
    profiles/iap/iapd \
    ${@bb.utils.contains('PACKAGECONFIG', 'btpclient', 'client/btpclient/btpclient client/btpclient/btpclientctl', '', d)} \
"
