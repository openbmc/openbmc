require netmap.inc

DEPENDS = "virtual/kernel"
do_configure[depends] += "virtual/kernel:do_shared_workdir"

inherit module

CLEANBROKEN = "1"

export INSTALL_MOD_DIR="kernel/netmap-modules"

EXTRA_OECONF = "--kernel-dir=${STAGING_KERNEL_BUILDDIR} \
                --kernel-sources=${STAGING_KERNEL_DIR} \
                --install-mod-path=${D} \
                --driver-suffix="-netmap" \
                "

# The driver builds are optional, but for deterministic builds,
# we should be able to explicitly enable/disable the builds
# for them in a proper place (maybe in BSP).
# But we can't use PACKAGECONFIG since there is no option for
# each driver, and the options are:
#  --no-drivers    do not compile any driver
#  --no-drivers=   do not compile the given drivers (comma sep.)
#  --drivers=      only compile the given drivers (comma sep.)
#
# So use NETMAP_DRIVERS and the following python code to add proper
# configs to EXTRA_OECONF.
#
# The default is no-drivers, and all supported drivers are listed
# in NETMAP_ALL_DRIVERS.
NETMAP_DRIVERS ??= ""
NETMAP_ALL_DRIVERS = "ixgbe igb e1000e e1000 veth.c forcedeth.c virtio_net.c r8169.c"

python __anonymous () {
    drivers_list = d.getVar("NETMAP_DRIVERS", True).split()
    all_drivers_list = d.getVar("NETMAP_ALL_DRIVERS", True).split()
    config_drivers = "--drivers=" + ",".join(drivers_list)

    extra_oeconf_drivers = bb.utils.contains_any('NETMAP_DRIVERS', all_drivers_list, config_drivers, '--no-drivers', d)
    d.appendVar("EXTRA_OECONF", extra_oeconf_drivers)
}

LDFLAGS := "${@'${LDFLAGS}'.replace('-Wl,-O1', '')}"
LDFLAGS := "${@'${LDFLAGS}'.replace('-Wl,--as-needed', '')}"

do_configure () {
    cd ${S}/LINUX
    ./configure ${EXTRA_OECONF}
}

do_configure_append () {
    cat >>  ${S}/LINUX/netmap_linux_config.h <<EOF
#define NETMAP_LINUX_HAVE_HRTIMER_MODE_REL
#define NETMAP_LINUX_HAVE_HRTIMER_FORWARD_NOW
#define NETMAP_LINUX_HAVE_PHYS_ADDR_T
#define NETMAP_LINUX_HAVE_ACCESS_ONCE
#define NETMAP_LINUX_HAVE_NETDEV_OPS
#define NETMAP_LINUX_HAVE_INIT_NET
#define NETMAP_LINUX_HAVE_LIVE_ADDR_CHANGE
#define NETMAP_LINUX_HAVE_TX_SKB_SHARING
#define NETMAP_LINUX_HAVE_UNLOCKED_IOCTL
#define NETMAP_LINUX_HAVE_PERNET_OPS_ID
#define NETMAP_LINUX_VIRTIO_FUNCTIONS
#define NETMAP_LINUX_VIRTIO_FREE_PAGES
#define NETMAP_LINUX_VIRTIO_GET_VRSIZE
#define NETMAP_LINUX_TIMER_RTYPE enum hrtimer_restart
#define NETMAP_LINUX_VIRTIO_MULTI_QUEUE
#define NETMAP_LINUX_HAVE_E1000E_EXT_RXDESC
#define NETMAP_LINUX_HAVE_E1000E_DOWN2
EOF

if ${@ 'false' if (bb.utils.vercmp_string(d.getVar('KERNEL_VERSION', True), '3.17') < 0) else 'true' } ; then
    echo OK
    cat >>  ${S}/LINUX/netmap_linux_config.h <<EOF
#define NETMAP_LINUX_ALLOC_NETDEV_4ARGS
EOF
fi
}

do_compile () {
    cd ${S}/LINUX
    oe_runmake
}

do_install () {
    cd ${S}/LINUX
    oe_runmake install
}
