# Warning: Any providers listed here will have their whitelists ignored.  Add
# providers with whitelists that should not be ignored to
# conf/machine/openpower.inc

VIRTUAL-RUNTIME_phosphor-ipmi-providers:append:df-openpower = " \
    openpower-host-ipmi-flash \
"
