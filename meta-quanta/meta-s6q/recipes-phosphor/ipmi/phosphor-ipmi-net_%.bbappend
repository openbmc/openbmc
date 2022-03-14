RMCPP_IFACE:s6q = "${@bb.utils.contains("MACHINE_FEATURES", "bonding", "bond0", "${DEFAULT_RMCPP_IFACE}", d)}"
